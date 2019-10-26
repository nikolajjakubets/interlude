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
import l2.gameserver.network.l2.s2c.ExVariationCancelResult;

public final class RequestRefineCancel extends L2GameClientPacket {
  private int _targetItemObjId;

  public RequestRefineCancel() {
  }

  protected void readImpl() {
    this._targetItemObjId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      ItemInstance item = activeChar.getInventory().getItemByObjectId(this._targetItemObjId);
      IRefineryHandler refineryHandler = activeChar.getRefineryHandler();
      if (item != null && refineryHandler != null) {
        refineryHandler.onRequestCancelRefine(activeChar, item);
      } else {
        activeChar.sendPacket(new IStaticPacket[]{Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ExVariationCancelResult.FAIL_PACKET});
      }
    }
  }
}
