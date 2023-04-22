package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ForgerAbility extends AbilityImpl {

	private Map<Material, ItemStack> forgerableItems;

	public ForgerAbility() {
		super("forger", Material.COAL, "Forje barras misturando carvão e minérios em seu inventário.", 19000);

		forgerableItems = new HashMap<>();
		forgerableItems.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT, 0));
		forgerableItems.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT, 0));
		forgerableItems.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND, 0));
		addItem(new ItemBuilder().name("§aForger").type(Material.COAL).amount(3).build());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (!hasAbility(player))
			return;

		ItemStack currentItem = event.getCurrentItem();

		if (currentItem == null || currentItem.getType() == Material.AIR)
			return;
		
		int coalAmount = 0;
		Inventory inv = event.getView().getBottomInventory();
		
		for (ItemStack item : inv.getContents()) {
			if (item != null && item.getType() == Material.COAL)
				coalAmount += item.getAmount();
		}
		
		if (coalAmount == 0)
			return;
		
		int hadCoal = coalAmount;
		
		if (currentItem.getType() == Material.COAL) {
			for (int slot = 0; slot < inv.getSize(); slot++) {
				ItemStack item = inv.getItem(slot);
				if (item != null && item.getType().name().contains("ORE")) {
					while (item.getAmount() > 0 && coalAmount > 0
							&& (item.getType() == Material.IRON_ORE || item.getType() == Material.GOLD_ORE)) {
						item.setAmount(item.getAmount() - 1);
						coalAmount--;
						if (item.getType() == Material.IRON_ORE)
							event.getWhoClicked().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
						else if (item.getType() == Material.GOLD_ORE)
							event.getWhoClicked().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
					}
					
					if (item.getAmount() == 0)
						inv.setItem(slot, new ItemStack(Material.AIR));
				}
			}
		} else if (currentItem.getType().name().contains("ORE")) {
			while (currentItem.getAmount() > 0 && coalAmount > 0
					&& (currentItem.getType() == Material.IRON_ORE || currentItem.getType() == Material.GOLD_ORE)) {
				currentItem.setAmount(currentItem.getAmount() - 1);
				coalAmount--;
				if (currentItem.getType() == Material.IRON_ORE)
					event.getWhoClicked().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
				else if (currentItem.getType() == Material.GOLD_ORE)
					event.getWhoClicked().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
			}
			
			if (currentItem.getAmount() == 0)
				event.setCurrentItem(new ItemStack(Material.AIR));
		}
		
		if (coalAmount != hadCoal) {
			boolean updateInventory = false;
			
			for (int slot = 0; slot < inv.getSize(); slot++) {
				ItemStack item = inv.getItem(slot);
				if (item != null && item.getType() == Material.COAL) {
					while (coalAmount < hadCoal && item.getAmount() > 0) {
						item.setAmount(item.getAmount() - 1);
						coalAmount++;
					}
					if (item.getAmount() == 0)
						inv.setItem(slot, new ItemStack(Material.AIR));
				}
				
				updateInventory = true;
			}
			
			if (updateInventory)
				player.updateInventory();
		}
	}

}
