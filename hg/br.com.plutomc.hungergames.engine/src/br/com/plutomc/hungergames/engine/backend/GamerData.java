package br.com.plutomc.hungergames.engine.backend;

import br.com.plutomc.hungergames.engine.gamer.Gamer;

import java.util.UUID;

public interface GamerData<T extends Gamer> {

	void createGamer(Gamer gamer);

	T loadGamer(UUID uniqueId);

	void deleteGamer(UUID uniqueId);

	void updateGamer(Gamer gamer, String fieldName);

}
