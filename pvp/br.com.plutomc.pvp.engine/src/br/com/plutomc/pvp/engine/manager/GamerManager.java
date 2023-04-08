package br.com.plutomc.pvp.engine.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.pvp.engine.gamer.Gamer;

public class GamerManager {
   private Map<UUID, Gamer> gamers = new HashMap<>();

   public void loadGamer(Gamer gamer) {
      this.gamers.put(gamer.getUniqueId(), gamer);
      CommonPlugin.getInstance().debug("The gamer " + gamer.getUniqueId() + " has been loaded.");
   }

   public <T extends Gamer> T getGamer(UUID uuid, Class<T> clazz) {
      return clazz.cast(this.gamers.get(uuid));
   }

   public Gamer getGamer(UUID uuid) {
      return this.gamers.get(uuid);
   }

   public Collection<Gamer> getGamers() {
      return this.gamers.values();
   }

   public void unloadGamer(UUID uuid) {
      this.gamers.remove(uuid);
   }
}
