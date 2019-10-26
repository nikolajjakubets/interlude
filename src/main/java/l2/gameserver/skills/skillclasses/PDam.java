//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.FinishRotating;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.StartRotating;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Formulas.AttackInfo;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class PDam extends Skill {
  private final boolean _onCrit;
  private final boolean _directHp;
  private final boolean _turner;
  private final boolean _blow;

  public PDam(StatsSet set) {
    super(set);
    this._onCrit = set.getBool("onCrit", false);
    this._directHp = set.getBool("directHp", false);
    this._turner = set.getBool("turner", false);
    this._blow = set.getBool("blow", false);
  }

  public boolean isBlowSkill() {
    return this._blow;
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    boolean ss = activeChar.getChargedSoulShot() && this.isSSPossible();
    Iterator var6 = targets.iterator();

    while(true) {
      Creature target;
      do {
        do {
          if (!var6.hasNext()) {
            if (this.isSuicideAttack()) {
              activeChar.doDie((Creature)null);
            } else if (this.isSSPossible()) {
              activeChar.unChargeShots(this.isMagic());
            }

            return;
          }

          target = (Creature)var6.next();
        } while(target == null);
      } while(target.isDead());

      if (this._turner && !target.isInvul()) {
        target.broadcastPacket(new L2GameServerPacket[]{new StartRotating(target, target.getHeading(), 1, 65535)});
        target.broadcastPacket(new L2GameServerPacket[]{new FinishRotating(target, activeChar.getHeading(), 65535)});
        target.setHeading(activeChar.getHeading());
        target.sendPacket((new SystemMessage(110)).addSkillName(this._displayId, this._displayLevel));
      }

      boolean reflected = target.checkReflectSkill(activeChar, this);
      Creature realTarget = reflected ? activeChar : target;
      AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, this._blow, ss, this._onCrit);
      if (info.lethal_dmg > 0.0D) {
        realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
      }

      if (!info.miss || info.damage >= 1.0D) {
        realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, info.lethal ? false : this._directHp, true, false, false, this.getPower() != 0.0D);
      }

      if (!reflected) {
        realTarget.doCounterAttack(this, activeChar, this._blow);
      }

      this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
    }
  }
}
