package br.com.plutomc.pvp.arena.kit.register;

import java.util.Arrays;
import java.util.Random;

import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReaperKit extends Kit {
   public ReaperKit() {
      super(
         "Reaper",
         "Ceife a alma de seus inimigos por alguns segundos com a sua enxada",
         Material.WOOD_HOE,
         11250,
         Arrays.asList(new ItemBuilder().name("§aReaper").type(Material.WOOD_HOE).build())
      );
   }

   @EventHandler
   public void onSnail(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();
            if (this.hasAbility(damager)) {
               ItemStack item = damager.getItemInHand();
               if (this.isAbilityItem(item)) {
                  event.setCancelled(true);
                  damager.updateInventory();
                  Random r = new Random();
                  Player damaged = (Player)event.getEntity();
                  if (damaged instanceof Player && r.nextInt(4) == 0) {
                     damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 4));
                  }
               }
            }
         }
      }
   }
}
