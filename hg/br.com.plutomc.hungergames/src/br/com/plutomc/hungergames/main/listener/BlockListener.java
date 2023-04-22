package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.core.bukkit.member.BukkitMember;
import br.com.plutomc.core.bukkit.utils.ItemUtils;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.hungergames.engine.GameAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlockListener implements Listener {

	private List<Material> blockList = Arrays.asList(Material.RED_MUSHROOM, Material.BROWN_MUSHROOM);

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!GameAPI.getInstance().isPlaceEnabled())
			if (!breakPermission(event.getPlayer()))
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!GameAPI.getInstance().isBucketEnabled())
			if (!breakPermission(event.getPlayer()))
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlock(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata("unbreakableBlock")) {
			event.setCancelled(true);
			return;
		}

		boolean breakPermission = breakPermission(event.getPlayer());

		if (!breakPermission) {
			if (!GameAPI.getInstance().isBuildEnabled()) {
				event.setCancelled(true);
			}

			if (GameAPI.getInstance().getMaterialSet().contains(event.getBlock().getType())) {
				event.setCancelled(true);
				return;
			}

			if (GameAPI.getInstance().getTime() >= 2700)
				if (event.getBlock().getType() == Material.GLOWSTONE) {
					event.setCancelled(true);
					return;
				}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().giveExp(event.getExpToDrop());
		event.setExpToDrop(0);

		if (!blockList.contains(event.getBlock().getType()))
			return;

		event.setCancelled(true);
		Player player = event.getPlayer();

		Collection<ItemStack> dropList = new ArrayList<>(
				player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR
						? event.getBlock().getDrops()
						: event.getBlock().getDrops(player.getItemInHand()));

		if (event.getBlock().getType() == Material.COCOA) {
			dropList.add(new ItemStack(Material.INK_SACK, event.getBlock().getData() >= 8 ? 3 : 1, (short) 3));
		} else if (event.getBlock().getType() == Material.CACTUS) {
			dropList.removeIf(item -> item.getType() == Material.CACTUS);
			Block block = event.getBlock();

			while (block.getType() == Material.CACTUS) {
				dropList.add(new ItemStack(Material.CACTUS));

				Block relative = block.getRelative(BlockFace.UP);

				blockBlock(relative);
				block.setType(Material.AIR);
				block = relative;
			}
		}

		for (ItemStack item : dropList) {
			ItemUtils.addItem(player, item, event.getBlock().getLocation());
		}

		if (event.isCancelled()) {
			if (event.getBlock().getType().name().contains("MUSHROOM")) {
				blockNearBlocks(event.getBlock());

				event.getBlock().setMetadata("phsicsBlock",
						new FixedMetadataValue(GameAPI.getInstance(), System.currentTimeMillis()));
			}

			event.getBlock().setType(Material.AIR);
		}
	}

	public void blockNearBlocks(Block block) {
		for (BlockFace blockFace : new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH,
				BlockFace.SOUTH }) {
			blockBlock(block.getRelative(blockFace));
		}
	}

	public void blockBlock(Block block) {
		if (!block.hasMetadata("phsicsBlock"))
			block.setMetadata("phsicsBlock", new FixedMetadataValue(GameAPI.getInstance(), System.currentTimeMillis()));
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (blockList.contains(event.getBlock().getType()))
			if (event.getBlock().hasMetadata("phsicsBlock"))
				event.setCancelled(true);
	}

	public boolean breakPermission(Player player) {
		BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId(),
				BukkitMember.class);

		if (member.isStaff())
			if (member.isBuildEnabled())
				return true;

		return false;
	}

}
