//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerChargesMax extends Condition {
  private final int _maxCharges;

  public ConditionPlayerChargesMax(int maxCharges) {
    this._maxCharges = maxCharges;
  }

  protected boolean testImpl(Env env) {
    if (env.character != null && env.character.isPlayer()) {
      if (((Player)env.character).getIncreasedForce() >= this._maxCharges) {
        env.character.sendPacket(Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }
}
