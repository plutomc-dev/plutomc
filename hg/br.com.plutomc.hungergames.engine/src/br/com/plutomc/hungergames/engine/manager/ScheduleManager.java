package br.com.plutomc.hungergames.engine.manager;

import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.hungergames.engine.game.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduleManager {

	private List<Schedule> scheduleList;

	public ScheduleManager() {
		scheduleList = new ArrayList<>();
	}

	public void startSchedule(Schedule schedule) {
		if (scheduleList.contains(schedule))
			return;
		
		scheduleList.add(schedule);
		Bukkit.getPluginManager().registerEvents(schedule, BukkitCommon.getInstance());
	}

	public void stopSchedule(Schedule schedule) {
		scheduleList.remove(schedule);
		HandlerList.unregisterAll(schedule);
	}

	public void pulse() {
		Iterator<Schedule> iterator = scheduleList.iterator();
		
		while (iterator.hasNext())
			iterator.next().pulse();
	}

}
