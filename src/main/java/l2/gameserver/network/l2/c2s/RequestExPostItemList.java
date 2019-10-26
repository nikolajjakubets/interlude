//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExReplyPostItemList;

public class RequestExPostItemList extends L2GameClientPacket {
  public RequestExPostItemList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      }

      if (!Config.ALLOW_MAIL) {
        activeChar.sendMessage(new CustomMessage("mail.Disabled", activeChar, new Object[0]));
        activeChar.sendActionFailed();
      } else {
        activeChar.sendPacket(new ExReplyPostItemList(activeChar));
      }
    }
  }
}
