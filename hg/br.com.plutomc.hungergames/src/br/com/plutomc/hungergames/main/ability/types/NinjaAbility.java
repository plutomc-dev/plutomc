package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;

public class NinjaAbility extends AbilityImpl {

	private Map<Player, Player> ninjaMap = new HashMap<>();

	public NinjaAbility() {
		super("ninja", Material.NETHER_STAR, "Aperte SHIFT para teletransportar-se para o ultimo jogador hitado.",
				24500);
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && !isCooldown(player)) {
			if (ninjaMap.containsKey(player)) {
				Player target = ninjaMap.get(player);
				if (player.getLocation().distance(target.getLocation()) > 150) {
					player.sendMessage("§cVocê está muito longe do último jogador que você hitou.");
					return;
				}

				player.sendMessage("§aTeleportado!");
				player.teleport(target);
				addCooldown(player, 5L);
				ninjaMap.remove(player);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)
				|| GameAPI.getInstance().getState() != MinigameState.GAMETIME)
			return;

		Player player = (Player) event.getDamager();

		if (hasAbility(player) && GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isPlaying())
			ninjaMap.put(player, (Player) event.getEntity());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (hasAbility(event.getEntity()))
			ninjaMap.remove(event.getEntity());

		if (event.getEntity().getKiller() instanceof Player)
			if (hasAbility(event.getEntity().getKiller()))
				ninjaMap.remove(event.getEntity().getKiller());
	}

}
