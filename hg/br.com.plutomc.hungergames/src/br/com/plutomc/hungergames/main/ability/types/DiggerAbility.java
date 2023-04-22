package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import br.com.plutomc.hungergames.main.stages.GameSchedule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;

public class DiggerAbility extends AbilityImpl {
	
    private static final int RADIUS = 4;
    private static final int HEIGHT = 6;

	public DiggerAbility() {
		super("digger", Material.DRAGON_EGG, "Escave um grande buraco e diga adeus para seus oponentes.", 23000);
		
		addItem(new ItemBuilder().type(Material.DRAGON_EGG).name("§aDigger").build());
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.hasItem() && isItemKit(event.getItem()) && event.getClickedBlock() != null) {
			event.setCancelled(true);
			player.updateInventory();
			
			if (isCooldown(player))
				return;

			if (GameAPI.getInstance().getState().isInvencibility()) {
				player.sendMessage("§cVocê não pode usar o kit " + getName() + " na invencibilidade.");
				return;
			}
			
			if (GameSchedule.feastStructure != null && event.getClickedBlock().getLocation().distance(GameSchedule.feastLocation) < 30) {
				player.sendMessage("§cVocê não pode usar o kit " + getName() + " no feast.");
				event.setCancelled(true);
				return;
			}
			
			for (int x = -RADIUS; x <= RADIUS; x++) {
				for (int z = -RADIUS; z <= RADIUS; z++) {
					for (int y = 0; y <= HEIGHT; y++) {
						Block block = event.getClickedBlock().getLocation().clone().subtract(x, y, z).getBlock();
						if (block.getType() == Material.BEDROCK)
							continue;

						block.setType(Material.AIR);
					}
				}
			}

			player.sendMessage("§aBlocos quebrados!");
			addCooldown(player, 35L);
		}
	}

}
