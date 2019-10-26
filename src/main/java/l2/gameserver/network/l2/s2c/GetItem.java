//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.Location;

public class GetItem extends L2GameServerPacket {
  private int _playerId;
  private int _itemObjId;
  private Location _loc;

  public GetItem(ItemInstance item, int playerId) {
    this._itemObjId = item.getObjectId();
    this._loc = item.getLoc();
    this._playerId = playerId;
  }

  protected final void writeImpl() {
    this.writeC(13);
    this.writeD(this._playerId);
    this.writeD(this._itemObjId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
