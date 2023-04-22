package br.com.plutomc.hungergames.main.inventory;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.utils.menu.MenuInventory;
import br.com.plutomc.core.bukkit.utils.menu.MenuItem;
import br.com.plutomc.hungergames.engine.GameAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SpectatorInventory {

	private int itemsPerPage = 7 * 3;

	public SpectatorInventory(Player player, int page) {
		MenuInventory menu = new MenuInventory("§7Jogadores", 6);
		List<MenuItem> list = GameAPI.getInstance().getGamerManager().getGamers().stream()
				.filter(gamer -> gamer.isPlaying() && gamer.getPlayer().isOnline())
				.map(gamer -> new MenuItem(
						new ItemBuilder().name("§a" + gamer.getPlayer().getName()).type(Material.SKULL_ITEM)
								.lore("\n§7Kits: §f" + gamer.getAbilitiesName() + "\n§7Kills: §f" + gamer.getKills())
								.durability(3).skin(gamer.getAbilitiesName()).build(),
						(p, inv, type, stack, slot) -> p.teleport(gamer.getPlayer())))
				.collect(Collectors.toList());

		int pageStart = 0;
		int pageEnd = itemsPerPage;

		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}

		if (pageEnd > list.size()) {
			pageEnd = list.size();
		}

		int w = 10;

		for (int i = pageStart; i < pageEnd; i++) {
			MenuItem item = list.get(i);
			menu.setItem(item, w);

			if (w % 9 == 7) {
				w += 3;
				continue;
			}

			w += 1;
		}

		menu.open(player);
	}

}
