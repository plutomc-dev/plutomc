package br.com.plutomc.pvp.arena.kit.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ThorKit extends Kit {
   private Map<UUID, Long> damageRaio = new HashMap<>();

   public ThorKit() {
      super(
         "Thor",
         "Jogue raios em seus inimigos com seu machado",
         Material.WOOD_AXE,
         10500,
         Arrays.asList(new ItemBuilder().name("§aThor").type(Material.WOOD_AXE).build())
      );
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (event.getEntity() instanceof LightningStrike) {
            if (this.damageRaio.containsKey(player.getUniqueId()) && this.damageRaio.get(player.getUniqueId()) < System.currentTimeMillis()) {
               event.setDamage(0.0);
            } else {
               event.setDamage(6.0);
               event.getEntity().setFireTicks(200);
            }
         }
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent e) {
      Player player = e.getPlayer();
      if (player.getItemInHand() != null) {
         if (this.isAbilityItem(player.getItemInHand())) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
               if (this.hasAbility(player)) {
                  if (this.isCooldown(player)) {
                     return;
                  }

                  Location loc = player.getTargetBlock((Set<Material>)null, 20).getLocation();
                  loc = loc.getWorld().getHighestBlockAt(loc).getLocation();
                  this.damageRaio.put(player.getUniqueId(), System.currentTimeMillis() + 4000L);
                  player.getWorld().strikeLightning(loc);
                  this.addCooldown(player, 8L);
               }
            }
         }
      }
   }
}
