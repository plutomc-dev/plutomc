package br.com.plutomc.hungergames.engine.team;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Team {
	
	private UUID teamLeader;
	
	private List<UUID> gamerList;
	
	public Team(Player leader) {
		this.teamLeader = leader.getUniqueId();
		this.gamerList = new ArrayList<>();
		this.gamerList.add(leader.getUniqueId());
	}

	public void addPlayerToTeam(UUID gamer) {
		this.gamerList.add(gamer);
	}
	
	public void removePlayerToTeam(UUID gamer) {
		this.gamerList.remove(gamer);
	}
	
	public UUID getTeamId() {
		return teamLeader;
	}

	public void sendMessage(String string) {
		gamerList.stream().filter(id -> Bukkit.getPlayer(id) != null).forEach(id -> Bukkit.getPlayer(id).sendMessage(string));
	}
}
