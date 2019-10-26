//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListBuy;
import l2.gameserver.utils.TradeHelper;

public class RequestPrivateStoreBuy extends L2GameClientPacket {
  public RequestPrivateStoreBuy() {
  }

  protected void readImpl() {
  }

  protected void runImpl() throws Exception {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.getSittingTask()) {
        activeChar.sendActionFailed();
      } else if (!TradeHelper.checksIfCanOpenStore(activeChar, 3)) {
        activeChar.sendActionFailed();
      } else {
        activeChar.setPrivateStoreType(0);
        activeChar.sendPacket(new PrivateStoreManageListBuy(activeChar));
      }
    }
  }
}
