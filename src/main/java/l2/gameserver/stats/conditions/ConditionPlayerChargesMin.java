//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerChargesMin extends Condition {
  private final int _minCharges;

  public ConditionPlayerChargesMin(int minCharges) {
    this._minCharges = minCharges;
  }

  protected boolean testImpl(Env env) {
    if (env.character != null && env.character.isPlayer()) {
      return ((Player)env.character).getIncreasedForce() >= this._minCharges;
    } else {
      return false;
    }
  }
}
