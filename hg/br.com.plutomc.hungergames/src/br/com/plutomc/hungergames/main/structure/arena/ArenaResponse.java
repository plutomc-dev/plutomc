package br.com.plutomc.hungergames.main.structure.arena;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockState;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArenaResponse {
	
	private Map<Location, BlockState> map;
	private int blocks;
	
	public ArenaResponse(int blocks) {
		this.blocks = -1;
		map = new HashMap<>();
	}
	
	public ArenaResponse() {
		map = new HashMap<>();
	}
	
	public void addMap(Location location, BlockState blockState) {
		map.put(location, blockState);
		blocks++;
	}
	
	public void addBlock() {
		blocks++;
	}

}
