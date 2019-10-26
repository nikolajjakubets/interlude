//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item;

public enum ItemFlags {
  DESTROYABLE(true),
  DROPABLE(true),
  FREIGHTABLE(false),
  AUGMENTABLE(true),
  ENCHANTABLE(true),
  ATTRIBUTABLE(true),
  SELLABLE(true),
  TRADEABLE(true),
  STOREABLE(true);

  public static final ItemFlags[] VALUES = values();
  private final int _mask;
  private final boolean _defaultValue;

  private ItemFlags(boolean defaultValue) {
    this._defaultValue = defaultValue;
    this._mask = 1 << this.ordinal();
  }

  public int mask() {
    return this._mask;
  }

  public boolean getDefaultValue() {
    return this._defaultValue;
  }
}
