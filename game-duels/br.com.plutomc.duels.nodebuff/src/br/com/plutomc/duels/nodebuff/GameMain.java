package br.com.plutomc.duels.nodebuff;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.server.ServerType;
import br.com.plutomc.core.common.server.loadbalancer.server.MinigameState;
import br.com.plutomc.core.common.utils.configuration.Configuration;
import br.com.plutomc.core.common.utils.configuration.impl.JsonConfiguration;
import br.com.plutomc.duels.engine.GameAPI;
import br.com.plutomc.duels.engine.scheduler.Scheduler;
import br.com.plutomc.duels.nodebuff.event.GameEndEvent;
import br.com.plutomc.duels.nodebuff.gamer.Gamer;
import br.com.plutomc.duels.nodebuff.listener.ScoreboardListener;
import br.com.plutomc.duels.nodebuff.scheduler.GameScheduler;
import br.com.plutomc.duels.nodebuff.scheduler.WaitingScheduler;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ItemSaddle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
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
        this.setGamerClass(Gamer.class);
        this.setCollectionName("nodebuff-gamer");
        this.setUnloadGamer(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.setTime(60);
        this.setState(MinigameState.STARTING);
        this.setMaxPlayers(2);
        this.startScheduler(new WaitingScheduler());
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
                .loadConfig("nodebuff.json", Paths.get(this.getDataFolder().toURI()).getParent().getParent().toFile(), true, JsonConfiguration.class);

        try {
            this.configuration.loadConfig();
        } catch (Exception var2) {
            var2.printStackTrace();
            this.getPlugin().getPluginPlatform().shutdown("Cannot load the configuration nodebuff.json.");
            return;
        }

        this.setMap(this.configuration.get("mapName", "Unknown"));
        this.debug("The configuration nodebuff.json has been loaded!");
    }

    public Configuration getConfiguration() {
        return CommonPlugin.getInstance().getConfigurationManager().getConfigByName("nodebuff");
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

            p.getInventory().clear();
            p.getInventory().setHelmet(new ItemBuilder().type(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            p.getInventory().setChestplate(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            p.getInventory().setLeggings(new ItemBuilder().type(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            p.getInventory().setBoots(new ItemBuilder().type(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());


            for(int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack item = new ItemStack(Material.POTION, 5);

                Potion pot = new Potion(1);
                pot.setType(PotionType.INSTANT_HEAL);
                pot.setSplash(true);
                pot.setLevel(1);
                pot.apply(item);
                p.getInventory().setItem(i, item);
            }

            p.getInventory().setItem(0, new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            p.getInventory().setItem(1, new ItemBuilder().type(Material.ENDER_PEARL).amount(16).build());
            p.getInventory().setItem(8, new ItemBuilder().type(Material.GOLDEN_CARROT).amount(64).build());
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }
    }

    public void checkWinner() {
        if (getAlivePlayers().size() < 2) {
            getServer().getPluginManager().callEvent(new GameEndEvent());
            handleServer();
        }
    }
}
