package br.com.plutomc.hungergames.main.stages;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Schedule;
import br.com.plutomc.hungergames.main.event.player.PlayerEliminateEvent;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import br.com.plutomc.hungergames.main.structure.arena.ArenaType;
import br.com.plutomc.hungergames.main.structure.types.FeastStructure;
import br.com.plutomc.hungergames.main.structure.types.MinifeastStructure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class GameSchedule implements Schedule {

	public static Location feastLocation;
	public static FeastStructure feastStructure;

	private int feastTimer;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		GameHelper.checkWinner();
	}

	@EventHandler
	public void onPlayerDeath(PlayerEliminateEvent event) {
		GameHelper.checkWinner();
	}

	@Override
	public void pulse() {
		int time = CommonPlugin.getInstance().getServerTime();

		if (!GameAPI.getInstance().getState().isGametime()) {
			GameAPI.getInstance().getScheduleManager().stopSchedule(this);
			return;
		}

		if (time % 300 == 0) {
			MinifeastStructure minifest = new MinifeastStructure();
			Location place = minifest.findPlace();
			minifest.spawnStructure(place);

			Bukkit.broadcastMessage("§cUm minifeast spawnou entre §c(X: " + ((int) place.getX() + 100) + ", "
					+ ((int) place.getX() - 100) + ") e §c(Z:" + ((int) place.getZ() + 100) + ", "
					+ ((int) place.getZ() - 100) + ")!");
		}

		if (feastStructure == null) {
			if (time == getDefaultFeastTime()) {
				feastStructure = new FeastStructure();
				feastLocation = feastStructure.findPlace();
				feastStructure.spawnStructure(feastLocation);

				feastTimer = 300;
				Bukkit.broadcastMessage(
						"§cO feast irá spawnar em " + (int) feastLocation.getX() + ", " + (int) feastLocation.getY()
								+ ", " + (int) feastLocation.getZ() + " em " + StringFormat.formatTime(feastTimer));
			}
		} else {
			int feastTime = time - getDefaultFeastTime();

			if (feastTime >= 300) {
				feastStructure.spawnChest(feastLocation);
				Bukkit.broadcastMessage("§cO feast spawnou em " + (int) feastLocation.getX() + ", "
						+ (int) feastLocation.getY() + ", " + (int) feastLocation.getZ() + "!");

				feastStructure = null;
			} else if ((feastTime % 60 == 0
					|| (feastTime > 240 && (feastTime % 15 == 0 || feastTime == 290 || feastTime >= 295)))) {
				Bukkit.broadcastMessage("§cO feast irá spawnar em " + (int) feastLocation.getX() + ", "
						+ (int) feastLocation.getY() + ", " + (int) feastLocation.getZ() + " em "
						+ StringFormat.formatTime(300 - feastTime));
			}
		}

		if (time == 60 * 35) {
			FeastStructure feast = new FeastStructure(25, 450);
			Location location = feast.findPlace();

			feast.spawnStructure(location);
			feast.spawnChest(location);
			Bukkit.broadcastMessage("§cO bonus feast spawnou em algum lugar do mapa!");
		}


			if (time == 60 * 40) {
				Bukkit.broadcastMessage("§cA arena final vai spawnar em 5 minutos!");


			if (time ==  60 * 40 + (60 * 5)) {
				Bukkit.broadcastMessage("§cA arena final foi gerada!");

				Location location = new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0, 1, 0);

				ArenaType.CIRCLE.place(location, Material.BEDROCK, 0,
						 30,
						120, false, false);

				Location teleportLocation = location.clone().add(0, 5, 0);

				Bukkit.getOnlinePlayers().forEach(player -> {
					player.teleport(teleportLocation);
					player.setMetadata("nofall",
							new FixedMetadataValue(GameAPI.getInstance(), System.currentTimeMillis() + (1000 * 10)));
				});
			}
		}

		/*if (GameAPI.getInstance().getVarManager().getVar("hg.most-kill-win", true)) {
			if (time == GameAPI.getInstance().getVarManager().getVar("hg.most-kill-win-time", 60 * 55)) {
				Bukkit.broadcastMessage("§cEm 5 minutos o jogador com a maior quantidade de kills irá vencer!");
			}

			if (time == GameAPI.getInstance().getVarManager().getVar("hg.most-kill-win-time", 60 * 55) + (60 * 5)) {
				Bukkit.broadcastMessage("§cO jogador com maior quantidade de kills irá ganhar!");

				List<Gamer> gamerList = GameAPI.getInstance().getGamerManager().getGamers().stream()
						.filter(gamer -> gamer.isPlaying())
						.sorted((gamer1, gamer2) -> Integer.valueOf(gamer2.getKills()).compareTo(gamer1.getKills()))
						.collect(Collectors.toList());

				for (int x = 1; x < gamerList.size(); x++) {
					Gamer gamer = gamerList.get(x);

					gamer.getPlayer().sendMessage("§cVocê morreu pois não é o jogador com maior quantidade de kills!");
					gamer.setSpectator(true);

					if (GameHelper.checkWinner())
						break;
				}
			}
		} */

		GameAPI.getInstance().setTime(time + 1);
	}

	private int getDefaultFeastTime() {
		return 720 ;
	}

}
