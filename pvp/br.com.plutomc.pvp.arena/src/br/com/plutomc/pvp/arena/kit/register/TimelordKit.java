package br.com.plutomc.pvp.arena.kit.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TimelordKit extends Kit {
   private static final int RADIUS = 5;
   private Map<Player, Long> timelordList = new HashMap<>();

   public TimelordKit() {
      super("Timelord", "Pare o tempo com seu relógio", Material.WATCH, 11700, Arrays.asList(new ItemBuilder().name("§aTimelord").type(Material.WATCH).build()));
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (this.hasAbility(event.getPlayer()) && this.isAbilityItem(event.getPlayer().getItemInHand())) {
         if (this.isCooldown(event.getPlayer())) {
            return;
         }

         for(Player game : Bukkit.getOnlinePlayers()) {
            if (game != event.getPlayer() && !GameMain.getInstance().getVanishManager().isPlayerVanished(game.getUniqueId())) {
               double distance = event.getPlayer().getLocation().distance(game.getPlayer().getLocation());
               if (distance <= 5.0) {
                  this.timelordList.put(game, System.currentTimeMillis() + 4000L);
               }
            }
         }

         event.setCancelled(true);
         this.addCooldown(event.getPlayer(), 45L);
         event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);
      }
   }

   @EventHandler
   public void onPlayeDeath(PlayerDeathEvent event) {
      if (this.timelordList.containsKey(event.getEntity())) {
         this.timelordList.remove(event.getEntity());
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      if (this.timelordList.containsKey(event.getPlayer())) {
         this.timelordList.remove(event.getPlayer());
      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {
      if (this.timelordList.containsKey(event.getPlayer())) {
         if (this.timelordList.get(event.getPlayer()) > System.currentTimeMillis()) {
            if (event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ()) {
               event.setCancelled(true);
            }
         } else {
            this.timelordList.remove(event.getPlayer());
         }
      }
   }
}
