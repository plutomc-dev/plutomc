package br.com.plutomc.hungergames.main.manager;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimplekitManager {
	
	@Getter
	private Map<String, SimpleKit> storeMap;
	
	public SimplekitManager() {
		storeMap = new HashMap<>();
	}
	
	public SimpleKit getSimplekit(String string) {
		return storeMap.get(string);
	}
	
	public void loadSimplekit(String kitName, SimpleKit simpleKit) {
		storeMap.put(kitName, simpleKit);
	}
	
	public boolean containsKey(String kitName) {
		return storeMap.containsKey(kitName);
	}
	
	@Getter
	public static class SimpleKit {

		private String kitName;

		private ItemStack[] armorContents;
		private ItemStack[] contents;

		private Collection<PotionEffect> effectList;

		public SimpleKit(String kitName, Player player) {
			this.kitName = kitName;

			this.contents = player.getInventory().getContents();
			this.armorContents = player.getInventory().getArmorContents();

			this.effectList = player.getActivePotionEffects();
		}

		public SimpleKit(String kitName, ItemStack[] armorContents, ItemStack[] contents,
				Collection<PotionEffect> effectList) {
			this.kitName = kitName;

			this.contents = contents;
			this.armorContents = armorContents;

			this.effectList = effectList;
		}
		
		/**
		 * 
		 * Reload the SimpleKit contents with the param player's contents
		 * 
		 * @param player
		 */

		public void updateKit(Player player) {
			this.contents = player.getInventory().getContents();
			this.armorContents = player.getInventory().getArmorContents();

			this.effectList = player.getActivePotionEffects();
		}

		/**
		 * 
		 * Clear player's inventory, set the SimpleKit contents and send message update
		 * message to player
		 * 
		 * @param player
		 */

		public void apply(Player player) {
			applySilent(player);
			player.sendMessage("§aKit " + getKitName() + " aplicado com sucesso!");
		}

		/**
		 * 
		 * Clear player inventory and set the SimpleKit contents
		 * 
		 * @param player
		 */

		public void applySilent(Player player) {
			player.getInventory().setContents(contents);
			player.getInventory().setArmorContents(armorContents);

			player.getActivePotionEffects().clear();
			player.addPotionEffects(getEffectList());
		}

	}

}
