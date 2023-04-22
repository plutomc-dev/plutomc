package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MonkAbility extends AbilityImpl {

	public MonkAbility() {
		super("monk", Material.BLAZE_ROD, "Desarme seu inimigo usando seu Blaze Rod.");
		addItem(new ItemBuilder().type(Material.BLAZE_ROD).name("§aMonk Rod").build());
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return;

		Player p = e.getPlayer();

		if (!hasAbility(p) || !isItemKit(p.getItemInHand()))
			return;

		Player clicked = (Player) e.getRightClicked();

		if (GameAPI.getInstance().getState().isInvencibility()) {
			p.sendMessage("§cVocê não pode usar isto agora!");
			return;
		}

		if (!isCooldown(p)) {
			addCooldown(p.getUniqueId(), 8l);

			int randomN = new Random().nextInt(36);

			ItemStack atual = (clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null);
			ItemStack random = (clicked.getInventory().getItem(randomN) != null
					? clicked.getInventory().getItem(randomN).clone()
					: null);

			if (random == null) {
				clicked.getInventory().setItem(randomN, atual);
				clicked.setItemInHand(null);
			} else {
				clicked.getInventory().setItem(randomN, atual);
				clicked.getInventory().setItemInHand(random);
			}
		}
	}

}
