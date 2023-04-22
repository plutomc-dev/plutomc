package br.com.plutomc.hungergames.engine.command;

import br.com.plutomc.hungergames.engine.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.command.CommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework.Command;
import br.com.plutomc.core.common.command.CommandSender;
import br.com.plutomc.core.common.utils.DateUtils;
import br.com.plutomc.core.common.utils.string.MessageBuilder;

public class ModeratorCommand implements CommandClass {

	@Command(name = "tempo", permission = "command.hg.time")
	public void tempoCommand(CommandArgs command) {
		CommandSender sender = command.getSender();
		String[] args = command.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§eUse §b/" + command.getLabel() + " <time>§e para alterar o tempo da partida!");
			return;
		}

		if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
			boolean turn = args[0].equalsIgnoreCase("on");

			if (turn == GameAPI.getInstance().isTimerEnabled()) {
				sender.sendMessage("§cO timer já está nesse estado!");
				return;
			}

			GameAPI.getInstance().setTimerEnabled(turn);
			CommonPlugin.getInstance().getMemberManager().staffLog(new MessageBuilder(
					"§7[O staff " + command.getSender().getName() + " " + (turn ? "ativou" : "desativou") + " o timer]")
							.create());
			return;
		}

		long time;

		try {
			time = DateUtils.getTime(args[0]);
		} catch (Exception e) {
			sender.sendMessage("§cSintaxe de tempo inválido.");
			return;
		}

		GameAPI.getInstance().setTime((int) ((time - System.currentTimeMillis()) / 1000));
		sender.sendMessage("§eVocê alterou o tempo para §b" + DateUtils.getTime("" + time) + "§e.");
		CommonPlugin.getInstance().getMemberManager()
				.staffLog(
						new MessageBuilder(
								"§7[O staff " + command.getSender().getName() + " alterou o tempo do jogo para "
										+ ((int) ((time - System.currentTimeMillis()) / 1000)) + "]").create());
	}

	@Command(name = "start", aliases = { "start" }, permission = "command.hg.start")
	public void startCommand(CommandArgs command) {
		if (GameAPI.getInstance().getState().isPregame()) {
			GameAPI.getInstance().setTimerEnabled(true);
			GameAPI.getInstance().startGame();
			CommonPlugin.getInstance().getMemberManager().staffLog(
					new MessageBuilder("§7[O staff " + command.getSender().getName() + " iniciou o jogo]").create());
		} else
			command.getSender().sendMessage("§cO jogo já iniciou!");
	}

	@Command(name = "togglebuild", permission = "command.hg.togglebuild")
	public void togglebuildCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			GameAPI.getInstance().setBuildEnabled(!GameAPI.getInstance().isBuildEnabled());

			Bukkit.broadcastMessage(
					GameAPI.getInstance().isBuildEnabled() ? "§aO build foi ativado!" : "§cO build foi desativado!");
			CommonPlugin.getInstance().getMemberManager().staffLog("§7[O " + sender.getName() + " "
					+ (GameAPI.getInstance().isBuildEnabled() ? "ativou" : "desativou") + " o build]");
			return;
		}

		Material blockMaterial = Material.getMaterial(args[0]);

		if (blockMaterial == null) {
			try {
				blockMaterial = Material.getMaterial(Integer.valueOf(args[0]));
			} catch (NumberFormatException e) {
			}
		}

		if (blockMaterial == null) {
			sender.sendMessage("§cO bloco " + args[1] + " não existe!");
			return;
		}

		if (GameAPI.getInstance().getMaterialSet().contains(blockMaterial)) {
			sender.sendMessage("§aO bloco " + blockMaterial + " foi removido da lista de blocos inquebraveis!");
			GameAPI.getInstance().getMaterialSet().remove(blockMaterial);
		} else {
			sender.sendMessage("§aO bloco " + blockMaterial + " foi adicionado na lista de blocos inquebraveis!");
			GameAPI.getInstance().getMaterialSet().add(blockMaterial);
		}
	}

	@Command(name = "toggleplace", aliases = { "place" }, permission = "command.hg.toggleplace")
	public void toggleplaceCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		GameAPI.getInstance().setPlaceEnabled(!GameAPI.getInstance().isPlaceEnabled());

		Bukkit.broadcastMessage(
				GameAPI.getInstance().isPlaceEnabled() ? "§aO place foi ativado!" : "§cO place foi desativado!");
		CommonPlugin.getInstance().getMemberManager().staffLog("§7[O " + sender.getName() + " "
				+ (GameAPI.getInstance().isPlaceEnabled() ? "ativou" : "desativou") + " o place]");
	}

	@Command(name = "togglebucket", aliases = { "bucket" }, permission = "command.hg.bucket")
	public void togglebucketCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		GameAPI.getInstance().setBucketEnabled(!GameAPI.getInstance().isBucketEnabled());

		Bukkit.broadcastMessage(
				GameAPI.getInstance().isBucketEnabled() ? "§aO bucket foi ativado!" : "§cO bucket foi desativado!");
		CommonPlugin.getInstance().getMemberManager().staffLog("§7[O " + sender.getName() + " "
				+ (GameAPI.getInstance().isBucketEnabled() ? "ativou" : "desativou") + " o bucket]");
	}

	@Command(name = "togglepvp", permission = "command.hg.togglepvp")
	public void togglepvpCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		GameAPI.getInstance().setPvpEnabled(!GameAPI.getInstance().isPvpEnabled());

		Bukkit.broadcastMessage(
				GameAPI.getInstance().isPvpEnabled() ? "§aO pvp foi ativado!" : "§cO pvp foi desativado!");
		CommonPlugin.getInstance().getMemberManager().staffLog("§7[O " + sender.getName() + " "
				+ (GameAPI.getInstance().isPvpEnabled() ? "ativou" : "desativou") + " o pvp]");
	}

	@Command(name = "toggledamage", permission = "command.hg.toggledamage")
	public void toggledamageCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		GameAPI.getInstance().setDamageEnabled(!GameAPI.getInstance().isDamageEnabled());

		Bukkit.broadcastMessage(
				GameAPI.getInstance().isDamageEnabled() ? "§aO dano foi ativado!" : "§cO dano foi desativado!");
		CommonPlugin.getInstance().getMemberManager().staffLog("§7[O " + sender.getName() + " "
				+ (GameAPI.getInstance().isDamageEnabled() ? "ativou" : "desativou") + " o dano]");
	}

	@Command(name = "cleardrop", aliases = { "cleardrops" }, permission = "command.hg.cleardrops")
	public void cleardropCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		for (World world : Bukkit.getWorlds())
			for (Item entity : world.getEntitiesByClass(Item.class)) {
				entity.remove();
			}

		Bukkit.broadcastMessage("§aO chão foi limpo!");
		CommonPlugin.getInstance().getMemberManager().staffLog("§7[O " + sender.getName() + " limpou o chão]");
	}

}
