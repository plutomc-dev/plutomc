package br.com.plutomc.pvp.arena.gamer;

import java.util.UUID;

import br.com.plutomc.pvp.arena.kit.Kit;

public class Gamer extends br.com.plutomc.pvp.engine.gamer.Gamer {
   private Kit primaryKit;
   private Kit secondaryKit;

   public Gamer(UUID uniqueId) {
      super(uniqueId);
   }

   public String getPrimary() {
      return this.primaryKit == null ? "Nenhum" : this.primaryKit.getName();
   }

   public String getSecondary() {
      return this.secondaryKit == null ? "Nenhum" : this.secondaryKit.getName();
   }

   public void setPrimaryKit(Kit primaryKit) {
      if (this.primaryKit != null) {
         this.primaryKit.removePlayer(this.getUniqueId());
      }

      this.primaryKit = primaryKit;
      if (this.primaryKit != null) {
         this.primaryKit.addPlayer(this.getUniqueId());
      }
   }

   public void setSecondaryKit(Kit secondaryKit) {
      if (this.secondaryKit != null) {
         this.secondaryKit.removePlayer(this.getUniqueId());
      }

      this.secondaryKit = secondaryKit;
      if (this.secondaryKit != null) {
         this.secondaryKit.addPlayer(this.getUniqueId());
      }
   }

   public Kit getPrimaryKit() {
      return this.primaryKit;
   }

   public Kit getSecondaryKit() {
      return this.secondaryKit;
   }
}
