package br.com.plutomc.hungergames.engine.manager;

import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.*;

public class AbilityManager {

	private Map<String, Ability> abilityMap;
	private Set<Class<? extends Ability>> abilitySet;

	public AbilityManager() {
		abilityMap = new HashMap<>();
		abilitySet = new HashSet<>();
	}

	public void loadAbility(String abilityName, Ability ability) {
		abilityMap.put(abilityName.toLowerCase(), ability);
		System.out.println("The ability " + abilityName + " has been loaded!");
	}

	public Ability getAbility(String abilityName) {
		return abilityMap.get(abilityName.toLowerCase());
	}

	public void unloadAbility(String abilityName) {
		abilityMap.remove(abilityName.toLowerCase());
	}

	public Collection<Ability> getAbilities() {
		return abilityMap.values();
	}

	public void registerAbilities() {
		GameAPI.getInstance().getAbilityManager().getAbilities().stream()
				.filter(ability -> !ability.getUsers().isEmpty())
				.forEach(ability -> registerAbility(ability));
	}
	
	public void registerAbility(Ability ability) {
		if (abilitySet.contains(ability.getClass()))
			return;
		
		Bukkit.getPluginManager().registerEvents(ability, GameAPI.getInstance());
		abilitySet.add(ability.getClass());
	}
	
	public void unregisterAbility(Ability ability) {
		if (abilitySet.contains(ability.getClass())) {
			HandlerList.unregisterAll(ability);
			abilitySet.remove(ability.getClass());
		}
	}

}
