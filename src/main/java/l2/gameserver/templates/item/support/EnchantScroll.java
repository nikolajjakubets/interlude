//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate.Grade;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class EnchantScroll extends EnchantItem {
  private final EnchantScrollOnFailAction _failResultType;
  private final boolean _isInfallible;
  private final boolean _hasAVE;
  private final int _failResultLevel;
  private final int _increment;
  private IntSet _items;

  public EnchantScroll(int itemId, int increment, double chanceMod, Grade scrollGrade, int minLvl, int maxLvl, EnchantTargetType ett, EnchantScrollOnFailAction frt, int lrl, boolean isInfallible, boolean hasAVE) {
    super(itemId, chanceMod, scrollGrade, minLvl, maxLvl, ett);
    this._items = Containers.EMPTY_INT_SET;
    this._increment = increment;
    this._failResultLevel = lrl;
    this._failResultType = frt;
    this._isInfallible = isInfallible;
    this._hasAVE = hasAVE;
  }

  public int getIncrement() {
    return this._increment;
  }

  public int getFailResultLevel() {
    return this._failResultLevel;
  }

  public EnchantScrollOnFailAction getOnFailAction() {
    return this._failResultType;
  }

  public void addItemRestrict(int item_type) {
    if (this._items.isEmpty()) {
      this._items = new HashIntSet();
    }

    this._items.add(item_type);
  }

  public boolean isHasAbnormalVisualEffect() {
    return this._hasAVE;
  }

  public boolean isInfallible() {
    return this._isInfallible;
  }

  public boolean isUsableWith(ItemInstance target) {
    if (!this._items.isEmpty() && !this._items.contains(target.getItemId())) {
      return false;
    } else {
      int toLvl = target.getEnchantLevel() + this.getIncrement();
      Grade itemGrade = target.getCrystalType();
      if (itemGrade.gradeOrd() != this.getGrade().gradeOrd()) {
        return false;
      } else if (toLvl >= this.getMinLvl() && toLvl <= this.getMaxLvl()) {
        return this.getTargetType().isUsableOn(target);
      } else {
        return false;
      }
    }
  }
}
