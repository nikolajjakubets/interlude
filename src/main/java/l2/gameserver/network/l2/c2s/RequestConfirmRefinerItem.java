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
import l2.gameserver.network.l2.s2c.ExPutIntensiveResultForVariationMake;

public class RequestConfirmRefinerItem extends L2GameClientPacket {
  private int _targetItemObjId;
  private int _refinerItemObjId;

  public RequestConfirmRefinerItem() {
  }

  protected void readImpl() {
    this._targetItemObjId = this.readD();
    this._refinerItemObjId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(this._targetItemObjId);
      ItemInstance mineralItem = activeChar.getInventory().getItemByObjectId(this._refinerItemObjId);
      IRefineryHandler refineryHandler = activeChar.getRefineryHandler();
      if (targetItem != null && mineralItem != null && refineryHandler != null) {
        refineryHandler.onPutMineralItem(activeChar, targetItem, mineralItem);
      } else {
        activeChar.sendPacket(new IStaticPacket[]{Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ExPutIntensiveResultForVariationMake.FAIL_PACKET});
      }
    }
  }
}
