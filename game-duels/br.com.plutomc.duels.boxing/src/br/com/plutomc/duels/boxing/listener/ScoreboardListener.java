package br.com.plutomc.duels.boxing.listener;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.member.PlayerLanguageChangeEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.plutomc.core.bukkit.utils.scoreboard.Scoreboard;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.GappleCategory;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.duels.boxing.GameConst;
import br.com.plutomc.duels.boxing.GameMain;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.engine.event.GameStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardListener implements Listener {

    private String text = "";

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

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handleScoreboard(event.getPlayer());
        (new BukkitRunnable() {
            @Override
            public void run() {
                ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
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
            }
        }).runTaskLater(GameAPI.getInstance(), 7L);
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent event) {
        this.handleScoreboard(event.getPlayer());
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

    private void handleScoreboard(Player player) {
        Scoreboard scoreboard = new Scoreboard(player, "§b§lDUELS");
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.DUEL);
        if (GameAPI.getInstance().getState().isPregame()) {
            scoreboard.add(9, "§a");
            scoreboard.add(8, "§%scoreboard-map%§: §a" + GameAPI.getInstance().getMapName());
            scoreboard.add(7, "§%scoreboard-players%§: §a" + GameMain.getInstance().getAlivePlayers().size() + "/" + Bukkit.getMaxPlayers());
            scoreboard.add(6, "");
            scoreboard.add(5, "§%scoreboard-starting%§: §a" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
            scoreboard.add(4, "");
            scoreboard.add(3, "Winstreak: §7" + status.getInteger(GappleCategory.GAPPLE_WINSTREAK));
            scoreboard.add(2, "");
            scoreboard.add(1, "§a" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        } else if(GameAPI.getInstance().getState().isGametime()) {

            scoreboard.add(10, "");
            scoreboard.add(9, "Modo: §a" + StringFormat.formatString(CommonPlugin.getInstance().getServerType().name().split("_")[1]));
            scoreboard.add(8, "Tempo: §a"+ StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
            scoreboard.add(7, "");
            Player player1 = GameMain.getInstance().getAlivePlayers().get(0).getPlayer();
            Player player2 = GameMain.getInstance().getAlivePlayers().get(1).getPlayer();

            scoreboard.add(6,"§c" + player1.getName() + ": §7" + GameConst.TOTAL_HITS.get(player1));
            scoreboard.add(5,"§9" + player2.getName() + ": §7" + GameConst.TOTAL_HITS.get(player2));
            scoreboard.add(4, "");
            scoreboard.add(3, "§fWinstreak: §7" + status.getInteger(GappleCategory.GAPPLE_WINSTREAK));
            scoreboard.add(2, "");
            scoreboard.add(1, "§a" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        } else if(GameAPI.getInstance().getState().isEnding()) {
            scoreboard.add(10, "");
            scoreboard.add(9, "Modo: §a" + StringFormat.formatString(CommonPlugin.getInstance().getServerType().name().split("_")[1]));
            scoreboard.add(8, "Tempo: §aFinalizado.");
            scoreboard.add(7, "");
            Player player1 = GameMain.getInstance().getAlivePlayers().get(0).getPlayer();
            Player player2 = GameMain.getInstance().getAlivePlayers().get(1).getPlayer();

            scoreboard.add(6,"§c" + player1.getName() + ": §7" + GameConst.TOTAL_HITS.get(player1));
            scoreboard.add(5,"§9" + player2.getName() + ": §7" + GameConst.TOTAL_HITS.get(player2));
            scoreboard.add(4, "");
            scoreboard.add(3, "§fWinstreak: §7" + status.getInteger(GappleCategory.GAPPLE_WINSTREAK));
            scoreboard.add(2, "");
            scoreboard.add(1, "§a" + CommonPlugin.getInstance().getPluginInfo().getWebsite());

        }

        ScoreHelper.getInstance().setScoreboard(player, scoreboard);
    }

    private void updateTimer() {
        if (GameAPI.getInstance().getState().isPregame()) {
            ScoreHelper.getInstance()
                    .updateScoreboard(5, "§%scoreboard-starting%§: §a" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
        } else if(GameAPI.getInstance().getState().isGametime()){
            ScoreHelper.getInstance()
                    .updateScoreboard(8, "Tempo: §a" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
            Player player1 = GameMain.getInstance().getAlivePlayers().get(0).getPlayer();
            Player player2 = GameMain.getInstance().getAlivePlayers().get(1).getPlayer();

            ScoreHelper.getInstance().updateScoreboard(6,"§c" + player1.getName() + ": §7" + GameConst.TOTAL_HITS.get(player1));
            ScoreHelper.getInstance().updateScoreboard(5,"§9" + player2.getName() + ": §7" + GameConst.TOTAL_HITS.get(player2));
        }
    }

    private void updatePlayers(int players) {
        if (GameAPI.getInstance().getState().isPregame()) {
            ScoreHelper.getInstance().updateScoreboard(7, "§%scoreboard-players%§: §a" + players + "/" + Bukkit.getMaxPlayers());
        }
    }

}
