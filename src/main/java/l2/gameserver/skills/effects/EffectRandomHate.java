//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.model.AggroList.AggroInfo;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.stats.Env;

import java.util.List;

public class EffectRandomHate extends Effect {
  public EffectRandomHate(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return this.getEffected().isMonster() && Rnd.chance(this._template.chance(100));
  }

  public void onStart() {
    MonsterInstance monster = (MonsterInstance)this.getEffected();
    Creature mostHated = monster.getAggroList().getMostHated();
    if (mostHated != null) {
      AggroInfo mostAggroInfo = monster.getAggroList().get(mostHated);
      List<Creature> hateList = monster.getAggroList().getHateList(monster.getAggroRange());
      hateList.remove(mostHated);
      if (!hateList.isEmpty()) {
        AggroInfo newAggroInfo = monster.getAggroList().get((Creature)hateList.get(Rnd.get(hateList.size())));
        int oldHate = newAggroInfo.hate;
        newAggroInfo.hate = mostAggroInfo.hate;
        mostAggroInfo.hate = oldHate;
      }

    }
  }

  public boolean isHidden() {
    return true;
  }

  protected boolean onActionTime() {
    return false;
  }
}
