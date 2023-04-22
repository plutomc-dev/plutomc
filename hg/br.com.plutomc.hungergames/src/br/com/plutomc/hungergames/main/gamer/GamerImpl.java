package br.com.plutomc.hungergames.main.gamer;

import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.common.utils.DateUtils;
import br.com.plutomc.core.common.utils.string.StringFormat;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.engine.game.Ability;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import br.com.plutomc.hungergames.main.event.player.PlayerSpectatorEvent;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class GamerImpl implements Gamer {

	private final String playerName;
	private final UUID uniqueId;

	private transient boolean spectator;
	@Setter
	private transient boolean playing;
	@Setter
	private transient boolean gamemaker;

	private transient int kills;

	private transient List<Ability> abilities;
	private transient Player player;

	private Map<Integer, String> diaryMap;
	private long nextRandom;
	private long diaryExpire;

	public Map<Integer, Ability> getDiaryMap() {
		if (nextRandom < System.currentTimeMillis()) {
			randomDiary();
		}

		Map<Integer, Ability> abilityMap = new HashMap<>();

		for (Entry<Integer, String> map : diaryMap.entrySet()) {
			abilityMap.put(map.getKey(), GameAPI.getInstance().getAbilityManager().getAbility(map.getValue()));
		}
		
		return abilityMap;
	}
	
	public void randomDiary() {
		if (diaryMap == null)
			diaryMap = new HashMap<>();
		
		List<Ability> abilityList = new ArrayList<>(GameAPI.getInstance().getAbilityManager().getAbilities());

		for (int x = 0; x < 3; x++) {
			Ability ability = abilityList.get(CommonConst.RANDOM.nextInt(abilityList.size()));
			diaryMap.put(1 + x, ability.getName());
			abilityList.remove(ability);
		}
		
		nextRandom = DateUtils.getMidNight();
		save("nextRandom");
		save("diaryMap");
	}

	public void setDiaryExpire(long diaryExpire) {
		this.diaryExpire = diaryExpire;
		save("diaryExpire");
	}

	@Override
	public void setSpectator(boolean spectator) {
		this.spectator = spectator;

		if (this.spectator)
			Bukkit.getPluginManager().callEvent(new PlayerSpectatorEvent(player, this));

		player.sendMessage("§aVocê entrou no modo espectador.");
	}

	@Override
	public boolean isPlaying() {
		return !(gamemaker || spectator || !playing);
	}

	@Override
	public void addKill() {
		this.kills++;
	}

	@Override
	public void setAbility(Ability ability, int abilityId) {
		if (abilities.size() < abilityId)
			abilities.add(ability);
		else
			abilities.set(abilityId - 1, ability);
	}

	@Override
	public void addAbility(Ability ability) {
		abilities.add(ability);
	}

	@Override
	public void removeAbility(Ability ability) {
		abilities.remove(ability);
	}

	@Override
	public void clearAbilities() {
		abilities.clear();
	}

	@Override
	public Ability getAbility(int abilityId) {
		return abilities.get(abilityId - 1);
	}

	@Override
	public int getAbilityId(Ability ability) {
		int x = 1;

		for (Ability abiliti : abilities) {
			if (ability == abiliti)
				break;

			x++;
		}

		return x;
	}

	@Override
	public boolean hasAbility(String abilityName) {
		return abilities.stream().filter(ability -> ability.getName().equalsIgnoreCase(abilityName)).findFirst()
				.isPresent();
	}

	@Override
	public boolean hasAbility(int abilityId) {
		return abilities.size() >= abilityId;
	}

	@Override
	public String getAbilitiesName() {
		return getAbilities().isEmpty() ? "Nenhum"
				: Joiner.on(", ").join(getAbilities().stream()
						.map(ability -> StringFormat.formatString(ability.getName())).collect(Collectors.toList()));
	}

	@Override
	public void loadPlayer(Player player) {
		this.abilities = new ArrayList<>();
		this.player = player;
	}

	@Override
	public boolean isAbilityItem(ItemStack item) {
		for (Ability ability : getAbilities())
			if (ability.isItemKit(item))
				return true;

		return false;
	}
	
	private void save(String string) {
		GameAPI.getInstance().getGamerData().updateGamer((Gamer)this, string);		
	}

}
