package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.event.PlayerStompedEvent;
import br.com.plutomc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class AntitowerKit extends Kit {
   public AntitowerKit() {
      super("Antitower", "Não seja stompado", Material.GOLD_HELMET, 22000, new ArrayList<>());
   }

   @EventHandler
   public void onPlayerStomped(PlayerStompedEvent event) {
      if (this.hasAbility(event.getPlayer())) {
         event.setCancelled(true);
         event.getPlayer().damage(4.0);
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL && this.hasAbility((Player)event.getEntity())) {
         event.setCancelled(true);
      }
   }
}
