//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class DefuseTrap extends Skill {
  public DefuseTrap(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (target != null && target.isTrap()) {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && target.isTrap()) {
        TrapInstance trap = (TrapInstance)target;
        if ((double)trap.getLevel() <= this.getPower()) {
          trap.deleteMe();
        }
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
