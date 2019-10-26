//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionTargetInTheSameParty extends Condition {
  private final boolean _val;

  public ConditionTargetInTheSameParty(boolean val) {
    this._val = val;
  }

  protected boolean testImpl(Env env) {
    Creature creature = env.character;
    Creature targetCreature = env.target;
    if (creature.isPlayable() && targetCreature != null && targetCreature.isPlayable()) {
      Player player = creature.getPlayer();
      Player target = targetCreature.getPlayer();
      if (player == target) {
        return this._val;
      } else if (player.isInParty() && player.getParty() == target.getParty()) {
        return this._val;
      } else {
        return !this._val;
      }
    } else {
      return !this._val;
    }
  }
}
