//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class AdminCreateItem implements IAdminCommandHandler {
  public AdminCreateItem() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminCreateItem.Commands command = (AdminCreateItem.Commands)comm;
    if (!activeChar.getPlayerAccess().UseGMShop) {
      return false;
    } else {
      int item_id;
      int elementId;
      switch(command) {
        case admin_itemcreate:
          activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/itemcreation.htm"));
          break;
        case admin_ci:
        case admin_create_item:
          try {
            if (wordList.length < 2) {
              activeChar.sendMessage("USAGE: create_item id [count]");
              return false;
            }

            item_id = Integer.parseInt(wordList[1]);
            long item_count = wordList.length < 3 ? 1L : Long.parseLong(wordList[2]);
            this.createItem(activeChar, item_id, item_count);
          } catch (NumberFormatException var12) {
            activeChar.sendMessage("USAGE: create_item id [count]");
          }

          activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/itemcreation.htm"));
          break;
        case admin_spreaditem:
          try {
            item_id = Integer.parseInt(wordList[1]);
            elementId = wordList.length > 2 ? Integer.parseInt(wordList[2]) : 1;
            long count = wordList.length > 3 ? Long.parseLong(wordList[3]) : 1L;

            for(int i = 0; i < elementId; ++i) {
              ItemInstance createditem = ItemFunctions.createItem(item_id);
              createditem.setCount(count);
              createditem.dropMe(activeChar, Location.findPointToStay(activeChar, 100));
            }

            return true;
          } catch (NumberFormatException var13) {
            activeChar.sendMessage("Specify a valid number.");
          } catch (StringIndexOutOfBoundsException var14) {
            activeChar.sendMessage("Can't create this item.");
          }
          break;
        case admin_create_item_element:
          try {
            if (wordList.length < 4) {
              activeChar.sendMessage("USAGE: create_item_attribue [id] [element id] [value]");
              return false;
            }

            item_id = Integer.parseInt(wordList[1]);
            elementId = Integer.parseInt(wordList[2]);
            int value = Integer.parseInt(wordList[3]);
            if (elementId > 5 || elementId < 0) {
              activeChar.sendMessage("Improper element Id");
              return false;
            }

            if (value < 1 || value > 300) {
              activeChar.sendMessage("Improper element value");
              return false;
            }

            ItemInstance item = this.createItem(activeChar, item_id, 1L);
            Element element = Element.getElementById(elementId);
            item.setAttributeElement(element, item.getAttributeElementValue(element, false) + value);
            activeChar.sendPacket((new InventoryUpdate()).addModifiedItem(item));
          } catch (NumberFormatException var15) {
            activeChar.sendMessage("USAGE: create_item id [count]");
          }

          activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("data/html/admin/itemcreation.htm"));
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminCreateItem.Commands.values();
  }

  private ItemInstance createItem(Player activeChar, int itemId, long count) {
    ItemInstance createditem = ItemFunctions.createItem(itemId);
    createditem.setCount(count);
    Log.LogItem(activeChar, ItemLog.Create, createditem);
    activeChar.getInventory().addItem(createditem);
    if (!createditem.isStackable()) {
      for(long i = 0L; i < count - 1L; ++i) {
        createditem = ItemFunctions.createItem(itemId);
        Log.LogItem(activeChar, ItemLog.Create, createditem);
        activeChar.getInventory().addItem(createditem);
      }
    }

    activeChar.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
    return createditem;
  }

  private static enum Commands {
    admin_itemcreate,
    admin_create_item,
    admin_ci,
    admin_spreaditem,
    admin_create_item_element;

    private Commands() {
    }
  }
}
