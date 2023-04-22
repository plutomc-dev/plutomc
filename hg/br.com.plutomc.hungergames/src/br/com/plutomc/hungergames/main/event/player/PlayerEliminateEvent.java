package br.com.plutomc.hungergames.main.event.player;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * This event will be called when a player is eliminated (death/quit)
 * 
 * @author Allan
 *
 */

@Getter
public class PlayerEliminateEvent extends PlayerEvent {
	
	private Gamer gamer;

	public PlayerEliminateEvent(Player player, Gamer gamer) {
		super(player);
		this.gamer = gamer;
	}

}
