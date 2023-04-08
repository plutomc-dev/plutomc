package br.com.plutomc.pvp.arena.kit.register;

import java.util.Arrays;
import java.util.Random;

import br.com.plutomc.pvp.arena.GameMain;
import br.com.plutomc.pvp.arena.kit.Kit;
import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MonkKit extends Kit {
   public MonkKit() {
      super(
         "Monk",
         "Bagunce o inventário de seus inimigos",
         Material.BLAZE_ROD,
         11500,
         Arrays.asList(new ItemBuilder().name("§aMonk").type(Material.BLAZE_ROD).build())
      );
   }

   @EventHandler
   public void onInteractEntity(PlayerInteractEntityEvent e) {
      if (e.getRightClicked() instanceof Player) {
         Player player = e.getPlayer();
         if (this.hasAbility(player)) {
            ItemStack item = player.getItemInHand();
            if (this.isAbilityItem(item)) {
               Player clicked = (Player)e.getRightClicked();
               if (!GameMain.getInstance().getGamerManager().getGamer(clicked.getUniqueId()).isSpawnProtection()) {
                  if (!this.isCooldown(player)) {
                     this.addCooldown(player, 15L);
                     int randomN = new Random().nextInt(36);
                     ItemStack atual = clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null;
                     ItemStack random = clicked.getInventory().getItem(randomN) != null ? clicked.getInventory().getItem(randomN).clone() : null;
                     if (random == null) {
                        clicked.getInventory().setItem(randomN, atual);
                        clicked.setItemInHand(null);
                     } else {
                        clicked.getInventory().setItem(randomN, atual);
                        clicked.getInventory().setItemInHand(random);
                     }
                  }
               }
            }
         }
      }
   }
}
