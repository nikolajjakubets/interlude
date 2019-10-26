//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;

public class RequestPetGetItem extends L2GameClientPacket {
  private int _objectId;

  public RequestPetGetItem() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else {
        Summon summon = activeChar.getPet();
        if (summon != null && summon.isPet() && !summon.isDead() && !summon.isActionsDisabled()) {
          ItemInstance item = (ItemInstance)activeChar.getVisibleObject(this._objectId);
          if (item == null) {
            activeChar.sendActionFailed();
          } else if (!ItemFunctions.checkIfCanPickup(summon, item)) {
            SystemMessage sm;
            if (item.getItemId() == 57) {
              sm = new SystemMessage(55);
              sm.addNumber(item.getCount());
            } else {
              sm = new SystemMessage(56);
              sm.addItemName(item.getItemId());
            }

            this.sendPacket(sm);
            activeChar.sendActionFailed();
          } else {
            summon.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item, (Object)null);
          }
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
