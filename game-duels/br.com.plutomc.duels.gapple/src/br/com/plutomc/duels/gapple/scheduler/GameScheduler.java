package br.com.plutomc.duels.gapple.scheduler;

import br.com.plutomc.core.bukkit.utils.scoreboard.ScoreboardAPI;
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
