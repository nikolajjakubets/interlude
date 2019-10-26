//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

public class SoulCrystal {
  private final int _itemId;
  private final int _level;
  private final int _nextItemId;
  private final int _cursedNextItemId;

  public SoulCrystal(int itemId, int level, int nextItemId, int cursedNextItemId) {
    this._itemId = itemId;
    this._level = level;
    this._nextItemId = nextItemId;
    this._cursedNextItemId = cursedNextItemId;
  }

  public int getItemId() {
    return this._itemId;
  }

  public int getLevel() {
    return this._level;
  }

  public int getNextItemId() {
    return this._nextItemId;
  }

  public int getCursedNextItemId() {
    return this._cursedNextItemId;
  }
}
