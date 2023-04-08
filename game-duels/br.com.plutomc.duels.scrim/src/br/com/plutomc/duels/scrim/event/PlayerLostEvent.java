package br.com.plutomc.duels.scrim.event;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerLostEvent extends PlayerEvent {
    public PlayerLostEvent(Player player) {
        super(player);
    }
}
