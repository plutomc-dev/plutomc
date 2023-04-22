package br.com.plutomc.hungergames.main.event.player;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;

@Getter
public class PlayerItemReceiveEvent extends PlayerCancellableEvent {
	
	public PlayerItemReceiveEvent(Player player) {
		super(player);
	}

}
