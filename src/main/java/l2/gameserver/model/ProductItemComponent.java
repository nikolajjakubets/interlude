//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;

public class ProductItemComponent {
  private final int _itemId;
  private final int _count;
  private final int _weight;
  private final boolean _dropable;

  public ProductItemComponent(int item_id, int count) {
    this._itemId = item_id;
    this._count = count;
    ItemTemplate item = ItemHolder.getInstance().getTemplate(item_id);
    if (item != null) {
      this._weight = item.getWeight();
      this._dropable = item.isDropable();
    } else {
      this._weight = 0;
      this._dropable = true;
    }

  }

  public int getItemId() {
    return this._itemId;
  }

  public int getCount() {
    return this._count;
  }

  public int getWeight() {
    return this._weight;
  }

  public boolean isDropable() {
    return this._dropable;
  }
}
