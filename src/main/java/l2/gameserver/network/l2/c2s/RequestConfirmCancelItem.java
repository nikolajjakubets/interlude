//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.items.IRefineryHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;

public class RequestConfirmCancelItem extends L2GameClientPacket {
  int _itemObjId;

  public RequestConfirmCancelItem() {
  }

  protected void readImpl() {
    this._itemObjId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      ItemInstance item = activeChar.getInventory().getItemByObjectId(this._itemObjId);
      IRefineryHandler refineryHandler = activeChar.getRefineryHandler();
      if (item != null && refineryHandler != null) {
        refineryHandler.onPutTargetCancelItem(activeChar, item);
      } else {
        activeChar.sendActionFailed();
        activeChar.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM);
      }
    }
  }
}
