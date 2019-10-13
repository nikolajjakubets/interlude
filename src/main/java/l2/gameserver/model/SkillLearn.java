//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.gameserver.Config;
import l2.gameserver.model.base.ClassType2;

public final class SkillLearn implements Comparable<SkillLearn> {
  private final int _id;
  private final int _level;
  private final int _minLevel;
  private final int _cost;
  private final int _itemId;
  private final long _itemCount;
  private final boolean _clicked;
  private final ClassType2 _classType;
  private final boolean _autoLearn;

  public SkillLearn(int id, int lvl, int minLvl, int cost, int itemId, long itemCount, boolean clicked, boolean autoLearn) {
    this._id = id;
    this._level = lvl;
    this._minLevel = minLvl;
    this._cost = cost;
    this._itemId = itemId;
    this._itemCount = itemCount;
    this._clicked = clicked;
    this._classType = ClassType2.None;
    this._autoLearn = autoLearn;
  }

  public SkillLearn(int id, int lvl, int minLvl, int cost, int itemId, long itemCount, boolean clicked, ClassType2 classType, boolean autoLearn) {
    this._id = id;
    this._level = lvl;
    this._minLevel = minLvl;
    this._cost = cost;
    this._itemId = itemId;
    this._itemCount = itemCount;
    this._clicked = clicked;
    this._classType = classType;
    this._autoLearn = autoLearn;
  }

  public int getId() {
    return this._id;
  }

  public int getLevel() {
    return this._level;
  }

  public int getMinLevel() {
    return this._minLevel;
  }

  public int getCost() {
    return this._cost;
  }

  public int getItemId() {
    return this._itemId;
  }

  public long getItemCount() {
    return this._itemCount;
  }

  public boolean isClicked() {
    return this._clicked;
  }

  public boolean canAutoLearn() {
    if (!Config.AUTO_LEARN_FORGOTTEN_SKILLS && this.isClicked()) {
      return false;
    } else {
      return this._id == 1405 ? Config.AUTO_LEARN_DIVINE_INSPIRATION : this._autoLearn;
    }
  }

  public int compareTo(SkillLearn o) {
    return this.getId() == o.getId() ? this.getLevel() - o.getLevel() : this.getId() - o.getId();
  }

  public ClassType2 getClassType2() {
    return this._classType;
  }
}
