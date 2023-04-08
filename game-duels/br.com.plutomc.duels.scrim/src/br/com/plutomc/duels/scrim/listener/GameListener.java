package br.com.plutomc.duels.scrim.listener;

import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.bukkit.utils.player.PlayerHelper;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.ScrimCategory;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.scrim.GameMain;
import br.com.plutomc.duels.scrim.event.GameEndEvent;
import br.com.plutomc.duels.scrim.event.PlayerKillPlayerEvent;
import br.com.plutomc.duels.scrim.event.PlayerLostEvent;
import br.com.plutomc.duels.scrim.event.PlayerWinEvent;
import br.com.plutomc.duels.scrim.gamer.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class GameListener implements Listener {

    public GameListener() {
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP));

        recipe.addIngredient(new MaterialData(Material.INK_SACK, (byte) 3));
        recipe.addIngredient(new MaterialData(Material.BOWL));

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player whoDied = e.getEntity();

        e.setDeathMessage(null);

        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(whoDied.getUniqueId(),Gamer.class);
        gamer.setAlive(false);
        GameMain.getInstance().getAlivePlayers().remove(gamer);

        GameMain.getInstance().setState(MinigameState.WINNING);
        if (whoDied.getKiller() != null) {
            Player killer = whoDied.getKiller();

            broadcastDeath(whoDied, killer);
            e.getDrops().clear();

            Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(whoDied, killer));
            Bukkit.getPluginManager().callEvent(new PlayerLostEvent(whoDied));
            Bukkit.getPluginManager().callEvent(new PlayerWinEvent(killer));
            Bukkit.getPluginManager().callEvent(new GameEndEvent());

        }

        GameMain.getInstance().checkWinner();

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
            event.setDamage(event.getDamage() + 2.5);

        if (sword.getType().name().contains("SWORD"))
            sword.setDurability((short) 0);
    }

    @EventHandler
    public void soupEat(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        Damageable hp = p;
        ItemStack bowl = new ItemStack(Material.BOWL);
        ItemMeta bowlMeta = bowl.getItemMeta();
        bowl.setItemMeta(bowlMeta);

        if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
        p.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
            if(hp.getHealth() != hp.getMaxHealth()) {
                p.setHealth(hp.getHealth() + 7.00 > hp.getMaxHealth() ? hp.getMaxHealth() : (hp.getHealth() + 7.0D));
                p.getItemInHand().setType(Material.BOWL);
                p.getItemInHand().setItemMeta(bowlMeta);
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        e.setCancelled(e.getItem().getItemStack().getType() != Material.MUSHROOM_SOUP);
    }
    @EventHandler
    public void onPlayerWin(PlayerWinEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(ScrimCategory.SCRIM_WINS, 1);
        status.addInteger(ScrimCategory.SCRIM_WINSTREAK, 1);
        status.addInteger(ScrimCategory.SCRIM_KILLS, 1);

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


    @EventHandler
    public void onPlayerLose(PlayerLostEvent e) {
        Player p = e.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(p.getUniqueId(),Gamer.class);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(p.getUniqueId(), StatusType.DUEL);

        status.addInteger(ScrimCategory.SCRIM_LOSSES, 1);
        status.setInteger(ScrimCategory.SCRIM_WINSTREAK, 0);
        status.addInteger(ScrimCategory.SCRIM_DEATHS, 1);

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
