package br.com.plutomc.hungergames.main.event.player;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import lombok.Getter;

@Getter
public class PlayerSelectKitEvent extends PlayerCancellableEvent {

	private Gamer gamer;
	private Ability ability;
	private int abilityId;

	public PlayerSelectKitEvent(Player player, Gamer gamer, Ability ability, int abilityId) {
		super(player);
		this.gamer = gamer;
		this.ability = ability;
		this.abilityId = abilityId;
	}

}
