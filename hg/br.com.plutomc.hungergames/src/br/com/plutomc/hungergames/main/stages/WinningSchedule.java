package br.com.plutomc.hungergames.main.stages;

import br.com.plutomc.core.bukkit.BukkitMain;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Schedule;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.event.player.GamerWinEvent;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;

public class WinningSchedule implements Schedule {

	private Gamer winner;
	private Location location;
	private EnderDragon enderDragon;

	public WinningSchedule(Gamer winner) {
		this.winner = winner;
		GameAPI.getInstance().setState(MinigameState.WINNING, 30);

		if (winner == null) {
			withoutWinner();
			return;
		}

		if (this.winner.getPlayer().isOnline())
			this.location = this.winner.getPlayer().getLocation();
		else
			this.location = new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0, 70, 0);

		this.location = location.clone().add(0, this.location.getY() > 70 ? 30 : 50, 0);

		if (this.winner.getPlayer().isOnline()) {
			this.winner.getPlayer().teleport(this.location);
			this.winner.getPlayer().getInventory().clear();
			this.winner.getPlayer().getInventory()
					.addItem(new ItemBuilder().name("§aVocê venceu!").type(Material.EMPTY_MAP).build());
			this.winner.getPlayer().getInventory()
					.addItem(new ItemBuilder().name("§aVocê venceu!").type(Material.WATER_BUCKET).build());
		}

		enderDragon = location.getWorld().spawn(location, EnderDragon.class);
		enderDragon.setPassenger(this.winner.getPlayer());
		enderDragon.setCustomName("§2§lWINNER");
		enderDragon.setCustomNameVisible(true);

		Bukkit.getPluginManager().callEvent(new GamerWinEvent(winner));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onMapInitialize(MapInitializeEvent event) {
		MapView map = event.getMap();

		map.getRenderers().forEach(renderer -> map.removeRenderer(renderer));

		map.addRenderer(new MapRenderer() {

			@Override
			public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
				mapCanvas.drawText(22, 6, MinecraftFont.Font, "Parabens, " + winner.getPlayerName());
				mapCanvas.drawText(12, 18, MinecraftFont.Font, "você venceu a partida!");
				mapCanvas.drawText(17, 120, MinecraftFont.Font, "www.plutomc.com.br");
				mapCanvas.drawImage(30, 40,
						new ImageIcon(GameAPI.getInstance().getDataFolder().getPath() + "/cake.png").getImage());
			}

		});
	}

	@Override
	public void pulse() {
		int time = GameAPI.getInstance().getTime();

		if (time == 0) {
			Bukkit.getOnlinePlayers().forEach(player -> BukkitMain.getInstance().sendPlayerToServer(player,
					CommonPlugin.getInstance().getServerType().getServerLobby()));
		}

		if (time <= 0)
			if (Bukkit.getOnlinePlayers().size() == 0 || time <= 10) {
				Bukkit.shutdown();
				return;
			}

		Player player = winner.getPlayer();

		if (player.isOnline()) {
			GameHelper.spawnFirework(player.getLocation(), Color.AQUA, 1);
			GameHelper.spawnFirework(player.getLocation(), Color.YELLOW, 1);
			GameHelper.spawnFirework(player.getLocation(), Color.SILVER, 1);
		}

		Bukkit.broadcastMessage(
				"§a" + (player.isOnline() ? winner.getPlayerName() : player.getName()) + " ganhou a partida!");
		GameAPI.getInstance().setTime(time - 1);
	}

	private void withoutWinner() {
		new BukkitRunnable() {

			int seconds = 7;

			@Override
			public void run() {
				if (seconds == 3) {
					Bukkit.getOnlinePlayers().forEach(player -> {
						BukkitMain.getInstance().sendPlayerToServer(player,
								CommonPlugin.getInstance().getServerType().getServerLobby());
						player.sendMessage("§cNinguém venceu a partida!");
					});
					return;
				}

				if (seconds <= 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitMain.getInstance(), () -> Bukkit.shutdown(),
							7l);
					return;
				}
			}
		}.runTaskTimer(GameAPI.getInstance(), 20, 20);
	}

}
