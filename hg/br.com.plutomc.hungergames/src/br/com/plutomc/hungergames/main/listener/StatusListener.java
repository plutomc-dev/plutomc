package br.com.plutomc.hungergames.main.listener;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.member.Member;
import br.com.plutomc.core.common.member.status.StatusType;
import br.com.plutomc.core.common.member.status.types.HungerGamesCategory;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.event.player.GamerWinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatusListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());
		Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (player.getKiller() instanceof Player) {
			Player killer = player.getKiller();
			Gamer killerGamer = GameAPI.getInstance().getGamerManager().getGamer(killer.getUniqueId());
			Member killerMember = CommonPlugin.getInstance().getMemberManager().getMember(killer.getUniqueId());

			CommonPlugin.getInstance().getStatusManager()
					.loadStatus(killer.getUniqueId(), StatusType.HG).addInteger(HungerGamesCategory.KILL, 1);
		}

		CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.HG)
				.addInteger(HungerGamesCategory.DEATH, 1);
		CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.HG)
				.addInteger(HungerGamesCategory.LOSS, 1);
	}

	@EventHandler
	public void onGamerWin(GamerWinEvent event) {
		CommonPlugin.getInstance().getStatusManager()
				.loadStatus(event.getGamer().getUniqueId(), StatusType.HG).addInteger(HungerGamesCategory.WIN, 1);
		//CommonPlugin.getInstance().getMemberManager().getMember(event.getGamer().getUniqueId()).addXp(LeagueType.HG, 40);
	}

	/*public int getRewardPerKill(Member member, Gamer gamer, Member killer, Gamer killerGamer) {
		double xpPerMinute = ((GameAPI.getInstance().getTime() - 120) / 120) * 0.65;
		double xpDifferenceMultiplier = member.getLeagueInfo(LeagueType.HG).getTotalXp() / killer.getLeagueInfo(LeagueType.HG).getTotalXp();

		if (xpDifferenceMultiplier > 3)
			xpDifferenceMultiplier = 3;

		if (xpDifferenceMultiplier < 1)
			xpDifferenceMultiplier = 1;

		int kills = killerGamer.getKills() / 2;
		int xp = (int) (xpPerMinute + (xpDifferenceMultiplier * 2.1) + kills);
		return xp;
	} */

	/*public int getLostPerDeath(Member member, Gamer gamer) {
		int kills = gamer.getKills();
		int xp = (int) (12 - (kills * 1.1));

		return Math.max(xp, 3);
	} */
}