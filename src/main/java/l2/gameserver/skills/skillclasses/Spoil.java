//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Formulas.AttackInfo;
import l2.gameserver.templates.StatsSet;

public class Spoil extends Skill {
  public Spoil(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      int ss = this.isSSPossible() ? (this.isMagic() ? activeChar.getChargedSpiritShot() : (activeChar.getChargedSoulShot() ? 2 : 0)) : 0;
      if (ss > 0 && this.getPower() > 0.0D) {
        activeChar.unChargeShots(false);
      }

      Iterator var4 = targets.iterator();

      while(var4.hasNext()) {
        Creature target = (Creature)var4.next();
        if (target != null && !target.isDead()) {
          if (target.isMonster()) {
            if (((MonsterInstance)target).isSpoiled()) {
              activeChar.sendPacket(Msg.ALREADY_SPOILED);
            } else {
              MonsterInstance monster = (MonsterInstance)target;
              int monsterLevel = monster.getLevel();
              int modifier = monsterLevel - this.getMagicLevel();
              double rateOfSpoil = (double)Math.max(this.getActivateRate(), 80);
              if (modifier > 8) {
                rateOfSpoil -= rateOfSpoil * (double)(modifier - 8) * 9.0D / 100.0D;
              }

              rateOfSpoil *= (double)this.getMagicLevel() / (double)monsterLevel;
              rateOfSpoil = Math.max(Config.MINIMUM_SPOIL_RATE, Math.min(rateOfSpoil, 99.0D));
              boolean success = Rnd.chance(rateOfSpoil);
              if (success && monster.setSpoiled((Player)activeChar)) {
                activeChar.sendPacket(Msg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
              }
            }
          }

          if (this.getPower() > 0.0D) {
            double damage;
            if (this.isMagic()) {
              damage = Formulas.calcMagicDam(activeChar, target, this, ss);
            } else {
              AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss > 0, false);
              damage = info.damage;
              if (info.lethal_dmg > 0.0D) {
                target.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
              }
            }

            target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
            target.doCounterAttack(this, activeChar, false);
          }

          this.getEffects(activeChar, target, false, false);
          target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(this._effectPoint, 1));
        }
      }

    }
  }
}
