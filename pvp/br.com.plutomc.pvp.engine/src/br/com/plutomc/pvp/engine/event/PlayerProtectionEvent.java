package br.com.plutomc.pvp.engine.event;

import lombok.NonNull;
import br.com.plutomc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerProtectionEvent extends PlayerEvent {
   private boolean newState;

   public PlayerProtectionEvent(@NonNull Player player, boolean newState) {
      super(player);
      if (player == null) {
         throw new NullPointerException("player is marked non-null but is null");
      } else {
         this.newState = newState;
      }
   }

   public boolean getNewState() {
      return this.newState;
   }

   public boolean getOldState() {
      return !this.newState;
   }
}
