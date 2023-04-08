package br.com.plutomc.pvp.engine.backend.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import br.com.plutomc.pvp.engine.backend.GamerData;
import br.com.plutomc.pvp.engine.gamer.Gamer;

public class VoidGamerData implements GamerData {
   @Override
   public <T extends Gamer> T loadGamer(UUID uniqueId, Class<T> clazz) {
      try {
         return clazz.getConstructor(UUID.class).newInstance(uniqueId);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   @Override
   public boolean deleteGamer(UUID uniqueId) {
      return true;
   }

   @Override
   public boolean createGamer(Gamer gamer) {
      return true;
   }
}
