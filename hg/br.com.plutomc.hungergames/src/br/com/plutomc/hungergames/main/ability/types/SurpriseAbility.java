package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import br.com.plutomc.hungergames.main.event.GameStartEvent;
import br.com.plutomc.hungergames.main.event.player.PlayerSelectedKitEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SurpriseAbility extends AbilityImpl {

	public SurpriseAbility() {
		super("surprise", Material.CAKE, "Selecione um kit aleatório no inicio da partida.");
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		for (UUID users : getUsers()) {
			Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(users);

			if (gamer == null)
				continue;

			random(gamer, false);
		}
	}

	@EventHandler
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
		if (event.getAbility() == this) {
			random(event.getGamer(), true);
		}
	}

	public void random(Gamer gamer, boolean item) {
		int abilityId = gamer.getAbilityId(this);
		gamer.removeAbility(this);

		List<Ability> abilities = new ArrayList<>(GameAPI.getInstance().getAbilityManager().getAbilities());

		abilities.removeAll(gamer.getAbilities());
		abilities.remove(this);

		Ability randomAbility = abilities.get(CommonConst.RANDOM.nextInt(abilities.size()));
		gamer.setAbility(randomAbility, abilityId);
		randomAbility.addUser(gamer.getUniqueId());
		GameAPI.getInstance().getAbilityManager().registerAbility(randomAbility);

		Bukkit.getPluginManager()
				.callEvent(new PlayerSelectedKitEvent(gamer.getPlayer(), gamer, randomAbility, abilityId));
		gamer.getPlayer()
				.sendMessage("§aO surprise selecionou o kit " + StringFormat.formatString(randomAbility.getName()));

		if (item)
			for (ItemStack abilityItem : randomAbility.getAbilityItems())
				gamer.getPlayer().getInventory().addItem(abilityItem);
	}

}
