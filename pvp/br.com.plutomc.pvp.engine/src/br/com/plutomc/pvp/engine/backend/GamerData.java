package br.com.plutomc.pvp.engine.backend;

import java.util.UUID;
import br.com.plutomc.pvp.engine.gamer.Gamer;

public interface GamerData {
   <T extends Gamer> T loadGamer(UUID var1, Class<T> var2);

   boolean deleteGamer(UUID var1);

   boolean createGamer(Gamer var1);
}
