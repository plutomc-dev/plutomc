package br.com.plutomc.hungergames.engine.command;

import br.com.plutomc.core.bukkit.member.BukkitMember;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.command.CommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework.Command;
import br.com.plutomc.core.common.utils.string.MessageBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.team.Team;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class GameCommand implements CommandClass {

	private Map<UUID, Map<UUID, ActionHandler>> inviteMap = new HashMap<>();

	@Command(name = "time", aliases = { "dupla", "team" })
	public void timeCommand(CommandArgs cmdArgs) {
		String[] args = cmdArgs.getArgs();
		Player sender = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();

		if (args.length == 0) {
			sender.sendMessage(
					"§eUse §b/" + cmdArgs.getLabel() + " convidar <player>§e para convidar alguém para seu time.");
			sender.sendMessage("§eUse §b/" + cmdArgs.getLabel() + " sair§e para sair do time.");

			if (GameAPI.getInstance().getTeamManager().hasTeam(sender.getUniqueId())) {
				Team team = GameAPI.getInstance().getTeamManager().getTeam(sender.getUniqueId());

				sender.sendMessage(" §a");
				sender.sendMessage(" §aSeu time:");
				sender.sendMessage("   §fParticipantes: §7"
						+ Joiner.on(", ").join(team.getGamerList().stream().filter(id -> Bukkit.getPlayer(id) != null)
								.map(id -> Bukkit.getPlayer(id).getName()).toArray(String[]::new)));
				sender.sendMessage(" ");
			}
			return;
		}

		switch (args[0].toLowerCase()) {
		case "leave":
		case "sair": {
			if (GameAPI.getInstance().getTeamManager().hasTeam(sender.getUniqueId())) {
				Team team = GameAPI.getInstance().getTeamManager().getTeam(sender.getUniqueId());

				if (team.getTeamLeader() == sender.getUniqueId()) {
					team.sendMessage("§cO time foi desfeito.");
					GameAPI.getInstance().getTeamManager().removeTeam(sender.getUniqueId());
				} else {
					team.sendMessage("§c" + sender.getName() + " saiu do time.");
					GameAPI.getInstance().getTeamManager().removeGamerToTeam(sender.getPlayer(), team);
				}
			} else
				sender.sendMessage("§cVocê não tem time!");
			break;
		}
		case "aceitar": {
			UUID teamId = null;

			if (args.length == 1) {
				if (inviteMap.containsKey(sender.getUniqueId())) {

					Entry<UUID, ActionHandler> orElse = inviteMap.get(sender.getUniqueId()).entrySet().stream()
							.findFirst().orElse(null);

					if (orElse == null) {
						sender.sendMessage("§cNenhum convite para aceitar.");
						inviteMap.remove(sender.getUniqueId());
						return;
					}

					teamId = orElse.getKey();
				}

			} else {
				Player target = Bukkit.getPlayer(args[2]);

				if (target == null) {
					sender.sendMessage("§cO jogador não está online!");
					return;
				}

				if (target == sender) {
					sender.sendMessage("§cVocê não pode aceitar um convite de você mesmo.");
					return;
				}

				teamId = target.getUniqueId();

				if (!inviteMap.containsKey(sender.getUniqueId())
						|| !inviteMap.get(sender.getUniqueId()).containsKey(teamId)) {
					sender.sendMessage("§cO jogador " + target.getName() + " não te enviou solicitações de time.");
					return;
				}
			}

			Team team = GameAPI.getInstance().getTeamManager().getTeam(teamId);

			if (team == null) {
				sender.sendMessage("§cEsse time não existe mais.");
				inviteMap.remove(sender.getUniqueId());
				return;
			}

			if (team.getGamerList().size() + 1 > GameAPI.getInstance().getMaxMembers())
				sender.sendMessage("§cO time está cheio.");
			else
				inviteMap.get(sender.getUniqueId()).get(teamId).accept();

			inviteMap.remove(sender.getUniqueId());
			break;
		}
		case "invite":
		case "convidar":
		default: {
			Player target = Bukkit.getPlayer(args.length >= 2 ? args[1] : args[0]);

			if (target == null) {
				sender.sendMessage("§cO jogador não está online!");
				return;
			}

			if (target == sender) {
				sender.sendMessage("§cVocê não pode se convidar para um time.");
				return;
			}

			if (!CommonPlugin.getInstance().getMemberManager().getMember(target.getUniqueId()).getMemberConfiguration()
					.isPartyInvites()) {
				sender.sendMessage("§cO jogador não está aceitando convites de time.");
				return;
			}

			if (GameAPI.getInstance().getTeamManager().hasTeam(sender.getUniqueId())) {
				sender.sendMessage("§eUse §b/" + cmdArgs.getLabel() + " remove§e para remover sua dupla.");
				return;
			}

			if (GameAPI.getInstance().getTeamManager().hasTeam(target.getUniqueId())) {
				sender.sendMessage("§cO jogador já está em um time.");
				return;
			}

			Map<UUID, ActionHandler> map = inviteMap.computeIfAbsent(target.getUniqueId(), v -> new HashMap<>());

			if (map.containsKey(sender.getUniqueId()))
				sender.sendMessage("§cVocê já convidou esse jogador.");
			else {
				Team t = GameAPI.getInstance().getTeamManager().getTeam(sender.getUniqueId());

				if (t == null) {
					t = GameAPI.getInstance().getTeamManager().createTeam(new Team(sender.getPlayer()));
					t.sendMessage("§aO time foi criado.");
				}

				final Team team = t;

				map.put(sender.getUniqueId(), () -> {
					GameAPI.getInstance().getTeamManager().addGamerToTeam(target, team);
					team.sendMessage("§a" + target.getName() + " entrou no time.");
				});

				sender.sendMessage("§aVocê convidou o " + target.getName() + " para seu time.");
				target.sendMessage(new MessageBuilder("§aVocê foi convidado para o time de " + sender.getName() + ".")
						.setHoverEvent("§aClique para aceitar.")
						.setClickEvent("/" + cmdArgs.getLabel() + " aceitar " + sender.getName()).create().getText());
			}
			break;
		}
		}
	}

	public interface ActionHandler {

		void accept();

	}

}
