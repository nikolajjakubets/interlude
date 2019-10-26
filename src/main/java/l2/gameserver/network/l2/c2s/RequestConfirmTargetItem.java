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
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExPutItemResultForVariationMake;

public class RequestConfirmTargetItem extends L2GameClientPacket {
  private int _itemObjId;

  public RequestConfirmTargetItem() {
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
        refineryHandler.onPutTargetItem(activeChar, item);
      } else {
        activeChar.sendPacket(new IStaticPacket[]{Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ExPutItemResultForVariationMake.FAIL_PACKET});
      }
    }
  }
}
