package br.com.plutomc.hungergames.main.stages;

import br.com.plutomc.core.bukkit.member.BukkitMember;
import br.com.plutomc.core.bukkit.utils.item.ActionItemStack;
import br.com.plutomc.core.bukkit.utils.item.ActionItemStack.ActionType;
import br.com.plutomc.core.bukkit.utils.item.ActionItemStack.Interact;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.game.Schedule;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.HardcoreMain;
import br.com.plutomc.hungergames.main.event.GameChangeTypeEvent;
import br.com.plutomc.hungergames.main.inventory.DiaryInventory;
import br.com.plutomc.hungergames.main.inventory.SelectorInventory;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitingSchedule implements Schedule {

	private static final ActionItemStack SELECTOR = new ActionItemStack(
			new ItemBuilder().name("§aSelecionar kit").type(Material.CHEST).build(), new Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {
					new SelectorInventory(player, 1, 1);
					return false;
				}
			});

	private static final ActionItemStack SELECTOR_SECOND = new ActionItemStack(
			new ItemBuilder().name("§aSelecionar kit 2").type(Material.CHEST).build(), new Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {
					new SelectorInventory(player, 2, 1);
					return false;
				}
			});
	
	private static final ActionItemStack DIARY_KIT = new ActionItemStack(
			new ItemBuilder().name("§aKit diário").type(Material.STORAGE_MINECART).build(), new Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {
					new DiaryInventory(player);
					return false;
				}
			});

	public WaitingSchedule() {
		Location location = new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0,
				Bukkit.getWorlds().stream().findFirst().orElse(null).getHighestBlockYAt(0, 0), 0);

		if (location.getBlock().getBiome() != Biome.FOREST)
			while (location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.STONE) {
				location = location.getWorld().getHighestBlockAt((int) location.getX() + 2, (int) location.getY() + 2)
						.getLocation();
			}

		Location spawnLocation = location.add(0, 5, 0);

		for (int i = 0; i < 4; i++) {
			Block block = spawnLocation.add(i, i, i).getBlock();
			Chunk chunk = block.getChunk();
			chunk.load(true);
			block = spawnLocation.add(-i, -i, -i).getBlock();
			chunk = block.getChunk();
			chunk.load(true);
		}

		GameAPI.getInstance().getLocationManager().saveAndLoadLocation("spawn", spawnLocation);
		GameAPI.getInstance().getLocationManager().removeLocationInConfig("respawn");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		loadInventoryForPlayer(player);
		updateMinimumPlayers(HardcoreMain.getInstance().getMinimunPlayers());

		player.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
		gamer.setPlaying(true);
		
		GameHelper.loadDefaulKit(player);
	}
	@EventHandler
	public void onGameChangeType(GameChangeTypeEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getOnlinePlayers().forEach(player -> loadInventoryForPlayer(player));
			}
		}.runTaskLater(GameAPI.getInstance(), 10l);
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!breakPermission(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!breakPermission(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!breakPermission(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!breakPermission(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

		for (Ability ability : gamer.getAbilities())
			ability.removeUser(gamer.getUniqueId());
	}

	@Override
	public void pulse() {
		int time = GameAPI.getInstance().getTime();

		if (!GameAPI.getInstance().getState().isPregame()) {
			GameAPI.getInstance().getScheduleManager().stopSchedule(this);
			return;
		}

		if (time <= 0) {
			GameAPI.getInstance().startGame();
			GameAPI.getInstance().getScheduleManager().stopSchedule(this);
			return;
		}

		if (time == 10)
			GameAPI.getInstance().setState(MinigameState.STARTING);

		if ((time > 0 && time <= 5) || time == 10 || time % 30 == 0)
			Bukkit.broadcastMessage("§eA partida irá iniciar em §b" + StringFormat.formatTime(time) + "§e.");

		if (time > 0 && time <= 2)
			Bukkit.getOnlinePlayers().forEach(player -> player.closeInventory());

		GameAPI.getInstance().setTime(time - 1);
	}

	public void updateMinimumPlayers(int minimumPlayers) {
		if (GameAPI.getInstance().getTime() == 300 && !GameAPI.getInstance().isTimerEnabled()
				&& GameAPI.getInstance().getState() == MinigameState.WAITING) {
			if (Bukkit.getOnlinePlayers().size() >= minimumPlayers) {
				GameAPI.getInstance().setState(MinigameState.PREGAME, 300);
				GameAPI.getInstance().setTimerEnabled(true);
			}
			return;
		}

		if (Bukkit.getOnlinePlayers().size() < minimumPlayers) {
			GameAPI.getInstance().setState(MinigameState.WAITING,
					GameAPI.getInstance().getTime() < 60 ? 60 : GameAPI.getInstance().getTime());
			GameAPI.getInstance().setTimerEnabled(false);
		}
	}

	public boolean breakPermission(Player player) {
		BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId(),
				BukkitMember.class);

		if (member.isStaff())
			if (member.isBuildEnabled())
				return true;

		return false;
	}

	private void loadInventoryForPlayer(Player player) {
		player.setExp(0);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getActivePotionEffects().clear();

		player.getInventory().addItem(SELECTOR.getItemStack());
		
		if (HardcoreMain.getInstance().getMaxAbilities() > 1)
			player.getInventory().addItem(SELECTOR_SECOND.getItemStack());
		
		player.getInventory().setItem(4, DIARY_KIT.getItemStack());
	}

}
