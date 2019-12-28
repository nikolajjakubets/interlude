//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.gameserver.model.AggroList.AggroInfo;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Guard extends Fighter {
  public Guard(NpcInstance actor) {
    super(actor);
  }

  public boolean canAttackCharacter(Creature target) {
    NpcInstance actor = this.getActor();
    if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
      AggroInfo ai = actor.getAggroList().get(target);
      return ai != null && ai.hate > 0;
    } else {
      return target.isMonster() || target.isPlayable();
    }
  }

  public boolean checkAggression(Creature target) {
    NpcInstance actor = this.getActor();
    if (this.getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && this.isGlobalAggro()) {
      if (target.isPlayable() && (target.getKarma() == 0 || actor.getParameter("evilGuard", false) && target.getPvpFlag() > 0)) {
        return false;
      } else {
        return !target.isMonster() && super.checkAggression(target);
      }
    } else {
      return false;
    }
  }

  public int getMaxAttackTimeout() {
    return 0;
  }

  protected boolean randomWalk() {
    return false;
  }
}
