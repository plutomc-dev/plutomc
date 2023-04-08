package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class FiremanKit extends Kit {
   public FiremanKit() {
      super("Fireman", "Não tome dano para fogo nem lava", Material.LAVA_BUCKET, 12000, new ArrayList<>());
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.hasAbility(player)) {
            if (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) {
               event.setCancelled(true);
            }
         }
      }
   }
}
