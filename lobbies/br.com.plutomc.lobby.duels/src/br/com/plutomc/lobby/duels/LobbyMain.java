package br.com.plutomc.lobby.duels;

import br.com.plutomc.core.bukkit.utils.character.handler.ActionHandler;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.lobby.core.CoreMain;
import br.com.plutomc.lobby.duels.listener.ScoreboardListener;
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
                "npc-gapple",
                "stopeey",
                new ActionHandler() {
                    @Override
                    public boolean onInteract(Player player, boolean right) {
                        LobbyMain.this.sendPlayerToServer(player, new ServerType[]{ServerType.DUELS_GAPPLE});
                        return false;
                    }
                },
                Arrays.asList(
                        ServerType.DUELS_GAPPLE
                ),
                "§bGapple");

        this.createCharacter(
                "npc-sumo",
                "unidade",
                new ActionHandler() {
                    @Override
                    public boolean onInteract(Player player, boolean right) {
                        LobbyMain.this.sendPlayerToServer(player, new ServerType[]{ServerType.DUELS_SUMO});
                        return false;
                    }
                },
                Arrays.asList(
                        ServerType.DUELS_SUMO
                ),
                "§bSumo");

        this.createCharacter(
                "npc-nodebuff",
                "TheComicON",
                new ActionHandler() {
                    @Override
                    public boolean onInteract(Player player, boolean right) {
                        LobbyMain.this.sendPlayerToServer(player, new ServerType[]{ServerType.DUELS_NODEBUFF});
                        return false;
                    }
                },
                Arrays.asList(
                        ServerType.DUELS_NODEBUFF
                ),
                "§bNoDebuff");

        this.createCharacter(
                "npc-simulator",
                "staack",
                new ActionHandler() {
                    @Override
                    public boolean onInteract(Player player, boolean right) {
                        LobbyMain.this.sendPlayerToServer(player, new ServerType[]{ServerType.DUELS_SIMULATOR});
                        return false;
                    }
                },
                Arrays.asList(
                        ServerType.DUELS_SIMULATOR
                ),
                "§bNoDebuff");

    }

    public static LobbyMain getInstance() {
        return instance;
    }

}
