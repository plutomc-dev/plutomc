package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;

public class MinerAbility extends AbilityImpl {

	public MinerAbility() {
		super("miner", getMinerPickaxe(), "Quebre minerios rapidamente.", 21000);
		addItem(getMinerPickaxe());
	}

	private static ItemStack getMinerPickaxe() {
		return new ItemBuilder().type(Material.STONE_PICKAXE).glow().enchantment(Enchantment.DURABILITY)
				.enchantment(Enchantment.DIG_SPEED, 2).build();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (hasAbility(event.getPlayer()))
			if (event.getBlock().getType().name().contains("ORE"))
				if (isItemKit(event.getPlayer().getItemInHand())) {
					blockBreak(event.getBlock());
					event.getPlayer().getItemInHand().setDurability((short) 0);
				}
	}

	public void blockBreak(Block block) {
		block.breakNaturally();

		for (BlockFace blockFace : new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
				BlockFace.EAST, BlockFace.WEST }) {
			Block relative = block.getRelative(blockFace);

			if (relative != null) {
				if (relative.getType().name().contains("ORE"))
					blockBreak(relative);
			}
		}
	}

}
