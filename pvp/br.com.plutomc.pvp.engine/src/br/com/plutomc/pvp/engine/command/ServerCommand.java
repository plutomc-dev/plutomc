package br.com.plutomc.pvp.engine.command;

import br.com.plutomc.pvp.engine.GameAPI;
import br.com.plutomc.pvp.engine.event.PlayerSpawnEvent;
import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.core.bukkit.member.BukkitMember;
import br.com.plutomc.pvp.engine.event.PlayerProtectionEvent;
import br.com.plutomc.core.common.command.CommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework;
import br.com.plutomc.core.common.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerCommand implements CommandClass {
   @CommandFramework.Command(
      name = "spawn"
   )
   public void spawnCommand(CommandArgs cmdArgs) {
      if (cmdArgs.isPlayer()) {
         Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
         Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(player));
         player.teleport(BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
         player.sendMessage("§aTeletransportado para o spawn.");
         GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).setSpawnProtection(true);
         Bukkit.getPluginManager().callEvent(new PlayerProtectionEvent(player, true));
      }
   }

   @CommandFramework.Command(
      name = "setfulliron",
      permission = "command.setfulliron"
   )
   public void setfullironCommand(CommandArgs cmdArgs) {
      CommandSender sender = cmdArgs.getSender();
      GameAPI.getInstance().setFullIron(!GameAPI.getInstance().isFullIron());
      sender.sendMessage("§aO modo do servidor foi alterado para " + (GameAPI.getInstance().isFullIron() ? "FullIron" : "Simulator") + ".");
   }

   @CommandFramework.Command(
      name = "setprotection",
      permission = "command.setprotection"
   )
   public void setprotectionCommand(CommandArgs cmdArgs) {
      CommandSender sender = cmdArgs.getSender();
      String[] args = cmdArgs.getArgs();
      if (args.length == 0) {
         sender.sendMessage(" §e» §fUse §a/" + cmdArgs.getLabel() + " <radius>§f para alterar o raio de proteção do spawn.");
      } else {
         Double integer = null;

         try {
            integer = Double.valueOf(args[0]);
         } catch (NumberFormatException var6) {
         }

         GameAPI.getInstance().setProtectionRadius(integer);
         sender.sendMessage("§aO raio de proteção do spawn foi alterado para " + CommonConst.DECIMAL_FORMAT.format(integer) + ".");
      }
   }
}
