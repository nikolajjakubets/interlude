//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerAgathion extends Condition {
  private final int _agathionId;

  public ConditionPlayerAgathion(int agathionId) {
    this._agathionId = agathionId;
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else if (((Player)env.character).getAgathionId() > 0 && this._agathionId == -1) {
      return true;
    } else {
      return ((Player)env.character).getAgathionId() == this._agathionId;
    }
  }
}
