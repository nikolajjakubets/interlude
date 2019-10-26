//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.stats.Env;

public class ConditionTargetAggro extends Condition {
  private final boolean _isAggro;

  public ConditionTargetAggro(boolean isAggro) {
    this._isAggro = isAggro;
  }

  protected boolean testImpl(Env env) {
    Creature target = env.target;
    if (target == null) {
      return false;
    } else if (target.isMonster()) {
      return ((MonsterInstance)target).isAggressive() == this._isAggro;
    } else if (target.isPlayer()) {
      return target.getKarma() > 0;
    } else {
      return false;
    }
  }
}
