//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import l2.gameserver.model.items.ItemInstance;

public enum EnchantTargetType {
  ALL(true, true, true),
  WEAPON(true, false, false),
  ARMOR(false, true, true);

  private final boolean _useOnWeapon;
  private final boolean _useOnArmor;
  private final boolean _useOnAccessory;

  private EnchantTargetType(boolean weapon, boolean armor, boolean accesory) {
    this._useOnWeapon = weapon;
    this._useOnArmor = armor;
    this._useOnAccessory = accesory;
  }

  public boolean isUsableOn(ItemInstance item) {
    if (this._useOnWeapon && item.isWeapon()) {
      return true;
    } else if (this._useOnArmor && item.isArmor()) {
      return true;
    } else {
      return this._useOnAccessory && item.isAccessory();
    }
  }
}
