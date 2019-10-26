//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NegateStats extends Skill {
  private final List<Stats> _negateStats;
  private final boolean _negateOffensive;
  private final int _negateCount;

  public NegateStats(StatsSet set) {
    super(set);
    String[] negateStats = set.getString("negateStats", "").split(" ");
    this._negateStats = new ArrayList(negateStats.length);
    String[] var3 = negateStats;
    int var4 = negateStats.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String stat = var3[var5];
      if (!stat.isEmpty()) {
        this._negateStats.add(Stats.valueOfXml(stat));
      }
    }

    this._negateOffensive = set.getBool("negateDebuffs", false);
    this._negateCount = set.getInteger("negateCount", 0);
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

        if (!this._negateOffensive && !Formulas.calcSkillSuccess(activeChar, target, this, this.getActivateRate())) {
          activeChar.sendPacket((new SystemMessage(139)).addString(target.getName()).addSkillName(this.getId(), this.getLevel()));
        } else {
          int count = 0;
          List<Effect> effects = target.getEffectList().getAllEffects();
          Iterator var7 = this._negateStats.iterator();

          label69:
          while(var7.hasNext()) {
            Stats stat = (Stats)var7.next();
            Iterator var9 = effects.iterator();

            while(true) {
              while(true) {
                if (!var9.hasNext()) {
                  continue label69;
                }

                Effect e = (Effect)var9.next();
                Skill skill = e.getSkill();
                if (!skill.isOffensive() && skill.getMagicLevel() > this.getMagicLevel() && Rnd.chance(skill.getMagicLevel() - this.getMagicLevel())) {
                  ++count;
                } else {
                  if (skill.isOffensive() == this._negateOffensive && this.containsStat(e, stat) && skill.isCancelable()) {
                    target.sendPacket((new SystemMessage(749)).addSkillName(e.getSkill().getId(), e.getSkill().getDisplayLevel()));
                    e.exit();
                    ++count;
                  }

                  if (this._negateCount > 0 && count >= this._negateCount) {
                    continue label69;
                  }
                }
              }
            }
          }

          this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        }
      }
    }
  }

  private boolean containsStat(Effect e, Stats stat) {
    FuncTemplate[] var3 = e.getTemplate().getAttachedFuncs();
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      FuncTemplate ft = var3[var5];
      if (ft._stat == stat) {
        return true;
      }
    }

    return false;
  }

  public boolean isOffensive() {
    return !this._negateOffensive;
  }

  public List<Stats> getNegateStats() {
    return this._negateStats;
  }
}
