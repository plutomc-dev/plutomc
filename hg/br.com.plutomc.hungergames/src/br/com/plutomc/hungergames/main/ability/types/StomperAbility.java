package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import br.com.plutomc.hungergames.main.event.kit.PlayerStompedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.gamer.Gamer;

public class StomperAbility extends AbilityImpl {

	public StomperAbility() {
		super("stomper", Material.IRON_BOOTS, "Esmague seus inimigos.", 35000);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player) || event.getCause() != DamageCause.FALL || event.getDamage() < 4.0D
				|| event.getCause() != DamageCause.FALL)
			return;

		Player player = (Player) event.getEntity();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.isGamemaker() || gamer.isSpectator())
			return;

		if (hasAbility(player)) {
			double dmg = event.getDamage();

			for (Player stomped : Bukkit.getOnlinePlayers()) {
				if (stomped.getUniqueId() == player.getUniqueId() || stomped.isDead())
					continue;

				if (stomped.getLocation().distance(player.getLocation()) > 5)
					continue;

				if (stomped.isSneaking() && dmg > 8)
					dmg = 8;

				PlayerStompedEvent playerStomperEvent = new PlayerStompedEvent(stomped, player);
				Bukkit.getPluginManager().callEvent(playerStomperEvent);

				if (!playerStomperEvent.isCancelled()) {
					stomped.damage(0.1D, player);
					stomped.damage(dmg);
				}
			}

			player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);

			if (event.getDamage() > 4.0D)
				event.setDamage(4.0d);
		}
	}

}
