package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;

public class ScoutAbility extends AbilityImpl {
	
	public ScoutAbility() {
		super("scout", new ItemBuilder().type(Material.POTION).durability(16418).build(), "Corra mais rápido.");
		addItem(new ItemBuilder().type(Material.POTION).durability(16418).amount(3).build());
	}

}
