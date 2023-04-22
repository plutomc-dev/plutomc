package br.com.plutomc.hungergames.engine.listener;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.hungergames.engine.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScheduleListener implements Listener {

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateEvent.UpdateType.SECOND && GameAPI.getInstance().isTimerEnabled())
			pulse();
	}

	private void pulse() {
		GameAPI.getInstance().getScheduleManager().pulse();
	}

}
