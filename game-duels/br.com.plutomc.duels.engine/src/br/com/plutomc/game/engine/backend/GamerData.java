package br.com.plutomc.game.engine.backend;

import br.com.plutomc.game.engine.gamer.Gamer;

import java.util.Optional;
import java.util.UUID;

public interface GamerData {
   <T extends Gamer> Optional<T> loadGamer(UUID var1);

   void createGamer(Gamer var1);

   void saveGamer(Gamer var1, String var2);
}
