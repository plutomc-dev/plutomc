package br.com.plutomc.pvp.arena;

import br.com.plutomc.pvp.arena.listener.ScoreboardListener;
import br.com.plutomc.pvp.engine.GameAPI;
import br.com.plutomc.pvp.arena.gamer.Gamer;
import br.com.plutomc.pvp.arena.listener.LauncherListener;
import br.com.plutomc.pvp.arena.listener.PlayerListener;
import br.com.plutomc.pvp.arena.manager.KitManager;
import org.bukkit.Bukkit;

public class GameMain extends GameAPI {
   private static GameMain instance;
   private KitManager kitManager;

   @Override
   public void onLoad() {
      super.onLoad();
      instance = this;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.setGamerClass(Gamer.class);
      this.setDropItems(true);
      this.kitManager = new KitManager();
      Bukkit.getPluginManager().registerEvents(new LauncherListener(), this);
      Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
      Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
   }

   public KitManager getKitManager() {
      return this.kitManager;
   }

   public static GameMain getInstance() {
      return instance;
   }
}
