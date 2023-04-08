package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;
import java.util.Random;

import br.com.plutomc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ViperKit extends Kit {
   public ViperKit() {
      super("Viper", "Envenene seus inimigos ao encosta-los", Material.SPIDER_EYE, 12250, new ArrayList<>());
   }

   @EventHandler
   public void onSnail(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();
            if (this.hasAbility(damager)) {
               Random r = new Random();
               Player damaged = (Player)event.getEntity();
               if (damaged instanceof Player && r.nextInt(4) == 0) {
                  damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
               }
            }
         }
      }
   }
}
