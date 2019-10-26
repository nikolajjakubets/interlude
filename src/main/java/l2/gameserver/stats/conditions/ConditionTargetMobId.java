//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionTargetMobId extends Condition {
  private final int _mobId;

  public ConditionTargetMobId(int mobId) {
    this._mobId = mobId;
  }

  protected boolean testImpl(Env env) {
    return env.target != null && env.target.getNpcId() == this._mobId;
  }
}
