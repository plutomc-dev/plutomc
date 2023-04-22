package br.com.plutomc.hungergames.main.event.kit.gladiator;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;


@Getter
public class GladiatorFinishEvent extends PlayerCancellableEvent {

	public GladiatorFinishEvent(Player challenger, Player challenged) {
		super(challenger);
	}

}