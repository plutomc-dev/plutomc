package br.com.plutomc.lobby.core.lobby.scoreboard;

public class ChristmasAnimation implements ScoreboardAnimation {
   private String text;
   private int frame;
   private int frameLimit;

   public ChristmasAnimation(String text) {
      this.text = text.replace("MC", "");
      this.frameLimit = 1;
   }

   @Override
   public String next() {
      this.frame = this.frame == this.frameLimit ? 0 : this.frame + 1;
      return this.frame == 0 ? "§f§l" + this.text : "§c§l" + this.text;
   }

   public String getText() {
      return this.text;
   }

   public int getFrame() {
      return this.frame;
   }

   public int getFrameLimit() {
      return this.frameLimit;
   }
}
