package br.com.plutomc.core.bukkit;

import br.com.plutomc.core.bukkit.utils.character.handler.ActionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BukkitMain extends BukkitCommon {
   @Override
   public void onEnable() {
      super.onEnable();
      this.createCharacter(new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0.0, 120.0, 0.0), "Kotcka", new ActionHandler() {
         @Override
         public boolean onInteract(Player player, boolean right) {
            player.sendMessage("viado");
            return false;
         }
      });
   }
}
