//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListSell;
import l2.gameserver.utils.TradeHelper;

public class RequestPrivateStoreSell extends L2GameClientPacket {
  public RequestPrivateStoreSell() {
  }

  protected void readImpl() throws Exception {
  }

  protected void runImpl() throws Exception {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.getSittingTask()) {
        activeChar.sendActionFailed();
      } else {
        if (activeChar.isInStoreMode()) {
          activeChar.setPrivateStoreType(0);
        } else if (!TradeHelper.checksIfCanOpenStore(activeChar, 1)) {
          activeChar.sendActionFailed();
          return;
        }

        activeChar.sendPacket(new PrivateStoreManageListSell(activeChar, false));
      }
    }
  }
}
