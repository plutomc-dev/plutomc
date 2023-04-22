package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcherAbility extends AbilityImpl {

	private Map<UUID, ArcherCategory> archerMap = new HashMap<>();

	public ArcherAbility() {
		super("archer", Material.BOW,
				"Receba um poderoso arco e escolha suas flechas entre fogo, veneno, dano e lentidão ao clicar com o botão direito.");
		addItem(new ItemBuilder().name("§aArcher").type(Material.BOW).build());
		addItem(new ItemBuilder().type(Material.ARROW).amount(20).build());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.hasItem() && isItemKit(event.getItem())) {
			ItemStack itemStack = event.getItem();

			if (event.getAction().name().contains("LEFT")) {
				for (Enchantment enchantment : itemStack.getEnchantments().keySet())
					itemStack.removeEnchantment(enchantment);

				int nextCategory = archerMap.containsKey(player.getUniqueId())
						? archerMap.get(player.getUniqueId()).ordinal() == ArcherCategory.values().length - 1 ? 0
								: archerMap.get(player.getUniqueId()).ordinal() + 1
						: 0;
				ArcherCategory archerCategory = ArcherCategory.values()[nextCategory];

				switch (archerCategory) {
				case FIRE: {
					itemStack.addEnchantment(Enchantment.ARROW_FIRE, 1);
					break;
				}
				case DAMAGE: {
					itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
					break;
				}
				default:
					break;
				}

				archerMap.put(player.getUniqueId(), archerCategory);
				player.sendMessage(
						"§aModo do arco alterado para " + StringFormat.formatString(archerCategory.name()) + "!");
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player))
			return;

		Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;

		Player shooter = (Player) arrow.getShooter();

		if (hasAbility(shooter)) {
			if (archerMap.containsKey(shooter.getUniqueId())) {
				Player player = (Player) event.getEntity();

				if (archerMap.get(shooter.getUniqueId()) == ArcherCategory.POISON)
					player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 1));
				else if (archerMap.get(shooter.getUniqueId()) == ArcherCategory.SLOWNESS)
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
			}

			shooter.getInventory().addItem(new ItemStack(Material.ARROW));
		}
	}

	public enum ArcherCategory {

		FIRE, POISON, SLOWNESS, DAMAGE;

	}

}
