package br.com.plutomc.pvp.engine.listener;

import java.util.ArrayList;
import java.util.List;

import br.com.plutomc.pvp.engine.event.PlayerSpawnEvent;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.plutomc.pvp.engine.GameAPI;
import br.com.plutomc.pvp.engine.event.PlayerProtectionEvent;
import br.com.plutomc.pvp.engine.event.PlayerRealRespawnEvent;
import br.com.plutomc.pvp.engine.gamer.Gamer;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.utils.player.PlayerHelper;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onPlayerJoin(PlayerJoinEvent event) {
      this.handlePlayer(event.getPlayer());
      event.getPlayer().teleport(BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerSpawn(PlayerSpawnEvent event) {
      this.handlePlayer(event.getPlayer());
   }

   @EventHandler
   public void onEntityRegain(EntityRegainHealthEvent event) {
      event.setCancelled(true);
   }

   private void handlePlayer(Player player) {
      player.getInventory().clear();
      player.getInventory().setArmorContents(new ItemStack[4]);
      player.setGameMode(GameMode.SURVIVAL);
      player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
      player.setHealth(20.0);
      player.setFoodLevel(20);
      GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).setSpawnProtection(true);
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      event.setDeathMessage(null);
      final Player player = event.getEntity();
      List<ItemStack> items = new ArrayList<>(event.getDrops());
      if (GameAPI.getInstance().isDropItems()) {
         items.forEach(item -> {
         });
      }

      if (player.getKiller() instanceof Player) {
         Player killer = player.getKiller();

         for(ItemStack itemStack : killer.getInventory().getArmorContents()) {
            if (itemStack != null) {
               itemStack.setDurability((short)0);
            }
         }

         Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(killer.getUniqueId(), StatusType.PVP);
         status.addInteger("kills", 1);
         status.addInteger("killstreak", 1);
         status.setInteger("killstreak-max", Math.max(status.getInteger("killstreak-max"), status.getInteger("killstreak")));
         status.save();
         killer.sendMessage("§aVocê matou o jogador " + player.getName() + ".");
         player.sendMessage("§cVocê foi morto pelo jogador " + killer.getName() + ".");
      } else {
         player.sendMessage("§cVocê morreu.");
      }

      Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP);
      status.addInteger("deaths", 1);
      status.setInteger("killstreak", 0);
      status.save();
      event.getDrops().clear();
      player.getInventory().clear();
      player.getInventory().setArmorContents(new ItemStack[4]);
      player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
      (new BukkitRunnable() {
            @Override
            public void run() {
               player.spigot().respawn();
               player.setFallDistance(-1.0F);
               PlayerRealRespawnEvent playerRespawnEvent = new PlayerRealRespawnEvent(
                  player, BukkitCommon.getInstance().getLocationManager().getLocation("spawn")
               );
               Bukkit.getPluginManager().callEvent(playerRespawnEvent);
               player.teleport(playerRespawnEvent.getRespawnLocation());
               GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).setSpawnProtection(true);
               Bukkit.getPluginManager().callEvent(new PlayerProtectionEvent(player, true));
            }
         })
         .runTaskLater(GameAPI.getInstance(), 7L);
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      ItemStack itemStack = player.getItemInHand();
      if (itemStack.getType() == Material.MUSHROOM_SOUP && event.getAction().name().contains("RIGHT") && player.getHealth() < player.getMaxHealth()) {
         event.setCancelled(true);
         int restores = 7;
         player.setHealth(Math.min(player.getHealth() + (double)restores, player.getMaxHealth()));
         player.setItemInHand(new ItemBuilder().type(Material.BOWL).build());
      } else {
         if (event.hasBlock() && event.getClickedBlock().getType() == Material.TRAP_DOOR) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      player.getInventory().clear();
      player.getInventory().setArmorContents(new ItemStack[4]);
      player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
      PlayerRealRespawnEvent playerRespawnEvent = new PlayerRealRespawnEvent(player, BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
      Bukkit.getPluginManager().callEvent(playerRespawnEvent);
      player.teleport(playerRespawnEvent.getRespawnLocation());
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onDamageDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());
         if (gamer.hasSpawnProtection()) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
      Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());
      Gamer damager = GameAPI.getInstance().getGamerManager().getGamer(event.getDamager().getUniqueId());
      if (!gamer.hasSpawnProtection() && !damager.hasSpawnProtection()) {
         Player player = event.getPlayer();
         ItemStack itemStack = player.getItemInHand();
         if (itemStack != null && itemStack.getType().name().contains("SWORD")) {
            itemStack.setDurability((short)0);
         }
      } else {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
      Player player = event.getPlayer();
      Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());
      if (gamer.hasSpawnProtection() && this.distanceSquared(event.getTo())) {
         gamer.setSpawnProtection(false);
         Bukkit.getPluginManager().callEvent(new PlayerProtectionEvent(player, false));
         player.sendMessage("§cVocê perdeu sua proteção de spawn.");
      }
   }

   @EventHandler
   public void onPlayerProtection(PlayerProtectionEvent event) {
      Player player = event.getPlayer();
      if (!event.getNewState() && player.getGameMode() == GameMode.CREATIVE) {
         PlayerHelper.title(player, "§c§lATENÇÃO", "§fvocê está no criativo.", 10, 60, 10);
      }
   }

   public boolean distanceSquared(Location locationTo) {
      return this.distanceSquared(locationTo, GameAPI.getInstance().getProtectionRadius());
   }

   public boolean distanceSquared(Location locatioTo, double radius) {
      Location spawnLocation = BukkitCommon.getInstance().getLocationManager().getLocation("spawn");
      double distX = locatioTo.getX() - spawnLocation.getX();
      double distZ = locatioTo.getZ() - spawnLocation.getZ();
      double distance = distX * distX + distZ * distZ;
      return distance > radius * radius;
   }
}
