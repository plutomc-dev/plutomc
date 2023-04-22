package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.UpdateEvent.UpdateType;
import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.bukkit.utils.scoreboard.Scoreboard;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.event.GameChangeTypeEvent;
import br.com.plutomc.hungergames.main.event.player.PlayerSelectedKitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class ScoreboardListener implements Listener {

	private static final HashMap<UUID, Scoreboard> scoreboardMap = new HashMap<>();
	private static final String SERVER_ID = CommonPlugin.getInstance().getServerId().substring(1, 2);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		handleScoreboard(event.getPlayer());
		updatePlayers();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerAdminEvent event) {
		updatePlayers();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		scoreboardMap.remove(event.getPlayer().getUniqueId());

		new BukkitRunnable() {

			@Override
			public void run() {
				updatePlayers();
			}
		}.runTaskLater(GameAPI.getInstance(), 20l);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
			Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

			if (gamer.getAbilities().isEmpty())
				addScoreboard(player, 4, "");
			else
				removeScoreboard(player, 4);

			addScoreboard(player, 3, "§fKills: §7" + gamer.getKills());
		}

		updateScoreboard(7, "§fJogadores: §7" + (GameAPI.getInstance().getGamerManager().getAlivePlayers().size()) + "/"
				+ Bukkit.getMaxPlayers());
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			for (Player player : Bukkit.getOnlinePlayers())
				addScoreboard(player, 8,
						GameAPI.getInstance().getState().isPregame()
								? "§fInicia em: §7" + StringFormat.format(GameAPI.getInstance().getTime())
								: (GameAPI.getInstance().getState().isInvencibility() ? "§fInvencibilidade: §7"
										: "§fTempo de jogo: §7")
										+ StringFormat.format(GameAPI.getInstance().getTime()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
		if (event.getAbilityId() > 2)
			return;

		if (event.getAbility() == null) {
			if (event.getGamer().getAbilities().isEmpty())
				removeScoreboard(event.getPlayer(), 6);

			removeScoreboard(event.getPlayer(), 5 - (event.getAbilityId() - 1));
		} else {
			addScoreboard(event.getPlayer(), 6, "");
			addScoreboard(event.getPlayer(), 5 - (event.getAbilityId() - 1),
					"§fKit " + event.getAbilityId() + ": §a" + StringFormat.formatString(event.getAbility().getName()));
		}
	}

	@EventHandler
	public void onGameChangeType(GameChangeTypeEvent event) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			removeScoreboard(player, 6);
			removeScoreboard(player, 5);
			removeScoreboard(player, 4);
		});
	}

	private void handleScoreboard(Player player) {
		Scoreboard scoreboard = new Scoreboard(player, "§b§lHG-" + SERVER_ID);

		scoreboard.add(9, "");

		if (GameAPI.getInstance().getState().isPregame())
			scoreboard.add(8, "§fInicia em: §7" + StringFormat.format(GameAPI.getInstance().getTime()));
		else
			scoreboard.add(8, (GameAPI.getInstance().getState().isInvencibility() ? "§fInvencibilidade: §7"
					: "§fTempo de jogo: §7") + StringFormat.format(GameAPI.getInstance().getTime()));

		scoreboard.add(7, "§fJogadores: §7" + GameAPI.getInstance().getGamerManager().getAlivePlayers().size() + "/"
				+ Bukkit.getMaxPlayers());

		scoreboard.add(2, "");
		scoreboard.add(1, "§awww.plutomc.com.br");

		player.setScoreboard(scoreboard.getScoreboard());
		scoreboardMap.put(player.getUniqueId(), scoreboard);
	}

	private void updatePlayers() {
		updateScoreboard(7, "§fJogadores: §7" + GameAPI.getInstance().getGamerManager().getAlivePlayers().size() + "/"
				+ Bukkit.getMaxPlayers());
	}

	public void setScoreboardName(Player player, String name) {
		if (scoreboardMap.containsKey(player.getUniqueId()))
			scoreboardMap.get(player.getUniqueId()).setDisplayName(name);
	}

	public void removeScoreboard(Player player, int index) {
		if (scoreboardMap.containsKey(player.getUniqueId()))
			scoreboardMap.get(player.getUniqueId()).remove(index);
	}

	public void addScoreboard(Player player, int index, String value) {
		if (scoreboardMap.containsKey(player.getUniqueId()))
			scoreboardMap.get(player.getUniqueId()).add(index, value);
	}

	public void updateScoreboard(int index, String value) {
		for (Player player : Bukkit.getOnlinePlayers())
			addScoreboard(player, index, value);
	}
}
