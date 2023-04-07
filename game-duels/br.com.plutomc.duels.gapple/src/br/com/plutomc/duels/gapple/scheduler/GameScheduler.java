package br.com.plutomc.duels.gapple.scheduler;

import br.com.plutomc.core.bukkit.utils.scoreboard.ScoreboardAPI;
import br.com.plutomc.core.common.language.Language;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.engine.scheduler.Scheduler;
import br.com.plutomc.duels.gapple.GameMain;
import br.com.plutomc.duels.gapple.listener.GameListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class GameScheduler implements Scheduler, Listener {
    public GameScheduler() {
        GameAPI.getInstance().setUnloadGamer(false);
        GameAPI.getInstance().setTagControl(false);
        GameAPI.getInstance().setTime(0);
        Bukkit.getOnlinePlayers().forEach(ScoreboardAPI::leaveCurrentTeamForOnlinePlayers);

        Player player1 = GameMain.getInstance().getAlivePlayers().get(0).getPlayer();
        Player player2 = GameMain.getInstance().getAlivePlayers().get(0).getPlayer();
        ScoreboardAPI.joinTeam(
                ScoreboardAPI.createTeamIfNotExistsToPlayer(
                        player1, "red", "§c", ""
                ),
                player1
        );

        ScoreboardAPI.joinTeam(
                ScoreboardAPI.createTeamIfNotExistsToPlayer(
                        player2, "blue", "§9", ""
                ),
                player2
        );

        Bukkit.getServer().getPluginManager().registerEvents(new GameListener(), GameMain.getInstance());
    }
    @Override
    public void pulse() {
        int time = GameAPI.getInstance().getTime();
    }

    public void load(Player p) {
        for(Player online : Bukkit.getOnlinePlayers()) {

        }
    }
}
