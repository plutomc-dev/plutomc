package br.com.plutomc.hungergames.main.structure.arena.block.future.type;

import br.com.plutomc.hungergames.main.structure.arena.block.future.FutureBlock;
import org.bukkit.Location;
import org.bukkit.Material;

public class DefaultFutureBlock extends FutureBlock {

	public DefaultFutureBlock(Location location, Material type, byte data) {
		super(location, type, data);
	}

	@Override
	public void place() {
		setBlock();
	}

}