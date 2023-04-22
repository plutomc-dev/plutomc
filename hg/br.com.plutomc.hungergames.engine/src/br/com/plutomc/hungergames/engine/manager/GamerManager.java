package br.com.plutomc.hungergames.engine.manager;

import br.com.plutomc.hungergames.engine.gamer.Gamer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GamerManager {
	
	private Map<UUID, Gamer> gamerMap;
	
	public GamerManager() {
		gamerMap = new HashMap<>();
	}
	
	public void loadGamer(UUID uniqueId, Gamer gamer) {
		gamerMap.put(uniqueId, gamer);
	}
	
	public Gamer getGamer(UUID uniqueId) {
		return gamerMap.get(uniqueId);
	}
	
	public <T extends Gamer> T getGamer(UUID uniqueId, Class<T> clazz) {
		if (gamerMap.containsKey(uniqueId))
			return clazz.cast(this.gamerMap.get(uniqueId));
		
		return null;
	}
	
	public void unloadGamer(UUID uniqueId) {
		gamerMap.remove(uniqueId);
	}
	
	public Collection<Gamer> getGamers() {
		return gamerMap.values();
	}

	public Collection<Gamer> getAlivePlayers() {
		return getGamers().stream().filter(gamer -> gamer.isPlaying()).collect(Collectors.toList());
	}
	
}
