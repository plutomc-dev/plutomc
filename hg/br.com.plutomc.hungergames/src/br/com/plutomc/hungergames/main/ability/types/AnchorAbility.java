package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;

public class AnchorAbility extends AbilityImpl {

	public AnchorAbility() {
		super("anchor", Material.ANVIL, "Não cause e nem receba repulsão.", 19000);
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		if (GameAPI.getInstance().getState() != MinigameState.GAMETIME)
			return;

		Player player = event.getPlayer();
		Player damager = event.getDamager();

		if (hasAbility(player) || hasAbility(damager)) {
			if (GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isPlaying()
					&& GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isPlaying()) {
				player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);

				velocityPlayer(player);
				velocityPlayer(damager);
			}
		}
	}

	private void velocityPlayer(Player player) {
		player.setVelocity(new Vector(0, 0, 0));

		new BukkitRunnable() {
			public void run() {
				player.setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskLater(GameAPI.getInstance(), 1L);
	}
}
