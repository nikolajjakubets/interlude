//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.instances.player;

public class ShortCut {
  public static final int TYPE_ITEM = 1;
  public static final int TYPE_SKILL = 2;
  public static final int TYPE_ACTION = 3;
  public static final int TYPE_MACRO = 4;
  public static final int TYPE_RECIPE = 5;
  public static final int TYPE_TPBOOKMARK = 6;
  public static final int PAGE_NORMAL_0 = 0;
  public static final int PAGE_NORMAL_1 = 1;
  public static final int PAGE_NORMAL_2 = 2;
  public static final int PAGE_NORMAL_3 = 3;
  public static final int PAGE_NORMAL_4 = 4;
  public static final int PAGE_NORMAL_5 = 5;
  public static final int PAGE_NORMAL_6 = 6;
  public static final int PAGE_NORMAL_7 = 7;
  public static final int PAGE_NORMAL_8 = 8;
  public static final int PAGE_NORMAL_9 = 9;
  public static final int PAGE_NORMAL_10 = 10;
  public static final int PAGE_NORMAL_11 = 11;
  public static final int PAGE_MAX = 11;
  private final int _slot;
  private final int _page;
  private final int _type;
  private final int _id;
  private final int _level;
  private final int _characterType;

  public ShortCut(int slot, int page, int type, int id, int level, int characterType) {
    this._slot = slot;
    this._page = page;
    this._type = type;
    this._id = id;
    this._level = level;
    this._characterType = characterType;
  }

  public int getSlot() {
    return this._slot;
  }

  public int getPage() {
    return this._page;
  }

  public int getType() {
    return this._type;
  }

  public int getId() {
    return this._id;
  }

  public int getLevel() {
    return this._level;
  }

  public int getCharacterType() {
    return this._characterType;
  }

  public String toString() {
    return "ShortCut: " + this._slot + "/" + this._page + " ( " + this._type + "," + this._id + "," + this._level + "," + this._characterType + ")";
  }
}
