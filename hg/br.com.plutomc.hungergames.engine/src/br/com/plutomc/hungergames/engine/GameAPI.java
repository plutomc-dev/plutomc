package br.com.plutomc.hungergames.engine;

import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.core.bukkit.command.BukkitCommandFramework;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.ClassGetter;
import br.com.plutomc.core.common.utils.configuration.impl.JsonConfiguration;
import br.com.plutomc.hungergames.engine.backend.GamerData;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.engine.listener.ScheduleListener;
import br.com.plutomc.hungergames.engine.manager.AbilityManager;
import br.com.plutomc.hungergames.engine.manager.GamerManager;
import br.com.plutomc.hungergames.engine.manager.ScheduleManager;
import br.com.plutomc.hungergames.engine.manager.TeamManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class GameAPI extends BukkitCommon {

	@Getter
	private static GameAPI instance;

	private GamerManager gamerManager;
	private AbilityManager abilityManager;
	private ScheduleManager scheduleManager;
	private TeamManager teamManager;

	private JsonConfiguration configuration;
	private int maxAbilities;
	private int maxMembers;

	@Setter
	private GamerData<? extends Gamer> gamerData;

	@Setter
	private boolean timerEnabled;

	@Setter
	private boolean damageEnabled = true;
	@Setter
	private boolean pvpEnabled = true;

	@Setter
	private boolean buildEnabled = true;
	private Set<Material> materialSet = new HashSet<>();
	@Setter
	private boolean placeEnabled = true;
	@Setter
	private boolean bucketEnabled = true;


	@Override
	public void onLoad() {
		super.onLoad();
		loadConfiguration();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;

		gamerManager = new GamerManager();
		abilityManager = new AbilityManager();
		scheduleManager = new ScheduleManager();
		teamManager = new TeamManager();

		Bukkit.getPluginManager().registerEvents(new ScheduleListener(), this);
		BukkitCommandFramework.INSTANCE.loadCommands("br.com.wisemc.gameapi.command");

		CommonPlugin.getInstance().debug("GameAPI started successfully!");
	}

	public void loadAbilities(String packageName) {
		for (Class<?> commandClass : ClassGetter.getClassesForPackage(getClass(), packageName))
			if (Ability.class.isAssignableFrom(commandClass)) {
				try {
					Ability ability = (Ability) commandClass.newInstance();
					getAbilityManager().loadAbility(ability.getName(), ability);
				} catch (Exception ex) {
					CommonPlugin.getInstance().getLogger()
							.warning("Error when loading command from " + commandClass.getSimpleName() + "!");
					ex.printStackTrace();
				}
			}
	}

	public void setState(MinigameState minigameState) {
		System.out.println(CommonPlugin.getInstance().getMinigameState() + " > " + minigameState);
		CommonPlugin.getInstance().setMinigameState(minigameState);
		CommonPlugin.getInstance().getServerData().updateStatus();
	}

	public void setState(MinigameState minigameState, int time) {
		System.out.println(CommonPlugin.getInstance().getMinigameState() + " > " + minigameState);
		CommonPlugin.getInstance().setMinigameState(minigameState);
		CommonPlugin.getInstance().setServerTime(time);
		CommonPlugin.getInstance().getServerData().updateStatus();
	}

	public void setMap(String map) {
		System.out.println(CommonPlugin.getInstance().getMap() + " > " + map);
		CommonPlugin.getInstance().setMap(map);
		CommonPlugin.getInstance().getServerData().updateStatus();
	}

	public void setTime(int time) {
		CommonPlugin.getInstance().setServerTime(time);
		CommonPlugin.getInstance().getServerData().updateStatus();
	}

	public int getTime() {
		return CommonPlugin.getInstance().getServerTime();
	}

	public MinigameState getState() {
		return CommonPlugin.getInstance().getMinigameState();
	}

	public int getMaxAbilities() {
		return maxAbilities;
	}

	private void loadConfiguration() {
		this.configuration = CommonPlugin.getInstance()
				.getConfigurationManager()
				.loadConfig("hg.json", Paths.get(this.getDataFolder().toURI()).getParent().getParent().toFile(), true, JsonConfiguration.class);

		try {
			this.configuration.loadConfig();
		} catch (Exception var2) {
			var2.printStackTrace();
			this.getPlugin().getPluginPlatform().shutdown("Cannot load the configuration hg.json.");
			return;
		}

		this.setMap(this.configuration.get("mapName", "Unknown"));
		this.maxAbilities = this.configuration.get("maxAbilities", 1);
		this.maxMembers = this.configuration.get("maxMembers", 1);
		this.debug("The configuration hg.json has been loaded!");
	}

	public void setMaxAbilities(int maxAbilities) {
		this.maxAbilities = maxAbilities;
		this.configuration.set("maxAbilities", maxAbilities);

		try {
			this.configuration.saveConfig();
		} catch (Exception var4) {
			var4.printStackTrace();
		}
	}

	public void setMaxMembers(int maxMembers) {
		this.maxMembers = maxMembers;
		this.configuration.set("maxMembers", maxMembers);

		try {
			this.configuration.saveConfig();
		} catch (Exception var4) {
			var4.printStackTrace();
		}
	}

	public abstract void startGame();

}
