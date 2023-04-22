package br.com.plutomc.hungergames.main.event.player;

import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import lombok.Getter;

@Getter
public class PlayerSelectedKitEvent extends PlayerEvent {

	private Gamer gamer;
	private Ability ability;
	private int abilityId;

	public PlayerSelectedKitEvent(Player player, Gamer gamer, Ability ability, int abilityId) {
		super(player);
		this.gamer = gamer;
		this.ability = ability;
		this.abilityId = abilityId;
	}


}
