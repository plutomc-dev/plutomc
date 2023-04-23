package br.com.plutomc.hungergames.main.manager;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.HardcoreMain;
import br.com.plutomc.hungergames.main.event.player.PlayerItemReceiveEvent;
import br.com.plutomc.hungergames.main.event.player.PlayerSelectKitEvent;
import br.com.plutomc.hungergames.main.event.player.PlayerSelectedKitEvent;
import br.com.plutomc.hungergames.main.stages.WinningSchedule;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class GameHelper {

	public static void selectAbility(Gamer gamer, Ability ability, int abilityId) {
		PlayerSelectKitEvent playerSelectKitEvent = new PlayerSelectKitEvent(gamer.getPlayer(), gamer, ability,
				abilityId);
		Bukkit.getPluginManager().callEvent(playerSelectKitEvent);

		if (!playerSelectKitEvent.isCancelled()) {
			if (gamer.hasAbility(abilityId))
				gamer.getAbility(abilityId).removeUser(gamer.getUniqueId());

			gamer.setAbility(ability, abilityId);
			gamer.getPlayer()
					.sendMessage("§aVocê selecionou o kit " + StringFormat.formatString(ability.getName()) + ".");
			ability.addUser(gamer.getUniqueId());

			if (!GameAPI.getInstance().getState().isPregame())
				GameAPI.getInstance().getAbilityManager().registerAbility(ability);

			Bukkit.getPluginManager()
					.callEvent(new PlayerSelectedKitEvent(gamer.getPlayer(), gamer, ability, abilityId));
		}
	}

	public static void removeAbility(Gamer gamer, Ability ability, int abilityId) {
		ability.removeUser(gamer.getUniqueId());
		gamer.removeAbility(ability);
		Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(gamer.getPlayer(), gamer, null, abilityId));
	}

	public static void loadItems(Player player, boolean giveAbilities) {
		PlayerItemReceiveEvent playerItemReceiveEvent = new PlayerItemReceiveEvent(player);
		Bukkit.getPluginManager().callEvent(playerItemReceiveEvent);

		if (playerItemReceiveEvent.isCancelled())
			return;

		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		player.setExp(0);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setNoDamageTicks(100);
		player.setFallDistance(-3f);
		player.setHealth(20);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.setItemOnCursor(null);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getActivePotionEffects().clear();
		player.closeInventory();
		player.getInventory().setItem(0, new ItemStack(Material.COMPASS));

		if (giveAbilities)
			for (Ability ability : gamer.getAbilities())
				for (ItemStack itemStack : ability.getAbilityItems())
					player.getInventory().addItem(itemStack);
	}

	public static boolean checkWinner() {
		if (GameAPI.getInstance().getState() == MinigameState.WINNING)
			return false;

		List<Gamer> gamerList = GameAPI.getInstance().getGamerManager().getGamers().stream()
				.filter(gamer -> gamer.isPlaying()).collect(Collectors.toList());

		if (gamerList.size() == 0) {
			GameAPI.getInstance().getScheduleManager().startSchedule(new WinningSchedule(null));
			return true;
		}

		if (gamerList.size() == 1) {
			Gamer gamer = gamerList.stream().findFirst().orElse(null);
			GameAPI.getInstance().getScheduleManager().startSchedule(new WinningSchedule(gamer));
			return true;
		}

		return false;
	}

	public static boolean isPriviligiedTime() {
		return CommonPlugin.getInstance().getMinigameState().isInvencibility()
				|| (CommonPlugin.getInstance().getMinigameState().isGametime()
						&& CommonPlugin.getInstance().getServerTime() < 300);
	}

	public static void spawnFirework(Location location, Color color, int amount) {
		Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.setPower(1);
		fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

		fw.setFireworkMeta(fwm);
		fw.detonate();

		for (int i = 0; i < amount; i++) {
			Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm);
		}
	}

	public static void deleteWorld(String world) {
		Bukkit.getServer().unloadWorld(world, false);
		deleteDir(new File(world));
		Bukkit.getLogger().info("Apagando mundo '" + world + "'...");
	}

	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));
			}
		}
		dir.delete();
	}

	public static void loadDefaulKit(Player player) {
		Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());

		for (int x = 1; x <= HardcoreMain.getInstance().getMaxAbilities(); x++) {
			Ability ability = GameAPI.getInstance().getAbilityManager()
					.getAbility("none");

			if (ability == null)
				break;

			gamer.setAbility(ability, x);
			Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(gamer.getPlayer(), gamer, ability, x));
		}
	}

	public static boolean hasAbility(Gamer gamer, Ability ability, int abilityId) {
		boolean isFree = (HardcoreMain.getInstance().getMaxAbilities() > 1 && abilityId == 1);
		return isFree || hasAbility(gamer, ability);
	}

	public static boolean hasAbility(Gamer gamer, Ability ability) {
		return gamer.getPlayer().hasPermission("kit." + ability.getName())
				|| CommonPlugin.getInstance().getMemberManager().getMember(gamer.getUniqueId()).hasGroup("pluto")
				|| HardcoreMain.FREE_ABILITIES.contains(ability.getName());
	}

}
