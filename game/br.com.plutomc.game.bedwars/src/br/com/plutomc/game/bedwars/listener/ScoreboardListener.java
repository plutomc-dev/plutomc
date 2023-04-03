package br.com.plutomc.game.bedwars.listener;

import java.util.List;
import java.util.stream.Collectors;

import br.com.plutomc.core.common.member.Member;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.BedwarsCategory;
import br.com.plutomc.game.bedwars.event.PlayerSpectateEvent;
import br.com.plutomc.game.bedwars.event.island.IslandBedBreakEvent;
import br.com.plutomc.game.bedwars.event.island.IslandLoseEvent;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.member.PlayerLanguageChangeEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.game.engine.GameAPI;
import br.com.plutomc.game.bedwars.GameMain;
import br.com.plutomc.game.bedwars.gamer.Gamer;
import br.com.plutomc.game.bedwars.island.Island;
import br.com.plutomc.game.engine.event.GameStateChangeEvent;
import br.com.plutomc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.plutomc.core.bukkit.utils.scoreboard.Scoreboard;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardListener implements Listener {
   private String text = "";

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerJoin(PlayerJoinEvent event) {
      this.handleScoreboard(event.getPlayer());
      (new BukkitRunnable() {
         @Override
         public void run() {
            ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
            if (GameAPI.getInstance().getState() == MinigameState.GAMETIME) {
               ScoreboardListener.this.updateIsland(null);
            }
         }
      }).runTaskLater(GameAPI.getInstance(), 7L);
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerQuit(PlayerQuitEvent event) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
            if (GameAPI.getInstance().getState() == MinigameState.GAMETIME) {
               ScoreboardListener.this.updateIsland(null);
            }
         }
      }).runTaskLater(GameAPI.getInstance(), 7L);
   }

   @EventHandler
   public void onPlayerLanguageChange(PlayerLanguageChangeEvent event) {
      this.handleScoreboard(event.getPlayer());
   }

   @EventHandler
   public void onPlayerSpectate(PlayerSpectateEvent event) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            ScoreboardListener.this.updateIsland(null);
         }
      }).runTaskLater(GameAPI.getInstance(), 7L);
   }

   @EventHandler
   public void onPlayerAdmin(PlayerAdminEvent event) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
         }
      }).runTaskLater(GameAPI.getInstance(), 7L);
   }

   @EventHandler
   public void onGameStateChange(GameStateChangeEvent event) {
      if (event.getState().isGametime()) {
         (new BukkitRunnable() {
            @Override
            public void run() {
               Bukkit.getOnlinePlayers().forEach(player -> {
                  if (player.getPlayer() != null) {
                     ScoreboardListener.this.handleScoreboard(player.getPlayer());
                  }
               });
            }
         }).runTaskLater(GameAPI.getInstance(), 7L);
      }
   }

   @EventHandler
   public void onIslandBreakEvent(IslandBedBreakEvent event) {
      this.updateScoreboard();
   }

   @EventHandler
   public void onIslandLoseEvent(IslandLoseEvent event) {
      this.updateScoreboard();
   }

   @EventHandler
   public void onUpdate(UpdateEvent event) {
      if (event.getType() == UpdateEvent.UpdateType.TICK && event.getCurrentTick() % 10L == 0L) {
         if (this.text.length() >= 3) {
            this.text = "";
         } else {
            this.text = this.text + ".";
         }

         if (!GameAPI.getInstance().isTimer()) {
            this.updateTimer();
            return;
         }
      } else if (event.getType() == UpdateEvent.UpdateType.SECOND && GameAPI.getInstance().isTimer()) {
         this.updateTimer();
      }
   }

   private void handleScoreboard(Player player) {
      Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
      Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.BEDWARS);
      Scoreboard scoreboard = new Scoreboard(player, "§b§lBED WARS");
      if (GameAPI.getInstance().getState().isPregame()) {
         scoreboard.add(10, "§a");
         scoreboard.add(9, "§%scoreboard-map%§: §a" + GameAPI.getInstance().getMapName());
         scoreboard.add(8, "§%scoreboard-players%§: §a" + GameMain.getInstance().getAlivePlayers().size() + "/" + Bukkit.getMaxPlayers());
         scoreboard.add(7, "");
         scoreboard.add(6, "§%scoreboard-starting%§: §a" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
         scoreboard.add(5, "");
         scoreboard.add(4, "§%scoreboard-mode%§: §7" + StringFormat.formatString(CommonPlugin.getInstance().getServerType().name().split("_")[1]));
         scoreboard.add(3, "Winstreak: §a" + status.getInteger(BedwarsCategory.BEDWARS_WINSTREAK));
         scoreboard.add(2, "");
         scoreboard.add(1, "§a" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
      } else {
         if (GameMain.getInstance().getGeneratorUpgrade() != null
            && GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime() > 0) {
            scoreboard.add(14, "§a");
            scoreboard.add(
               13,
               "§%scoreboard-"
                  + GameMain.getInstance().getGeneratorUpgrade().getName().toLowerCase()
                  + "-upgrade%§: §a"
                  + StringFormat.formatTime(
                     GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT
                  )
            );
         }

         scoreboard.add(12, "");
         this.updateIsland(scoreboard);
         scoreboard.add(2, "");
         scoreboard.add(1, "§a" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
      }

      ScoreHelper.getInstance().setScoreboard(player, scoreboard);
   }

   private void updateScoreboard() {
      this.updateIsland(null);
   }

   private void updateIsland(Scoreboard scoreboard) {
      List<Island> islandList = GameMain.getInstance()
         .getIslandManager()
         .values()
         .stream()
         .sorted(
            (i1, i2) -> Character.valueOf(GameMain.CHARS[i1.getIslandColor().getColor().ordinal()])
                  .compareTo(GameMain.CHARS[i2.getIslandColor().getColor().ordinal()])
         )
         .collect(Collectors.toList());

      for(int i = 0; i < islandList.size(); ++i) {
         Island island = islandList.get(i);
         String status = island.getIslandStatus() == Island.IslandStatus.ALIVE
            ? (island.stream(false).count() >= 1L ? "§a" : "§a") + "✔"
            : (
               island.getIslandStatus() == Island.IslandStatus.BED_BROKEN
                  ? "§a"
                     + island.getTeam()
                        .getPlayerSet()
                        .stream()
                        .filter(uuid -> !GameAPI.getInstance().getGamerManager().getGamer(uuid, Gamer.class).isSpectator())
                        .count()
                  : "§c✖"
            );
         int index = this.getInitialIslandIndex() - i;
         String text = ""
            + island.getIslandColor().getColor()
            + ChatColor.BOLD
            + "§%"
            + island.getIslandColor().name().toLowerCase()
            + "-symbol%§ §f§%"
            + island.getIslandColor().name().toLowerCase()
            + "-name%§ "
            + status;
         if (scoreboard == null) {
            for(Player player : Bukkit.getOnlinePlayers()) {
               Island playerIsland = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
               boolean sameIsland = playerIsland == island;
               ScoreHelper.getInstance().addScoreboard(player, index, text + (sameIsland ? " §7§%scoreboard-you%§" : ""));
            }
         } else {
            boolean sameIsland = GameMain.getInstance().getIslandManager().getIsland(scoreboard.getPlayer().getUniqueId()) == island;
            scoreboard.add(index, text + (sameIsland ? " §7§%scoreboard-you%§" : ""));
         }
      }
   }

   private void updateTimer() {
      if (GameAPI.getInstance().getState().isPregame()) {
         ScoreHelper.getInstance()
            .updateScoreboard(4, "§%scoreboard-starting%§: §7" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.SHORT));
      } else if (GameMain.getInstance().getGeneratorUpgrade() != null
         && GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime() > 0) {
         ScoreHelper.getInstance()
            .updateScoreboard(
               13,
               "§%scoreboard-"
                  + GameMain.getInstance().getGeneratorUpgrade().getName().toLowerCase()
                  + "-upgrade%§: §7"
                  + StringFormat.formatTime(
                     GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT
                  )
            );
      } else {
         ScoreHelper.getInstance().removeScoreboard(14);
         ScoreHelper.getInstance().removeScoreboard(13);
      }
   }

   private void updatePlayers(int players) {
      if (GameAPI.getInstance().getState().isPregame()) {
         ScoreHelper.getInstance().updateScoreboard(6, "§%scoreboard-players%§: §a" + players + "/" + Bukkit.getMaxPlayers());
      }
   }

   public int getInitialIslandIndex() {
      return 11;
   }
}
