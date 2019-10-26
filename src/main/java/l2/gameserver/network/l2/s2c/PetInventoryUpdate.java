//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;

public class PetInventoryUpdate extends L2GameServerPacket {
  public static final int UNCHANGED = 0;
  public static final int ADDED = 1;
  public static final int MODIFIED = 2;
  public static final int REMOVED = 3;
  private final List<ItemInfo> _items = new ArrayList(1);

  public PetInventoryUpdate() {
  }

  public PetInventoryUpdate addNewItem(ItemInstance item) {
    this.addItem(item).setLastChange(1);
    return this;
  }

  public PetInventoryUpdate addModifiedItem(ItemInstance item) {
    this.addItem(item).setLastChange(2);
    return this;
  }

  public PetInventoryUpdate addRemovedItem(ItemInstance item) {
    this.addItem(item).setLastChange(3);
    return this;
  }

  private ItemInfo addItem(ItemInstance item) {
    ItemInfo info;
    this._items.add(info = new ItemInfo(item));
    return info;
  }

  protected final void writeImpl() {
    this.writeC(179);
    this.writeH(this._items.size());
    Iterator var1 = this._items.iterator();

    while(var1.hasNext()) {
      ItemInfo item = (ItemInfo)var1.next();
      this.writeH(item.getLastChange());
      this.writeH(item.getType1());
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD((int)item.getCount());
      this.writeH(item.getType2());
      this.writeH(item.getCustomType1());
      this.writeH(item.isEquipped() ? 1 : 0);
      this.writeD(item.getItem().getBodyPart());
      this.writeH(item.getEnchantLevel());
      this.writeH(item.getCustomType2());
    }

  }
}
