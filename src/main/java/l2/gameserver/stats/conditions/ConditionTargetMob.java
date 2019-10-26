//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionTargetMob extends Condition {
  private final boolean _isMob;

  public ConditionTargetMob(boolean isMob) {
    this._isMob = isMob;
  }

  protected boolean testImpl(Env env) {
    return env.target != null && env.target.isMonster() == this._isMob;
  }
}
