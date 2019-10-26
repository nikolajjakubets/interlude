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
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NegateEffects extends Skill {
  private Map<EffectType, Integer> _negateEffects = new HashMap();
  private Map<String, Integer> _negateStackType = new HashMap();
  private final boolean _onlyPhysical;
  private final boolean _negateDebuffs;

  public NegateEffects(StatsSet set) {
    super(set);
    String[] negateEffectsString = set.getString("negateEffects", "").split(";");

    for(int i = 0; i < negateEffectsString.length; ++i) {
      if (!negateEffectsString[i].isEmpty()) {
        String[] entry = negateEffectsString[i].split(":");
        this._negateEffects.put(Enum.valueOf(EffectType.class, entry[0]), entry.length > 1 ? Integer.decode(entry[1]) : 2147483647);
      }
    }

    String[] negateStackTypeString = set.getString("negateStackType", "").split(";");

    for(int i = 0; i < negateStackTypeString.length; ++i) {
      if (!negateStackTypeString[i].isEmpty()) {
        String[] entry = negateStackTypeString[i].split(":");
        this._negateStackType.put(entry[0], entry.length > 1 ? Integer.decode(entry[1]) : 2147483647);
      }
    }

    this._onlyPhysical = set.getBool("onlyPhysical", false);
    this._negateDebuffs = set.getBool("negateDebuffs", true);
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

        if (!this._negateDebuffs && !Formulas.calcSkillSuccess(activeChar, target, this, this.getActivateRate())) {
          activeChar.sendPacket((new SystemMessage(139)).addString(target.getName()).addSkillName(this.getDisplayId(), this.getDisplayLevel()));
        } else {
          Iterator var5;
          Entry e;
          if (!this._negateEffects.isEmpty()) {
            var5 = this._negateEffects.entrySet().iterator();

            while(var5.hasNext()) {
              e = (Entry)var5.next();
              this.negateEffectAtPower(target, (EffectType)e.getKey(), (Integer)e.getValue());
            }
          }

          if (!this._negateStackType.isEmpty()) {
            var5 = this._negateStackType.entrySet().iterator();

            while(var5.hasNext()) {
              e = (Entry)var5.next();
              this.negateEffectAtPower(target, (String)e.getKey(), (Integer)e.getValue());
            }
          }

          this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        }
      }
    }
  }

  private void negateEffectAtPower(Creature target, EffectType type, int power) {
    Iterator var4 = target.getEffectList().getAllEffects().iterator();

    while(true) {
      Effect e;
      Skill skill;
      do {
        do {
          do {
            do {
              if (!var4.hasNext()) {
                return;
              }

              e = (Effect)var4.next();
              skill = e.getSkill();
            } while(this._onlyPhysical && skill.isMagic());
          } while(!skill.isCancelable());
        } while(skill.isOffensive() && !this._negateDebuffs);
      } while(!skill.isOffensive() && skill.getMagicLevel() > this.getMagicLevel() && Rnd.chance(skill.getMagicLevel() - this.getMagicLevel()));

      if (e.getEffectType() == type && e.getStackOrder() <= power) {
        e.exit();
      }
    }
  }

  private void negateEffectAtPower(Creature target, String stackType, int power) {
    Iterator var4 = target.getEffectList().getAllEffects().iterator();

    while(true) {
      Effect e;
      Skill skill;
      do {
        do {
          do {
            do {
              if (!var4.hasNext()) {
                return;
              }

              e = (Effect)var4.next();
              skill = e.getSkill();
            } while(this._onlyPhysical && skill.isMagic());
          } while(!skill.isCancelable());
        } while(skill.isOffensive() && !this._negateDebuffs);
      } while(!skill.isOffensive() && skill.getMagicLevel() > this.getMagicLevel() && Rnd.chance(skill.getMagicLevel() - this.getMagicLevel()));

      if (e.isStackTypeMatch(new String[]{stackType}) && e.getStackOrder() <= power) {
        e.exit();
      }
    }
  }
}
