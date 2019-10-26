//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Env;

public class EffectEnervation extends Effect {
  public EffectEnervation(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (this._effected.isNpc()) {
      ((NpcInstance)this._effected).setParameter("DebuffIntention", 0.5D);
    }

  }

  public boolean onActionTime() {
    return false;
  }

  public void onExit() {
    super.onExit();
    if (this._effected.isNpc()) {
      ((NpcInstance)this._effected).setParameter("DebuffIntention", 1.0D);
    }

  }
}
