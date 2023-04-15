package br.com.plutomc.duels.boxing.listener;

import br.com.plutomc.core.bukkit.utils.player.PlayerHelper;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.BoxingCategory;
import br.com.plutomc.core.common.member.status.types.GappleCategory;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.duels.boxing.GameConst;
import br.com.plutomc.duels.boxing.GameMain;
import br.com.plutomc.duels.boxing.event.PlayerHitPlayerEvent;
import br.com.plutomc.duels.boxing.event.PlayerWinEvent;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.boxing.event.PlayerKillPlayerEvent;
import br.com.plutomc.duels.boxing.event.PlayerLostEvent;
import br.com.plutomc.duels.boxing.gamer.Gamer;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
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

    }

    @EventHandler
    public void onHit(PlayerHitPlayerEvent e) {
        Player p = e.getPlayer();
        Player hitter = e.getHitter();

        if(!GameConst.TOTAL_HITS.containsKey(hitter)) {
            GameConst.TOTAL_HITS.put(hitter, 1);
        } else {
            GameConst.TOTAL_HITS.put(hitter, GameConst.TOTAL_HITS.get(hitter) + 1);
        }

        if(GameConst.TOTAL_HITS.get(hitter) == 100) {
            Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
            gamer.setAlive(false);
            GameMain.getInstance().getAlivePlayers().remove(gamer);

            GameMain.getInstance().setState(MinigameState.WINNING);

            broadcastDeath(p, hitter);

            Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p, hitter));
            Bukkit.getPluginManager().callEvent(new PlayerLostEvent(p));
            Bukkit.getPluginManager().callEvent(new PlayerWinEvent(hitter));



            GameMain.getInstance().checkWinner();
            for(Player p1 : Bukkit.getOnlinePlayers()) {
                p1.setGameMode(GameMode.ADVENTURE);
            }
        }
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

        Bukkit.getPluginManager().callEvent(new PlayerHitPlayerEvent((Player) event.getEntity(), (Player) event.getDamager()));
    }


    @EventHandler
    public void onPlayerItemConsume(final PlayerItemConsumeEvent e) {
        if (e.getItem().getType().equals(Material.POTION) && e.getItem().getDurability() != 0)
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameMain.getInstance(), new Runnable()
                    {
                        public void run() {
                            if (e.getPlayer().getItemInHand().getType().equals(Material.GLASS_BOTTLE))
                                e.getPlayer().getInventory().setItemInHand(null);
                        }
                    }
                    , 0L);
    }
    @EventHandler
    public void onPlayerWin(PlayerWinEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(BoxingCategory.BOXING_WINS, 1);
        status.addInteger(BoxingCategory.BOXING_WINSTREAK, 1);
        status.addInteger(BoxingCategory.BOXING_KILLS, 1);

        p.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
        p.sendMessage("§aVocê venceu!");
        PlayerHelper.title(p, "§a§lVITÓRIA", "§eVocê venceu!");

        GameAPI.getInstance().sendPlayerToServer(p, new ServerType[]{CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY});

    }

    @EventHandler
    public void onPlayerLose(PlayerLostEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(BoxingCategory.BOXING_LOSSES, 1);
        status.setInteger(BoxingCategory.BOXING_WINSTREAK, 0);
        status.addInteger(BoxingCategory.BOXING_DEATHS, 1);

        p.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
        p.sendMessage("§cVocê perdeu!");
        PlayerHelper.title(p, "§c§lDERROTA", "§eVocê perdeu!");

        GameAPI.getInstance().sendPlayerToServer(p, new ServerType[]{CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY});
    }



    public void broadcastDeath(Player died, Player killer) {
        Bukkit.broadcastMessage("§e" +died.getName() + " §efoi morto por " + killer.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

}
