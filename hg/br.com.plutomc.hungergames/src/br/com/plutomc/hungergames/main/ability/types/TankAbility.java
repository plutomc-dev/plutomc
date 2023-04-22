package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class TankAbility extends AbilityImpl {

	public TankAbility() {
		super("tank", Material.TNT, "Seus inimigos explodirao quando você matar eles.");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Location loc = e.getEntity().getLocation();
		
		if (e.getEntity().getKiller() instanceof Player && hasAbility(e.getEntity().getKiller())) {
			e.getEntity().getWorld().createExplosion(loc, 4.0F);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause().name().contains("EXPLOSION") && hasAbility((Player) e.getEntity())) {
			e.setDamage(0.0D);	
		}
	}

}
