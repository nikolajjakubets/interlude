//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;

public class ConditionTargetPlayerNotMe extends Condition {
  private final boolean _flag;

  public ConditionTargetPlayerNotMe(boolean flag) {
    this._flag = flag;
  }

  protected boolean testImpl(Env env) {
    Creature activeChar = env.character;
    Creature target = env.target;
    return activeChar != null && activeChar != target == this._flag;
  }
}
