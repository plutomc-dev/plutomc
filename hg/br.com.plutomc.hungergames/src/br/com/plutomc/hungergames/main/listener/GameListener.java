package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.UpdateEvent.UpdateType;
import br.com.plutomc.core.bukkit.utils.ItemUtils;
import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.HardcoreMain;
import br.com.plutomc.hungergames.main.event.player.PlayerDeathDropItemEvent;
import br.com.plutomc.hungergames.main.event.player.PlayerEliminateEvent;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class GameListener implements Listener {

	private Map<Gamer, Long> timeoutMap;

	public GameListener() {
		timeoutMap = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		if (!CommonPlugin.getInstance().getMinigameState().isPregame() && !CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()).hasGroup("vip")) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cA partida já iniciou.");
			return;
		}

		if (CommonPlugin.getInstance().getMinigameState() == MinigameState.WINNING) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cA partida já finalizou.");
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (timeoutMap.containsKey(gamer)) {
			timeoutMap.remove(gamer);
			return;
		}

		if (gamer.isGamemaker() || gamer.isSpectator() || gamer.isPlaying()) {
			if (!gamer.isPlaying())
				setSpectator(gamer);
		} else {
			if (GameHelper.isPriviligiedTime()) {
				gamer.setPlaying(true);
				player.teleport(getRandomLocation());
				player.sendMessage("§aVocê entrou na partida!");
				GameHelper.loadDefaulKit(player);
				GameHelper.loadItems(player, false);
			} else
				setSpectator(gamer);
		}
	}

	private void setSpectator(Gamer gamer) {
		if (CommonPlugin.getInstance().getMemberManager().getMember(gamer.getUniqueId()).isStaff()) {
			gamer.setGamemaker(true);
			GameAPI.getInstance().getVanishManager().setPlayerInAdmin(gamer.getPlayer());
		} else
			gamer.setSpectator(true);

		gamer.setPlaying(false);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);

		Player player = event.getEntity();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		List<ItemStack> list = new ArrayList<>(event.getDrops()).stream().filter(item -> !gamer.isAbilityItem(item))
				.collect(Collectors.toList());
		event.getDrops().clear();
		event.setDroppedExp(0);

		player.closeInventory();
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		if (gamer.isGamemaker() || gamer.isSpectator())
			return;

		PlayerDeathDropItemEvent playerDeathDropItemEvent = new PlayerDeathDropItemEvent(player,
				player.getLocation() == null ? player.getKiller().getLocation() : player.getLocation());

		Bukkit.getPluginManager().callEvent(playerDeathDropItemEvent);

		if (!playerDeathDropItemEvent.isCancelled()) {
			ItemUtils.dropItems(list, playerDeathDropItemEvent.getLocation());
		}

		if (player.getKiller() instanceof Player) {
			Player killer = player.getKiller();
			Gamer killerGamer = GameAPI.getInstance().getGamerManager().getGamer(killer.getUniqueId());

			killer.sendMessage("§aVocê matou " + player.getName() + ".");
			killer.playSound(killer.getLocation(), Sound.NOTE_PIANO, 100, 100);
			killerGamer.addKill();

			for (Ability abilities : gamer.getAbilities())
				event.getDrops().removeAll(abilities.getAbilityItems());

			Bukkit.broadcastMessage("§b" + player.getName() + "(" + gamer.getAbilitiesName() + ") morreu para "
					+ killer.getName() + "(" + killerGamer.getAbilitiesName() + ") usando "
					+ convertItemName(killer.getItemInHand().getType().name()) + ".");
		} else
			Bukkit.broadcastMessage("§b" + player.getName() + "(" + gamer.getAbilitiesName() + ") morreu.");

		new BukkitRunnable() {

			@Override
			public void run() {
				respawn(player, gamer);
			}
		}.runTaskLater(GameAPI.getInstance(), 10);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasItem() && event.getItem().getType() == Material.COMPASS) {
			Player player = event.getPlayer();
			Player target = getTarget(player);

			if (target == null) {
				event.getPlayer().sendMessage("§cNenhum jogador foi encontrado!");
				event.getPlayer().setCompassTarget(event.getPlayer().getWorld().getSpawnLocation());
			} else {
				event.getPlayer().sendMessage("§aBússola apontando para " + target.getName() + ".");
				event.getPlayer().setCompassTarget(target.getLocation());
			}

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		Iterator<Entry<Gamer, Long>> iterator = getTimeouts().iterator();

		while (iterator.hasNext()) {
			Entry<Gamer, Long> entry = iterator.next();

			if (entry.getValue() < System.currentTimeMillis()) {
				entry.getKey().setPlaying(false);
				Bukkit.broadcastMessage(
						"§b" + entry.getKey().getPlayerName() + " §edemorou muito para relogar e foi desclassificado!");
				Bukkit.getPluginManager()
						.callEvent(new PlayerEliminateEvent(entry.getKey().getPlayer(), entry.getKey()));
				// make the items drop
				iterator.remove();
			}
		}
	}

	@EventHandler
	public void onPlayerEliminate(PlayerEliminateEvent event) {
		GameHelper.checkWinner();
		Bukkit.broadcastMessage(
				"§e" + GameAPI.getInstance().getGamerManager().getAlivePlayers().size() + " jogadores restantes.");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (!GameAPI.getInstance().getState().isEnding() && gamer.isPlaying())
			setTimeout(gamer);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(!HardcoreMain.getInstance().isFoodLevel());
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		event.setCancelled(!HardcoreMain.getInstance().isRegainHealth());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(!HardcoreMain.getInstance().isDropItem());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemSpawn(ItemSpawnEvent event) {
		event.setCancelled(!HardcoreMain.getInstance().isSpawnItem());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		event.setCancelled(!HardcoreMain.getInstance().isPickItem());
	}

	public String convertItemName(String name) {
		String itemName = "";
		if (name.contains("SWORD")) {
			itemName = "Espada de ";
		} else if (name.contains("AXE")) {
			itemName = "Machado de ";
		} else if (name.contains("PICKAXE")) {
			itemName = "Picareta de ";
		} else if (name.contains("SPADE")) {
			itemName = "Pá de ";
		}

		if (name.contains("DIAMOND")) {
			itemName = itemName + "Diamante";
		} else if (name.contains("IRON")) {
			itemName = itemName + "Ferro";
		} else if (name.contains("STONE")) {
			itemName = itemName + "Pedra";
		} else if (name.contains("GOLD")) {
			itemName = itemName + "Ouro";
		} else if (name.contains("WOOD")) {
			itemName = itemName + "Madeira";
		}

		if (itemName.equals(""))
			itemName = "Mão";

		return itemName;
	}

	private void respawn(Player player, Gamer gamer) {
		player.spigot().respawn();
		player.setNoDamageTicks(100);
		player.setFireTicks(0);
		player.setHealth(20d);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0);

		if (player.getPassenger() != null)
			player.getPassenger().eject();

		if (CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId()).hasGroup("vip")) {
			if (GameHelper.isPriviligiedTime()) {
				player.teleport(getRandomLocation());
				GameHelper.loadItems(player, CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId()).hasGroup("vip"));
			} else {
				setSpectator(gamer);
				Bukkit.getPluginManager().callEvent(new PlayerEliminateEvent(player, gamer));
			}
		} else {
			GameAPI.getInstance().sendPlayerToServer(player, ServerType.HG_LOBBY);
			gamer.setPlaying(false);
			Bukkit.getPluginManager().callEvent(new PlayerEliminateEvent(player, gamer));
		}
	}

	public void setTimeout(Gamer gamer) {
		timeoutMap.put(gamer, System.currentTimeMillis() + (1000 * 60));
	}

	public Set<Entry<Gamer, Long>> getTimeouts() {
		return timeoutMap.entrySet();
	}

	private Player getTarget(Player player) {
		Player target = null;
		for (Gamer gamer : GameAPI.getInstance().getGamerManager().getAlivePlayers()) {
			Player playerTarget = Bukkit.getPlayer(gamer.getUniqueId());

			if (!playerTarget.equals(player)) {
				if (playerTarget.getLocation().distance(player.getLocation()) >= 15.0D) {
					if (target == null) {
						target = playerTarget;
					} else if (target.getLocation().distance(player.getLocation()) > playerTarget.getLocation()
							.distance(player.getLocation())) {
						target = playerTarget;
					}
				}
			}
		}
		return target;
	}

	public Location getRandomLocation() {
		int x = 50 + CommonConst.RANDOM.nextInt(300);
		int z = 50 + CommonConst.RANDOM.nextInt(300);

		if (CommonConst.RANDOM.nextBoolean())
			x = -x;

		if (CommonConst.RANDOM.nextBoolean())
			z = -z;

		World world = Bukkit.getWorlds().stream().findFirst().orElse(null);
		int y = world.getHighestBlockYAt(x, z);
		Location location = new Location(world, x, y + 5, z);

		if (!location.getChunk().isLoaded()) {
			location.getChunk().load();
		}

		return location;
	}

}