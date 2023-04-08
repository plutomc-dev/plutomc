package br.com.plutomc.pvp.arena.event;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class PlayerStompedEvent extends PlayerCancellableEvent {
   private Player stomper;

   public PlayerStompedEvent(Player stomper, Player stomped) {
      super(stomped);
      this.stomper = stomper;
   }

   public Player getStomper() {
      return this.stomper;
   }
}
