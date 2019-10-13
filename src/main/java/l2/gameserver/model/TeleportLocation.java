//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;

public class TeleportLocation extends Location {
  private final long _price;
  private final int _minLevel;
  private final int _maxLevel;
  private final ItemTemplate _item;
  private final String _name;
  private final int _castleId;

  public TeleportLocation(int item, long price, int minLevel, int maxLevel, String name, int castleId) {
    this._price = price;
    this._minLevel = minLevel;
    this._maxLevel = maxLevel;
    this._name = name;
    this._item = ItemHolder.getInstance().getTemplate(item);
    this._castleId = castleId;
  }

  public TeleportLocation(int item, long price, String name, int castleId) {
    this._price = price;
    this._minLevel = 0;
    this._maxLevel = 0;
    this._name = name;
    this._item = ItemHolder.getInstance().getTemplate(item);
    this._castleId = castleId;
  }

  public int getMinLevel() {
    return this._minLevel;
  }

  public int getMaxLevel() {
    return this._maxLevel;
  }

  public long getPrice() {
    return this._price;
  }

  public ItemTemplate getItem() {
    return this._item;
  }

  public String getName() {
    return this._name;
  }

  public int getCastleId() {
    return this._castleId;
  }
}
