//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionPlayerPercentCp extends Condition {
  private final double _cp;

  public ConditionPlayerPercentCp(int cp) {
    this._cp = (double)cp / 100.0D;
  }

  protected boolean testImpl(Env env) {
    return env.character.getCurrentCpRatio() <= this._cp;
  }
}
