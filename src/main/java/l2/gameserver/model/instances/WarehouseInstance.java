//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PackageToList;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.WarehouseFunctions;

public class WarehouseInstance extends NpcInstance {
  public WarehouseInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom = "";
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    return this.getTemplate().getHtmRoot() != null ? this.getTemplate().getHtmRoot() + pom + ".htm" : "warehouse/" + pom + ".htm";
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (player.getEnchantScroll() != null) {
        Log.add("Player " + player.getName() + " trying to use enchant exploit[Warehouse], ban this player!", "illegal-actions");
        player.setEnchantScroll((ItemInstance)null);
      } else {
        if (command.startsWith("deposit_items")) {
          player.sendPacket(new PackageToList(player));
        } else if (command.startsWith("withdraw_items")) {
          WarehouseFunctions.showFreightWindow(player);
        } else {
          int val;
          NpcHtmlMessage html;
          if (command.startsWith("WithdrawP")) {
            val = Integer.parseInt(command.substring(10));
            if (val == 99) {
              html = new NpcHtmlMessage(player, this);
              html.setFile("warehouse/personal.htm");
              html.replace("%npcname%", this.getName());
              player.sendPacket(html);
            } else {
              WarehouseFunctions.showRetrieveWindow(player, val);
            }
          } else if (command.equals("DepositP")) {
            WarehouseFunctions.showDepositWindow(player);
          } else if (command.startsWith("WithdrawC")) {
            val = Integer.parseInt(command.substring(10));
            if (val == 99) {
              html = new NpcHtmlMessage(player, this);
              html.setFile("warehouse/clan.htm");
              html.replace("%npcname%", this.getName());
              player.sendPacket(html);
            } else {
              WarehouseFunctions.showWithdrawWindowClan(player, val);
            }
          } else if (command.equals("DepositC")) {
            WarehouseFunctions.showDepositWindowClan(player);
          } else {
            super.onBypassFeedback(player, command);
          }
        }

      }
    }
  }
}
