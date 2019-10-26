//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import gnu.trove.TIntHashSet;
import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;

public class ConditionTargetForbiddenClassId extends Condition {
  private TIntHashSet _classIds = new TIntHashSet();

  public ConditionTargetForbiddenClassId(String[] ids) {
    String[] var2 = ids;
    int var3 = ids.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String id = var2[var4];
      this._classIds.add(Integer.parseInt(id));
    }

  }

  protected boolean testImpl(Env env) {
    Creature target = env.target;
    if (!target.isPlayable()) {
      return false;
    } else {
      return !target.isPlayer() || !this._classIds.contains(target.getPlayer().getActiveClassId());
    }
  }
}
