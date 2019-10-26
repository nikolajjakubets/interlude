//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.Location;

public class DropItem extends L2GameServerPacket {
  private Location _loc;
  private int _playerId;
  private int item_obj_id;
  private int item_id;
  private int _stackable;
  private long _count;

  public DropItem(ItemInstance item, int playerId) {
    this._playerId = playerId;
    this.item_obj_id = item.getObjectId();
    this.item_id = item.getItemId();
    this._loc = item.getLoc();
    this._stackable = item.isStackable() ? 1 : 0;
    this._count = item.getCount();
  }

  public DropItem(int dropperId, int itemObjId, int itemId, Location loc, boolean isStackable, int count) {
    this._playerId = dropperId;
    this.item_obj_id = itemObjId;
    this.item_id = itemId;
    this._loc = loc.clone();
    this._stackable = isStackable ? 1 : 0;
    this._count = (long)count;
  }

  protected final void writeImpl() {
    this.writeC(12);
    this.writeD(this._playerId);
    this.writeD(this.item_obj_id);
    this.writeD(this.item_id);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
    this.writeD(this._stackable);
    this.writeD((int)this._count);
    this.writeD(1);
  }
}
