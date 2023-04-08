package br.com.plutomc.pvp.arena.event.gladiator;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class ChallengeGladiatorEvent extends PlayerCancellableEvent {
   private Player target;

   public ChallengeGladiatorEvent(Player player, Player target) {
      super(player);
      this.target = target;
   }

   public Player getTarget() {
      return this.target;
   }
}
