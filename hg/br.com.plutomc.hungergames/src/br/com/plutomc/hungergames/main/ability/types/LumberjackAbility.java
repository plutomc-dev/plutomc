package br.com.plutomc.hungergames.main.ability.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;

public class LumberjackAbility extends AbilityImpl {

	public LumberjackAbility() {
		super("lumberjack", Material.WOOD_AXE, "Quebre árvores como um verdadeiro lenhador.");
		addItem(new ItemBuilder().name("§aLumberjack").type(Material.WOOD_AXE).build());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (hasAbility(event.getPlayer()) && (event.getBlock().getType().name().contains("LOG")))
			if (event.getPlayer().getItemInHand().getType().name().contains("AXE")) {
				blockBreak(event.getBlock());
				event.setCancelled(true);
			}
	}

	public void blockBreak(Block block) {
		block.breakNaturally();
		for (BlockFace blockFace : new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
				BlockFace.EAST, BlockFace.WEST }) {
			Block relative = block.getRelative(blockFace);

			if (relative != null) {
				if (relative.getType() == Material.LOG || relative.getType() == Material.LOG_2)
					blockBreak(relative);
			}
		}
	}

}
