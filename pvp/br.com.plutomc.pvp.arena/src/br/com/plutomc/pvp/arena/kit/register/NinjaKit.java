package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import br.com.plutomc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class NinjaKit extends Kit {
   private HashMap<String, NinjaHit> ninjaHits = new HashMap<>();

   public NinjaKit() {
      super("Ninja", "Como um ninja teletransporte-se para as costas de seus inimigos", Material.EMERALD, 17000, new ArrayList<>());
   }

   @EventHandler
   public void onNinjaHit(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player damager = (Player)event.getDamager();
         Player damaged = (Player)event.getEntity();
         if (this.hasAbility(damager)) {
            NinjaHit ninjaHit = this.ninjaHits.get(damager.getName());
            if (ninjaHit == null) {
               ninjaHit = new NinjaHit(damaged);
            } else {
               ninjaHit.setTarget(damaged);
            }

            this.ninjaHits.put(damager.getName(), ninjaHit);
         }
      }
   }

   @EventHandler
   public void onShift(PlayerToggleSneakEvent event) {
      Player p = event.getPlayer();
      if (event.isSneaking()) {
         if (this.hasAbility(p)) {
            if (this.ninjaHits.containsKey(p.getName())) {
               NinjaHit ninjaHit = this.ninjaHits.get(p.getName());
               Player target = ninjaHit.getTarget();
               if (!target.isDead()) {
                  if (ninjaHit.getTargetExpires() >= System.currentTimeMillis()) {
                     if (p.getLocation().distance(target.getLocation()) > 50.0) {
                        p.sendMessage("§a§l> §fO jogador está muito longe§f!");
                     } else if (!this.isCooldown(p)) {
                        p.teleport(target.getLocation());
                        p.sendMessage("§a§l> §fTeletransportado até o §a" + target.getName() + "§f!");
                        this.addCooldown(p, 6L);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      Player p = event.getEntity();
      if (p.getKiller() != null) {
         Iterator<Entry<String, NinjaHit>> iterator = this.ninjaHits.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<String, NinjaHit> entry = iterator.next();
            if (entry.getValue().target == p.getKiller()) {
               iterator.remove();
            }
         }
      }

      if (this.ninjaHits.containsKey(p.getName())) {
         this.ninjaHits.remove(p.getName());
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player p = event.getPlayer();
      if (this.ninjaHits.containsKey(p.getName())) {
         this.ninjaHits.remove(p.getName());
      }
   }

   private static class NinjaHit {
      private Player target;
      private long targetExpires;

      public NinjaHit(Player target) {
         this.target = target;
         this.targetExpires = System.currentTimeMillis() + 15000L;
      }

      public Player getTarget() {
         return this.target;
      }

      public long getTargetExpires() {
         return this.targetExpires;
      }

      public void setTarget(Player player) {
         this.target = player;
         this.targetExpires = System.currentTimeMillis() + 20000L;
      }
   }
}
