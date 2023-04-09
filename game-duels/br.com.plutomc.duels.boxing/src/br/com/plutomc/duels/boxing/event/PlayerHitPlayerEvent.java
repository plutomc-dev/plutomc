package br.com.plutomc.duels.boxing.event;

import br.com.plutomc.core.bukkit.event.PlayerEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class PlayerHitPlayerEvent extends PlayerEvent {
    private Player hitter;

    public PlayerHitPlayerEvent(@NonNull Player player, Player hitter) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        } else {
            this.hitter = hitter;
        }
    }

    public Player getHitter() {
        return this.hitter;
    }

}