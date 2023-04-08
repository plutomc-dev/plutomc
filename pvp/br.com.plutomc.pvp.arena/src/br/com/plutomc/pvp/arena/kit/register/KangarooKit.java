package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class KangarooKit extends Kit {
   private final List<Player> kangarooMap = new ArrayList<>();

   public KangarooKit() {
      super(
         "Kangaroo",
         "Use o seu foguete para movimentar-se mais rapidamente pelo mapa",
         Material.FIREWORK,
         18000,
         Arrays.asList(new ItemBuilder().name("§aKangaroo").type(Material.FIREWORK).build())
      );
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onEntityDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.hasAbility(player)) {
            this.addCooldown(player, 8L);
         }
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (this.hasAbility(player) && event.getAction() != Action.PHYSICAL && this.isAbilityItem(player.getItemInHand())) {
         event.setCancelled(true);
         if (this.isCooldown(player) || this.kangarooMap.contains(player)) {
            return;
         }

         Vector vector = player.getEyeLocation().getDirection().multiply(player.isSneaking() ? 1.8F : 0.6F).setY(player.isSneaking() ? 0.6 : 0.9F);
         player.setFallDistance(-1.0F);
         player.setVelocity(vector);
         this.kangarooMap.add(player);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.hasAbility(player) && event.getCause().name().contains("FALL")) {
            if (event.getDamage() > 7.0) {
               event.setDamage(player.getHealth() - 5.0 > 0.5 ? 5.0 : 0.0);
            } else if (event.getDamage() < 2.0) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerMove(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      if (this.hasAbility(player) && this.kangarooMap.contains(player)) {
         Block block = player.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
         if (block.getType() != Material.AIR) {
            this.kangarooMap.remove(player);
         }
      }
   }
}
