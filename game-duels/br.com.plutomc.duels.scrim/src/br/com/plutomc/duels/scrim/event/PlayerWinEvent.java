package br.com.plutomc.duels.scrim.event;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerWinEvent extends PlayerEvent {
    public PlayerWinEvent(Player player) {
        super(player);
    }

}
