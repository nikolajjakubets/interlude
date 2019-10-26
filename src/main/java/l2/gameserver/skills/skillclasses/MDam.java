//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class MDam extends Skill {
  public MDam(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (this._targetType != SkillTargetType.TARGET_AREA_AIM_CORPSE || target != null && target.isDead() && (target.isNpc() || target.isSummon())) {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    } else {
      activeChar.sendPacket(SystemMsg.INVALID_TARGET);
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    int sps = this.isSSPossible() ? (this.isMagic() ? activeChar.getChargedSpiritShot() : (activeChar.getChargedSoulShot() ? 2 : 0)) : 0;
    Iterator var6 = targets.iterator();

    while(var6.hasNext()) {
      Creature target = (Creature)var6.next();
      if (target != null && !target.isDead()) {
        boolean reflected = target.checkReflectSkill(activeChar, this);
        Creature realTarget = reflected ? activeChar : target;
        double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
        if (damage >= 1.0D) {
          realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
        }

        this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
      }
    }

    if (this.isSuicideAttack()) {
      activeChar.doDie((Creature)null);
    } else if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

    if (this._targetType == SkillTargetType.TARGET_AREA_AIM_CORPSE && targets.size() > 0) {
      Creature corpse = (Creature)targets.get(0);
      if (corpse != null && corpse.isDead()) {
        if (corpse.isNpc()) {
          ((NpcInstance)corpse).endDecayTask();
        } else if (corpse.isSummon()) {
          ((SummonInstance)corpse).endDecayTask();
        }

        activeChar.getAI().setAttackTarget((Creature)null);
      }
    }

  }
}
