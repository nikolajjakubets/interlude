//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestItemList extends L2GameClientPacket {
  public RequestItemList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getPlayerAccess().UseInventory && !activeChar.isBlocked()) {
        activeChar.sendItemList(true);
        activeChar.sendStatusUpdate(false, false, new int[]{14});
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
