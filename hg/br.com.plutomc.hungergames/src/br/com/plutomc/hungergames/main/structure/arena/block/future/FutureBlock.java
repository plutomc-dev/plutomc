package br.com.plutomc.hungergames.main.structure.arena.block.future;

import org.bukkit.Location;
import org.bukkit.Material;

import lombok.Getter;

@SuppressWarnings("deprecation")
public abstract class FutureBlock {

	private Location location;
	private Material type;
	private byte data;
	
	@Getter
	private boolean async;

	public FutureBlock(Location location, Material type, byte data) {
		this.location = location;
		this.type = type;
		this.data = data;
	}

	public byte getData() {
		return data;
	}

	public Location getLocation() {
		return location;
	}

	public Material getType() {
		return type;
	}
	
	public FutureBlock async() {
		this.async = true;
		return this;
	}

	public void setBlock() {
		location.getBlock().setType(type);
		location.getBlock().setData(data);
	}

	public abstract void place();
}