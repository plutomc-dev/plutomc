package br.com.plutomc.hungergames.main.event.player;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;

public class PlayerEndermageEvent extends PlayerCancellableEvent {

	public PlayerEndermageEvent(Player player) {
		super(player);
	}

}
