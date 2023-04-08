package br.com.plutomc.pvp.arena.event;

import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.event.PlayerCancellableEvent;
import br.com.plutomc.pvp.arena.menu.AbilityInventory;
import org.bukkit.entity.Player;

public class PlayerSelectedKitEvent extends PlayerCancellableEvent {
   private Kit kit;
   private AbilityInventory.InventoryType inventoryType;

   public PlayerSelectedKitEvent(Player player, Kit kit, AbilityInventory.InventoryType inventoryType) {
      super(player);
      this.kit = kit;
      this.inventoryType = inventoryType;
   }

   public Kit getKit() {
      return this.kit;
   }

   public AbilityInventory.InventoryType getInventoryType() {
      return this.inventoryType;
   }
}
