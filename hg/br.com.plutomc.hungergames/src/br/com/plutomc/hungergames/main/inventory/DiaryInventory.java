package br.com.plutomc.hungergames.main.inventory;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.utils.menu.MenuInventory;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.Member;
import br.com.plutomc.core.common.utils.DateUtils;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.main.gamer.GamerImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class DiaryInventory {

	public DiaryInventory(Player player) {
		MenuInventory menuInventory = new MenuInventory("§7Kit diário", 4);
		GamerImpl gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), GamerImpl.class);
		Member member = CommonPlugin.getInstance().getMemberManager().getMember(gamer.getUniqueId());

		for (Entry<Integer, Ability> entry : gamer.getDiaryMap().entrySet()) {
			menuInventory.setItem(11 + ((entry.getKey() - 1) * 2), new ItemBuilder().name("§aOpção " + entry.getKey())
					.type(Material.STAINED_GLASS_PANE).durability(13).build());

			Ability ability = entry.getValue();

			menuInventory.setItem(20 + ((entry.getKey() - 1) * 2),
					new ItemBuilder().name("§a" + StringFormat.formatString(ability.getName()))
							.type(ability.getAbilityIcon().getType())
							.durability(ability.getAbilityIcon().getDurability())
							.lore("§7" + ability.getDescription() + "\n\n§aClique para selecionar!").build(),
					(p, inv, type, stack, slot) -> {
						long expire = DateUtils.getMidNight();
						
						gamer.setDiaryExpire(expire);
						member.addPermission("kit." + ability.getName());
						p.closeInventory();
						p.sendMessage("§aVocê selecionou o " + StringFormat.formatString(ability.getName())
								+ " como kit diário!");
					});
		}

		/*menuInventory.setItem(35, new ItemBuilder().name("§aNova rotação").type(Material.GOLD_INGOT).lore(
				"\n§7Caso você queira uma nova rotação de 3 kits diários pelo custo de §6500 coins§7.\n\n§aClique para selecionar.")
				.build(), (p, inv, type, stack, slot) -> {
					if (member.getCoins() > 500) {
						gamer.randomDiary();
						member.removeCoins(500);
						new DiaryInventory(player);
					} else {
						p.closeInventory();
						p.sendMessage("§cVocê não possui coins o suficiente para isso.");
					}
				}); */

		if (gamer.getDiaryExpire() > System.currentTimeMillis()) {
			player.sendMessage("§cVocê precisa esperar mais " + DateUtils.getTime(gamer.getDiaryExpire())
					+ " para usar isso novamente!");
		} else {
			menuInventory.open(player);
		}
	}

}
