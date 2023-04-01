package br.com.plutomc.duels.sumo;

import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.engine.scheduler.Scheduler;
import br.com.plutomc.duels.sumo.gamer.Gamer;
import br.com.plutomc.duels.sumo.scheduler.WaitingScheduler;
import org.bukkit.event.HandlerList;

import java.util.List;

public class GameMain extends GameAPI {

    private static GameMain instance;

    @Override
    public void onLoad() {
        super.onLoad();
        this.setGamerClass(Gamer.class);
        this.setCollectionName("sumo-gamer");
        this.setUnloadGamer(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.setTime(60);
        this.setState(MinigameState.STARTING);
        this.startScheduler(new WaitingScheduler());
    }

    public static GameMain getInstance() {
        return instance;
    }

    public List<Gamer> getAlivePlayers() {
        return GameAPI.getInstance().getGamerManager().filter(Gamer::isAlive, Gamer.class);
    }

    public void startGame() {
        for(Scheduler scheduler : this.getSchedulerManager().getSchedulers()) {
            this.getSchedulerManager().unloadScheduler(scheduler);
            if (scheduler instanceof WaitingScheduler) {
                HandlerList.unregisterAll((WaitingScheduler)scheduler);
            }
        }

        this.setUnloadGamer(false);
        GameAPI.getInstance().setState(MinigameState.GAMETIME);
       // GameAPI.getInstance().startScheduler(new GameScheduler());
    }
}
