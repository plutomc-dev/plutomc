package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class KangarooAbility extends AbilityImpl {

	private final List<Player> kangarooMap = new ArrayList<>();

	public KangarooAbility() {
		super("kangaroo", Material.FIREWORK, "Movimente-se mais rapido com seu kangaroo", 32000);
		addItem(new ItemBuilder().name("§aKangaroo").type(Material.FIREWORK).build());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (hasAbility(player))
			addCooldown(player, 8L);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.getAction() != Action.PHYSICAL && isItemKit(player.getItemInHand())) {
			event.setCancelled(true);

			if (isCooldown(player) || kangarooMap.contains(player))
				return;

			Vector vector = player.getEyeLocation().getDirection().multiply(player.isSneaking() ? 2.3F : 0.7f)
					.setY(player.isSneaking() ? 0.5 : 1F);
			player.setFallDistance(-1.0F);
			player.setVelocity(vector);
			kangarooMap.add(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (hasAbility(player)) {
				if (event.getCause().name().contains("FALL")) {
					if (event.getDamage() > 7.0D) {
						event.setDamage(5.0D);
					} else if (event.getDamage() < 2.0D) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player)) {
			if (kangarooMap.contains(player)) {
				Block block = player.getLocation().clone().add(0, -1, 0).getBlock();
				if (block.getType() != Material.AIR) {
					kangarooMap.remove(player);
				}
			}
		}
	}

}
