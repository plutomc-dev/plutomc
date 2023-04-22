package br.com.plutomc.hungergames.main.event;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.bukkit.event.NormalEvent;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;

public class GameEvent extends NormalEvent {

	public MinigameState getState() {
		return CommonPlugin.getInstance().getMinigameState();
	}

	public int getTime() {
		return CommonPlugin.getInstance().getServerTime();
	}

}
