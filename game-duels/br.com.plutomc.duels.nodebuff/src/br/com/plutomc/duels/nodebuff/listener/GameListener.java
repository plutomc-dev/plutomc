package br.com.plutomc.duels.nodebuff.listener;

import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.bukkit.utils.player.PlayerHelper;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.NodebuffCategory;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.nodebuff.GameConst;
import br.com.plutomc.duels.nodebuff.GameMain;
import br.com.plutomc.duels.nodebuff.event.PlayerKillPlayerEvent;
import br.com.plutomc.duels.nodebuff.event.PlayerLostEvent;
import br.com.plutomc.duels.nodebuff.event.PlayerWinEvent;
import br.com.plutomc.duels.nodebuff.gamer.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.ADVENTURE);
        }
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
        PlayerHelper.title(p, "§a§lVITÓRIA", "§eVocê venceu!");

        Bukkit.getScheduler().runTaskLater(GameMain.getInstance(), new Runnable() {
            @Override
            public void run() {
                GameAPI.getInstance().sendPlayerToServer(p, new ServerType[]{CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY});
            }
        }, 60);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        Player p = (Player) event.getDamager();
        ItemStack sword = p.getItemInHand();

        if (sword == null)
            return;

        if (sword.getType() == Material.DIAMOND_SWORD)
            event.setDamage(event.getDamage() + 4.5);

        if (sword.getType().name().contains("SWORD"))
            sword.setDurability((short) 0);


        if(event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Entity entity = event.getEntity();

            entity.setVelocity(damager.getLocation().getDirection().setY(0).normalize().multiply(0.33));
            entity.setVelocity(damager.getLocation().getDirection().setX(0).normalize().multiply(0.33));

        }
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
        PlayerHelper.title(p, "§c§lDERROTA", "§eVocê perdeu!");

        Bukkit.getScheduler().runTaskLater(GameMain.getInstance(), new Runnable() {
            @Override
            public void run() {
                GameAPI.getInstance().sendPlayerToServer(p, new ServerType[]{CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY});
            }
        }, 60);
    }



    public void broadcastDeath(Player died, Player killer) {
        Bukkit.broadcastMessage("§e" +died.getName() + " §efoi morto por " + killer.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

}
