package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.plutomc.core.bukkit.utils.item.ActionItemStack;
import br.com.plutomc.core.bukkit.utils.item.ActionItemStack.ActionType;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.Member;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.event.player.PlayerSpectatorEvent;
import br.com.plutomc.hungergames.main.inventory.SpectatorInventory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.stream.Collectors;

public class SpectatorListener implements Listener {

	private static final ActionItemStack GAMER_LIST = new ActionItemStack(
			new ItemBuilder().name("§aJogadores").type(Material.COMPASS).build(), new ActionItemStack.Interact() {

		@Override
		public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
								  ActionType action) {

			if (CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId()).hasGroup("vip"))
				new SpectatorInventory(player, 1);
			else
				player.sendMessage(
						"§cAdquira VIP em nossa loja para poder usar isto! §b" + "www.plutomc.com.br");
			return false;
		}
	});

	private static final ActionItemStack PLAY_AGAIN = new ActionItemStack(
			new ItemBuilder().name("§aJogar novamente").type(Material.PAPER).build(), new ActionItemStack.Interact() {

		@Override
		public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
								  ActionType action) {
			GameAPI.getInstance().sendPlayerToServer(player, ServerType.HG);
			return false;
		}
	});

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (CommonPlugin.getInstance().getMinigameState() == MinigameState.WINNING)
			return;

		if (isSpectator(event.getPlayer())) {
			Member member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
			String tag = member.getTag().getTagPrefix();

			for (Player r : event.getRecipients().stream()
					.filter(r -> isSpectator(r) || GameAPI.getInstance().getVanishManager().isPlayerInAdmin(r))
					.collect(Collectors.toList())) {

				r.sendMessage("§7[ESPECTADOR] " + tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "")
						+ event.getPlayer().getName() + "§7: " + event.getMessage());
			}

			System.out.println("<SPECTATOR - " + event.getPlayer().getName() + "> " + event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerSpectator(PlayerSpectatorEvent event) {
		Player player = event.getPlayer();

		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getActivePotionEffects().clear();
		player.setHealth(20d);
		player.setMaxHealth(1);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 99999, 1, false, false));

		player.getInventory().setItem(3, GAMER_LIST.getItemStack());
		player.getInventory().setItem(5, PLAY_AGAIN.getItemStack());
		GameAPI.getInstance().getVanishManager().setPlayerVanishToGroup(player, CommonPlugin.getInstance().getPluginInfo().getGroupByName("youtuber"));

		if (!CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()).hasGroup("vip"))
			player.teleport(new Location(player.getWorld(), 0.5, 2, 0.5));
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (isSpectator((Player) event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		if (isSpectator((Player) event.getDamager()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setAmount(0);
	}

	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player)
			if (isSpectator((Player) event.getTarget()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerAdminMode(PlayerAdminEvent event) {
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

		if (event.getAdminMode() == PlayerAdminEvent.AdminMode.ADMIN) {
			gamer.setGamemaker(true);
		} else {
			gamer.setGamemaker(false);
			gamer.setSpectator(false);
		}
	}

	private boolean isSpectator(Player player) {
		return GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isSpectator();
	}

}