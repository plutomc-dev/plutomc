package br.com.plutomc.hungergames.main.listener;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class SoupListener implements Listener {

    @SuppressWarnings("deprecation")
    public SoupListener() {
        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
        ShapelessRecipe cocoa = new ShapelessRecipe(soup);
        ShapelessRecipe cactus = new ShapelessRecipe(soup);
        ShapelessRecipe pumpkin = new ShapelessRecipe(soup);
        ShapelessRecipe melon = new ShapelessRecipe(soup);
        ShapelessRecipe flower = new ShapelessRecipe(soup);
        ShapelessRecipe nether = new ShapelessRecipe(soup);

        cocoa.addIngredient(Material.BOWL);
        cocoa.addIngredient(Material.INK_SACK, 3);

        cactus.addIngredient(Material.BOWL);
        cactus.addIngredient(Material.CACTUS);

        pumpkin.addIngredient(Material.BOWL);
        pumpkin.addIngredient(2, Material.PUMPKIN_SEEDS);

        melon.addIngredient(Material.BOWL);
        melon.addIngredient(2, Material.MELON_SEEDS);

        nether.addIngredient(Material.BOWL);
        nether.addIngredient(Material.getMaterial(372));

        flower.addIngredient(Material.BOWL);
        flower.addIngredient(Material.RED_ROSE);
        flower.addIngredient(Material.YELLOW_FLOWER);

        Bukkit.addRecipe(cocoa);
        Bukkit.addRecipe(cactus);
        Bukkit.addRecipe(pumpkin);
        Bukkit.addRecipe(melon);
        Bukkit.addRecipe(nether);
        Bukkit.addRecipe(flower);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onSoup(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP
                && event.getAction().name().contains("RIGHT_CLICK")) {
            if (player.getHealth() < 20.0D || player.getFoodLevel() < 20) {
                int restores = 7;
                double life = player.getHealth();

                player.setHealth(Math.min(life + restores, 20.0D));
                player.setFoodLevel(Math.min(player.getFoodLevel() + restores, 20));
                player.setSaturation(5.0f);

                player.setItemInHand(new ItemStack(Material.BOWL));

                event.setCancelled(true);
            }
        }
    }

}