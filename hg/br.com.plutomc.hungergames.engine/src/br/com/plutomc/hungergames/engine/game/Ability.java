package br.com.plutomc.hungergames.engine.game;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Ability extends Listener {

	String getName();

	ItemStack getAbilityIcon();

	List<ItemStack> getAbilityItems();
	
	boolean isItemKit(ItemStack itemStack);
	
	int getPrice();
	
	/*
	 * Ability State
	 */
	
	void setAbilityEnabled(boolean enabled);

	boolean isAbilityEnabled();
	
	/*
	 * Ability Users
	 */
	
	void addUser(UUID uniqueId);
	
	void removeUser(UUID uniqueId);
	
	Set<UUID> getUsers();

	String getDescription();

}
