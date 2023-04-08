package br.com.plutomc.pvp.engine.listener;

import br.com.plutomc.pvp.engine.GameAPI;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.pvp.engine.gamer.Gamer;
import br.com.plutomc.core.common.member.status.StatusType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class GamerListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
      if (event.getLoginResult() == Result.ALLOWED) {
         Gamer gamer = GameAPI.getInstance().getGamerData().loadGamer(event.getUniqueId(), GameAPI.getInstance().getGamerClass());
         if (gamer == null) {
            event.disallow(Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("gamer-not-loaded") + " [0]");
         } else {
            GameAPI.getInstance().getGamerManager().loadGamer(gamer);
            CommonPlugin.getInstance().getStatusManager().loadStatus(gamer.getUniqueId(), StatusType.PVP);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerLogin(PlayerLoginEvent event) {
      if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
         if (GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()) == null) {
            event.disallow(
               PlayerLoginEvent.Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("gamer-not-loaded") + " [1]"
            );
         }
      }
   }
}
