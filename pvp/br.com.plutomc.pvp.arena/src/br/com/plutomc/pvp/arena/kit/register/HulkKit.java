package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HulkKit extends Kit {
   public HulkKit() {
      super("Hulk", "Pegue seus inimigos em suas costas e lançe-os para longe", Material.SADDLE, 14000, new ArrayList<>());
   }

   @EventHandler
   public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
      Player player = event.getPlayer();
      if (this.hasAbility(player) && event.getRightClicked() instanceof Player) {
         Player clicked = (Player)event.getRightClicked();
         if (!GameMain.getInstance().getGamerManager().getGamer(clicked.getUniqueId()).isSpawnProtection()
            && !player.isInsideVehicle()
            && !clicked.isInsideVehicle()
            && player.getItemInHand().getType() == Material.AIR) {
            if (this.isCooldown(player)) {
               return;
            }

            this.addCooldown(player, 12L);
            player.setPassenger(clicked);
         }
      }
   }

   @EventHandler
   public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
      final Player player = event.getPlayer();
      Player hulk = event.getDamager();
      if (hulk.getPassenger() != null && hulk.getPassenger() == player && this.hasAbility(hulk) && hulk.getPassenger() == player) {
         event.setCancelled(true);
         player.setSneaking(true);
         Vector v = hulk.getEyeLocation().getDirection().multiply(1.6F);
         v.setY(0.6);
         player.setVelocity(v);
         (new BukkitRunnable() {
            @Override
            public void run() {
               player.setSneaking(false);
            }
         }).runTaskLater(GameMain.getInstance(), 10L);
      }
   }
}
