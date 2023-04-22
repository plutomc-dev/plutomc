package br.com.plutomc.hungergames.main.structure;

import org.bukkit.Location;

public interface Structure {
	
	Location findPlace();

	void spawnStructure(Location location);
	
}
