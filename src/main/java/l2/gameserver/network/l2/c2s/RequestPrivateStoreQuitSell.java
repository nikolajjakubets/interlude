//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestPrivateStoreQuitSell extends L2GameClientPacket {
  public RequestPrivateStoreQuitSell() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isInStoreMode() && (activeChar.getPrivateStoreType() == 1 || activeChar.getPrivateStoreType() == 8)) {
        activeChar.setPrivateStoreType(0);
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
