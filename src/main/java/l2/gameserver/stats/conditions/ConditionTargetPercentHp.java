//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionTargetPercentHp extends Condition {
  private final double _hp;

  public ConditionTargetPercentHp(int hp) {
    this._hp = (double)hp / 100.0D;
  }

  protected boolean testImpl(Env env) {
    return env.target != null && env.target.getCurrentHpRatio() <= this._hp;
  }
}
