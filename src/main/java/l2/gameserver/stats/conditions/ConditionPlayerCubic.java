//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class ConditionPlayerCubic extends Condition {
  private int _id;

  public ConditionPlayerCubic(int id) {
    this._id = id;
  }

  protected boolean testImpl(Env env) {
    if (env.target != null && env.target.isPlayer()) {
      Player targetPlayer = (Player)env.target;
      if (targetPlayer.getCubic(this._id) != null) {
        return true;
      } else {
        int size = (int)targetPlayer.calcStat(Stats.CUBICS_LIMIT, 1.0D);
        if (targetPlayer.getCubics().size() >= size) {
          if (env.character == targetPlayer) {
            targetPlayer.sendPacket(Msg.CUBIC_SUMMONING_FAILED);
          }

          return false;
        } else {
          return true;
        }
      }
    } else {
      return false;
    }
  }
}
