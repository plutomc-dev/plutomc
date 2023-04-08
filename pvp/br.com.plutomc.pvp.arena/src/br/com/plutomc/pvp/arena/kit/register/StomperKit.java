package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.event.PlayerStompedEvent;
import br.com.plutomc.pvp.arena.kit.Kit;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class StomperKit extends Kit {
   public StomperKit() {
      super("Stomper", "Pise em cima de seus inimigos", Material.IRON_BOOTS, 21000, new ArrayList<>());
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getCause() == DamageCause.FALL) {
            Player stomper = (Player)event.getEntity();
            if (this.hasAbility(stomper)) {
               DamageCause cause = event.getCause();
               if (cause == DamageCause.FALL) {
                  double dmg = event.getDamage();

                  for(Player stomped : Bukkit.getOnlinePlayers()) {
                     if (stomped.getUniqueId() != stomper.getUniqueId() && !stomped.isDead() && !(stomped.getLocation().distance(stomper.getLocation()) > 5.0)) {
                        if (stomped.isSneaking() && dmg > 8.0) {
                           dmg = 8.0;
                        }

                        PlayerStompedEvent playerStomperEvent = new PlayerStompedEvent(stomped, stomper);
                        Bukkit.getPluginManager().callEvent(playerStomperEvent);
                        if (!playerStomperEvent.isCancelled()) {
                           stomped.damage(0.1, stomper);
                           stomped.damage(dmg);
                        }
                     }
                  }

                  for(int x = -3; x <= 3; ++x) {
                     for(int z = -3; z <= 3; ++z) {
                        Location effect = stomper.getLocation().clone().add((double)x, 0.0, (double)z);
                        if (!(effect.distance(stomper.getLocation()) > 3.0)) {
                           PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                              EnumParticle.SPELL_WITCH, true, (float)effect.getX(), (float)effect.getY(), (float)effect.getZ(), 0.1F, 0.1F, 0.1F, 1.0F, 30
                           );
                           Bukkit.getOnlinePlayers()
                              .stream()
                              .filter(viewer -> viewer.canSee(stomper))
                              .forEach(viewer -> ((CraftPlayer)viewer).getHandle().playerConnection.sendPacket(packet));
                        }
                     }
                  }

                  stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                  if (event.getDamage() > 4.0) {
                     event.setDamage(4.0);
                  }
               }
            }
         }
      }
   }
}
