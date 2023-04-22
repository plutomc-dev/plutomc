package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ViperAbility extends AbilityImpl {

	public ViperAbility() {
		super("viper", Material.SPIDER_EYE, "Deixe seus inimigos envenenados.");
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = event.getDamager();

		if (!hasAbility(damager))
			return;

		Random r = new Random();
		Player damaged = event.getPlayer();
		
		if (r.nextInt(3) == 0)
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
	}
}
