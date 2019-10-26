//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestExBuySellUIClose extends L2GameClientPacket {
  public RequestExBuySellUIClose() {
  }

  protected void runImpl() {
  }

  protected void readImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setBuyListId(0);
      activeChar.sendItemList(true);
    }
  }
}
