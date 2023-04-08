package br.com.plutomc.pvp.arena.command;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.member.BukkitMember;
import br.com.plutomc.pvp.arena.event.PlayerSelectedKitEvent;
import br.com.plutomc.pvp.arena.gamer.Gamer;
import br.com.plutomc.pvp.arena.menu.AbilityInventory;
import br.com.plutomc.core.bukkit.utils.player.PlayerHelper;
import br.com.plutomc.core.common.command.CommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KitCommand implements CommandClass {
   @CommandFramework.Command(
      name = "kit"
   )
   public void kitCommand(CommandArgs cmdArgs) {
      if (cmdArgs.isPlayer()) {
         String[] args = cmdArgs.getArgs();
         Player player = ((BukkitMember)cmdArgs.getSender()).getPlayer();
         Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
         if (!gamer.isSpawnProtection()) {
            player.sendMessage("§cVocê não pode usar kits fora da área de proteção!");
         } else if (args.length == 0) {
            player.sendMessage("§eUse /" + cmdArgs.getLabel() + " para selecionar um kit");
         } else {
            Kit kit = GameMain.getInstance().getKitManager().getKit(args[0]);
            if (kit == null) {
               player.sendMessage("§cO kit " + args[0] + " não existe!");
            } else {
               AbilityInventory.InventoryType inventoryType = args.length >= 2
                  ? (args[1].equalsIgnoreCase("1") ? AbilityInventory.InventoryType.PRIMARY : AbilityInventory.InventoryType.SECONDARY)
                  : AbilityInventory.InventoryType.PRIMARY;
               if ((inventoryType == AbilityInventory.InventoryType.PRIMARY ? gamer.getSecondary() : gamer.getPrimary()).equalsIgnoreCase(kit.getName())) {
                  player.sendMessage("§cVocê já está usando esse kit!");
               } else {
                  if (inventoryType == AbilityInventory.InventoryType.PRIMARY) {
                     gamer.setPrimaryKit(kit);
                  } else {
                     gamer.setSecondaryKit(kit);
                  }

                  Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(player, kit, inventoryType));
                  player.sendMessage("§aVocê selecionou o kit " + kit.getName());
                  PlayerHelper.title(player, "§a" + kit.getName(), "§fselecionado!");
                  player.closeInventory();
               }
            }
         }
      }
   }
}
