package br.com.plutomc.hungergames.main.event.kit.gladiator;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class GladiatorScapeEvent extends PlayerCancellableEvent {
	
	private Player target;
	
	public GladiatorScapeEvent(Player player, Player target) {
		super(player);
		this.target = target;
	}

}
