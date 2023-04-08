package br.com.plutomc.pvp.engine.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignListener implements Listener {
   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) {
            Player player = event.getPlayer();
            Sign sign = (Sign)event.getClickedBlock().getState();
            String[] lines = sign.getLines();
            if (lines[1].toLowerCase().contains("sopas")) {
               Inventory soup = Bukkit.createInventory(null, 54, "§7Sopas");

               for(int i = 0; i < 54; ++i) {
                  soup.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
               }

               player.openInventory(soup);
            } else if (lines[1].toLowerCase().contains("recraft")) {
               Inventory recraft = Bukkit.createInventory(null, 9, "§7Sopas");
               recraft.setItem(3, new ItemStack(Material.BOWL, 64));
               recraft.setItem(4, new ItemStack(Material.RED_MUSHROOM, 64));
               recraft.setItem(5, new ItemStack(Material.BROWN_MUSHROOM, 64));
               player.openInventory(recraft);
            } else if (lines[1].toLowerCase().contains("cactus")) {
               Inventory cactu = Bukkit.createInventory(null, 9, "§7Sopas");
               cactu.setItem(3, new ItemStack(Material.BOWL, 64));
               cactu.setItem(4, new ItemStack(Material.CACTUS, 64));
               cactu.setItem(5, new ItemStack(Material.CACTUS, 64));
               player.openInventory(cactu);
            } else if (lines[1].toLowerCase().contains("cocoa")) {
               Inventory cocoa = Bukkit.createInventory(null, 9, "§7Cocoa");
               cocoa.setItem(2, new ItemStack(Material.BOWL, 64));
               cocoa.setItem(3, new ItemStack(Material.INK_SACK, 64, (short)3));
               cocoa.setItem(5, new ItemStack(Material.BOWL, 64));
               cocoa.setItem(6, new ItemStack(Material.INK_SACK, 64, (short)3));
               player.openInventory(cocoa);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onSignChange(SignChangeEvent event) {
      String line = event.getLine(0);
      if (line.equalsIgnoreCase("sopa") || line.equalsIgnoreCase("sopas")) {
         event.setLine(0, "§5Pluto§fMC");
         event.setLine(1, "§bSopas");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("recraft") || line.equalsIgnoreCase("recrafts")) {
         event.setLine(0, "§5Pluto§fMC");
         event.setLine(1, "§eRecraft");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("cocoa") || line.equalsIgnoreCase("cocoabean")) {
         event.setLine(0, "§5Pluto§fMC");
         event.setLine(1, "§cCocoabean");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("cactu") || line.equalsIgnoreCase("cactus")) {
         event.setLine(0, "§5Pluto§fMC");
         event.setLine(1, "§aCactus");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("dificil")) {
         event.setLine(0, "§6Lava");
         event.setLine(1, "§c§lHARD");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("facil")) {
         event.setLine(0, "§6Lava");
         event.setLine(1, "§a§lEASY");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("medio")) {
         event.setLine(0, "§6Lava");
         event.setLine(1, "§e§lMEDIUM");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.equalsIgnoreCase("extreme")) {
         event.setLine(0, "§6Lava");
         event.setLine(1, "§4§lEXTREME");
         event.setLine(2, "§6§m>-----<");
         event.setLine(3, " ");
      } else if (line.contains(":")) {
         String[] code = line.split(":");
         if (code.length > 1) {
            if (code[0].equalsIgnoreCase("money")) {
               try {
                  int quantity = Integer.valueOf(code[1]);
                  event.setLine(0, "§6§lMOEDAS");
                  event.setLine(1, "§e§l" + quantity);
                  event.setLine(2, " ");
                  event.setLine(3, "§a§lClique!");
               } catch (NumberFormatException var7) {
               }
            } else if (code[0].equalsIgnoreCase("ticket")) {
               try {
                  int quantity = Integer.valueOf(code[1]);
                  event.setLine(0, "§b§lTICKET");
                  event.setLine(1, "§3§l" + quantity);
                  event.setLine(2, " ");
                  event.setLine(3, "§a§lClique!");
               } catch (NumberFormatException var6) {
               }
            } else if (code[0].equalsIgnoreCase("doublexp")) {
               try {
                  int quantity = Integer.valueOf(code[1]);
                  event.setLine(0, "§3§lDOUBLEXP");
                  event.setLine(1, "§b§l" + quantity);
                  event.setLine(2, " ");
                  event.setLine(3, "§a§lClique!");
               } catch (NumberFormatException var5) {
               }
            }
         }
      }
   }
}
