package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;

public class GrapplerAbility extends AbilityImpl {

	public GrapplerAbility() {
		super("grappler", Material.LEASH, "Movimente-se mais rapido com sua corda.", 30000);
	}

}
