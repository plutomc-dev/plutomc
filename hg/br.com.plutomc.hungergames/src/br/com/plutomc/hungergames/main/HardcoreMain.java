package br.com.plutomc.hungergames.main;

import br.com.plutomc.core.bukkit.command.BukkitCommandFramework;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.event.GameStartEvent;
import br.com.plutomc.hungergames.main.gamer.GamerDataImpl;
import br.com.plutomc.hungergames.main.listener.*;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import br.com.plutomc.hungergames.main.manager.SimplekitManager;
import br.com.plutomc.hungergames.main.stages.InvencibilitySchedule;
import br.com.plutomc.hungergames.main.stages.WaitingSchedule;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

@Getter
public class HardcoreMain extends GameAPI {

	public static final List<String> FREE_ABILITIES = Arrays.asList("boxer", "magma", "digger", "switcher", "blink",
			"stomper", "lumberjack", "surprise");

	private SimplekitManager simplekitManager;

	@Getter
	private static HardcoreMain instance;

	@Override
	public void onLoad() {
		instance = this;
		super.onLoad();

		GameHelper.deleteWorld("world");
	}

	@Override
	public void onEnable() {
		World world = Bukkit.getWorlds().stream().findFirst().orElse(null);

		world.setAutoSave(false);

		if (world.hasStorm())
			world.setStorm(false);

		world.setDifficulty(Difficulty.NORMAL);

		world.setTime(0l);
		world.setWeatherDuration(999999999);
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("announceAdvancements", "false");
		((CraftWorld) world).getHandle().savingDisabled = true;

		long pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		long time = System.currentTimeMillis();

		try {
			for (int x = 0; x <= 20; x++) {
				if (x % 10 == 0)
					System.gc();

				for (int z = 0; z <= 20; z++) {
					world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
					world.getSpawnLocation().clone().add(x * -16, 0, z * -16).getChunk().load();
					world.getSpawnLocation().clone().add(x * 16, 0, z * -16).getChunk().load();
					world.getSpawnLocation().clone().add(x * -16, 0, z * 16).getChunk().load();
				}

				if (x % 2 == 0)
					CommonPlugin.getInstance()
							.debug("[World] "
									+ StringFormat.formatTime((int) ((System.currentTimeMillis() - time) / 1000))
									+ " have passed! PID: " + pid + " - used mem: "
									+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L
											/ 1048576L));
			}
		} catch (OutOfMemoryError ex) {

		}

		super.onEnable();
		saveResource("cake.png", true);

		simplekitManager = new SimplekitManager();

		getScheduleManager().startSchedule(new WaitingSchedule());
		Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);

		BukkitCommandFramework.INSTANCE.loadCommands("br.com.plutomc");
		loadAbilities("br.com.plutomc.hungergames.main.ability.types");

		createSquare(new Location(world, 0, 0, 0), Material.BEDROCK, 0, 3, 3);
		createSquare(new Location(world, 0, 1, 0), Material.AIR, 0, 2, 3);
		createSquare(new Location(world, 0, 1, 0), Material.AIR, 0, 1, 3);
		createSquare(new Location(world, 0, 1, 0), Material.AIR, 0, 0, 3);

		setGamerData(new GamerDataImpl(CommonPlugin.getInstance().getMongoConnection()));
		loadType();
	}

	public void loadType() {
		setMap(getMaxAbilities() == 1 ? "SINGLEKIT" : getMaxAbilities() == 2 ? "DOUBLEKIT" : "MULTIKIT");
		setState(MinigameState.WAITING, 300);
		Bukkit.setWhitelist(CommonPlugin.getInstance().getServerType() != ServerType.HG);

	}

	@SuppressWarnings("deprecation")
	public void createSquare(Location location, Material material, int id, int radius, int height) {
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				Location currentLocation = location.clone().add(x, 0, z);

				if (z == radius || z == -radius || x == radius || x == -radius) {
					for (int y = 1; y <= height; y++) {
						Location actualLocation = currentLocation.clone().add(0, y, 0);

						actualLocation.getBlock().setType(material);
						actualLocation.getBlock().setData((byte) id);
					}
				}

				currentLocation.getBlock().setType(material);
				currentLocation.getBlock().setData((byte) id);
			}
		}
	}

	@Override
	public void startGame() {
		setState(MinigameState.INVINCIBILITY, 120);
		Bukkit.getPluginManager().registerEvents(new AbilityListener(), GameAPI.getInstance());
		Bukkit.getPluginManager().registerEvents(new BlockListener(), GameAPI.getInstance());
		Bukkit.getPluginManager().registerEvents(new CombatListener(), GameAPI.getInstance());
		Bukkit.getPluginManager().registerEvents(new GameListener(), GameAPI.getInstance());
		Bukkit.getPluginManager().registerEvents(new StatusListener(), GameAPI.getInstance());
		Bukkit.broadcastMessage("§aA partida iniciou!");
		getAbilityManager().registerAbilities();
		Bukkit.getPluginManager().callEvent(new GameStartEvent());

		getGamerManager().getGamers().stream().filter(gamer -> gamer.isPlaying())
				.forEach(gamer -> GameHelper.loadItems(gamer.getPlayer(), true));

		getScheduleManager().startSchedule(new InvencibilitySchedule());
	}

}
