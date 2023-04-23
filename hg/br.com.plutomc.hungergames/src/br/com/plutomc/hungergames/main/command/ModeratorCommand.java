package br.com.plutomc.hungergames.main.command;

import br.com.plutomc.core.bukkit.member.BukkitMember;
import br.com.plutomc.core.common.command.CommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework.Command;
import br.com.plutomc.core.common.command.CommandSender;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.main.HardcoreMain;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import br.com.plutomc.hungergames.main.manager.SimplekitManager;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModeratorCommand implements CommandClass {

	@Command(name = "toggleability", permission = "command.hg.togglekit")
	public void togglekitCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§eUse /" + cmdArgs.getLabel() + " <kit> <on:off> para ativar ou desativar um kit.");
			return;
		}

		List<Ability> abilityList = new ArrayList<>();

		if (args[0].equalsIgnoreCase("all"))
			abilityList.addAll(GameAPI.getInstance().getAbilityManager().getAbilities());
		else
			for (String abilityName : args[0].split(",")) {
				Ability ability = GameAPI.getInstance().getAbilityManager().getAbility(abilityName);

				if (ability == null) {
					sender.sendMessage("§cO kit " + args[0] + " não existe!");
					return;
				}

				abilityList.add(ability);
			}

		if (abilityList.isEmpty()) {
			sender.sendMessage("§cO kit " + args[0] + " não existe!");
			return;
		}

		boolean enabled = args[1].equalsIgnoreCase("on");
		String abilities = Joiner.on(", ").join(abilityList.stream().map(Ability::getName).toArray());

		sender.sendMessage(abilityList.size() == 1
				? enabled ? "§aO kit " + abilities + " foi ativado!" : "§cO kit " + abilities + " foi desativado!"
				: enabled ? "§aOs kits " + abilities + " foram ativados!"
				: "§cOs kits " + abilities + " foram desativados!");

		for (Ability ability : abilityList) {
			ability.setAbilityEnabled(enabled);

			if (!HardcoreMain.getInstance().getState().isPregame())
				if (ability.isAbilityEnabled())
					HardcoreMain.getInstance().getAbilityManager().registerAbility(ability);
				else {
					HardcoreMain.getInstance().getAbilityManager().unregisterAbility(ability);
					GameAPI.getInstance().getGamerManager().getAlivePlayers().stream()
							.filter(gamer -> gamer.hasAbility(ability.getName()))
							.forEach(gamer -> GameHelper.selectAbility(gamer, null, gamer.getAbilityId(ability)));
				}
		}
	}

	@Command(name = "forcekit", aliases = { "fkit" }, permission = "command.hg.forcekit")
	public void forcekitCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§eUse /" + cmdArgs.getLabel() + " <kit> <player:all> para forçar um kit a alguém!");
			return;
		}

		Ability ability = GameAPI.getInstance().getAbilityManager().getAbility(args[0]);

		if (ability == null) {
			sender.sendMessage("§cO kit " + args[0] + " não existe!");
			return;
		}

		List<Player> playerList = new ArrayList<>();

		if (args[1].equalsIgnoreCase("all"))
			playerList.addAll(Bukkit.getOnlinePlayers());
		else
			for (String playerName : args[1].split(",")) {
				Player player = Bukkit.getPlayer(playerName);

				if (player == null) {
					sender.sendMessage("§cO jogador " + args[1] + " não existe!");
					return;
				}

				playerList.add(player);
			}

		Integer aId = 1;

		try {
			aId = Integer.valueOf(args[2]);

			if (HardcoreMain.getInstance().getMaxAbilities() < aId)
				aId = Math.min(HardcoreMain.getInstance().getMaxAbilities(), aId);
		} catch (Exception ex) {
			// foda-se
		}

		final Integer abilityId = aId;

		playerList.forEach(player -> GameHelper.selectAbility(
				GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()), ability, abilityId));
		sender.sendMessage("§aVocê forçou o kit " + ability.getName() + " (" + abilityId + ") para "
				+ (args[1].equalsIgnoreCase("all") ? "todos"
				: Joiner.on(", ").join(playerList.stream().map(Player::getName).toArray(Player[]::new))));
		staffLog(
				"O " + sender.getName() + " forçou o kit " + ability.getName() + " (" + abilityId + ") para "
						+ (args[1].equalsIgnoreCase("all") ? "todos"
						: Joiner.on(", ")
						.join(playerList.stream().map(Player::getName).toArray(Player[]::new))));
	}

	@Command(name = "simplekit", aliases = { "skit" }, permission = "command.hg.simplekit")
	public void simplekitCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			handleHelp(sender, cmdArgs.getLabel());
			return;
		}

		switch (args[0].toLowerCase()) {
			case "criar": {
				if (args.length == 1) {
					handleHelp(sender, cmdArgs.getLabel());
				} else {
					String kitName = args[1];

					if (HardcoreMain.getInstance().getSimplekitManager().containsKey(kitName)) {
						sender.sendMessage("§cO kit §c\"" + kitName + "\"§c já existe!");
						break;
					}

					sender.sendMessage("§aVocê criou o kit §a\"" + kitName + "\"§a!");
					HardcoreMain.getInstance().getSimplekitManager().loadSimplekit(kitName, new SimplekitManager.SimpleKit(kitName, sender));
					staffLog("O " + sender.getName() + " criou o kit " + kitName + "");
				}
				break;
			}
			case "editar": {
				if (args.length == 1) {
					handleHelp(sender, cmdArgs.getLabel());
				} else {
					SimplekitManager.SimpleKit simpleKit = HardcoreMain.getInstance().getSimplekitManager().getSimplekit(args[1]);

					if (simpleKit == null) {
						sender.sendMessage("§cO kit §c\"" + args[1] + "\"§c não existe!");
						break;
					}

					sender.sendMessage("§aVocê editou o kit §a\"" + simpleKit.getKitName() + "\"§a!");
					simpleKit.updateKit(sender);
					staffLog("O " + sender.getName() + " editou o kit " + simpleKit.getKitName() + "");
				}
				break;
			}
//		case "default": {
//			if (args.length == 1) {
//				handleHelp(sender, cmdArgs.getLabel());
//			} else {
//
//				if (args[1].equalsIgnoreCase("remove")) {
//					sender.sendMessage(" §c* §fO kit default foi removido!");
//					ServerConfig.getInstance().setDefaultSimpleKit(null);
//					CommonPlugin.getInstance().getMemberManager().broadcast("", Group.MOD);
//					break;
//				}
//
//				SimpleKit simpleKit = HardcoreMain.getInstance().getSimplekitManager().getValue(args[1]);
//
//				if (simpleKit == null) {
//					sender.sendMessage(" §c* §fO kit §c\"" + args[1] + "\"§f não existe!");
//					break;
//				}
//
//				sender.sendMessage(" §c* §fVocê alterou o kit default para §a\"" + simpleKit.getKitName() + "\"§f!");
//				ServerConfig.getInstance().setDefaultSimpleKit(simpleKit);
//				staffLog("O " + sender.getName() + " setou o kit " + simpleKit.getKitName() + " como default!",
//						Group.MOD);
//			}
//			break;
//		}
			case "list": {
				sender.sendMessage("§aOs kits disponíveis são: "
						+ (HardcoreMain.getInstance().getSimplekitManager().getStoreMap().values().isEmpty()
						? "§cNenhum disponível"
						: "§a" + Joiner.on(", ").join(HardcoreMain.getInstance().getSimplekitManager().getStoreMap()
						.values().stream().map(SimplekitManager.SimpleKit::getKitName).collect(Collectors.toList()))));
				break;
			}
			case "aplicar": {
				if (args.length <= 2) {
					handleHelp(sender, cmdArgs.getLabel());
				} else {
					SimplekitManager.SimpleKit simpleKit = HardcoreMain.getInstance().getSimplekitManager().getSimplekit(args[1]);

					if (simpleKit == null) {
						sender.sendMessage("§cO kit §c\"" + args[1] + "\"§c não existe!");
						break;
					}

					if (args[2].equalsIgnoreCase("all")) {
						Bukkit.getOnlinePlayers().forEach(player -> simpleKit.apply(player));
						sender.sendMessage("§aKit " + simpleKit.getKitName() + " aplicado para todos os jogadores!");
						staffLog("O " + sender.getName() + " aplicou o kit " + simpleKit.getKitName()
								+ " em todos os jogadores");
					} else {
						Player player = Bukkit.getPlayer(args[2]);

						if (player == null) {

							Integer v = null;

							try {
								v = Integer.valueOf(args[2]);
							} catch (NumberFormatException ex) {
								handleHelp(sender, cmdArgs.getLabel());
								return;
							}

							if (v >= 1000) {
								v = 1000;
							}

							final int value = v;

							Bukkit.getOnlinePlayers().stream()
									.filter(target -> target.getLocation().distance(sender.getLocation()) <= value)
									.forEach(target -> simpleKit.apply(target));

							sender.sendMessage("§aKit " + simpleKit.getKitName()
									+ " aplicado para o todos os jogadores em um raio de  " + v + "!");
							staffLog("O " + sender.getName() + " aplicou o kit " + simpleKit.getKitName()
									+ " em todos em um raio de " + v);
							break;
						}

						simpleKit.apply(player);
						sender.sendMessage(
								"§aKit " + simpleKit.getKitName() + " aplicado para o jogador " + player.getName() + "!");
						staffLog("O " + sender.getName() + " aplicou o kit " + simpleKit.getKitName() + " em "
								+ simpleKit.getKitName() + "");
					}
				}
				break;
			}
			default:
				handleHelp(sender, cmdArgs.getLabel());
		}
	}

	public void handleHelp(Player sender, String label) {
		sender.sendMessage("§eUse §b/" + label + " criar <nome>§e para criar um skit");
		sender.sendMessage("§eUse §b/" + label + " editar <nome>§e para editar um skit");
//		sender.sendMessage(" §e* §fUse §a/" + label + " default <nome:remove>§f para setar o kit default");
		sender.sendMessage("§eUse §b/" + label + " aplicar <nome> <all:distancia:player>§e para aplicar um skit");
	}

}