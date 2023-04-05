package br.com.plutomc.duels.gapple.listener;

import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.GappleCategory;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.gapple.GameMain;
import br.com.plutomc.duels.gapple.event.PlayerLostEvent;
import br.com.plutomc.duels.gapple.event.PlayerWinEvent;
import br.com.plutomc.duels.gapple.gamer.Gamer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player whoDied = e.getEntity();

        GameMain.getInstance().getServer().getPluginManager().callEvent(new PlayerLostEvent(whoDied));
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(whoDied.getUniqueId(),Gamer.class);
        GameMain.getInstance().getAlivePlayers().remove(gamer);

        GameMain.getInstance().checkWinner();

    }

    @EventHandler //test
    public void onPlayerAdmin(PlayerAdminEvent event) {
        Player whoDied = event.getPlayer();

        GameMain.getInstance().getServer().getPluginManager().callEvent(new PlayerLostEvent(whoDied));
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(whoDied.getUniqueId(),Gamer.class);
        GameMain.getInstance().getAlivePlayers().remove(gamer);

        GameMain.getInstance().checkWinner();
    }

    @EventHandler
    public void onPlayerWin(PlayerWinEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(GappleCategory.GAPPLE_WINS, 1);
        status.addInteger(GappleCategory.GAPPLE_WINSTREAK, 1);
        status.addInteger(GappleCategory.GAPPLE_KILLS, 1);

    }

    @EventHandler
    public void onPlayerLose(PlayerLostEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(GappleCategory.GAPPLE_LOSSES, 1);
        status.setInteger(GappleCategory.GAPPLE_WINSTREAK, 0);
        status.addInteger(GappleCategory.GAPPLE_DEATHS, 1);

    }

}
