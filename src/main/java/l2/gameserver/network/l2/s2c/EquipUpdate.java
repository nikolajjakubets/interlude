//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;

/** @deprecated */
@Deprecated
public class EquipUpdate extends L2GameServerPacket {
  private ItemInfo _item;

  public EquipUpdate(ItemInstance item, int change) {
    this._item = new ItemInfo(item);
    this._item.setLastChange(change);
  }

  protected final void writeImpl() {
    this.writeC(75);
    this.writeD(this._item.getLastChange());
    this.writeD(this._item.getObjectId());
    this.writeD(this._item.getEquipSlot());
  }
}
