//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemAttributes;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.ExBaseAttributeCancelResult;
import l2.gameserver.network.l2.s2c.ExShowBaseAttributeCancelWindow;
import l2.gameserver.network.l2.s2c.InventoryUpdate;

public class RequestExRemoveItemAttribute extends L2GameClientPacket {
  private int _objectId;
  private int _attributeId;

  public RequestExRemoveItemAttribute() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._attributeId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (!activeChar.isActionsDisabled() && !activeChar.isInStoreMode() && !activeChar.isInTrade()) {
        PcInventory inventory = activeChar.getInventory();
        ItemInstance itemToUnnchant = inventory.getItemByObjectId(this._objectId);
        if (itemToUnnchant == null) {
          activeChar.sendActionFailed();
        } else {
          ItemAttributes set = itemToUnnchant.getAttributes();
          Element element = Element.getElementById(this._attributeId);
          if (element != Element.NONE && set.getValue(element) > 0) {
            if (!activeChar.reduceAdena(ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemToUnnchant), true)) {
              activeChar.sendPacket(new IStaticPacket[]{new ExBaseAttributeCancelResult(false, itemToUnnchant, element), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, ActionFail.STATIC});
            } else {
              boolean equipped = false;
              if (equipped = itemToUnnchant.isEquipped()) {
                activeChar.getInventory().unEquipItem(itemToUnnchant);
              }

              itemToUnnchant.setAttributeElement(element, 0);
              if (equipped) {
                activeChar.getInventory().equipItem(itemToUnnchant);
              }

              activeChar.sendPacket((new InventoryUpdate()).addModifiedItem(itemToUnnchant));
              activeChar.sendPacket(new ExBaseAttributeCancelResult(true, itemToUnnchant, element));
              activeChar.updateStats();
            }
          } else {
            activeChar.sendPacket(new IStaticPacket[]{new ExBaseAttributeCancelResult(false, itemToUnnchant, element), ActionFail.STATIC});
          }
        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
