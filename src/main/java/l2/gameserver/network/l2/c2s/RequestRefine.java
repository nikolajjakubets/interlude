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
import l2.gameserver.network.l2.s2c.ExPutCommissionResultForVariationMake;

public final class RequestRefine extends L2GameClientPacket {
  private static final int GEMSTONE_D = 2130;
  private static final int GEMSTONE_C = 2131;
  private static final int GEMSTONE_B = 2132;
  private int _targetItemObjId;
  private int _refinerItemObjId;
  private int _gemstoneItemObjId;
  private long _gemstoneCount;

  public RequestRefine() {
  }

  protected void readImpl() {
    this._targetItemObjId = this.readD();
    this._refinerItemObjId = this.readD();
    this._gemstoneItemObjId = this.readD();
    this._gemstoneCount = (long)this.readD();
  }

  protected void runImpl() {
    if (this._gemstoneCount > 0L) {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
        ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(this._targetItemObjId);
        ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(this._refinerItemObjId);
        ItemInstance gemstoneItem = activeChar.getInventory().getItemByObjectId(this._gemstoneItemObjId);
        IRefineryHandler IRefineryHandler = activeChar.getRefineryHandler();
        if (targetItem != null && refinerItem != null && gemstoneItem != null && IRefineryHandler != null) {
          IRefineryHandler.onRequestRefine(activeChar, targetItem, refinerItem, gemstoneItem, this._gemstoneCount);
        } else {
          activeChar.sendPacket(new IStaticPacket[]{Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ExPutCommissionResultForVariationMake.FAIL_PACKET});
        }
      }
    }
  }
}
