//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerClassId extends Condition {
  private final int[] _classIds;

  public ConditionPlayerClassId(String[] ids) {
    this._classIds = new int[ids.length];

    for(int i = 0; i < ids.length; ++i) {
      this._classIds[i] = Integer.parseInt(ids[i]);
    }

  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else {
      int playerClassId = ((Player)env.character).getActiveClassId();
      int[] var3 = this._classIds;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        int id = var3[var5];
        if (playerClassId == id) {
          return true;
        }
      }

      return false;
    }
  }
}
