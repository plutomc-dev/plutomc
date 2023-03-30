package br.com.plutomc.core.bukkit.menu.profile;

import java.util.ArrayList;
import java.util.List;

import br.com.plutomc.core.bukkit.utils.item.ItemBuilder;
import br.com.plutomc.core.bukkit.utils.menu.MenuInventory;
import br.com.plutomc.core.bukkit.utils.menu.MenuItem;
import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.common.CommonPlugin;
import br.com.plutomc.core.common.PluginInfo;
import br.com.plutomc.core.common.member.Member;
import br.com.plutomc.core.common.punish.Punish;
import br.com.plutomc.core.common.punish.PunishType;
import br.com.plutomc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class PunishmentInventory {
   public PunishmentInventory(Player player) {
      Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
      MenuInventory menuInventory = new MenuInventory(
         PluginInfo.t(member, "inventory.punishment.title", "%player%", member.getPlayerName()), InventoryType.HOPPER
      );
      menuInventory.setItem(
         0,
         new ItemBuilder().name("§%inventory.punishment.item.ban-name%§").type(Material.PAPER).lore("§%inventory.punishment.item.ban-description%§").build(),
         (p, inv, type, stack, slot) -> new PunishmentInventory(player, PunishType.BAN, 1)
      );
      menuInventory.setItem(
         1,
         new ItemBuilder().name("§%inventory.punishment.item.mute-name%§").type(Material.PAPER).lore("§%inventory.punishment.item.mute-description%§").build(),
         (p, inv, type, stack, slot) -> new PunishmentInventory(player, PunishType.MUTE, 1)
      );
      menuInventory.setItem(
         2,
         new ItemBuilder().name("§%inventory.punishment.item.kick-name%§").type(Material.PAPER).lore("§%inventory.punishment.item.kick-description%§").build(),
         (p, inv, type, stack, slot) -> new PunishmentInventory(player, PunishType.KICK, 1)
      );
      menuInventory.setItem(4, new ItemBuilder().name("§a§%back%§").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new ProfileInventory(player));
      menuInventory.open(player);
   }

   public PunishmentInventory(Player player, Punish punish, int page) {
      Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
      MenuInventory menuInventory = new MenuInventory(PluginInfo.t(member, "inventory.punishment.title", "%player%", member.getPlayerName()), 3);
      menuInventory.setItem(10, new ItemBuilder().name("§a" + punish.getId()).lore(CommonConst.GSON_PRETTY.toJson(punish)).type(Material.PAPER).build());
      menuInventory.setItem(
         11,
         new ItemBuilder()
            .name(PluginInfo.t(member, "inventory.punishment.item.reason-name"))
            .lore(PluginInfo.t(member, "inventory.punishment.item.reason-description", "%reason%", punish.getPunishReason()))
            .type(Material.PAPER)
            .build()
      );
      menuInventory.setItem(
         12,
         new ItemBuilder()
            .name(PluginInfo.t(member, "inventory.punishment.item.date-name"))
            .lore(
               PluginInfo.t(
                  member,
                  "inventory.punishment.item.date-description",
                  "%createAt%",
                  CommonConst.DATE_FORMAT.format(punish.getCreatedAt()),
                  "%expireAt%",
                  punish.isPermanent() ? "Never" : CommonConst.DATE_FORMAT.format(punish.getExpireAt()),
                  "%duration%",
                  punish.isPermanent() ? "Permanent" : DateUtils.getTime(member.getLanguage(), punish.getExpireAt())
               )
            )
            .type(Material.WATCH)
            .build()
      );
      if (punish.isUnpunished() || punish.hasExpired()) {
         menuInventory.setItem(13, new ItemBuilder().name("§aOK").type(Material.BARRIER).build());
      }

      menuInventory.setItem(
         16,
         new ItemBuilder().name("§a§%back%§").type(Material.ARROW).build(),
         (p, inv, type, stack, slot) -> new PunishmentInventory(player, punish.getPunishType(), page)
      );
      menuInventory.open(player);
   }

   public PunishmentInventory(Player player, PunishType punishType, int page) {
      Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
      MenuInventory menuInventory = new MenuInventory(PluginInfo.t(member, "inventory.punishment.title", "%player%", member.getPlayerName()), 5);
      List<MenuItem> items = new ArrayList<>();

      for(Punish punish : member.getPunishConfiguration().getPunish(punishType)) {
         items.add(
            new MenuItem(
               new ItemBuilder()
                  .name(PluginInfo.t(member, "inventory.punishment.item.info-name", "%id%", punish.getId().replace("#", "")))
                  .lore(
                     PluginInfo.t(
                           member,
                           "inventory.punishment.item.info-description",
                           "%punisher%",
                           punish.getPunisherName(),
                           "%reason%",
                           punish.getPunishReason(),
                           "%createAt%",
                           CommonConst.DATE_FORMAT.format(punish.getCreatedAt()),
                           "%expireAt%",
                           punish.isPermanent() ? "Never" : CommonConst.DATE_FORMAT.format(punish.getExpireAt()),
                           "%duration%",
                           punish.isPermanent() ? "Permanent" : DateUtils.getTime(member.getLanguage(), punish.getExpireAt()),
                           "%id%",
                           punish.getId().replace("#", "")
                        )
                        + (
                           punish.isUnpunished()
                              ? "§%inventory.punishment.item.info-description-pardoned%§"
                              : (punish.hasExpired() ? "§%inventory.punishment.item.info-description-expired%§" : "")
                        )
                  )
                  .type(Material.PAPER)
                  .build(),
               (p, inv, type, stack, s) -> new PunishmentInventory(player, punish, page)
            )
         );
      }

      int itemsPerPage = 21;
      int pageStart = 0;
      int pageEnd = itemsPerPage;
      if (page > 1) {
         pageStart = (page - 1) * itemsPerPage;
         pageEnd = page * itemsPerPage;
      }

      if (pageEnd > items.size()) {
         pageEnd = items.size();
      }

      int w = 10;

      for(int i = pageStart; i < pageEnd; ++i) {
         MenuItem item = items.get(i);
         menuInventory.setItem(item, w);
         if (w % 9 == 7) {
            w += 3;
         } else {
            ++w;
         }
      }

      if (page == 1) {
         menuInventory.setItem(
            39, new ItemBuilder().name("§a§%back%§").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new PunishmentInventory(player)
         );
      } else {
         menuInventory.setItem(
            new MenuItem(
               new ItemBuilder().type(Material.ARROW).name("§a§%page%§ " + (page - 1)).build(),
               (p, inv, type, stack, s) -> new PunishmentInventory(player, punishType, page - 1)
            ),
            39
         );
      }

      if (Math.ceil((double)(items.size() / itemsPerPage)) + 1.0 > (double)page) {
         menuInventory.setItem(
            new MenuItem(
               new ItemBuilder().type(Material.ARROW).name("§a§%page%§ " + (page + 1)).build(),
               (p, inventory, clickType, itemx, slot) -> new PunishmentInventory(player, punishType, page + 1)
            ),
            41
         );
      }

      menuInventory.open(player);
   }
}
