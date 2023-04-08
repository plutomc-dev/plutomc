package br.com.plutomc.pvp.arena.kit.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.event.gladiator.ChallengeGladiatorEvent;
import br.com.plutomc.pvp.arena.event.gladiator.GladiatorFinishEvent;
import br.com.plutomc.pvp.arena.event.gladiator.GladiatorScapeEvent;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.event.UpdateEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.plutomc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.plutomc.pvp.engine.GameAPI;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GladiatorKit extends Kit {
   private final GladiatorController gladiatorController = new GladiatorController();
   private static final double HEIGHT = 190.0;

   public GladiatorKit() {
      super(
         "Gladiator",
         "Puxe os jogadores em uma jaula, onde ficará somente você e ele para tirarem x1",
         Material.IRON_FENCE,
         22000,
         Arrays.asList(new ItemBuilder().name("§aGladiator").type(Material.IRON_FENCE).build())
      );
   }

   @EventHandler
   public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
      if (event.getRightClicked() instanceof Player) {
         if (event.getPlayer().getItemInHand() != null) {
            Player player = event.getPlayer();
            if (this.hasAbility(player) && this.isAbilityItem(player.getItemInHand())) {
               event.setCancelled(true);
               if (this.isCooldown(player)) {
                  return;
               }

               Player target = (Player)event.getRightClicked();
               if (GameMain.getInstance().getGamerManager().getGamer(target.getUniqueId()).isSpawnProtection()) {
                  return;
               }

               ChallengeGladiatorEvent challengeGladiatorEvent = new ChallengeGladiatorEvent(player, target);
               challengeGladiatorEvent.setCancelled(this.gladiatorController.isInFight(player) || this.gladiatorController.isInFight(target));
               Bukkit.getPluginManager().callEvent(challengeGladiatorEvent);
               if (!challengeGladiatorEvent.isCancelled()) {
                  this.gladiatorController.sendGladiator(player, target);
                  player.setMetadata("combatlog", GameAPI.getInstance().createMeta(Long.valueOf(System.currentTimeMillis() + 12000L)));
                  target.setMetadata("combatlog", GameAPI.getInstance().createMeta(Long.valueOf(System.currentTimeMillis() + 12000L)));
               }
            }
         }
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent e) {
      Player player = e.getPlayer();
      if (e.getAction() != Action.PHYSICAL && this.hasAbility(player) && this.isAbilityItem(e.getItem())) {
         player.updateInventory();
         e.setCancelled(true);
      }
   }

   public class GladiatorController {
      private int radius = 8;
      private int height = 12;
      private Map<Player, Gladiator> playerList;
      private List<Gladiator> gladiatorList;
      private List<Block> blockList;
      private GladiatorListener listener = new GladiatorListener();

      public GladiatorController() {
         this.playerList = new HashMap<>();
         this.gladiatorList = new ArrayList<>();
         this.blockList = new ArrayList<>();
      }

      public Location[] createGladiator(List<Block> blockList, Location gladiatorLocation) {
         Location loc = gladiatorLocation;
         boolean hasGladi = true;

         while(hasGladi) {
            hasGladi = false;
            boolean stop = false;

            for(double x = -8.0; x <= 8.0; ++x) {
               for(double z = -8.0; z <= 8.0; ++z) {
                  for(double y = 0.0; y <= 10.0; ++y) {
                     Location l = new Location(loc.getWorld(), loc.getX() + x, 190.0 + y, loc.getZ() + z);
                     if (l.getBlock().getType() != Material.AIR) {
                        hasGladi = true;
                        loc = new Location(loc.getWorld(), loc.getX() + 20.0, loc.getY(), loc.getZ());
                        stop = true;
                     }

                     if (stop) {
                        break;
                     }
                  }

                  if (stop) {
                     break;
                  }
               }

               if (stop) {
                  break;
               }
            }
         }

         Block mainBlock = loc.getBlock();

         for(double x = (double)(-this.radius); x <= (double)this.radius; ++x) {
            for(double z = (double)(-this.radius); z <= (double)this.radius; ++z) {
               for(double y = 0.0; y <= (double)this.height; ++y) {
                  Location l = new Location(mainBlock.getWorld(), (double)mainBlock.getX() + x, 190.0 + y, (double)mainBlock.getZ() + z);
                  l.getBlock().setType(Material.GLASS);
                  blockList.add(l.getBlock());
                  this.blockList.add(l.getBlock());
               }
            }
         }

         for(double x = (double)(-this.radius + 1); x <= (double)(this.radius - 1); ++x) {
            for(double z = (double)(-this.radius + 1); z <= (double)(this.radius - 1); ++z) {
               for(double y = 1.0; y <= (double)this.height; ++y) {
                  Location l = new Location(mainBlock.getWorld(), (double)mainBlock.getX() + x, 190.0 + y, (double)mainBlock.getZ() + z);
                  l.getBlock().setType(Material.AIR);
                  this.blockList.remove(l.getBlock());
               }
            }
         }

         return new Location[]{
            new Location(mainBlock.getWorld(), (double)mainBlock.getX() + 6.5, 191.0, (double)mainBlock.getZ() + 6.5),
            new Location(mainBlock.getWorld(), (double)mainBlock.getX() - 5.5, 191.0, (double)mainBlock.getZ() - 5.5)
         };
      }

      public boolean isInFight(Player player) {
         return this.playerList.containsKey(player);
      }

      public Gladiator getGladiator(Player player) {
         return this.playerList.get(player);
      }

      public boolean isGladiatorBlock(Block block) {
         return this.blockList.contains(block);
      }

      public void sendGladiator(Player player, Player target) {
         Gladiator gladiator = new Gladiator(player, target);
         this.playerList.put(player, gladiator);
         this.playerList.put(target, gladiator);
         this.gladiatorList.add(gladiator);
         this.listener.register();
      }

      public void removeGladiator(Gladiator gladiator) {
         this.playerList.remove(gladiator.gladiator);
         this.playerList.remove(gladiator.player);
         this.gladiatorList.remove(gladiator);
         if (this.playerList.isEmpty()) {
            this.listener.unregister();
         }
      }

      public class Gladiator {
         private Player gladiator;
         private Player player;
         private Location gladiatorLocation;
         private Location backLocation;
         private List<Block> gladiatorBlocks;
         private List<Block> playersBlocks;
         private int time;

         public Gladiator(Player gladiator, Player player) {
            this.gladiator = gladiator;
            this.player = player;
            this.gladiatorBlocks = new ArrayList<>();
            this.playersBlocks = new ArrayList<>();
            this.gladiatorLocation = gladiator.getLocation();
            this.backLocation = gladiator.getLocation();
            Location[] location = GladiatorController.this.createGladiator(this.gladiatorBlocks, this.gladiatorLocation);
            Location l1 = location[0];
            l1.setYaw(135.0F);
            gladiator.teleport(l1);
            gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
            Location l2 = location[1];
            l2.setYaw(315.0F);
            player.teleport(l2);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
            player.damage(1.0, gladiator);
            gladiator.damage(1.0, player);
         }

         public void handleEscape(boolean teleportBack) {
            this.clearGladiator();
            if (teleportBack) {
               this.teleportBack();
            }

            this.gladiator.removePotionEffect(PotionEffectType.WITHER);
            this.player.removePotionEffect(PotionEffectType.WITHER);
            GladiatorController.this.removeGladiator(this);
            Bukkit.getPluginManager().callEvent(new GladiatorScapeEvent(this.gladiator, this.player));
         }

         public void handleWin(Player death) {
            Player winner = death == this.gladiator ? this.player : this.gladiator;
            this.clearGladiator();
            winner.teleport(this.backLocation);
            winner.removePotionEffect(PotionEffectType.WITHER);
            winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
            GladiatorController.this.removeGladiator(this);
            Bukkit.getPluginManager().callEvent(new GladiatorFinishEvent(this.gladiator, this.player));
         }

         public void handleFinish() {
            this.clearGladiator();
            this.teleportBack();
            if (this.gladiator.isOnline()) {
               this.gladiator.removePotionEffect(PotionEffectType.WITHER);
            }

            if (this.player.isOnline()) {
               this.player.removePotionEffect(PotionEffectType.WITHER);
            }

            GladiatorController.this.removeGladiator(this);
            Bukkit.getPluginManager().callEvent(new GladiatorFinishEvent(this.gladiator, this.player));
         }

         public void pulse() {
            ++this.time;
            if (this.time == 10) {
               for(Block block : this.gladiatorBlocks) {
                  if (block.hasMetadata("gladiatorBreakable")) {
                     block.setType(Material.AIR);
                  }
               }
            }

            if (this.time == 120) {
               this.gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 3));
               this.player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 3));
            }

            if (this.time == 180) {
               this.handleFinish();
            }
         }

         public void addBlock(Block block) {
            if (!this.playersBlocks.contains(block)) {
               this.playersBlocks.add(block);
            }
         }

         public boolean removeBlock(Block block) {
            if (this.playersBlocks.contains(block)) {
               this.playersBlocks.remove(block);
               return true;
            } else {
               return false;
            }
         }

         private void clearGladiator() {
            for(Block block : this.gladiatorBlocks) {
               block.setType(Material.AIR);
               if (GladiatorController.this.blockList.contains(block)) {
                  GladiatorController.this.blockList.remove(block);
               }
            }

            for(Block block : this.playersBlocks) {
               block.setType(Material.AIR);
               if (GladiatorController.this.blockList.contains(block)) {
                  GladiatorController.this.blockList.remove(block);
               }
            }
         }

         private void teleportBack() {
            this.gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
            this.gladiator.teleport(this.backLocation);
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
            this.player.teleport(this.backLocation);
         }

         public boolean isInGladiator(Player player) {
            return player == this.player || player == this.gladiator;
         }
      }

      public class GladiatorListener implements Listener {
         private boolean registered;

         @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
         )
         public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            if (GladiatorController.this.isInFight(player)) {
               event.getDrops().clear();
               GladiatorController.this.getGladiator(player).handleWin(player);
            }
         }

         @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.LOWEST
         )
         public void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            if (GladiatorController.this.isInFight(player)) {
               GladiatorController.this.getGladiator(player).handleWin(player);
            }
         }

         @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
         )
         public void onPlayerDamage(PlayerDamagePlayerEvent event) {
            Player damager = event.getDamager();
            Player player = event.getPlayer();
            if (GladiatorController.this.isInFight(player)) {
               Gladiator gladiator = GladiatorController.this.getGladiator(player);
               if (gladiator.isInGladiator(damager)) {
                  event.setCancelled(false);
               } else {
                  event.setCancelled(true);
               }
            } else if (GladiatorController.this.isInFight(damager)) {
               Gladiator gladiator = GladiatorController.this.getGladiator(damager);
               if (gladiator.isInGladiator(player)) {
                  event.setCancelled(false);
               } else {
                  event.setCancelled(true);
               }
            }
         }

         @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
         )
         public void onBlockPlace(BlockPlaceEvent event) {
            Player player = event.getPlayer();
            if (GladiatorController.this.isInFight(player)) {
               GladiatorController.this.getGladiator(player).addBlock(event.getBlock());
            }
         }

         @EventHandler(
            priority = EventPriority.MONITOR
         )
         public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (GladiatorController.this.blockList.contains(event.getBlock())) {
               event.setCancelled(true);
            } else {
               if (GladiatorController.this.isInFight(player)) {
                  GladiatorController.this.getGladiator(player).removeBlock(event.getBlock());
               }
            }
         }

         @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
         )
         public void onBlockBreak(BlockDamageEvent event) {
            if (GladiatorController.this.blockList.contains(event.getBlock())) {
               Player player = event.getPlayer();
               Block block = event.getBlock();
               player.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte)0);
            }
         }

         @EventHandler
         public void onUpdate(UpdateEvent event) {
            if (event.getType() == UpdateEvent.UpdateType.SECOND) {
               GladiatorController.this.gladiatorList.iterator().forEachRemaining(Gladiator::pulse);
            }
         }

         @EventHandler
         public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
            Player player = event.getPlayer();
            if (GladiatorController.this.isInFight(player)) {
               Gladiator gladiator = GladiatorController.this.getGladiator(player);
               if (event.getFrom().getY() - 190.0 > (double)GladiatorController.this.height) {
                  gladiator.handleEscape(true);
               } else if (event.getFrom().getY() <= 188.0 && gladiator.time > 2) {
                  gladiator.handleEscape(true);
               }
            }
         }

         @EventHandler
         public void onExplode(EntityExplodeEvent event) {
            Iterator<Block> blockIt = event.blockList().iterator();

            while(blockIt.hasNext()) {
               Block b = blockIt.next();
               if (GladiatorController.this.blockList.contains(b)) {
                  blockIt.remove();
               }
            }
         }

         public void register() {
            if (!this.registered) {
               Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
               this.registered = true;
            }
         }

         public void unregister() {
            if (this.registered) {
               HandlerList.unregisterAll(this);
               this.registered = false;
            }
         }
      }
   }
}
