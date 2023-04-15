package br.com.plutomc.duels.scrim;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.configuration.Configuration;
import br.com.plutomc.core.common.utils.configuration.impl.JsonConfiguration;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.engine.scheduler.Scheduler;
import br.com.plutomc.duels.scrim.event.GameEndEvent;
import br.com.plutomc.duels.scrim.gamer.Gamer;
import br.com.plutomc.duels.scrim.listener.PlayerListener;
import br.com.plutomc.duels.scrim.listener.ScoreboardListener;
import br.com.plutomc.duels.scrim.scheduler.GameScheduler;
import br.com.plutomc.duels.scrim.scheduler.WaitingScheduler;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Paths;
import java.util.List;

public class GameMain extends GameAPI {

    @Getter
    private static GameMain instance;
    private JsonConfiguration configuration;


    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
        loadConfiguration();
        this.setGamerClass(Gamer.class);
        this.setCollectionName("scrim-gamer");
        this.setUnloadGamer(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.setTime(60);
        this.setState(MinigameState.STARTING);
        this.setMaxPlayers(2);
        this.startScheduler(new WaitingScheduler());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
            public void onPacketSending(PacketEvent e) {
                if (e.getPacketType() == PacketType.Play.Server.CHAT || e.getPacketType() == PacketType.Play.Client.CHAT) {
                    try {
                        String json = ((WrappedChatComponent) e.getPacket().getChatComponents().read(0)).getJson();
                        if (json.equals("{\"translate\":\"tile.bed.noSleep\"}") || json.equals("{\"translate\":\"tile.bed.notValid\"}")) {
                            e.setCancelled(true);
                        }
                    } catch (Exception var3) {
                    }
                }
            }
        });

        getAlivePlayers().clear();
    }


    private void loadConfiguration() {
        this.configuration = CommonPlugin.getInstance()
                .getConfigurationManager()
                .loadConfig("scrim.json", Paths.get(this.getDataFolder().toURI()).getParent().getParent().toFile(), true, JsonConfiguration.class);

        try {
            this.configuration.loadConfig();
        } catch (Exception var2) {
            var2.printStackTrace();
            this.getPlugin().getPluginPlatform().shutdown("Cannot load the configuration scrim.json.");
            return;
        }

        this.setMap(this.configuration.get("mapName", "Unknown"));
        this.debug("The configuration scrim.json has been loaded!");
    }

    public Configuration getConfiguration() {
        return CommonPlugin.getInstance().getConfigurationManager().getConfigByName("scrim");
    }

    @Override
    public void onDisable() {
        Bukkit.getWorld("world").getEntities().forEach(entity -> {
            if (!(entity instanceof ItemFrame)) {
                entity.remove();
            }
        });
        super.onDisable();
    }

    private void handleServer() {
        (new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    Bukkit.shutdown();
                } else {
                    if (++this.time == 8) {
                        Bukkit.getOnlinePlayers()
                                .forEach(
                                        player -> GameAPI.getInstance()
                                                .sendPlayerToServer(
                                                        player,
                                                        CommonPlugin.getInstance().getServerType(),
                                                        CommonPlugin.getInstance().getServerType().getServerLobby(),
                                                        ServerType.LOBBY)
                                );
                    } else if (this.time == 3) {
                        Bukkit.shutdown();
                    }
                }
            }
        })
                .runTaskTimer(GameAPI.getInstance(), 20L, 20L);
    }

    public List<Gamer> getAlivePlayers() {
        return GameAPI.getInstance().getGamerManager().filter(Gamer::isAlive, Gamer.class);
    }

    public void startGame() {
        for (Scheduler scheduler : this.getSchedulerManager().getSchedulers()) {
            this.getSchedulerManager().unloadScheduler(scheduler);
            if (scheduler instanceof WaitingScheduler) {
                HandlerList.unregisterAll((WaitingScheduler) scheduler);
            }
        }

        this.setUnloadGamer(false);
        GameAPI.getInstance().setState(MinigameState.GAMETIME);
        GameAPI.getInstance().startScheduler(new GameScheduler());

        getAlivePlayers().get(0).getPlayer().teleport(GameAPI.getInstance().getLocationManager().getLocation("pvp-loc1"));
        getAlivePlayers().get(1).getPlayer().teleport(GameAPI.getInstance().getLocationManager().getLocation("pvp-loc2"));

        getAlivePlayers().get(0).getPlayer().setPlayerListName("§c" + getAlivePlayers().get(0).getPlayer().getName());

        getAlivePlayers().get(1).getPlayer().setPlayerListName("§9" + getAlivePlayers().get(1).getPlayer().getName());

        for (Gamer g : getAlivePlayers()) {
            Player p = g.getPlayer();

            handleInventory(p ,false);
        }
    }

    public void checkWinner() {
        if (getAlivePlayers().size() < 2) {
            getServer().getPluginManager().callEvent(new GameEndEvent());
            handleServer();
        }
    }

    public void handleInventory(Player player, boolean old) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        if (old) {
            player.getInventory().setItem(0,
                    new ItemBuilder().name("§aEspada de Pedra").type(Material.STONE_SWORD).build());

            player.getInventory().setItem(1, new ItemStack(Material.WOOD, 48));
            player.getInventory().setItem(8, new ItemStack(Material.WOOD_STAIRS, 24));

            player.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
            player.getInventory().setItem(22, new ItemStack(Material.BOWL, 64));
            player.getInventory().setItem(14, new ItemStack(Material.INK_SACK, 64, (short) 3));
            player.getInventory().setItem(23, new ItemStack(Material.INK_SACK, 64, (short) 3));

            player.getInventory().setItem(17, new ItemStack(Material.WOOD_AXE));
        } else {
            player.getInventory().setItem(0, new ItemBuilder().name("§aEspada de Diamante!")
                    .type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL).build());
            player.getInventory().setItem(8, new ItemStack(Material.WOOD, 64));

            player.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
            player.getInventory().setItem(14, new ItemStack(Material.INK_SACK, 64, (short) 3));
            player.getInventory().setItem(15, new ItemStack(Material.INK_SACK, 64, (short) 3));
            player.getInventory().setItem(16, new ItemStack(Material.INK_SACK, 64, (short) 3));

            player.getInventory().setItem(22, new ItemStack(Material.BOWL, 64));
            player.getInventory().setItem(23, new ItemStack(Material.INK_SACK, 64, (short) 3));
            player.getInventory().setItem(24, new ItemStack(Material.INK_SACK, 64, (short) 3));
            player.getInventory().setItem(25, new ItemStack(Material.INK_SACK, 64, (short) 3));

            player.getInventory().setItem(9, new ItemStack(Material.IRON_HELMET));
            player.getInventory().setItem(10, new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setItem(11, new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setItem(12, new ItemStack(Material.IRON_BOOTS));

            player.getInventory().setItem(18, new ItemStack(Material.IRON_HELMET));
            player.getInventory().setItem(19, new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setItem(20, new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setItem(21, new ItemStack(Material.IRON_BOOTS));

            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        }

        for (int x = 0; x < 32; x++)
            player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));

        player.updateInventory();
    }
}
