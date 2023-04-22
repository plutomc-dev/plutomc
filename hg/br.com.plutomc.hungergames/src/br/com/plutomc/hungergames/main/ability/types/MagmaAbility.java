package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Random;
import java.util.UUID;

public class MagmaAbility extends AbilityImpl {

	public MagmaAbility() {
		super("magma", Material.MAGMA_CREAM, "Tenha 33% de chance de colocar fogo em quem você bater.");
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!hasAbility(player))
			return;

		if (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = event.getDamager();

		if (!hasAbility(damager))
			return;

		Random r = new Random();
		Player damaged = event.getPlayer();

		if (r.nextInt(3) == 0)
			damaged.setFireTicks(80);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (!GameAPI.getInstance().getState().isInvencibility())
			if (event.getCurrentTick() % 10 == 0)
				for (UUID uniqueId : getUsers()) {
					Player player = Bukkit.getPlayer(uniqueId);

					if (player == null
							|| !GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isPlaying())
						continue;

					if (hasAbility(player)) {
						if (!player.getLocation().getBlock().getType().name().contains("WATER"))
							continue;

						((CraftPlayer) player).getHandle().damageEntity(DamageSource.DROWN, 1.0F);
					}
				}
	}

}
