package br.com.plutomc.duels.gapple.gamer;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Gamer extends br.com.plutomc.duels.engine.gamer.Gamer {

    @Getter @Setter
    private transient boolean alive;

    @Getter @Setter
    private transient boolean spectator;

    public Gamer(String playerName, UUID uniqueId) {
        super(playerName, uniqueId);
    }
}
