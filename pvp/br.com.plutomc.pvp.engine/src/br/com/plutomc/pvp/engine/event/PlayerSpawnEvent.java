package br.com.plutomc.pvp.engine.event;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerSpawnEvent extends PlayerEvent {
   public PlayerSpawnEvent(Player player) {
      super(player);
   }
}
