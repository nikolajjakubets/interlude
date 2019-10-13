//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.BuyList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.GameStats;

public class AdminShop implements IAdminCommandHandler {
  public AdminShop() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminShop.Commands command = (AdminShop.Commands)comm;
    if (!activeChar.getPlayerAccess().UseGMShop) {
      return false;
    } else {
      switch(command) {
        case admin_buy:
          try {
            this.handleBuyRequest(activeChar, fullString.substring(10));
          } catch (IndexOutOfBoundsException var7) {
            activeChar.sendMessage("Please specify buylist.");
          }
          break;
        case admin_gmshop:
          activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/gmshops.htm"));
          break;
        case admin_tax:
          activeChar.sendMessage("TaxSum: " + GameStats.getTaxSum());
          break;
        case admin_taxclear:
          GameStats.addTax(-GameStats.getTaxSum());
          activeChar.sendMessage("TaxSum: " + GameStats.getTaxSum());
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminShop.Commands.values();
  }

  private void handleBuyRequest(Player activeChar, String command) {
    int val = -1;

    try {
      val = Integer.parseInt(command);
    } catch (Exception var5) {
    }

    NpcTradeList list = BuyListHolder.getInstance().getBuyList(val);
    if (list != null) {
      activeChar.sendPacket(new BuyList(list, activeChar, 0.0D));
    }

    activeChar.sendActionFailed();
  }

  private static enum Commands {
    admin_buy,
    admin_gmshop,
    admin_tax,
    admin_taxclear;

    private Commands() {
    }
  }
}
