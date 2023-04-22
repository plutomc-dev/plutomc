package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class DemomanAbility extends AbilityImpl {

	public DemomanAbility() {
		super("demoman", Material.STONE_PLATE,
				"Tenha a habilidade de montar uma mina e com ela exploda seus inimigos.", 21000);

		addItem(new ItemBuilder().type(Material.GRAVEL).name("§aDemoman").amount(6).build());
		addItem(new ItemBuilder().type(Material.STONE_PLATE).name("§aDemoman").amount(6).build());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() == Material.STONE_PLATE)
			if (block.hasMetadata("demoman"))
				block.removeMetadata("demoman", GameAPI.getInstance());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!hasAbility(player) && block.getType() == Material.STONE_PLATE)
			block.setMetadata("demoman",
					new FixedMetadataValue(GameAPI.getInstance(), player.getUniqueId().toString()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		if (block == null)
			return;

		if (GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).isPlaying())
			if (block.hasMetadata("demoman"))
				if (block.getRelative(BlockFace.DOWN).getType() == Material.GRAVEL)
					if (event.getAction() == Action.PHYSICAL) {
						String playerId = block.getMetadata("demoman").stream().findFirst().orElse(null).asString();

						if (UUID.fromString(playerId).equals(event.getPlayer().getUniqueId())) {
							event.getPlayer().sendMessage("§cVocê não pode usar sua própria armadilha!");
							return;
						}

						block.getWorld().createExplosion(block.getLocation().clone().add(0.5D, 0.5D, 0.5D), 3.0F);
					}
	}

}
