package br.com.plutomc.hungergames.main.stages;

import br.com.plutomc.hungergames.main.HardcoreMain;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Schedule;
import br.com.plutomc.hungergames.main.manager.GameHelper;

public class InvencibilitySchedule implements Schedule {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void pulse() {
		int time = CommonPlugin.getInstance().getServerTime();

		if (!GameAPI.getInstance().getState().isInvencibility()) {
			GameAPI.getInstance().getScheduleManager().stopSchedule(this);
			return;
		}

		if (time == 0) {
			GameAPI.getInstance().setState(MinigameState.GAMETIME,
					HardcoreMain.getInstance().getStartTime());
			Bukkit.broadcastMessage("§cA invencibilidade acabou!");
			Bukkit.getOnlinePlayers()
					.forEach(player -> player.playSound(player.getLocation(), Sound.ANVIL_USE, 100, 100));
			GameAPI.getInstance().getScheduleManager().stopSchedule(this);
			GameAPI.getInstance().getScheduleManager().startSchedule(new GameSchedule());
			GameHelper.checkWinner();
			
			Bukkit.getWorlds().forEach(world -> ((CraftWorld) world).getHandle().savingDisabled = false);
			return;
		}

		if ((time > 0 && time <= 5) || time == 10 || time % 30 == 0) {
			Bukkit.broadcastMessage("§cA invencibilidade irá acabar em " + StringFormat.formatTime(time) + ".");
			Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.CLICK, 100, 100));
		}

		GameAPI.getInstance().setTime(time - 1);
	}

}
