package br.com.plutomc.hungergames.main.event.player;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PlayerSpectatorEvent extends PlayerEvent {

	private Gamer gamer;
	
	public PlayerSpectatorEvent(@NonNull Player player, Gamer gamer) {
		super(player);
		this.gamer = gamer;
	}
	
	

}
