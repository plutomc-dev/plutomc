package br.com.plutomc.hungergames.main.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDeathDropItemEvent extends PlayerCancellableEvent {
	
	private Location location;

	public PlayerDeathDropItemEvent(Player player, Location location) {
		super(player);
		this.location = location;
	}
	
}
