//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionPlayerPercentMp extends Condition {
  private final double _mp;

  public ConditionPlayerPercentMp(int mp) {
    this._mp = (double)mp / 100.0D;
  }

  protected boolean testImpl(Env env) {
    return env.character.getCurrentMpRatio() <= this._mp;
  }
}
