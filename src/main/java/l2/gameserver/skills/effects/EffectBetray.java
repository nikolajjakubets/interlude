//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Summon;
import l2.gameserver.stats.Env;

public class EffectBetray extends Effect {
  public EffectBetray(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (this._effected != null && this._effected.isSummon()) {
      Summon summon = (Summon)this._effected;
      summon.setDepressed(true);
      summon.getAI().Attack(summon.getPlayer(), true, false);
    }

  }

  public void onExit() {
    super.onExit();
    if (this._effected != null && this._effected.isSummon()) {
      Summon summon = (Summon)this._effected;
      summon.setDepressed(false);
      summon.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
