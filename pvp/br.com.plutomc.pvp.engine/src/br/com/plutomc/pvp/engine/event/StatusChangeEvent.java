package br.com.plutomc.pvp.engine.event;

import lombok.NonNull;
import br.com.plutomc.core.bukkit.event.PlayerEvent;
import br.com.plutomc.core.common.member.status.Status;
import org.bukkit.entity.Player;

public class StatusChangeEvent extends PlayerEvent {
   private Status status;

   public StatusChangeEvent(@NonNull Player player, Status status) {
      super(player);
      if (player == null) {
         throw new NullPointerException("player is marked non-null but is null");
      } else {
         this.status = status;
      }
   }

   public Status getStatus() {
      return this.status;
   }
}
