package br.com.plutomc.game.bedwars.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.plutomc.game.bedwars.GameMain;
import br.com.plutomc.game.bedwars.gamer.Gamer;
import br.com.plutomc.game.bedwars.generator.Generator;
import br.com.plutomc.game.engine.GameAPI;
import br.com.plutomc.game.bedwars.island.Island;
import br.com.plutomc.game.bedwars.island.IslandColor;
import br.com.plutomc.game.engine.gamer.Team;
import br.com.plutomc.core.common.member.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class IslandManager {
   private Map<IslandColor, Island> islandMap = new HashMap<>();
   private Map<UUID, IslandColor> playerMap = new HashMap<>();

   public Collection<Island> loadIsland() {
      List<Island> islandList = new ArrayList<>(GameMain.getInstance().getConfiguration().getList("islands", Island.class));
      List<Gamer> playerList = GameAPI.getInstance()
         .getGamerManager()
         .getGamers(Gamer.class)
         .stream()
         .filter(gamer -> gamer.isAlive() && gamer.getPlayer() != null)
         .sorted((o1, o2) -> {
            Party o1Party = GameAPI.getInstance().getPlugin().getPartyManager().getPartyById(o1.getUniqueId());
            Party o2Party = GameAPI.getInstance().getPlugin().getPartyManager().getPartyById(o2.getUniqueId());
            return o1Party != null && o2Party != null ? o1Party.getPartyId().compareTo(o2Party.getPartyId()) : 0;
         })
         .collect(Collectors.toList());
      List<Team> teamList = new ArrayList<>();

      for(int i = 0; i < GameMain.getInstance().getMaxTeams(); ++i) {
         teamList.add(new Team(i, GameMain.getInstance().getPlayersPerTeam()));
      }

      boolean stop = true;

      for(Team team : teamList) {
         if (!team.isFull()) {
            for(int i = 0; i < GameMain.getInstance().getPlayersPerTeam(); ++i) {
               Gamer player = playerList.stream().findFirst().orElse(null);
               if (player != null) {
                  playerList.remove(player);
                  team.addPlayer(player.getUniqueId());
               }
            }

            if (team.getPlayerSet().size() >= 1) {
               stop = false;
            }

            Island island = islandList.stream().findAny().orElse(null);
            if (island == null) {
               team.getPlayerSet().stream().map(id -> GameAPI.getInstance().getGamerManager().getGamer(id)).forEach(gamer -> {
                  if (gamer.getPlayer() != null) {
                     gamer.getPlayer().kickPlayer("§%bedwars.kick.island-not-found%§");
                  }

                  GameAPI.getInstance().getGamerManager().unloadGamer(gamer.getUniqueId());
               });
            } else {
               island.loadIsland(team);
               island.getTeam().getPlayerSet().forEach(id -> {
               });
               this.islandMap.put(island.getIslandColor(), island);
               islandList.remove(island);
            }
         }
      }

      if (stop) {
         Bukkit.shutdown();
      }

      return this.islandMap.values();
   }

   public Island getIsland(IslandColor islandColor) {
      return this.islandMap.get(islandColor);
   }

   public Island getIsland(UUID uniqueId) {
      return this.playerMap.containsKey(uniqueId) ? this.islandMap.get(this.playerMap.get(uniqueId)) : null;
   }

   public Collection<Island> values() {
      return this.islandMap.values();
   }

   public Island getClosestIsland(Location location) {
      return this.islandMap
         .values()
         .stream()
         .sorted((o1, o2) -> (int)(o1.getSpawnLocation().getAsLocation().distance(location) - o2.getSpawnLocation().getAsLocation().distance(location)))
         .findFirst()
         .orElse(null);
   }

   public Collection<Island> getIslands() {
      return this.islandMap.values();
   }

   public Optional<Generator> getClosestGenerator(Location location) {
      return GameMain.getInstance().getIslandManager().getClosestIsland(location).getIslandGenerators().stream().findFirst();
   }
}
