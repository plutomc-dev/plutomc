package br.com.plutomc.pvp.arena.event.gladiator;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class GladiatorFinishEvent extends PlayerCancellableEvent {
   public GladiatorFinishEvent(Player challenger, Player challenged) {
      super(challenger);
   }
}
