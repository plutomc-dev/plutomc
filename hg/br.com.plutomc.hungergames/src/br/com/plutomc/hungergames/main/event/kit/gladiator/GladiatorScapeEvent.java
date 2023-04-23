package br.com.plutomc.hungergames.main.event.kit.gladiator;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;

@Getter
public class GladiatorScapeEvent extends PlayerCancellableEvent {
	
	private Player target;
	
	public GladiatorScapeEvent(Player player, Player target) {
		super(player);
		this.target = target;
	}

}
