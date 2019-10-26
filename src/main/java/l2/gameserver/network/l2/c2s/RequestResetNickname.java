//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestResetNickname extends L2GameClientPacket {
  public RequestResetNickname() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getTitleColor() != 16777079) {
        activeChar.setTitleColor(16777079);
        activeChar.broadcastUserInfo(true);
      }

    }
  }
}
