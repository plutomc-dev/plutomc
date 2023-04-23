package br.com.plutomc.hungergames.main.inventory;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.utils.menu.MenuInventory;
import br.com.plutomc.core.bukkit.utils.menu.MenuItem;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.HardcoreMain;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SelectorInventory {

	private int itemsPerPage = 7 * 3;

	public SelectorInventory(Player player, int abilityId, int page, OrderType orderType) {
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());
		MenuInventory menu = new MenuInventory("§7Selecionar kit", 6);

		List<MenuItem> items = new ArrayList<>();

		for (Ability ability : orderType.order(gamer, GameAPI.getInstance().getAbilityManager().getAbilities())) {
			boolean hasKit = GameHelper.hasAbility(gamer, ability, abilityId) && ability.isAbilityEnabled();

			if (hasKit) {
				items.add(new MenuItem(
						new ItemBuilder().type(ability.getAbilityIcon().getType())
								.durability(ability.getAbilityIcon().getDurability())
								.name("§a" + StringFormat.formatString(ability.getName()))
								.lore("\n§7" + ability.getDescription() + "\n\n§eClique para selecionar.").build(),
						(p, inv, type, stack, slot) -> {
							if (gamer.hasAbility(ability.getName())) {
								p.sendMessage(" §4» §fVocê já está usando esse kit!");
								p.closeInventory();
								return;
							}

							if (abilityId <= HardcoreMain.getInstance().getMaxAbilities())
								GameHelper.selectAbility(gamer, ability, abilityId);
							p.closeInventory();
						}));
			}
		}

		int pageStart = 0;
		int pageEnd = itemsPerPage;

		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}

		if (pageEnd > items.size()) {
			pageEnd = items.size();
		}

		int w = 10;

		for (int i = pageStart; i < pageEnd; i++) {
			MenuItem item = items.get(i);
			menu.setItem(item, w);

			if (w % 9 == 7) {
				w += 3;
				continue;
			}

			w += 1;
		}

		if (page != 1) {
			menu.setItem(
					new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
							(p, inventory, clickType, item, slot) -> new SelectorInventory(p, abilityId, page - 1)),
					45);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(
					new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
							(p, inventory, clickType, item, slot) -> new SelectorInventory(p, abilityId, page + 1)),
					53);
		}

		if (gamer.hasAbility(abilityId))
			menu.setItem(48, new ItemBuilder().name("§cRemover kit").type(Material.BARRIER).build(),
					(p, inventory, clickType, item, slot) -> {
						GameHelper.removeAbility(gamer, gamer.getAbility(abilityId), abilityId);
						p.closeInventory();
					});

		menu.setItem(49,
				new ItemBuilder()
						.name("§eKit Selecionado - §a" + (gamer.hasAbility(abilityId)
								? StringFormat.formatString(gamer.getAbility(abilityId).getName())
								: "Nenhum"))
						.type(gamer.hasAbility(abilityId) ? gamer.getAbility(abilityId).getAbilityIcon().getType()
								: Material.ITEM_FRAME)
						.build());

		menu.setItem(50,
				new ItemBuilder().name("§aOrdenando por: §7" + orderType.getName()).type(Material.PAPER).build(),
				(p, inventory, clickType, item, slot) -> {
					new SelectorInventory(player, abilityId, page,
							orderType.ordinal() == OrderType.values().length - 1 ? OrderType.values()[0]
									: OrderType.values()[orderType.ordinal() + 1]);
				});

		menu.open(player);
	}

	public SelectorInventory(Player player, int abilityid, int page) {
		this(player, abilityid, page, OrderType.ALPHABET);
	}

	public interface Order<T> {

		Collection<T> order(Gamer gamer, Collection<T> tList);

	}

	@Getter
	@AllArgsConstructor
	public enum OrderType implements Order<Ability> {

		ALPHABET("Alfabeto") {

			@Override
			public Collection<Ability> order(Gamer gamer, Collection<Ability> tList) {
				return tList.stream().sorted((a1, a2) -> a1.getName().compareTo(a2.getName()))
						.collect(Collectors.toList());
			}

		},
		MY_OWN("Meus kits") {
			@Override
			public Collection<Ability> order(Gamer gamer, Collection<Ability> tList) {
				return tList.stream().sorted((a1, a2) -> Boolean.valueOf(GameHelper.hasAbility(gamer, a1))
						.compareTo(GameHelper.hasAbility(gamer, a2))).collect(Collectors.toList());
			}
		};

		private String name;

	}

}
