//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;

public class ExRpItemLink extends L2GameServerPacket {
  private ItemInfo _item;

  public ExRpItemLink(ItemInfo item) {
    this._item = item;
  }

  protected final void writeImpl() {
    this.writeEx(108);
    this.writeItemInfo(this._item);
  }
}
