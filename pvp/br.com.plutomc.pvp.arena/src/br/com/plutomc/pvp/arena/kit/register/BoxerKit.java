package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BoxerKit extends Kit {
   public BoxerKit() {
      super("Boxer", "Vire um boxeador e esteja acustumado a receber pancadas e a revida-las", Material.STONE_SWORD, 12000, new ArrayList<>());
   }

   @EventHandler
   public void onBoxer(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Player damager = (Player)event.getDamager();
         if (this.hasAbility(damager)) {
            if (damager.getItemInHand().getType() == Material.AIR) {
               event.setDamage(event.getDamage() + 2.0);
            }
         }
      }
   }

   public void onSnail(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player damaged = (Player)event.getEntity();
         if (this.hasAbility(damaged)) {
            if (event.getDamage() - 1.0 >= 1.0) {
               event.setDamage(event.getDamage() - 1.0);
            } else {
               event.setDamage(1.0);
            }
         }
      }
   }
}
