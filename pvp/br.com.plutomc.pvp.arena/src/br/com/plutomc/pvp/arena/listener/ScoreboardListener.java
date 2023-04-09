package br.com.plutomc.pvp.arena.listener;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.event.PlayerSelectedKitEvent;
import br.com.plutomc.pvp.arena.gamer.Gamer;
import br.com.plutomc.pvp.engine.event.PlayerProtectionEvent;
import br.com.plutomc.pvp.engine.event.StatusChangeEvent;
import br.com.plutomc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.plutomc.core.bukkit.utils.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onStatusChange(StatusChangeEvent event) {
      this.updateScoreboard(event.getPlayer());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerJoin(PlayerJoinEvent event) {
      this.handleScoreboard(event.getPlayer());
      this.updateScoreboard(event.getPlayer());
   }

   @EventHandler
   public void onGamerChange(PlayerSelectedKitEvent event) {
      this.updateScoreboard(event.getPlayer());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.updateScoreboard(event.getEntity());
      if (event.getEntity().getKiller() != null) {
         this.updateScoreboard(event.getEntity().getKiller());
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerProtection(PlayerProtectionEvent event) {
      this.updateScoreboard(event.getPlayer());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
      this.updateScoreboard(event.getPlayer());
   }

   private void handleScoreboard(Player player) {
      Scoreboard scoreboard = new Scoreboard(player, "§b§lARENA");
      scoreboard.add(9, "§a");
      scoreboard.add(8, "§fKills: §70");
      scoreboard.add(7, "§fDeaths: §70");
      scoreboard.add(6, "§fKillstreak: §70");
      scoreboard.add(5, "§a");
      scoreboard.add(4, "§fKit 1: §aNenhum");
      scoreboard.add(3, "§fKit 2: §aNenhum");
      scoreboard.add(2, "§a");
      scoreboard.add(1, "§awww." + CommonPlugin.getInstance().getPluginInfo().getWebsite());
      ScoreHelper.getInstance().setScoreboard(player, scoreboard);
   }

   private void updateScoreboard(Player player) {
      ScoreHelper.getInstance().updateScoreboard(player, 8, "§fKills: §70");
      ScoreHelper.getInstance().updateScoreboard(player, 7, "§fDeaths: §70");
      ScoreHelper.getInstance().updateScoreboard(player, 6, "§fKillstreak: §70");
      Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
      ScoreHelper.getInstance().updateScoreboard(player, 4, "§fKit 1: §a" + gamer.getPrimary());
      ScoreHelper.getInstance().updateScoreboard(player, 3, "§fKit 2: §a" + gamer.getSecondary());
   }
}
