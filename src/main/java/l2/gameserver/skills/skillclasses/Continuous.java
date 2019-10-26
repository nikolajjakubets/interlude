//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class Continuous extends Skill {
  private final int _lethal1;
  private final int _lethal2;

  public Continuous(StatsSet set) {
    super(set);
    this._lethal1 = set.getInteger("lethal1", 0);
    this._lethal2 = set.getInteger("lethal2", 0);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var5 = targets.iterator();

    while(true) {
      Creature target;
      do {
        do {
          if (!var5.hasNext()) {
            if (this.isSSPossible() && (!Config.SAVING_SPS || this._skillType != SkillType.BUFF)) {
              activeChar.unChargeShots(this.isMagic());
            }

            return;
          }

          target = (Creature)var5.next();
        } while(target == null);
      } while(this.getSkillType() == SkillType.BUFF && target != activeChar && (target.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped()));

      boolean reflected = target.checkReflectSkill(activeChar, this);
      Creature realTarget = reflected ? activeChar : target;
      double mult = 0.01D * realTarget.calcStat(Stats.DEATH_VULNERABILITY, activeChar, this);
      double lethal1 = (double)this._lethal1 * mult;
      double lethal2 = (double)this._lethal2 * mult;
      if (lethal1 > 0.0D && Rnd.chance(lethal1)) {
        if (realTarget.isPlayer()) {
          realTarget.reduceCurrentHp(realTarget.getCurrentCp(), activeChar, this, true, true, false, true, false, false, true);
          realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
          activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        } else if (realTarget.isNpc() && !realTarget.isLethalImmune()) {
          realTarget.reduceCurrentHp(realTarget.getCurrentHp() / 2.0D, activeChar, this, true, true, false, true, false, false, true);
          activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        }
      } else if (lethal2 > 0.0D && Rnd.chance(lethal2)) {
        if (realTarget.isPlayer()) {
          realTarget.reduceCurrentHp(realTarget.getCurrentHp() + realTarget.getCurrentCp() - 1.0D, activeChar, this, true, true, false, true, false, false, true);
          realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
          activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        } else if (realTarget.isNpc() && !realTarget.isLethalImmune()) {
          realTarget.reduceCurrentHp(realTarget.getCurrentHp() - 1.0D, activeChar, this, true, true, false, true, false, false, true);
          activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        }
      }

      this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
    }
  }
}
