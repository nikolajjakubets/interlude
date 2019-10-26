//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;

public class RequestExCancelEnchantItem extends L2GameClientPacket {
  public RequestExCancelEnchantItem() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setEnchantScroll((ItemInstance)null);
      activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
    }

  }
}
