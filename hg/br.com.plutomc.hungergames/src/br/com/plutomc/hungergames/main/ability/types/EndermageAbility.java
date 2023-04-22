package br.com.plutomc.hungergames.main.ability.types;

import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.UpdateEvent.UpdateType;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.ability.AbilityImpl;
import br.com.plutomc.hungergames.main.event.player.PlayerEndermageEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public class EndermageAbility extends AbilityImpl {

	private Map<Player, Endermage> endermageMap;

	public EndermageAbility() {
		super("endermage", Material.ENDER_PORTAL_FRAME, "Teleporte jogadores até você usando o seu portal.");
		endermageMap = new HashMap<>();
		addItem(new ItemBuilder().type(Material.ENDER_PORTAL_FRAME).name("§aEndermage").build());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().hasMetadata("invincibility")) {
			MetadataValue metadata = event.getEntity().getMetadata("invincibility").stream().findFirst().orElse(null);
			
			if (metadata.asLong() < System.currentTimeMillis()) {
				metadata.invalidate();
				return;
			}
			
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.getAction().name().contains("BLOCK"))
			return;

		Player player = event.getPlayer();

		if (hasAbility(player) && isItemKit(event.getPlayer().getItemInHand())) {

			event.setCancelled(true);
			player.updateInventory();

			if (endermageMap.containsKey(player)) {
				player.sendMessage("§cAguarde para colocar o endermage novamente!");
				return;
			}

			Block block = event.getClickedBlock();
			Location portalLocation = block.getLocation().add(0.5, 1.5, 0.5);

			endermageMap.put(player,
					new Endermage(player, portalLocation, player.getInventory().getHeldItemSlot(), block.getState()) {

						@Override
						public void result(List<Player> nearbyPlayers, boolean timeout) {
							if (!timeout) {
								for (Player nearby : nearbyPlayers) {
									nearby.teleport(player);

									nearby.sendMessage("§dVocê foi puxado pelo endermage!");
									nearby.sendMessage("§dVocê está invencível por 5 segundos!");
									nearby.setMetadata("invincibility", new FixedMetadataValue(GameAPI.getInstance(),
											System.currentTimeMillis() + 5000l));
								}
								
								player.teleport(portalLocation);
								player.setMetadata("invincibility", new FixedMetadataValue(GameAPI.getInstance(),
										System.currentTimeMillis() + 5000l));
							}

							block.setType(getBlockState().getType());
							block.setData(getBlockState().getData().getData());

							endermageMap.remove(player);
						}
					});

			block.setType(Material.ENDER_PORTAL_FRAME);
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			for (Entry<Player, Endermage> entry : endermageMap.entrySet()) {
				List<Player> players = new ArrayList<>();

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.equals(entry.getKey()))
						continue;

					if (!isEnderable(entry.getValue().getPortalLocation(), player.getLocation()))
						continue;

					if (hasAbility(player))
						continue;

					PlayerEndermageEvent playerEvent = new PlayerEndermageEvent(player);
					Bukkit.getPluginManager().callEvent(playerEvent);

					if (!playerEvent.isCancelled())
						players.add(player);
				}

				if (players.isEmpty())
					entry.getValue().pulse();
				else
					entry.getValue().result(players, false);
			}
		}
	}

	private boolean isEnderable(Location portal, Location player) {
		return (Math.abs(portal.getX() - player.getX()) < 2.0D) && (Math.abs(portal.getZ() - player.getZ()) < 2.0D)
				&& (Math.abs(portal.getY() - player.getY()) > 2.0D);
	}

	@RequiredArgsConstructor
	@Getter
	public abstract class Endermage {

		private final Player endermage;
		private final Location portalLocation;
		private final int heldItemSlot;
		private final BlockState blockState;

		private int time;

		public void pulse() {
			time++;

			if (time == 3) {
				result(new ArrayList<>(), true);
			}
		}

		public abstract void result(List<Player> nearbyPlayers, boolean timeout);
	}

}
