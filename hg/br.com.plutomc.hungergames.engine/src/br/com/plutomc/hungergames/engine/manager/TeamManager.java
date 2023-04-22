package br.com.plutomc.hungergames.engine.manager;

import br.com.plutomc.hungergames.engine.team.Team;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager {

	private Map<UUID, Team> teamMap;
	private Map<UUID, UUID> gamerMap;

	public TeamManager() {
		teamMap = new HashMap<>();
		gamerMap = new HashMap<>();
	}

	public Team createTeam(Team team) {
		gamerMap.put(team.getTeamId(), team.getTeamId());
		teamMap.put(team.getTeamId(), team);
		return team;
	}

	public void removeTeam(UUID teamId) {
		if (teamMap.containsKey(teamId)) {
			Team team = teamMap.get(teamId);

			team.getGamerList().forEach(gamer -> {
				gamerMap.remove(gamer);
			});
		}
	}

	public boolean addGamerToTeam(Player player, Team team) {
		if (hasTeam(player.getUniqueId()))
			return false;
		
		team.addPlayerToTeam(player.getUniqueId());;
		gamerMap.put(player.getUniqueId(), team.getTeamId());
		return true;
	}

	public boolean removeGamerToTeam(Player player, Team team) {
		if (hasTeam(player.getUniqueId()) && team.getGamerList()
				.stream().filter(gamer -> gamer == player.getUniqueId()).findFirst().isPresent()) {
			team.removePlayerToTeam(player.getUniqueId());
			gamerMap.remove(player.getUniqueId());
			return true;
		}
		
		return false;
	}
	
	public boolean hasTeam(UUID uniqueId) {
		return gamerMap.containsKey(uniqueId);
	}

	public Team getTeam(UUID uniqueId) {
		return gamerMap.containsKey(uniqueId) ? teamMap.get(gamerMap.get(uniqueId)) : null;
	}

}
