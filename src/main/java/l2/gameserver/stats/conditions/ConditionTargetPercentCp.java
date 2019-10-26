//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionTargetPercentCp extends Condition {
  private final double _cp;

  public ConditionTargetPercentCp(int cp) {
    this._cp = (double)cp / 100.0D;
  }

  protected boolean testImpl(Env env) {
    return env.target != null && env.target.getCurrentCpRatio() <= this._cp;
  }
}
