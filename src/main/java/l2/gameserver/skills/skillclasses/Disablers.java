//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class Disablers extends Skill {
  private final boolean _skillInterrupt;

  public Disablers(StatsSet set) {
    super(set);
    this._skillInterrupt = set.getBool("skillInterrupt", false);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var5 = targets.iterator();

    while(var5.hasNext()) {
      Creature target = (Creature)var5.next();
      if (target != null) {
        boolean reflected = target.checkReflectSkill(activeChar, this);
        Creature realTarget = reflected ? activeChar : target;
        if (this._skillInterrupt) {
          if (realTarget.getCastingSkill() != null && !realTarget.getCastingSkill().isMagic() && !realTarget.isRaid()) {
            realTarget.abortCast(false, true);
          }

          if (!realTarget.isRaid()) {
            realTarget.abortAttack(true, true);
          }
        }

        this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
