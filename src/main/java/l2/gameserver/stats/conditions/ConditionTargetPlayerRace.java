//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.stats.Env;

public class ConditionTargetPlayerRace extends Condition {
  private final Race _race;

  public ConditionTargetPlayerRace(String race) {
    this._race = Race.valueOf(race.toLowerCase());
  }

  protected boolean testImpl(Env env) {
    Creature target = env.target;
    return target != null && target.isPlayer() && this._race == ((Player)target).getRace();
  }
}
