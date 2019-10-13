//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.StopMove;
import l2.gameserver.utils.Location;

public class CannotMoveAnymore extends L2GameClientPacket {
  private Location _loc = new Location();

  public CannotMoveAnymore() {
  }

  protected void readImpl() {
    this._loc.x = this.readD();
    this._loc.y = this.readD();
    this._loc.z = this.readD();
    this._loc.h = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isOlyObserver()) {
        activeChar.sendPacket(new StopMove(activeChar.getObjectId(), this._loc));
      } else {
        if (!activeChar.isOutOfControl()) {
          activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, this._loc, (Object)null);
          activeChar.stopMove();
        }

      }
    }
  }
}
