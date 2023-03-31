package br.com.plutomc.lobby.bedwars;

import br.com.plutomc.core.bukkit.utils.character.handler.ActionHandler;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.lobby.bedwars.listener.ScoreboardListener;
import br.com.plutomc.lobby.core.CoreMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class LobbyMain extends CoreMain {

    private static LobbyMain instance;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();

        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);

        this.createCharacter(
                "npc-solo",
                "Abodicom4You",
                new ActionHandler() {
                    @Override
                    public boolean onInteract(Player player, boolean right) {
                        LobbyMain.this.sendPlayerToServer(player, new ServerType[]{ServerType.BW_SOLO});
                        return false;
                    }
                },
                Arrays.asList(
                        ServerType.BW_LOBBY, ServerType.BW_SOLO, ServerType.BW_DUOS, ServerType.BW_TRIO, ServerType.BW_SQUAD, ServerType.BW_1X1, ServerType.BW_2X2
                ),
                new String[]{"§bSolo"}
        );
        this.createCharacter(
                "npc-duo",
                "Abodicom4You",
                new ActionHandler() {
                    @Override
                    public boolean onInteract(Player player, boolean right) {
                        LobbyMain.this.sendPlayerToServer(player, new ServerType[]{ServerType.BW_DUOS});
                        return false;
                    }
                },
                Arrays.asList(
                        ServerType.BW_LOBBY, ServerType.BW_SOLO, ServerType.BW_DUOS, ServerType.BW_TRIO, ServerType.BW_SQUAD, ServerType.BW_1X1, ServerType.BW_2X2
                ),
                new String[]{"§bSolo"}
        );
    }

    public static LobbyMain getInstance() {
        return instance;
    }
}
