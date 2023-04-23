package br.com.plutomc.hungergames.engine;

import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.ClassGetter;
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



	public abstract void startGame();

}
