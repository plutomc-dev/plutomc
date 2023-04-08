package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AnchorKit extends Kit {
   public AnchorKit() {
      super("Anchor", "Se prenda ao chão e não saia dele", Material.ANVIL, 9000, new ArrayList<>());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
      Player player = event.getPlayer();
      Player damager = event.getDamager();
      if (this.hasAbility(player) || this.hasAbility(damager)) {
         player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);
         this.velocityPlayer(player);
         this.velocityPlayer(damager);
      }
   }

   private void velocityPlayer(final Player player) {
      player.setVelocity(new Vector(0, 0, 0));
      (new BukkitRunnable() {
         @Override
         public void run() {
            player.setVelocity(new Vector(0, 0, 0));
         }
      }).runTaskLater(GameMain.getInstance(), 1L);
   }
}
