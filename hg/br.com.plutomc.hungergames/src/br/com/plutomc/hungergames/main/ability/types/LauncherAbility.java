package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.plutomc.core.bukkit.BukkitMain;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.plutomc.core.bukkit.utils.ItemUtils;
import br.com.plutomc.hungergames.engine.GameAPI;

public class LauncherAbility extends AbilityImpl {

	public LauncherAbility() {
		super("launcher", new ItemBuilder().name("§aLauncher").type(Material.SPONGE).build(), "Pule alto com suas esponjas.");
		addItem(new ItemBuilder().name("§aLauncher").type(Material.SPONGE).amount(20).build());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player))
			if (event.getBlockPlaced().getType() == Material.SPONGE)
				event.getBlockPlaced().setMetadata("launcher", new FixedMetadataValue(BukkitMain.getInstance(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata("launcher")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);

			ItemUtils.addItem(event.getPlayer(), new ItemBuilder().name("§aLauncher").type(Material.SPONGE).build(),
					event.getPlayer().getLocation());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

		if (block.getType() == Material.SPONGE) {
			if (block.hasMetadata("launcher")) {
				player.setVelocity(player.getLocation().getDirection().multiply(0).setY(3.5));
				player.setMetadata("nofall",
						new FixedMetadataValue(GameAPI.getInstance(), System.currentTimeMillis() + 5000l));
			}
		}
	}

}
