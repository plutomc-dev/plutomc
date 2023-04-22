package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;

public class FiremanAbility extends AbilityImpl {

	public FiremanAbility() {
		super("fireman", Material.WATER_BUCKET, "Não receba dano de fogo ou lava.", 18000);
		addItem(new ItemBuilder().type(Material.WATER_BUCKET).build());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (hasAbility(player) && (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK))
			event.setCancelled(true);
	}

}
