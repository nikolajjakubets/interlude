//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Env;

public class EffectUnAggro extends Effect {
  public EffectUnAggro(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (this._effected.isNpc()) {
      ((NpcInstance)this._effected).setUnAggred(true);
    }

  }

  public void onExit() {
    super.onExit();
    if (this._effected.isNpc()) {
      ((NpcInstance)this._effected).setUnAggred(false);
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
