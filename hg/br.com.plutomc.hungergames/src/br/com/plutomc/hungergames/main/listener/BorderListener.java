package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.hungergames.main.HardcoreMain;
import br.com.plutomc.hungergames.main.event.GameStageChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.UpdateEvent.UpdateType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;

public class BorderListener implements Listener {

	public BorderListener() {
		World world = Bukkit.getWorlds().stream().findFirst().orElse(null);

		world.getWorldBorder().setSize(HardcoreMain.getInstance().getBorderMax());
		world.getWorldBorder().setCenter(0, 0);
	}
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isPlaying())
					continue;

				if (isOnWarning(player)) {
					if (!isPregame()) {
						player.sendMessage("§eVocê está perto da borda do mundo!");
						return;
					}
				}

				if (isNotInBoard(player) || player.getLocation().getY() > 155) {
					if (isPregame()) {
						if (player.getLocation().getY() > 155)
							player.setVelocity(player.getLocation().toVector().setX(0).setZ(0).setY(80)
									.subtract(player.getLocation().toVector()).normalize().multiply(1.2));
						else
							player.setVelocity(player.getLocation().toVector().setX(0).setZ(0)
									.subtract(player.getLocation().toVector()).normalize().multiply(1.2));
					} else {
						player.sendMessage("§cVocê passou da borda do mundo!");

						@SuppressWarnings("deprecation")
						EntityDamageEvent entityDamageEvent = new EntityDamageEvent(player, DamageCause.CUSTOM, 4.0d);

						if (entityDamageEvent.isCancelled())
							entityDamageEvent.setCancelled(false);

						player.setLastDamageCause(entityDamageEvent);
						player.damage(4.0);
						player.setFireTicks(50);
					}
				}
			}
	}

	private boolean isPregame() {
		return false;
	}

	@EventHandler
	public void onGameStateChange(GameStageChangeEvent event) {
		if (event.getState() == MinigameState.WINNING)
			HandlerList.unregisterAll(this);
	}

	private boolean isNotInBoard(Player p) {
		int size = (int) 1000 / 2;
		return ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
				|| (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
	}

	private boolean isOnWarning(Player p) {
		int size = (int) 1000 / 2;
		size = size - 20;
		return !isNotInBoard(p) && ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
				|| (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
	}

}
