package br.com.plutomc.pvp.engine.listener;

import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.core.bukkit.member.BukkitMember;
import org.bukkit.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldListener implements Listener {
   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class);
      event.setCancelled(member == null ? true : !member.isBuildEnabled());
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class);
      event.setCancelled(member == null ? true : !member.isBuildEnabled());
   }

   @EventHandler
   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
      BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class);
      event.setCancelled(member == null ? true : !member.isBuildEnabled());
   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      event.setCancelled(event.getSpawnReason() != SpawnReason.CUSTOM);
   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent event) {
      event.blockList().clear();
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockExplode(BlockExplodeEvent event) {
      event.blockList().clear();
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockBurn(BlockBurnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockIgnite(BlockIgniteEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockSpread(BlockSpreadEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent event) {
      if (event.getItemDrop().getItemStack().getType().name().contains("SWORD")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onItemSpawn(final ItemSpawnEvent event) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.NOTE, 1);
            event.getEntity().remove();
         }
      }).runTaskLater(BukkitCommon.getInstance(), 60L);
   }
}
