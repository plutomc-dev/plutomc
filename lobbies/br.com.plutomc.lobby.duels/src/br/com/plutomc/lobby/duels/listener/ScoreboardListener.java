package br.com.plutomc.lobby.duels.listener;

import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.core.bukkit.event.member.PlayerGroupChangeEvent;
import br.com.plutomc.core.bukkit.event.member.PlayerLanguageChangeEvent;
import br.com.plutomc.core.bukkit.event.server.PlayerChangeEvent;
import br.com.plutomc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.plutomc.core.bukkit.utils.scoreboard.Scoreboard;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.Member;
import br.com.plutomc.core.common.member.status.Status;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.GappleCategory;
import br.com.plutomc.core.common.member.status.types.BoxingCategory;
import br.com.plutomc.core.common.permission.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {
    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handleScoreboard(event.getPlayer());
        this.updateScoreboard(event.getPlayer());
        this.updatePlayers();
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.updatePlayers();
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerChange(PlayerChangeEvent event) {
        this.updatePlayers();
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent event) {
        ScoreHelper.getInstance().removeScoreboard(event.getPlayer());
        this.handleScoreboard(event.getPlayer());
        this.updateScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGroupChange(PlayerGroupChangeEvent event) {
        this.updateScoreboard(event.getPlayer());
    }

    private void handleScoreboard(Player player) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        Status statusSumo = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.DUEL);
        Status statusGapple = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.DUEL);
        Scoreboard scoreboard = new Scoreboard(player, "§b§lDUELS");
        scoreboard.add(11, "");
        scoreboard.add(10, "§eGapple:");
        scoreboard.add(9, " Wins: §a" + statusGapple.getInteger(GappleCategory.GAPPLE_WINS));
        scoreboard.add(8, " Winstreak: §a"+ statusGapple.getInteger(GappleCategory.GAPPLE_WINSTREAK));
        scoreboard.add(7, "§eBoxing:");
        scoreboard.add(6, " Wins: §a" + statusSumo.getInteger(BoxingCategory.BOXING_WINS));
        scoreboard.add(5, " Winstreak: §a" + statusSumo.getInteger(BoxingCategory.BOXING_WINSTREAK));
        scoreboard.add(4, "");
        scoreboard.add(3, "§f§%scoreboard-players%§: §b" + BukkitCommon.getInstance().getServerManager().getTotalMembers());
        scoreboard.add(2, "§a");
        scoreboard.add(1, "§a" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        ScoreHelper.getInstance().setScoreboard(player, scoreboard);
    }

    private void updateScoreboard(Player player) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        Tag tag = CommonPlugin.getInstance().getPluginInfo().getTagByGroup(member.getServerGroup());
    }

    private void updatePlayers() {
        ScoreHelper.getInstance().updateScoreboard(3, "§f§%scoreboard-players%§: §b" + BukkitCommon.getInstance().getServerManager().getTotalMembers());
    }
}