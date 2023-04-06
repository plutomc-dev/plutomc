package br.com.plutomc.duels.nodebuff.listener;

import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.NodebuffCategory;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.nodebuff.GameConst;
import br.com.plutomc.duels.nodebuff.GameMain;
import br.com.plutomc.duels.nodebuff.event.PlayerKillPlayerEvent;
import br.com.plutomc.duels.nodebuff.event.PlayerLostEvent;
import br.com.plutomc.duels.nodebuff.event.PlayerWinEvent;
import br.com.plutomc.duels.nodebuff.gamer.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player whoDied = e.getEntity();

        e.setDeathMessage(null);

        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(whoDied.getUniqueId(),Gamer.class);
        gamer.setAlive(false);
        GameMain.getInstance().getAlivePlayers().remove(gamer);

        GameMain.getInstance().setState(MinigameState.WINNING);
        if (whoDied.getKiller() instanceof Player) {
            Player killer = whoDied.getKiller();

            broadcastDeath(whoDied, killer);
            e.getDrops().clear();

            Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(whoDied, killer));
            Bukkit.getPluginManager().callEvent(new PlayerLostEvent(whoDied));
            Bukkit.getPluginManager().callEvent(new PlayerWinEvent(killer));

        }

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

        status.addInteger(NodebuffCategory.NODEBUFF_WINS, 1);
        status.addInteger(NodebuffCategory.NODEBUFF_WINSTREAK, 1);
        status.addInteger(NodebuffCategory.NODEBUFF_KILLS, 1);

        p.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
        p.sendMessage("§aVocê venceu!");

    }

    @EventHandler
    public void onPlayerThrowEnderPearl(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(GameConst.cooldown.contains(p) && e.getMaterial() == Material.ENDER_PEARL) {
            p.sendMessage("§cAguarde o cooldwn acabar para usar a ender pearl novamente!");
            e.setCancelled(true);
            return;
        }

        if(e.getMaterial() == Material.ENDER_PEARL) {
            GameConst.cooldown.add(p);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameMain.getInstance(), new Runnable() {
                @Override
                public void run() {
                    GameConst.cooldown.remove(p);
                }
            }, 200);
        }
    }

    @EventHandler
    public void onPlayerLose(PlayerLostEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(NodebuffCategory.NODEBUFF_LOSSES, 1);
        status.setInteger(NodebuffCategory.NODEBUFF_WINSTREAK, 0);
        status.addInteger(NodebuffCategory.NODEBUFF_DEATHS, 1);

        p.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
        p.sendMessage("§cVocê perdeu!");
    }



    public void broadcastDeath(Player died, Player killer) {
        Bukkit.broadcastMessage("§e" +died.getName() + " §efoi morto por " + killer.getName());
    }

}
