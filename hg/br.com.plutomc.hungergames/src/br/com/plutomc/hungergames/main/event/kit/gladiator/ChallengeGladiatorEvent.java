package br.com.plutomc.hungergames.main.event.kit.gladiator;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;

@Getter
public class ChallengeGladiatorEvent extends PlayerCancellableEvent {

	private Player target;

	public ChallengeGladiatorEvent(Player player, Player target) {
		super(player);
		this.target = target;
	}

}
