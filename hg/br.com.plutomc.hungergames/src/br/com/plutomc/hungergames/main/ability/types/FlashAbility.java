package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

import java.util.Set;

public class FlashAbility extends AbilityImpl {

	public FlashAbility() {
		super("flash", Material.REDSTONE_TORCH_ON, "Movimente-se tão rapido que parecerá que você está se teletransportando.");
		addItem(new ItemBuilder().name("§aFlash").type(Material.REDSTONE_TORCH_ON).build());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!hasAbility(player) || !isItemKit(event.getItem()))
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_AIR)
			return;

		if (isCooldown(player))
			return;

		event.setCancelled(true);

		Block block = player.getTargetBlock((Set<Material>) null, 100);
		Location location = block.getWorld().getHighestBlockAt(block.getLocation()).getLocation();

		BlockIterator list = new BlockIterator(player.getEyeLocation(), 0, 100);

		while (list.hasNext()) {
			player.getWorld().playEffect(list.next().getLocation(), Effect.ENDER_SIGNAL, 100);
		}

		player.teleport(location.clone().add(0, 1.5, 0));
		player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		addCooldown(player, 40);
	}

}
