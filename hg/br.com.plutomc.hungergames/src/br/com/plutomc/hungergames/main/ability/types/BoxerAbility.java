package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BoxerAbility extends AbilityImpl {

	public BoxerAbility() {
		super("boxer", Material.STONE_SWORD, "Leve menos dano e dê mais dano");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player)
			if (hasAbility((Player) event.getEntity()))
				event.setDamage(event.getDamage() - 0.5);
	}

}
