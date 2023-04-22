package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;

public class SwitcherAbility extends AbilityImpl {

	public SwitcherAbility() {
		super("switcher", new ItemBuilder().type(Material.SNOW_BALL).name("§aSwitcher").build(), "Troque de lugar com suas snowballs.");
		addItem(new ItemBuilder().type(Material.SNOW_BALL).name("§aSwitcher").build());
	}

	@EventHandler
	public void onProjectileLaunch(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (hasAbility(player) && isItemKit(player.getItemInHand())) {
				event.setCancelled(true);
				player.updateInventory();

				if (isCooldown(player))
					return;

				if (GameAPI.getInstance().getState().isInvencibility()) {
					player.sendMessage("§cVocê não pode usar o seu kit durante a invencibilidade!");
					return;
				}

				Snowball ball = player.launchProjectile(Snowball.class);
				ball.setMetadata("switch", new FixedMetadataValue(GameAPI.getInstance(), player));
				addCooldown(player, 7l);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
			Player player = (Player) event.getDamager().getMetadata("switch").get(0).value();

			if (player == null)
				return;

			Location loc = event.getEntity().getLocation().clone();
			event.getEntity().teleport(player.getLocation().clone());
			player.teleport(loc);
		}
	}

}
