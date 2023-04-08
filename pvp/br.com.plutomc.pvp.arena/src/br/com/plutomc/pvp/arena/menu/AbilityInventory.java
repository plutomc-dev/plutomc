package br.com.plutomc.pvp.arena.menu;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.utils.menu.MenuInventory;
import org.bukkit.entity.Player;

public class AbilityInventory {
   public AbilityInventory(Player player, InventoryType selectClass) {
      MenuInventory menuInventory = new MenuInventory("§7§nSelecionar kit", 6);
      int slot = 10;

      for(Kit kit : GameMain.getInstance().getKitManager().getKitList()) {
         menuInventory.setItem(
            slot,
            new ItemBuilder().name("§a" + kit.getKitName()).lore("§7" + kit.getKitDescription()).type(kit.getKitType()).build(),
            (p, inv, type, stack, s) -> player.performCommand(
                  "kit " + kit.getName() + " " + (selectClass == InventoryType.PRIMARY ? "1" : "2")
               )
         );
         if (slot % 9 == 7) {
            slot += 3;
         } else {
            ++slot;
         }
      }

      menuInventory.open(player);
   }

   public static enum InventoryType {
      PRIMARY,
      SECONDARY;
   }
}
