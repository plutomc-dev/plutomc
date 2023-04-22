package br.com.plutomc.hungergames.main.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GameChangeTypeEvent extends GameEvent {
	
	private int maxAbilities;
	
}
