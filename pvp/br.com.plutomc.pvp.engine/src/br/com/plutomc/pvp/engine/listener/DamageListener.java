package br.com.plutomc.pvp.engine.listener;

import br.com.plutomc.pvp.engine.GameAPI;
import br.com.plutomc.pvp.engine.GameConst;
import br.com.plutomc.core.bukkit.event.player.PlayerCommandEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

public class DamageListener implements Listener {
   @EventHandler
   public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
      Player player = event.getPlayer();
      Player damager = event.getDamager();
      if (!GameAPI.getInstance().getGamerManager().getGamer(damager.getUniqueId()).isSpawnProtection()
         && !GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isSpawnProtection()) {
         player.setMetadata("combatlog", GameAPI.getInstance().createMeta(Long.valueOf(System.currentTimeMillis() + 12000L)));
         damager.setMetadata("combatlog", GameAPI.getInstance().createMeta(Long.valueOf(System.currentTimeMillis() + 12000L)));
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (this.isInCombatlog(player)) {
         player.damage(2.147483647E9);
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerDeathEvent event) {
      Player player = event.getEntity();
      if (player.getKiller() instanceof Player) {
         player.getKiller().removeMetadata("combatlog", GameAPI.getInstance());
      }

      player.removeMetadata("combatlog", GameAPI.getInstance());
   }

   @EventHandler
   public void onPlayerCommand(PlayerCommandEvent event) {
      Player player = event.getPlayer();
      if (this.isInCombatlog(player) && GameConst.BLOCKED_COMMANDS.contains(event.getCommandLabel().toLowerCase())) {
         event.setCancelled(true);
         player.sendMessage("§cVocê está em combate, aguarde para executar esse comando.");
      }
   }

   public boolean isInCombatlog(Player player) {
      if (player.hasMetadata("combatlog")) {
         MetadataValue metadataValue = player.getMetadata("combatlog").stream().findFirst().orElse(null);
         long expire = metadataValue.asLong();
         return expire > System.currentTimeMillis();
      } else {
         return false;
      }
   }
}
