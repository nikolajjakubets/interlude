//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public class GMViewQuestItemList extends L2GameServerPacket {
  private int _size;
  private ItemInstance[] _items;
  private int _limit;
  private String _name;

  public GMViewQuestItemList(Player player, ItemInstance[] items, int size) {
    this._items = items;
    this._size = size;
    this._name = player.getName();
    this._limit = player.getInventoryLimit();
  }

  protected final void writeImpl() {
    this.writeC(147);
    this.writeS(this._name);
    this.writeD(this._limit);
    this.writeH(1);
    this.writeH(this._size);
    ItemInstance[] var1 = this._items;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance temp = var1[var3];
      if (temp.getTemplate().isQuest()) {
        this.writeItemInfo(temp);
      }
    }

  }
}
