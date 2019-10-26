//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionPlayerMinMaxDamage extends Condition {
  private final double _min;
  private final double _max;

  public ConditionPlayerMinMaxDamage(double min, double max) {
    this._min = min;
    this._max = max;
  }

  protected boolean testImpl(Env env) {
    if (this._min > 0.0D && env.value < this._min) {
      return false;
    } else {
      return this._max <= 0.0D || env.value <= this._max;
    }
  }
}
