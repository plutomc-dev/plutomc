package br.com.plutomc.hungergames.main.ability;

import br.com.plutomc.core.bukkit.utils.cooldown.Cooldown;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class AbilityImpl implements Ability {

	private final String name;
	private final ItemStack abilityIcon;
	private final String description;

	private final int price;

	private List<ItemStack> abilityItems = new ArrayList<>();

	@Setter
	private boolean abilityEnabled = true;
	private Set<UUID> users = new HashSet<>();

	public AbilityImpl(String name, ItemStack itemStack, String description) {
		this(name, itemStack, description, 15000);
	}

	public AbilityImpl(String name, Material material, String description) {
		this(name, new ItemBuilder().type(material).build(), description, 15000);
	}

	public AbilityImpl(String name, Material material, String description, int price) {
		this(name, new ItemBuilder().type(material).build(), description, price);
	}

	public AbilityImpl(String name, ItemStack itemStack) {
		this(name, itemStack, "Sem descrição", 15000);
	}

	public AbilityImpl(String name, Material material) {
		this(name, new ItemBuilder().type(material).build(), "Sem descrição", 15000);
	}

	public boolean isItemKit(ItemStack itemStack) {
		if (itemStack == null)
			return false;

		for (ItemStack kitItem : abilityItems) {
			if (kitItem.getType() == itemStack.getType()) {
				if (kitItem.hasItemMeta() && itemStack.hasItemMeta()) {
					if (kitItem.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
						if (itemStack.getItemMeta().getDisplayName().equals(kitItem.getItemMeta().getDisplayName()))
							return true;
					} else if (!kitItem.getItemMeta().hasDisplayName() && !itemStack.getItemMeta().hasDisplayName())
						return true;
				} else if (!kitItem.hasItemMeta() && !itemStack.hasItemMeta())
					return true;
			}
		}

		return false;
	}

	public boolean isItem(ItemStack itemStack, ItemStack kitItem) {
		if (itemStack == null)
			return false;

		if (kitItem.getType() == itemStack.getType()) {
			if (kitItem.hasItemMeta() && itemStack.hasItemMeta()) {
				if (kitItem.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
					if (itemStack.getItemMeta().getDisplayName().equals(kitItem.getItemMeta().getDisplayName()))
						return true;
				} else if (!kitItem.getItemMeta().hasDisplayName() && !itemStack.getItemMeta().hasDisplayName())
					return true;
			} else if (!kitItem.hasItemMeta() && !itemStack.hasItemMeta())
				return true;
		}

		return false;
	}

	public void addItem(ItemStack itemStack) {
		abilityItems.add(itemStack);
	}

	public boolean hasAbility(Player player) {
		return users.contains(player.getUniqueId());
	}

	public void addUser(UUID uniqueId) {
		users.add(uniqueId);
	}

	public void removeUser(UUID uniqueId) {
		users.remove(uniqueId);
	}

	public boolean isCooldown(Player player) {
		if (GameAPI.getInstance().getCooldownManager()
				.hasCooldown(player.getUniqueId(), "Kit " + StringFormat.formatString(getName()))) {
			Cooldown cooldown = GameAPI.getInstance().getCooldownManager().getCooldown(player.getUniqueId(),
					"Kit " + StringFormat.formatString(getName()));

			player.sendMessage("§cAguarde " + CommonConst.DECIMAL_FORMAT.format(cooldown.getRemaining())
					+ "s para usar o Kit " + StringFormat.formatString(getName()) + " novamente!");
			return true;
		}

		return false;
	}

	public boolean isCooldownSilent(Player player) {
		if (GameAPI.getInstance().getCooldownManager()
				.hasCooldown(player.getUniqueId(), "Kit " + StringFormat.formatString(getName()))) {
			return true;
		}

		return false;
	}

	public void addCooldown(Player player, long time) {
		GameAPI.getInstance().getCooldownManager().addCooldown(player.getUniqueId(), getName(), time);
	}

	public void addCooldown(UUID uniqueId, long time) {
		GameAPI.getInstance().getCooldownManager().addCooldown(uniqueId, getName(), time);
	}

}
