package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.packet.types.ActionBar;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlinkAbility extends AbilityImpl {

	private Map<UUID, Integer> uses = new HashMap<>();

	public BlinkAbility() {
		super("blink", Material.NETHER_STAR, "Teletransporte-se para onde você estiver olhando.");
		addItem(new ItemBuilder().type(Material.NETHER_STAR).name("§aBlink").build());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!hasAbility(player))
			return;

		ItemStack itemStack = event.getItem();

		if (!isItemKit(itemStack))
			return;

		event.setCancelled(true);

		if (isCooldown(player))
			return;

		Block block = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(5.0D)).getBlock();

		if (block.getY() > 132) {
			player.sendMessage("§cVocê não pode usar o blink depois da altura!");
			return;
		}

		int used = uses.computeIfAbsent(player.getUniqueId(), v -> 0) + 1;

		if (used >= 4) {
			addCooldown(player, 15);
			uses.remove(player.getUniqueId());
			return;
		}

		uses.put(player.getUniqueId(), used);

		if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
			block.getRelative(BlockFace.DOWN).setType(Material.LEAVES);
		}

		new ActionBar(player.getUniqueId(), "§aUsos restantes: " + (3 - used));

		player.teleport(new Location(player.getWorld(), block.getX(), block.getY(), block.getZ(),
				player.getLocation().getYaw(), player.getLocation().getPitch()));
		player.setFallDistance(0.0F);
		player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 50.0F);
	}

}
