package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WormAbility extends AbilityImpl {

	public WormAbility() {
		super("worm", Material.DIRT, "Coma terra e receba regeneração.");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWorm(BlockDamageEvent event) {
		if (hasAbility(event.getPlayer()) && event.getBlock().getType() == Material.DIRT
				&& event.getBlock().getData() == 0) {
			Player player = event.getPlayer();
			double dist = event.getBlock().getLocation().distance(player.getWorld().getSpawnLocation());

			if (dist < 500) {
				if (player.getHealth() < 20.0D) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 1, 2));
				} else if (player.getFoodLevel() < 20) {
					player.setFoodLevel(player.getFoodLevel() + 1);
				}

				event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND,
						Material.DIRT.getId());
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5D, 0.0D, 0.5D),
						new ItemStack(Material.DIRT));
			}
		}
	}
}
