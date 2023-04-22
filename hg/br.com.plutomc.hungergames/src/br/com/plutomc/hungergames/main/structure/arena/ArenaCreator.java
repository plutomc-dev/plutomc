package br.com.plutomc.hungergames.main.structure.arena;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * 
 * Basic ArenaCreator to create a simple arena
 * 
 * @author yandv
 *
 */

public interface ArenaCreator {
	
	ArenaResponse place(Location location, Material material, int id, int radius, int height, boolean wall, boolean async);

}
