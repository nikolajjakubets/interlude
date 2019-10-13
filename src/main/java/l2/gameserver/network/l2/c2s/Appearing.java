//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class Appearing extends L2GameClientPacket {
  public Appearing() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isLogoutStarted()) {
        activeChar.sendActionFailed();
      } else if (activeChar.getObserverMode() == 1) {
        activeChar.appearObserverMode();
      } else if (activeChar.getObserverMode() == 2) {
        activeChar.returnFromObserverMode();
      } else if (!activeChar.isTeleporting()) {
        activeChar.sendActionFailed();
      } else {
        activeChar.onTeleported();
      }
    }
  }
}
