package br.com.plutomc.pvp.engine;

import br.com.plutomc.pvp.engine.listener.PlayerListener;
import br.com.plutomc.core.bukkit.BukkitCommon;
import br.com.plutomc.pvp.engine.backend.GamerData;
import br.com.plutomc.pvp.engine.backend.impl.VoidGamerData;
import br.com.plutomc.pvp.engine.gamer.Gamer;
import br.com.plutomc.pvp.engine.listener.DamageListener;
import br.com.plutomc.pvp.engine.listener.GamerListener;
import br.com.plutomc.pvp.engine.listener.SignListener;
import br.com.plutomc.pvp.engine.listener.WorldListener;
import br.com.plutomc.pvp.engine.manager.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class GameAPI extends BukkitCommon {
   private static GameAPI instance;
   private GamerData gamerData;
   private Class<? extends Gamer> gamerClass;
   private GamerManager gamerManager;
   private boolean dropItems;
   private double protectionRadius;
   private boolean fullIron;
   private boolean fallDamageProtection = false;

   @Override
   public void onEnable() {
      instance = this;
      super.onEnable();
      this.protectionRadius = this.getConfig().getDouble("protectionRadius", 30.0);
      this.fullIron = this.getConfig().getBoolean("fullIron", true);
      this.gamerData = new VoidGamerData();
      this.gamerManager = new GamerManager();
      Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
      Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
      Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
      Bukkit.getPluginManager().registerEvents(new SignListener(), this);
      Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
      this.loadSoups();
   }

   private void loadSoups() {
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
      pumpkin.addIngredient(1, Material.PUMPKIN_SEEDS);
      melon.addIngredient(Material.BOWL);
      melon.addIngredient(1, Material.MELON_SEEDS);
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

   public void setProtectionRadius(double protectionRadius) {
      this.protectionRadius = protectionRadius;
      this.getConfig().set("protectionRadius", Double.valueOf(protectionRadius));
      this.saveDefaultConfig();
   }

   public void setFullIron(boolean fullIron) {
      this.fullIron = fullIron;
      this.getConfig().set("fullIron", Boolean.valueOf(fullIron));
      this.saveDefaultConfig();
   }

   public GamerData getGamerData() {
      return this.gamerData;
   }

   public Class<? extends Gamer> getGamerClass() {
      return this.gamerClass;
   }

   public GamerManager getGamerManager() {
      return this.gamerManager;
   }

   public boolean isDropItems() {
      return this.dropItems;
   }

   public double getProtectionRadius() {
      return this.protectionRadius;
   }

   public boolean isFullIron() {
      return this.fullIron;
   }

   public boolean isFallDamageProtection() {
      return this.fallDamageProtection;
   }

   public static GameAPI getInstance() {
      return instance;
   }

   public void setGamerClass(Class<? extends Gamer> gamerClass) {
      this.gamerClass = gamerClass;
   }

   public void setDropItems(boolean dropItems) {
      this.dropItems = dropItems;
   }

   public void setFallDamageProtection(boolean fallDamageProtection) {
      this.fallDamageProtection = fallDamageProtection;
   }
}
