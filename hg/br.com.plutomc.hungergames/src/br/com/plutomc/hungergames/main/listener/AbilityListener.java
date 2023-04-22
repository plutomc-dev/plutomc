package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.hungergames.main.event.player.PlayerSelectedKitEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.bukkit.event.cooldown.CooldownFinishEvent;
import br.com.plutomc.core.bukkit.event.cooldown.CooldownStartEvent;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.event.player.PlayerSelectKitEvent;
import br.com.plutomc.hungergames.main.manager.GameHelper;

public class AbilityListener implements Listener {

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player))
			return;

		Player player = (Player) event.getView().getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		for (Ability ability : gamer.getAbilities()) {
			for (ItemStack item : event.getInventory().getContents()) {
				if (item == null)
					continue;

				if (ability.isItemKit(item)) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		Player player = (Player) event.getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		for (Ability ability : gamer.getAbilities()) {
			if (ability.isItemKit(item)) {
				event.setCancelled(true);
				player.updateInventory();
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		Gamer gamer = event.getGamer();

		if (GameHelper.isPriviligiedTime()) {
			if (gamer.hasAbility(event.getAbilityId()) || !CommonPlugin.getInstance().getMemberManager().getMember(gamer.getUniqueId()).hasGroup("vip")) {
				event.getPlayer().sendMessage("§cO jogo já iniciou!");
				event.setCancelled(true);
				return;
			}
		} else {
			event.getPlayer().sendMessage("§cO jogo já iniciou!");
			event.setCancelled(true);
		}
		
		if (!event.isCancelled())
			GameAPI.getInstance().getAbilityManager().registerAbilities();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
		if (event.getAbility() != null)
			for (ItemStack item : event.getAbility().getAbilityItems())
				event.getPlayer().getInventory().addItem(item);
	}

	@EventHandler
	public void onCooldownStart(CooldownStartEvent event) {
		Ability ability = GameAPI.getInstance().getAbilityManager().getAbility(event.getCooldown().getName());

		if (ability != null)
			event.getCooldown().setName("Kit " + StringFormat.formatString(ability.getName()));
	}

	@EventHandler
	public void onCooldownFinish(CooldownFinishEvent event) {
		Ability ability = GameAPI.getInstance().getAbilityManager()
				.getAbility(event.getCooldown().getName().replace("Kit ", ""));

		if (ability != null) {
			event.getPlayer().sendMessage("§aVocê agora pode usar o " + event.getCooldown().getName() + "!");
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP, 1F, 1F);
		}
	}

}
