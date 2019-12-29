//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;

public class Action extends L2GameClientPacket {
  private int _objectId;
  private int _actionId;

  public Action() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this.readD();
    this.readD();
    this.readD();
    this._actionId = this.readC();
  }

  protected void runImpl() {
    Player activeChar = this.getClient().getActiveChar();
    if (activeChar != null) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendActionFailed();
      } else {
        GameObject obj = activeChar.getVisibleObject(this._objectId);
        if (obj == null) {
          activeChar.sendActionFailed();
        } else {
          activeChar.setActive();
          if (activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != obj) {
            activeChar.sendActionFailed();
          } else if (activeChar.isFrozen()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.getStatic());
          } else {
            obj.onAction(activeChar, this._actionId == 1);
          }
        }
      }
    }
  }
}
