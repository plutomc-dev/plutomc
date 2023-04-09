package br.com.plutomc.duels.boxing.event;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class PlayerKillPlayerEvent extends PlayerEvent {
   private Player killer;

   public PlayerKillPlayerEvent(@NonNull Player player, Player killer) {
      super(player);
      if (player == null) {
         throw new NullPointerException("player is marked non-null but is null");
      } else {
         this.killer = killer;
      }
   }

   public Player getKiller() {
      return this.killer;
   }

}
