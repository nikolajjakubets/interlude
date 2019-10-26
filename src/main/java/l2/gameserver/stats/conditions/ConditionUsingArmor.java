//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.templates.item.ArmorTemplate.ArmorType;

public class ConditionUsingArmor extends Condition {
  private final ArmorType _armor;

  public ConditionUsingArmor(ArmorType armor) {
    this._armor = armor;
  }

  protected boolean testImpl(Env env) {
    return env.character.isPlayer() && ((Player)env.character).isWearingArmor(this._armor);
  }
}
