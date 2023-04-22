package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.projectiles.ProjectileSource;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;

public class FishermanAbility extends AbilityImpl {

	public FishermanAbility() {
		super("fisherman", Material.FISHING_ROD, "Pesque seus oponentes com sua vara de pesca.");
		addItem(new ItemBuilder().name("§aFishing Rod").type(Material.FISHING_ROD).build());
	}
	
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof LivingEntity))
            return;

        Player player = event.getPlayer();
        
        if (hasAbility(player)) {
            if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY)
                event.getCaught().teleport(player.getLocation());

            player.getItemInHand().setDurability((short) 0);
        }
    }
    
    @EventHandler
    public void onPlayerFish(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof FishHook) {
			FishHook fishHook = (FishHook) event.getDamager();
			ProjectileSource shooter = fishHook.getShooter();
			
			if (shooter instanceof Player) {
				Player player = (Player) shooter;
			
				if (hasAbility(player)) 
					event.setCancelled(true);
			}
    	}
    }

}
