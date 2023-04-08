package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.plutomc.pvp.arena.kit.Kit;
import org.bukkit.Material;

public class PvpKit extends Kit {
   public PvpKit() {
      super("PvP", "Kit padrão sem nenhuma habilidade!", Material.DIAMOND_SWORD, 0, new ArrayList<>());
   }
}
