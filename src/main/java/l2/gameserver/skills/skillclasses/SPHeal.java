//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class SPHeal extends Skill {
  public SPHeal(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    return !activeChar.isPlayer() ? false : super.checkCondition(activeChar, target, forceUse, dontMove, first);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null) {
        target.getPlayer().addExpAndSp(0L, (long)this._power);
        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
