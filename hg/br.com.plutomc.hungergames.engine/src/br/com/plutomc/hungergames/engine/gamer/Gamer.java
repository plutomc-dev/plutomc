package br.com.plutomc.hungergames.engine.gamer;

import br.com.plutomc.hungergames.engine.game.Ability;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface Gamer {
	
	/*
	 * Info
	 */

	String getPlayerName();
	
	UUID getUniqueId();
	
	/*
	 * Ability
	 */
	
	String getAbilitiesName();
	
	void setAbility(Ability ability, int abilityId);
	
	void addAbility(Ability ability);
	
	void removeAbility(Ability ability);
	
	void clearAbilities();

	List<Ability> getAbilities();
	
	Ability getAbility(int abilityId);
	
	int getAbilityId(Ability ability);
	
	boolean hasAbility(String abilityName);
	
	boolean hasAbility(int abilityId);
	
	/*
	 * 
	 * Game Status
	 * 
	 */
	
	int getKills();
	
	void addKill();
	
	/*
	 * Game State
	 * 
	 */
	
	void setPlaying(boolean playing);
	
	boolean isPlaying();
	
	void setSpectator(boolean spectator);
	
	boolean isSpectator();
	
	void setGamemaker(boolean gamemaker);
	
	boolean isGamemaker();
	
	/*
	 * 
	 * 
	 * 
	 */
	
	Player getPlayer();
	
	void loadPlayer(Player player);

	boolean isAbilityItem(ItemStack item);

}
