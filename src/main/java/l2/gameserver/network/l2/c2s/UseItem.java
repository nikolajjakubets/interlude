//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExUseSharedGroupItem;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.PetDataTable;

public class UseItem extends L2GameClientPacket {
  private int _objectId;
  private boolean _ctrlPressed;

  public UseItem() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._ctrlPressed = this.readD() == 1;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setActive();
      ItemInstance item = activeChar.getInventory().getItemByObjectId(this._objectId);
      if (item == null) {
        activeChar.sendActionFailed();
      } else {
        int itemId = item.getItemId();
        if (activeChar.isInStoreMode()) {
          if (PetDataTable.isPetControlItem(item)) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
          } else {
            activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP);
          }

        } else if (activeChar.isFishing() && (itemId < 6535 || itemId > 6540)) {
          activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
        } else if (activeChar.isSharedGroupDisabled(item.getTemplate().getReuseGroup())) {
          activeChar.sendReuseMessage(item);
        } else if (item.getTemplate().testCondition(activeChar, item, true)) {
          if (!activeChar.getInventory().isLockedItem(item)) {
            if (item.getTemplate().isForPet()) {
              activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_EQUIP_A_PET_ITEM);
            } else if (Config.ALT_IMPROVED_PETS_LIMITED_USE && activeChar.isMageClass() && item.getItemId() == 10311) {
              activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addItemName(itemId));
            } else if (Config.ALT_IMPROVED_PETS_LIMITED_USE && !activeChar.isMageClass() && item.getItemId() == 10313) {
              activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addItemName(itemId));
            } else if (activeChar.isOutOfControl()) {
              activeChar.sendActionFailed();
            } else {
              boolean success = item.getTemplate().getHandler().useItem(activeChar, item, this._ctrlPressed);
              if (success) {
                long nextTimeUse = item.getTemplate().getReuseType().next(item);
                if (nextTimeUse > System.currentTimeMillis()) {
                  TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, (long)item.getTemplate().getReuseDelay());
                  activeChar.addSharedGroupReuse(item.getTemplate().getReuseGroup(), timeStamp);
                  if (item.getTemplate().getReuseDelay() > 0) {
                    activeChar.sendPacket(new ExUseSharedGroupItem(item.getTemplate().getDisplayReuseGroup(), timeStamp));
                  }
                }
              } else {
                activeChar.sendActionFailed();
              }

            }
          }
        }
      }
    }
  }
}
