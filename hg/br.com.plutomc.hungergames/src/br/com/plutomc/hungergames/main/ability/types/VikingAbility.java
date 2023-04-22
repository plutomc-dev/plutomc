package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;

public class VikingAbility extends AbilityImpl {

	public VikingAbility() {
		super("viking", Material.STONE_AXE, "Dê mais dano com machados.", 28000);
	}

	@EventHandler
	public void onEntityDamageByEntity(PlayerDamagePlayerEvent event) {
		Player player = (Player) event.getDamager();
		ItemStack item = player.getItemInHand();

		if (hasAbility(player) && item.getType().name().contains("_AXE"))
			event.setDamage(event.getDamage() + 1.0d);
	}

}
