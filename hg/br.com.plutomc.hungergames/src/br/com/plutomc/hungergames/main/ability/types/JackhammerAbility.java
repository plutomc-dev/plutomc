package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;

public class JackhammerAbility extends AbilityImpl {

	private Map<Player, Integer> useMap;

	public JackhammerAbility() {
		super("jackhammer", Material.STONE_AXE, "Faça um buraco até e bedrock e mate seus inimigos.");
		useMap = new HashMap<>();
		addItem(new ItemBuilder().name("§aJackHammer").type(Material.STONE_AXE).build());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (!hasAbility(player) || player.getItemInHand() == null || player.getItemInHand().getType() != Material.STONE_AXE)
			return;

		if (GameAPI.getInstance().getState().isInvencibility()) {
			player.sendMessage("§cVocê não pode usar o kit na invencibilidade!");
			return;
		}

		int x = event.getBlock().getX();
		int z = event.getBlock().getZ();

		if (x >= 489 || x <= -489 || z >= 489 || z <= -489) {
			event.setCancelled(true);
			return;
		}

		if (isCooldown(player)) {
			return;
		}

		useMap.put(player, useMap.computeIfAbsent(player, v -> 0) + 1);

		if (useMap.get(player) == 6) {
			if (event.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
				breakBlock(event.getBlock(), BlockFace.UP);
			}

			breakBlock(event.getBlock(), BlockFace.DOWN);

			useMap.remove(player);
			addCooldown(player.getUniqueId(), 28l);
		} else {
			if (event.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
				breakBlock(event.getBlock(), BlockFace.UP);
			}

			breakBlock(event.getBlock(), BlockFace.DOWN);
		}
	}

	@SuppressWarnings("deprecation")
	private void breakBlock(Block block, BlockFace face) {
		while (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_PORTAL_FRAME && block.getY() <= 128
				&& !block.hasMetadata("inquebravel")) {
			block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 30);
			block.setType(Material.AIR);
			block = block.getRelative(face);
		}
	}

}
