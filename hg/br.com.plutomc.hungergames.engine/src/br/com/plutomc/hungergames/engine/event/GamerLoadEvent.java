package br.com.plutomc.hungergames.engine.event;

import br.com.plutomc.hungergames.engine.gamer.Gamer;
import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import lombok.Getter;

@Getter
public class GamerLoadEvent extends PlayerEvent {
	
	private Gamer gamer;
	
	public GamerLoadEvent(Player player, Gamer gamer) {
		super(player);
		this.gamer = gamer;
	}

}
