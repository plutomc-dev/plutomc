package br.com.plutomc.hungergames.main.event.player;

import br.com.plutomc.hungergames.main.event.GameEvent;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GamerWinEvent extends GameEvent {
	
	@Getter
	private Gamer gamer;

}
