package br.com.plutomc.game.engine.listener;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.game.engine.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SchedulerListener implements Listener {
   @EventHandler
   public void update(UpdateEvent event) {
      if (event.getType() == UpdateEvent.UpdateType.SECOND) {
         GameAPI.getInstance().getSchedulerManager().pulse();
      }
   }
}
