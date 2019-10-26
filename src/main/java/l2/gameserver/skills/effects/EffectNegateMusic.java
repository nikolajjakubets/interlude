//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

import java.util.Iterator;

public class EffectNegateMusic extends Effect {
  public EffectNegateMusic(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
  }

  public void onExit() {
    super.onExit();
  }

  public boolean onActionTime() {
    Iterator var1 = this._effected.getEffectList().getAllEffects().iterator();

    while(var1.hasNext()) {
      Effect e = (Effect)var1.next();
      if (e.getSkill().isMusic()) {
        e.exit();
      }
    }

    return false;
  }
}
