//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class DestroySummon extends Skill {
  public DestroySummon(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(true) {
      while(true) {
        Creature target;
        do {
          if (!var3.hasNext()) {
            if (this.isSSPossible()) {
              activeChar.unChargeShots(this.isMagic());
            }

            return;
          }

          target = (Creature)var3.next();
        } while(target == null);

        if (this.getActivateRate() > 0 && !Formulas.calcSkillSuccess(activeChar, target, this, this.getActivateRate())) {
          activeChar.sendPacket((new SystemMessage(139)).addString(target.getName()).addSkillName(this.getId(), this.getLevel()));
        } else if (target.isSummon()) {
          ((Summon)target).saveEffects();
          ((Summon)target).unSummon();
          this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        }
      }
    }
  }
}
