package br.com.plutomc.duels.nodebuff.command;

import br.com.plutomc.core.bukkit.command.BukkitCommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework;
import br.com.plutomc.duels.nodebuff.GameMain;

public class ModeratorCommand implements CommandClass {
    @CommandFramework.Command(
            name = "start",
            permission = "command.start"
    )
    public void startCommand(BukkitCommandArgs cmdArgs) {
        if (GameMain.getInstance().getState().isPregame()) {
            GameMain.getInstance().setTimer(true);
            GameMain.getInstance().startGame();
        } else {
            cmdArgs.getSender().sendMessage("§cA partida já iniciou.");
        }
    }
}
