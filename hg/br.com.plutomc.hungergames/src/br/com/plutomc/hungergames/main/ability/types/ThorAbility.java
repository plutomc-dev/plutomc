package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;
import net.md_5.bungee.api.ChatColor;

public class ThorAbility extends AbilityImpl {

	public ThorAbility() {
		super("thor", new ItemBuilder().type(Material.WOOD_AXE).name(ChatColor.GOLD + "Thor").build(),
				"Lance raios com o seu machado.");
		addItem(new ItemBuilder().type(Material.WOOD_AXE).name(ChatColor.GOLD + "Thor").build());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getCause() == DamageCause.LIGHTNING) {
			MetadataValue metadata = player.getMetadata("thor").stream().findFirst().orElse(null);

			if (metadata == null) {
				event.setDamage(3.0D);
				event.getEntity().setFireTicks(200);
			} else if (metadata.asLong() > System.currentTimeMillis()) {
				event.setCancelled(true);
				metadata.invalidate();
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();

		if (hasAbility(player) && isItemKit(event.getItem())) {
			if (isCooldown(player))
				return;

			if (GameAPI.getInstance().getState() == MinigameState.GAMETIME) {
				if (event.getClickedBlock() != null) {
					Location loc = event.getClickedBlock().getLocation().getWorld()
							.getHighestBlockAt(event.getClickedBlock().getLocation()).getLocation();

					player.setMetadata("thor",
							new FixedMetadataValue(GameAPI.getInstance(), System.currentTimeMillis() + 4000l));
					player.getWorld().strikeLightning(loc);

					if (loc.getBlock().getY() >= 110) {
						Location newLocation = loc.clone();

						if (newLocation.getBlock().getType() == Material.NETHERRACK) {
							newLocation.getWorld().createExplosion(newLocation, 2.5F);
						} else {
							loc.clone().add(0, 1, 0).getBlock().setType(Material.NETHERRACK);
						}
					}

					addCooldown(player.getUniqueId(), 8l);
				}
			}

			event.getItem().setDurability((short) 0);
		}
	}

}
