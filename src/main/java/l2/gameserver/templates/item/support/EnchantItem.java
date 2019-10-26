//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import l2.gameserver.templates.item.ItemTemplate.Grade;

public abstract class EnchantItem {
  private final int _itemId;
  private final double _chanceMod;
  private final Grade _grade;
  private final int _minLvl;
  private final int _maxLvl;
  private final EnchantTargetType _targetType;

  public EnchantItem(int itemId, double chanceMod, Grade grade, int minLvl, int maxLvl, EnchantTargetType ett) {
    this._itemId = itemId;
    this._chanceMod = chanceMod;
    this._grade = grade;
    this._minLvl = minLvl;
    this._maxLvl = maxLvl;
    this._targetType = ett;
  }

  public int getItemId() {
    return this._itemId;
  }

  public double getChanceMod() {
    return this._chanceMod;
  }

  public Grade getGrade() {
    return this._grade;
  }

  public int getMinLvl() {
    return this._minLvl;
  }

  public int getMaxLvl() {
    return this._maxLvl;
  }

  public EnchantTargetType getTargetType() {
    return this._targetType;
  }
}
