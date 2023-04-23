package br.com.plutomc.hungergames.main.ability.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;

public class SpecialistAbility extends AbilityImpl {

	public SpecialistAbility() {
		super("specialist", Material.BOOK,
				"Mate jogadores e ganhe experiência para encantar seus itens usando sua mesa de encantamento portátil.",
				30000);
		addItem(new ItemBuilder().name("§aSpecialist").type(Material.BOOK).build());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && isItemKit(player.getItemInHand())) {
			Block block = new Location(player.getLocation().getWorld(), 501, 0, 500).getBlock();
			block.setType(Material.ENCHANTMENT_TABLE);
			player.openEnchanting(block.getLocation(), true);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player killer = event.getEntity().getKiller();

		if (killer != null)
			if (hasAbility(killer))
				killer.setLevel(killer.getLevel() + 1);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreEnchantTest(PrepareItemEnchantEvent event) {
		if (isItemKit(event.getItem()))
			event.setCancelled(true);
	}

}
