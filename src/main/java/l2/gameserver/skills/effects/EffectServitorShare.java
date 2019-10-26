//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Summon;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;

public class EffectServitorShare extends Effect {
  public EffectServitorShare(Env env, EffectTemplate template) {
    super(env, template);
  }

  protected void onStart() {
    super.onStart();
    if (this._effector.isPlayer() && !this._effector.isAlikeDead()) {
      Summon target = this._effector.getPet();
      if (target != null && !target.isAlikeDead()) {
        target.addStatFuncs(this.getShareFuncs());
      }
    }

  }

  protected void onExit() {
    if (this._effector.isPlayer() && !this._effector.isAlikeDead()) {
      Summon target = this._effector.getPet();
      if (target != null && !target.isAlikeDead()) {
        target.removeStatsOwner(this);
      }
    }

    super.onExit();
  }

  public Func[] getStatFuncs() {
    return Func.EMPTY_FUNC_ARRAY;
  }

  private Func[] getShareFuncs() {
    FuncTemplate[] funcTemplates = this.getTemplate().getAttachedFuncs();
    Func[] funcs = new Func[funcTemplates.length];

    for(int i = 0; i < funcs.length; ++i) {
      funcs[i] = new EffectServitorShare.FuncShare(funcTemplates[i]._stat, funcTemplates[i]._order, this, funcTemplates[i]._value);
    }

    return funcs;
  }

  protected boolean onActionTime() {
    return false;
  }

  public class FuncShare extends Func {
    public FuncShare(Stats stat, int order, Object owner, double value) {
      super(stat, order, owner, value);
    }

    public void calc(Env env) {
      env.value += env.character.getPlayer().calcStat(this.stat, this.stat.getInit()) * this.value;
    }
  }
}
