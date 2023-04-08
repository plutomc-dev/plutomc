package br.com.plutomc.pvp.arena.event.gladiator;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class GladiatorScapeEvent extends PlayerCancellableEvent {
   private Player target;

   public GladiatorScapeEvent(Player player, Player target) {
      super(player);
      this.target = target;
   }

   public Player getTarget() {
      return this.target;
   }
}
