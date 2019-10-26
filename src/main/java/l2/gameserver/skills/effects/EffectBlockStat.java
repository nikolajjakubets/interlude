//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.skillclasses.NegateStats;
import l2.gameserver.stats.Env;

public class EffectBlockStat extends Effect {
  public EffectBlockStat(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this._effected.addBlockStats(((NegateStats)this._skill).getNegateStats());
  }

  public void onExit() {
    super.onExit();
    this._effected.removeBlockStats(((NegateStats)this._skill).getNegateStats());
  }

  public boolean onActionTime() {
    return false;
  }
}
