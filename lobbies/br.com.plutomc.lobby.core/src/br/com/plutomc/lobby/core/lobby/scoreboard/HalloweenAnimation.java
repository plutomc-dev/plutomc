package br.com.plutomc.lobby.core.lobby.scoreboard;

import java.util.List;

public class HalloweenAnimation implements ScoreboardAnimation {
   private String text;
   private List<String> colorList;
   private int frame;
   private int frameLimit;

   public HalloweenAnimation(String text, List<String> colorList) {
      this.text = text;
      this.colorList = colorList;
      this.frameLimit = colorList.size() - 1;
   }

   @Override
   public String next() {
      this.frame = this.frame == this.frameLimit ? 0 : this.frame + 1;
      if (this.frame % 2 == 0 && this.frame != 0) {
         return (String)this.colorList.get(this.frame)
            + this.text.substring(0, this.text.toCharArray().length / 2)
            + (String)this.colorList.get(this.frame - 1)
            + this.text.substring(this.text.length() / 2);
      } else {
         return this.frame % 3 == 0 && this.frame != 0
            ? (String)this.colorList.get(this.frame - 1)
               + this.text.substring(0, this.text.length() / 2)
               + (String)this.colorList.get(this.frame)
               + this.text.substring(this.text.length() / 2)
            : (String)this.colorList.get(this.frame) + this.text;
      }
   }

   public String getText() {
      return this.text;
   }

   public List<String> getColorList() {
      return this.colorList;
   }

   public int getFrame() {
      return this.frame;
   }

   public int getFrameLimit() {
      return this.frameLimit;
   }
}
