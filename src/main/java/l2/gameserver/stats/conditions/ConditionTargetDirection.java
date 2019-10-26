//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.PositionUtils.TargetDirection;

public class ConditionTargetDirection extends Condition {
  private final TargetDirection _dir;

  public ConditionTargetDirection(TargetDirection direction) {
    this._dir = direction;
  }

  protected boolean testImpl(Env env) {
    return PositionUtils.getDirectionTo(env.target, env.character) == this._dir;
  }
}
