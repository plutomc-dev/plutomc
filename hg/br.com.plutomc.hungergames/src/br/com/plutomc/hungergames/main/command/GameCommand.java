package br.com.plutomc.hungergames.main.command;

import br.com.plutomc.core.bukkit.command.BukkitCommandArgs;
import br.com.plutomc.core.common.command.CommandArgs;
import br.com.plutomc.core.common.command.CommandClass;
import br.com.plutomc.core.common.command.CommandFramework.Command;
import br.com.plutomc.core.common.command.CommandSender;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.manager.GameHelper;
import br.com.plutomc.hungergames.main.stages.GameSchedule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameCommand implements CommandClass {

	@Command(name = "ability", aliases = { "kit" })
	public void abilityCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(sender.getUniqueId());

		if (args.length == 0) {
			sender.sendMessage("§eUse §b/" + cmdArgs.getLabel() + " <kit>§e para escolher seu kit!");
			return;
		}

		Ability ability = GameAPI.getInstance().getAbilityManager().getAbility(args[0]);

		if (ability == null) {
			sender.sendMessage("§cO kit §c" + args[0] + "§c não existe!");
			return;
		}

		if (gamer.hasAbility(ability.getName())) {
			sender.sendMessage("§cVocê já está usando esse kit!");
			return;
		}

		if (ability.isAbilityEnabled()) {
			int abilityId = 1;

			if (args.length == 1)
				while (gamer.hasAbility(abilityId)
						&& gamer.getAbilities().size() < GameAPI.getInstance().getMaxAbilities()) {
					abilityId++;
				}
			else
				try {
					abilityId = Integer.valueOf(args[1]);

					if (GameAPI.getInstance().getMaxAbilities() < abilityId)
						abilityId = Math.min(GameAPI.getInstance().getMaxAbilities(), abilityId);
				} catch (Exception ex) {
					// foda-se
				}

			if (GameHelper.hasAbility(gamer, ability, abilityId))
				GameHelper.selectAbility(gamer, ability, abilityId);
			else
				sender.sendMessage(
						"§cVocê não possui o kit §c" + StringFormat.formatString(ability.getName()) + "§c!");
		} else
			sender.sendMessage("§cO kit " + ability.getName() + " está desativado!");
	}

	@Command(name = "ability2", aliases = { "kit2" })
	public void ability2Command(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		if (cmdArgs.getArgs().length == 0)
			cmdArgs.getPlayer().performCommand("kit");
		else
			cmdArgs.getPlayer().performCommand("kit " + cmdArgs.getArgs()[0] + " 2");
	}

	@Command(name = "desistir")
	public void desistirCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (GameAPI.getInstance().getState().isPregame())
			player.sendMessage("§cVocê só pode desistir quando o jogo começar!");
		else {
			gamer.setSpectator(true);
		}
	}

	@Command(name = "bussola", aliases = { "compass" })
	public void compassCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();

		if (!GameAPI.getInstance().getState().isPregame())
			player.getInventory().addItem(new ItemStack(Material.COMPASS));
	}

	@Command(name = "spawn")
	public void spawnCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();

		if (GameAPI.getInstance().getState().isPregame())
			player.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
	}

	@Command(name = "feast")
	public void feastCommand(BukkitCommandArgs cmdArgs) {
		if (cmdArgs.isPlayer()) {
			Player player = cmdArgs.getPlayer();

			if (GameSchedule.feastLocation == null)
				player.sendMessage("§cO feast ainda não spawnou!");
			else {
				player.sendMessage("§aBussola apontando para o feast!");
				player.setCompassTarget(GameSchedule.feastLocation);
			}
		}
	}

}
