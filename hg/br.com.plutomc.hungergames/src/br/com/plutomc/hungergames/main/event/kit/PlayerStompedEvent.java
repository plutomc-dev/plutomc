package br.com.plutomc.hungergames.main.event.kit;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;

@Getter
public class PlayerStompedEvent extends PlayerCancellableEvent {

	private Player stomper;
	
	public PlayerStompedEvent(Player player, Player stomper) {
		super(player);
		this.stomper = stomper;
	}

}
