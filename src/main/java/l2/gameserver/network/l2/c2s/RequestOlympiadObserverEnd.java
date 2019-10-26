//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestOlympiadObserverEnd extends L2GameClientPacket {
  public RequestOlympiadObserverEnd() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (Config.OLY_ENABLED && activeChar.isOlyObserver()) {
        activeChar.leaveOlympiadObserverMode();
      }

    }
  }
}
