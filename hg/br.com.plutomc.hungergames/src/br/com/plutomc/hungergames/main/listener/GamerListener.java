package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.event.GamerLoadEvent;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.gamer.GamerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GamerListener implements Listener {

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (GameAPI.getInstance().getState() == MinigameState.NONE)
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cO servidor ainda não está disponível!");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer == null) {
			gamer = GameAPI.getInstance().getGamerData().loadGamer(player.getUniqueId());

			if (gamer == null) {
				gamer = new GamerImpl(
						CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId()).getPlayerName(),
						player.getUniqueId());
				GameAPI.getInstance().getGamerData().createGamer(gamer);
			}

			GameAPI.getInstance().getGamerManager().loadGamer(player.getUniqueId(), gamer);
			Bukkit.getPluginManager().callEvent(new GamerLoadEvent(player, gamer));
		}

		gamer.loadPlayer(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (GameAPI.getInstance().getState().isPregame())
			GameAPI.getInstance().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
	}

}