//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.items.ItemInstance;

public class SpawnItem extends L2GameServerPacket {
  private int _objectId;
  private int _itemId;
  private int _x;
  private int _y;
  private int _z;
  private int _stackable;
  private long _count;

  public SpawnItem(ItemInstance item) {
    this._objectId = item.getObjectId();
    this._itemId = item.getItemId();
    this._x = item.getX();
    this._y = item.getY();
    this._z = item.getZ();
    this._stackable = item.isStackable() ? 1 : 0;
    this._count = item.getCount();
  }

  protected final void writeImpl() {
    this.writeC(11);
    this.writeD(this._objectId);
    this.writeD(this._itemId);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z + Config.CLIENT_Z_SHIFT);
    this.writeD(this._stackable);
    this.writeD((int)this._count);
    this.writeD(0);
  }
}
