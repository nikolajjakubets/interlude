//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

import java.util.Iterator;

public class EffectNegateEffects extends Effect {
  public EffectNegateEffects(Env env, EffectTemplate template) {
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

    while(true) {
      Effect e;
      do {
        if (!var1.hasNext()) {
          return false;
        }

        e = (Effect)var1.next();
      } while((e.getStackType().equals("none") || !e.getStackType().equals(this.getStackType()) && !e.getStackType().equals(this.getStackType2())) && (e.getStackType2().equals("none") || !e.getStackType2().equals(this.getStackType()) && !e.getStackType2().equals(this.getStackType2())));

      if (e.getStackOrder() <= this.getStackOrder()) {
        e.exit();
      }
    }
  }
}
