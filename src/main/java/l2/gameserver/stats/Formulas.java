//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.SkillTrait;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.PositionUtils.TargetDirection;

public class Formulas {
  public Formulas() {
  }

  public static double calcHpRegen(Creature cha) {
    double init;
    if (cha.isPlayer()) {
      init = (cha.getLevel() <= 10 ? 1.5D + (double)cha.getLevel() / 20.0D : 1.4D + (double)cha.getLevel() / 10.0D) * cha.getLevelMod();
    } else {
      init = cha.getTemplate().baseHpReg;
    }

    if (cha.isPlayable()) {
      init *= BaseStats.CON.calcBonus(cha);
      if (cha.isSummon()) {
        init *= 2.0D;
      }
    }

    return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null);
  }

  public static double calcMpRegen(Creature cha) {
    double init;
    if (cha.isPlayer()) {
      init = (0.87D + (double)cha.getLevel() * 0.03D) * cha.getLevelMod();
    } else {
      init = cha.getTemplate().baseMpReg;
    }

    if (cha.isPlayable()) {
      init *= BaseStats.MEN.calcBonus(cha);
      if (cha.isSummon()) {
        init *= 2.0D;
      }
    }

    return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null);
  }

  public static double calcCpRegen(Creature cha) {
    double init = (1.5D + (double)(cha.getLevel() / 10)) * cha.getLevelMod() * BaseStats.CON.calcBonus(cha);
    return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null);
  }

  public static Formulas.AttackInfo calcPhysDam(Creature attacker, Creature target, Skill skill, boolean dual, boolean blow, boolean ss, boolean onCrit) {
    Formulas.AttackInfo info = new Formulas.AttackInfo();
    info.damage = attacker.getPAtk(target);
    info.defence = target.getPDef(attacker);
    info.crit_static = attacker.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill);
    info.death_rcpt = 0.01D * target.calcStat(Stats.DEATH_VULNERABILITY, attacker, skill);
    info.lethal1 = skill == null ? 0.0D : skill.getLethal1() * info.death_rcpt;
    info.lethal2 = skill == null ? 0.0D : skill.getLethal2() * info.death_rcpt;
    info.crit = Rnd.chance(calcCrit(attacker, target, skill, blow));
    info.shld = (skill == null || !skill.getShieldIgnore()) && calcShldUse(attacker, target);
    info.lethal = false;
    info.miss = false;
    boolean isPvP = attacker.isPlayable() && target.isPlayable();
    if (info.shld) {
      info.defence += target.getShldDef();
    }

    info.defence = Math.max(info.defence, 1.0D);
    if (skill != null) {
      if (!blow && !target.isLethalImmune()) {
        if (Rnd.chance(info.lethal1)) {
          if (target.isPlayer()) {
            info.lethal = true;
            info.lethal_dmg = target.getCurrentCp();
            target.sendPacket(Msg.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
          } else {
            info.lethal_dmg = target.getCurrentHp() / 2.0D;
          }

          attacker.sendPacket(Msg.HALF_KILL);
        } else if (Rnd.chance(info.lethal2)) {
          if (target.isPlayer()) {
            info.lethal = true;
            info.lethal_dmg = target.getCurrentHp() + target.getCurrentCp() - 1.1D;
            target.sendPacket(SystemMsg.LETHAL_STRIKE);
          } else {
            info.lethal_dmg = target.getCurrentHp() - 1.0D;
          }

          attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        }
      }

      if (skill.getPower(target) == 0.0D) {
        info.damage = 0.0D;
        return info;
      }

      if (blow && !skill.isBehind() && ss) {
        info.damage *= 2.04D;
      }

      if (skill.isChargeBoost()) {
        info.damage = attacker.calcStat(Stats.SKILL_POWER, info.damage + skill.getPower(target), null, null);
      } else {
        info.damage += attacker.calcStat(Stats.SKILL_POWER, skill.getPower(target), null, null);
      }

      if (blow && skill.isBehind() && ss) {
        info.damage *= 1.5D;
      }

      if (!skill.isChargeBoost()) {
        info.damage *= 1.0D + (Rnd.get() * (double)attacker.getRandomDamage() * 2.0D - (double)attacker.getRandomDamage()) / 100.0D;
      }

      if (blow) {
        info.damage *= 0.01D * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
        info.damage = target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, skill);
        info.damage += 6.1D * info.crit_static;
      }

      if (skill.isChargeBoost()) {
        info.damage *= 0.8D + 0.2D * (double)(attacker.getIncreasedForce() + Math.max(skill.getNumCharges(), 0));
      } else if (skill.isSoulBoost()) {
        info.damage *= 1.0D + 0.06D * (double)Math.min(attacker.getConsumedSouls(), 5);
      }

      if (info.crit) {
        info.damage *= 2.0D;
      }
    } else {
      info.damage *= 1.0D + (Rnd.get() * (double)attacker.getRandomDamage() * 2.0D - (double)attacker.getRandomDamage()) / 100.0D;
      if (dual) {
        info.damage /= 2.0D;
      }

      if (info.crit) {
        info.damage *= 0.01D * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
        info.damage = 2.0D * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, skill);
        info.damage += info.crit_static;
      }
    }

    if (info.crit) {
      int chance = attacker.getSkillLevel(467);
      if (chance > 0) {
        if (chance >= 21) {
          chance = 30;
        } else if (chance >= 15) {
          chance = 25;
        } else if (chance >= 9) {
          chance = 20;
        } else if (chance >= 4) {
          chance = 15;
        }

        if (Rnd.chance(chance)) {
          attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
        }
      }
    }

    if (skill == null || !skill.isChargeBoost()) {
      switch(PositionUtils.getDirectionTo(target, attacker)) {
        case BEHIND:
          info.damage *= 1.1D;
          break;
        case SIDE:
          info.damage *= 1.05D;
      }
    }

    if (ss) {
      info.damage *= blow ? 1.0D : 2.0D;
    }

    info.damage *= 70.0D / info.defence;
    info.damage = attacker.calcStat(Stats.PHYSICAL_DAMAGE, info.damage, target, skill);
    if (info.shld && Rnd.chance(5)) {
      info.damage = 1.0D;
    }

    if (isPvP) {
      if (skill == null) {
        info.damage *= attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1.0D, null, null);
        info.damage /= target.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1.0D, null, null);
      } else {
        info.damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1.0D, null, null);
        info.damage /= target.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1.0D, null, null);
      }
    }

    if (skill != null) {
      if (info.shld) {
        if (info.damage == 1.0D) {
          target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
        } else {
          target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
        }
      }

      if (info.damage > 1.0D && !skill.hasEffects() && Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0.0D, attacker, skill))) {
        attacker.sendPacket(new SystemMessage(43));
        target.sendPacket((new SystemMessage(42)).addName(attacker));
        info.damage = 0.0D;
      }

      if (info.damage > 1.0D && skill.isDeathlink()) {
        info.damage *= 1.8D * (1.0D - attacker.getCurrentHpRatio());
      }

      if (onCrit && !calcBlow(attacker, target, skill)) {
        info.miss = true;
        info.damage = 0.0D;
        attacker.sendPacket(new SystemMessage(43));
      }

      if (blow) {
        if (Rnd.chance(info.lethal1)) {
          if (target.isPlayer()) {
            info.lethal = true;
            info.lethal_dmg = target.getCurrentCp();
            target.sendPacket(Msg.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
          } else if (target.isLethalImmune()) {
            info.damage *= 2.0D;
          } else {
            info.lethal_dmg = target.getCurrentHp() / 2.0D;
          }

          attacker.sendPacket(Msg.HALF_KILL);
        } else if (Rnd.chance(info.lethal2)) {
          if (target.isPlayer()) {
            info.lethal = true;
            info.lethal_dmg = target.getCurrentHp() + target.getCurrentCp() - 1.1D;
            target.sendPacket(SystemMsg.LETHAL_STRIKE);
          } else if (target.isLethalImmune()) {
            info.damage *= 3.0D;
          } else {
            info.lethal_dmg = target.getCurrentHp() - 1.0D;
          }

          attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        }
      }

      if (info.damage > 0.0D) {
        attacker.displayGiveDamageMessage(target, (int)info.damage, info.crit || blow, false, false, false);
      }

      if (target.isStunned() && calcStunBreak(info.crit)) {
        target.getEffectList().stopEffects(EffectType.Stun);
      }

      if (calcCastBreak(target, info.crit)) {
        target.abortCast(false, true);
      }
    }

    return info;
  }

  public static double calcMagicDam(Creature attacker, Creature target, Skill skill, int sps) {
    boolean isPvP = attacker.isPlayable() && target.isPlayable();
    boolean shield = skill.getShieldIgnore() && calcShldUse(attacker, target);
    double mAtk = attacker.getMAtk(target, skill);
    if (sps == 2) {
      mAtk *= 4.0D;
    } else if (sps == 1) {
      mAtk *= 2.0D;
    }

    double mdef = target.getMDef(null, skill);
    if (shield) {
      mdef += target.getShldDef();
    }

    if (mdef == 0.0D) {
      mdef = 1.0D;
    }

    double power = skill.getPower(target);
    double lethalDamage = 0.0D;
    if (Rnd.chance(skill.getLethal1())) {
      if (target.isPlayer()) {
        lethalDamage = target.getCurrentCp();
        target.sendPacket(Msg.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
      } else if (!target.isLethalImmune()) {
        lethalDamage = target.getCurrentHp() / 2.0D;
      } else {
        power *= 2.0D;
      }

      attacker.sendPacket(Msg.HALF_KILL);
    } else if (Rnd.chance(skill.getLethal2())) {
      if (target.isPlayer()) {
        lethalDamage = target.getCurrentHp() + target.getCurrentCp() - 1.1D;
        target.sendPacket(SystemMsg.LETHAL_STRIKE);
      } else if (!target.isLethalImmune()) {
        lethalDamage = target.getCurrentHp() - 1.0D;
      } else {
        power *= 3.0D;
      }

      attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
    }

    if (power == 0.0D) {
      if (lethalDamage > 0.0D) {
        attacker.displayGiveDamageMessage(target, (int)lethalDamage, false, false, false, false);
      }

      return lethalDamage;
    } else {
      if (skill.isSoulBoost()) {
        power *= 1.0D + 0.06D * (double)Math.min(attacker.getConsumedSouls(), 5);
      }

      double damage = 91.0D * power * Math.sqrt(mAtk) / mdef;
      boolean crit = calcMCrit(attacker, target, attacker.getMagicCriticalRate(target, skill));
      if (crit) {
        damage *= attacker.calcStat(Stats.MCRITICAL_DAMAGE, attacker.isPlayable() && target.isPlayable() ? Config.MCRITICAL_CRIT_POWER : Config.MCRITICAL_CRIT_POWER, target, skill);
      }

      damage = attacker.calcStat(Stats.MAGIC_DAMAGE, damage, target, skill);
      if (shield) {
        if (Rnd.chance(5)) {
          damage = 0.0D;
          target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
          attacker.sendPacket((new SystemMessage(159)).addName(attacker));
        } else {
          target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
          attacker.sendPacket(new SystemMessage(2151));
        }
      }

      int mLevel = skill.getMagicLevel() == 0 ? attacker.getLevel() : skill.getMagicLevel();
      int levelDiff = target.getLevel() - mLevel;
      if (damage > 1.0D && skill.isDeathlink()) {
        damage *= 1.8D * (1.0D - attacker.getCurrentHpRatio());
      }

      if (damage > 1.0D && skill.isBasedOnTargetDebuff()) {
        damage *= 1.0D + 0.05D * (double)Math.min(36, target.getEffectList().getAllEffects().size());
      }

      damage += lethalDamage;
      if (skill.getSkillType() == SkillType.MANADAM) {
        damage = Math.max(1.0D, damage / 4.0D);
      }

      if (isPvP && damage > 1.0D) {
        damage *= attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0D, null, null);
        damage /= target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1.0D, null, null);
      }

      boolean gradePenalty = attacker.isPlayer() && ((Player)attacker).getGradePenalty() > 0;
      double lvlMod = 4.0D * Math.max(1.0D, target.getLevel() >= 80 ? (double)(levelDiff - 4) * 1.6D : (double)((levelDiff - 14) * 2));
      double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, attacker, skill) - attacker.calcStat(Stats.MAGIC_POWER, target, skill);
      double failChance = gradePenalty ? 95.0D : Math.min(lvlMod * (1.0D + magic_rcpt / 100.0D), 95.0D);
      double resistChance = gradePenalty ? 95.0D : (double)(5 * Math.max(levelDiff - 10, 1));
      if (attacker.isPlayer() && ((Player)attacker).isDebug()) {
        attacker.sendMessage("Fail chance " + (int)failChance + "/" + (int)resistChance);
      }

      if (Rnd.chance(failChance)) {
        SystemMessage msg;
        SystemMessage msg1;
        if (Rnd.chance(resistChance)) {
          damage = 0.0D;
          msg = new SystemMessage(158);
          attacker.sendPacket(msg);
          msg1 = (new SystemMessage(159)).addName(attacker);
          target.sendPacket(msg1);
        } else {
          damage /= 2.0D;
          msg = new SystemMessage(158);
          attacker.sendPacket(msg);
          msg1 = (new SystemMessage(159)).addName(attacker);
          target.sendPacket(msg1);
        }
      }

      if (damage > 0.0D) {
        attacker.displayGiveDamageMessage(target, (int)damage, crit, false, false, true);
      }

      if (calcCastBreak(target, crit)) {
        target.abortCast(false, true);
      }

      return damage;
    }
  }

  public static boolean calcStunBreak(boolean crit) {
    return Rnd.chance(crit ? 75 : 10);
  }

  public static boolean calcBlow(Creature activeChar, Creature target, Skill skill) {
    WeaponTemplate weapon = activeChar.getActiveWeaponItem();
    double base_weapon_crit = weapon == null ? 4.0D : (double)weapon.getCritical();
    double crit_height_bonus = 0.008D * (double)Math.min(25, Math.max(-25, target.getZ() - activeChar.getZ())) + 1.1D;
    double buffs_mult = activeChar.calcStat(Stats.FATALBLOW_RATE, target, skill);
    double skill_mod = skill.isBehind() ? 3.0D : 2.0D;
    double chance = base_weapon_crit * buffs_mult * crit_height_bonus * skill_mod;
    if (!target.isInCombat()) {
      chance *= 1.1D;
    }

    switch(PositionUtils.getDirectionTo(target, activeChar)) {
      case BEHIND:
        chance *= 1.1D;
        break;
      case SIDE:
        chance *= 1.05D;
        break;
      case FRONT:
        if (skill.isBehind()) {
          chance = 3.0D;
        }
    }

    chance = Math.min(skill.isBehind() ? 100.0D : 80.0D, chance);
    return Rnd.chance(chance);
  }

  public static double calcCrit(Creature attacker, Creature target, Skill skill, boolean blow) {
    if (attacker.isPlayer() && attacker.getActiveWeaponItem() == null) {
      return 0.0D;
    } else if (skill != null) {
      return (double)skill.getCriticalRate() * (blow ? BaseStats.DEX.calcBonus(attacker) : BaseStats.STR.calcBonus(attacker)) * 0.01D * attacker.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, target, skill);
    } else {
      double rate = (double)attacker.getCriticalHit(target, null) * 0.01D * target.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, attacker, skill);
      switch(PositionUtils.getDirectionTo(target, attacker)) {
        case BEHIND:
          rate *= 1.1D;
          break;
        case SIDE:
          rate *= 1.05D;
      }

      return rate / 10.0D;
    }
  }

  public static boolean calcMCrit(Creature attacker, Creature target, double mRate) {
    if (attacker != null && attacker.isNpc()) {
      return Rnd.get() * 100.0D <= Math.min(Config.ALT_NPC_LIM_MCRIT, mRate);
    } else {
      return Rnd.get() * 100.0D <= Math.min(Config.LIM_MCRIT, mRate);
    }
  }

  public static boolean calcCastBreak(Creature target, boolean crit) {
    if (target != null && !target.isInvul() && !target.isRaid() && target.isCastingNow()) {
      Skill skill = target.getCastingSkill();
      return (skill == null || (skill.isMagic() && skill.getSkillType() != SkillType.TAKECASTLE)) && Rnd.chance(target.calcStat(Stats.CAST_INTERRUPT, crit ? 75.0D : 10.0D, null, skill));
    } else {
      return false;
    }
  }

  public static int calcPAtkSpd(double rate) {
    double base = 500.0D / rate;
    int result = (int)(base * 1000.0D * 0.9777777791023254D);
    if (base * 1000.0D > (double)result) {
      result += (int)((double)(-result) - base * -1000.0D);
    }

    return result;
  }

  public static int calcMAtkSpd(Creature attacker, Skill skill, double skillTime) {
    return skill.isMagic() ? (int)(skillTime * 333.0D / (double)Math.max(attacker.getMAtkSpd(), 1)) : (int)(skillTime * 333.0D / (double)Math.max(attacker.getPAtkSpd(), 1));
  }

  public static long calcSkillReuseDelay(Creature actor, Skill skill) {
    long reuseDelay = skill.getReuseDelay();
    if (actor.isMonster()) {
      reuseDelay = skill.getReuseForMonsters();
    }

    if (!skill.isReuseDelayPermanent() && !skill.isHandler() && !skill.isItemSkill()) {
      if (actor.getSkillMastery(skill.getId()) == 1) {
        actor.removeSkillMastery(skill.getId());
        return 0L;
      } else if (skill.isMusic()) {
        return (long)actor.calcStat(Stats.MUSIC_REUSE_RATE, (double)reuseDelay, null, skill) * 333L / (long)Math.max(actor.getMAtkSpd(), 1);
      } else {
        return skill.isMagic() ? (long)actor.calcStat(Stats.MAGIC_REUSE_RATE, (double)reuseDelay, null, skill) * 333L / (long)Math.max(actor.getMAtkSpd(), 1) : (long)actor.calcStat(Stats.PHYSIC_REUSE_RATE, (double)reuseDelay, null, skill) * 333L / (long)Math.max(actor.getPAtkSpd(), 1);
      }
    } else {
      return reuseDelay;
    }
  }

  public static boolean calcHitMiss(Creature attacker, Creature target) {
    double chanceToHit = 100.0D - 10.0D * Math.pow(1.085D, target.getEvasionRate(attacker) - attacker.getAccuracy());
    chanceToHit = Math.max(chanceToHit, 28.0D);
    chanceToHit = Math.min(chanceToHit, 98.0D);
    TargetDirection direction = PositionUtils.getDirectionTo(attacker, target);
    switch(direction) {
      case BEHIND:
        chanceToHit *= 1.2D;
        break;
      case SIDE:
        chanceToHit *= 1.1D;
    }

    return !Rnd.chance(chanceToHit);
  }

  public static boolean calcShldUse(Creature attacker, Creature target) {
    WeaponTemplate template = target.getSecondaryWeaponItem();
    if (template != null && template.getItemType() == WeaponType.NONE) {
      int angle = (int)target.calcStat(Stats.SHIELD_ANGLE, attacker, null);
      return PositionUtils.isFacing(target, attacker, angle) && Rnd.chance((int) target.calcStat(Stats.SHIELD_RATE, attacker, null));
    } else {
      return false;
    }
  }

  public static boolean calcSkillSuccess(Env env, EffectTemplate et, int spiritshot) {
    if (env.value == -1.0D) {
      return true;
    } else {
      env.value = Math.max(Math.min(env.value, 100.0D), 1.0D);
      double base = env.value;
      Skill skill = env.skill;
      if (!skill.isOffensive()) {
        return Rnd.chance(env.value);
      } else {
        Creature caster = env.character;
        Creature target = env.target;
        boolean debugCaster = false;
        boolean debugTarget = false;
        boolean debugGlobal = false;
        if (Config.ALT_DEBUG_ENABLED) {
          debugCaster = caster.getPlayer() != null && caster.getPlayer().isDebug();
          debugTarget = target.getPlayer() != null && target.getPlayer().isDebug();
          boolean debugPvP = Config.ALT_DEBUG_PVP_ENABLED && debugCaster && debugTarget && (!Config.ALT_DEBUG_PVP_DUEL_ONLY || caster.getPlayer().isInDuel() && target.getPlayer().isInDuel());
          debugGlobal = debugPvP || Config.ALT_DEBUG_PVE_ENABLED && (debugCaster && target.isMonster() || debugTarget && caster.isMonster());
        }

        double statMod = 1.0D;
        if (skill.getSaveVs() != null) {
          statMod = skill.getSaveVs().calcChanceMod(target);
          env.value *= statMod;
        }

        env.value = Math.max(env.value, 1.0D);
        double mAtkMod = 1.0D;
//        int ssMod = false;
        if (skill.isMagic()) {
          int mdef = Math.max(1, target.getMDef(target, skill));
          double matk = caster.getMAtk(target, skill);
          if (skill.isSSPossible()) {
            byte ssMod;
            switch(spiritshot) {
              case 1:
                ssMod = 2;
                break;
              case 2:
                ssMod = 4;
                break;
              default:
                ssMod = 1;
            }

            matk *= ssMod;
          }

          mAtkMod = Config.SKILLS_CHANCE_MOD * Math.pow(matk, Config.SKILLS_CHANCE_POW) / (double)mdef;
          env.value *= mAtkMod;
          env.value = Math.max(env.value, 1.0D);
        }

        double lvlDependMod = skill.getLevelModifier();
        if (lvlDependMod != 0.0D) {
          int attackLevel = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : caster.getLevel();
          lvlDependMod = 1.0D + (double)(attackLevel - target.getLevel()) * 0.03D * lvlDependMod;
          if (lvlDependMod < 0.0D) {
            lvlDependMod = 0.0D;
          } else if (lvlDependMod > 2.0D) {
            lvlDependMod = 2.0D;
          }

          env.value *= lvlDependMod;
        }

        double vulnMod = 0.0D;
        double profMod = 0.0D;
        double resMod = 1.0D;
        double debuffMod = 1.0D;
        if (!skill.isIgnoreResists()) {
          debuffMod = 1.0D - target.calcStat(Stats.DEBUFF_RESIST, caster, skill) / 120.0D;
          if (debuffMod != 1.0D) {
            if (debuffMod == -1.0D / 0.0) {
              if (debugGlobal) {
                if (debugCaster) {
                  caster.getPlayer().sendMessage("Full debuff immunity");
                }

                if (debugTarget) {
                  target.getPlayer().sendMessage("Full debuff immunity");
                }
              }

              return false;
            }

            if (debuffMod == 1.0D / 0.0) {
              if (debugGlobal) {
                if (debugCaster) {
                  caster.getPlayer().sendMessage("Full debuff vulnerability");
                }

                if (debugTarget) {
                  target.getPlayer().sendMessage("Full debuff vulnerability");
                }
              }

              return true;
            }

            debuffMod = Math.max(debuffMod, 0.0D);
            env.value *= debuffMod;
          }

          SkillTrait trait = skill.getTraitType();
          if (trait != null) {
            vulnMod = trait.calcVuln(env);
            profMod = trait.calcProf(env);
            double maxResist = 90.0D + profMod * 0.85D;
            resMod = (maxResist - vulnMod) / 60.0D;
          }

          if (resMod != 1.0D) {
            if (resMod == -1.0D / 0.0) {
              if (debugGlobal) {
                if (debugCaster) {
                  caster.getPlayer().sendMessage("Full immunity");
                }

                if (debugTarget) {
                  target.getPlayer().sendMessage("Full immunity");
                }
              }

              return false;
            }

            if (resMod == 1.0D / 0.0) {
              if (debugGlobal) {
                if (debugCaster) {
                  caster.getPlayer().sendMessage("Full vulnerability");
                }

                if (debugTarget) {
                  target.getPlayer().sendMessage("Full vulnerability");
                }
              }

              return true;
            }

            resMod = Math.max(resMod, 0.0D);
            env.value *= resMod;
          }
        }

        double elementMod = 0.0D;
        Element element = skill.getElement();
        if (element != Element.NONE) {
          elementMod = skill.getElementPower();
          Element attackElement = getAttackElement(caster, target);
          if (attackElement == element) {
            elementMod += caster.calcStat(element.getAttack(), 0.0D);
          }

          elementMod -= target.calcStat(element.getDefence(), 0.0D);
          elementMod = (double)Math.round(elementMod / 10.0D);
          env.value += elementMod;
        }

        env.value = Math.max(env.value, Math.min(base, Config.SKILLS_CHANCE_MIN));
        env.value = Math.max(Math.min(env.value, Config.SKILLS_CHANCE_CAP), 1.0D);
        boolean result = Rnd.chance((int)env.value);
        if (debugGlobal) {
          StringBuilder stat = new StringBuilder(100);
          if (et == null) {
            stat.append(skill.getName());
          } else {
            stat.append(et._effectType.name());
          }

          stat.append(" AR:");
          stat.append((int)base);
          stat.append(" ");
          if (skill.getSaveVs() != null) {
            stat.append(skill.getSaveVs().name());
            stat.append(":");
            stat.append(String.format("%1.1f", statMod));
          }

          if (skill.isMagic()) {
            stat.append(" ");
            stat.append(" mAtk:");
            stat.append(String.format("%1.1f", mAtkMod));
          }

          if (skill.getTraitType() != null) {
            stat.append(" ");
            stat.append(skill.getTraitType().name());
          }

          stat.append(" ");
          stat.append(String.format("%1.1f", resMod));
          stat.append("(");
          stat.append(String.format("%1.1f", profMod));
          stat.append("/");
          stat.append(String.format("%1.1f", vulnMod));
          if (debuffMod != 0.0D) {
            stat.append("+");
            stat.append(String.format("%1.1f", debuffMod));
          }

          stat.append(") lvl:");
          stat.append(String.format("%1.1f", lvlDependMod));
          stat.append(" elem:");
          stat.append((int)elementMod);
          stat.append(" Chance:");
          stat.append(String.format("%1.1f", env.value));
          if (!result) {
            stat.append(" failed");
          }

          if (debugCaster) {
            caster.getPlayer().sendMessage(stat.toString());
          }

          if (debugTarget) {
            target.getPlayer().sendMessage(stat.toString());
          }
        }

        return result;
      }
    }
  }

  public static boolean calcSkillSuccess(Creature player, Creature target, Skill skill, int activateRate) {
    Env env = new Env();
    env.character = player;
    env.target = target;
    env.skill = skill;
    env.value = activateRate;
    return calcSkillSuccess(env, null, player.getChargedSpiritShot());
  }

  public static void calcSkillMastery(Skill skill, Creature activeChar) {
    if (!skill.isHandler()) {
      if (activeChar.getSkillLevel(331) > 0 && activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getINT(), null, skill) >= (double)Rnd.get(5000) || activeChar.getSkillLevel(330) > 0 && activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getSTR(), null, skill) >= (double)Rnd.get(5000)) {
        SkillType type = skill.getSkillType();
        byte masteryLevel;
        if (!skill.isMusic() && type != SkillType.BUFF && type != SkillType.HOT && type != SkillType.HEAL_PERCENT) {
          if (type == SkillType.HEAL) {
            masteryLevel = 3;
          } else {
            masteryLevel = 1;
          }
        } else {
          masteryLevel = 2;
        }

        if (masteryLevel > 0) {
          activeChar.setSkillMastery(skill.getId(), masteryLevel);
        }
      }

    }
  }

  public static double calcDamageResists(Skill skill, Creature attacker, Creature defender, double value) {
    if (attacker == defender) {
      return value;
    } else {
      if (attacker.isBoss()) {
        value *= Config.RATE_EPIC_ATTACK;
      } else if (attacker.isRaid() && attacker.getLevel() >= Config.RATE_MOD_MIN_LEVEL_LIMIT && attacker.getLevel() <= Config.RATE_MOD_MAX_LEVEL_LIMIT) {
        value *= Config.RATE_RAID_ATTACK;
      }

      if (defender.isBoss()) {
        value /= Config.RATE_EPIC_DEFENSE;
      } else if (defender.isRaid() && defender.getLevel() >= Config.RATE_MOD_MIN_LEVEL_LIMIT && defender.getLevel() <= Config.RATE_MOD_MAX_LEVEL_LIMIT) {
        value /= Config.RATE_RAID_DEFENSE;
      }

      Player pAttacker = attacker.getPlayer();
      Element element = Element.NONE;
      double power = 0.0D;
      if (skill != null) {
        element = skill.getElement();
        power = skill.getElementPower();
      } else {
        element = getAttackElement(attacker, defender);
      }

      if (element == Element.NONE) {
        return value;
      } else {
        if (pAttacker != null && pAttacker.isGM() && Config.DEBUG) {
          pAttacker.sendMessage("Element: " + element.name());
          pAttacker.sendMessage("Attack: " + attacker.calcStat(element.getAttack(), power));
          pAttacker.sendMessage("Defence: " + defender.calcStat(element.getDefence(), 0.0D));
          pAttacker.sendMessage("Modifier: " + getElementMod(defender.calcStat(element.getDefence(), 0.0D), attacker.calcStat(element.getAttack(), power)));
        }

        return value * getElementMod(defender.calcStat(element.getDefence(), 0.0D), attacker.calcStat(element.getAttack(), power));
      }
    }
  }

  private static double getElementMod(double defense, double attack) {
    if (defense < 0.0D) {
      attack += -defense;
      defense = 0.0D;
    }

    double attrAtk = 1.0D + attack / 100.0D;
    double attrDef = 1.0D + defense / 100.0D;
    return attrAtk / attrDef;
  }

  public static Element getAttackElement(Creature attacker, Creature target) {
    double max = 4.9E-324D;
    Element result = Element.NONE;
    Element[] var7 = Element.VALUES;
    int var8 = var7.length;

    for (Element e : var7) {
      double val = attacker.calcStat(e.getAttack(), 0.0D, null, null);
      if (val > 0.0D) {
        if (target != null) {
          val -= target.calcStat(e.getDefence(), 0.0D, null, null);
        }

        if (val > max) {
          result = e;
          max = val;
        }
      }
    }

    return result;
  }

  public static class AttackInfo {
    public double damage = 0.0D;
    public double defence = 0.0D;
    public double crit_static = 0.0D;
    public double death_rcpt = 0.0D;
    public double lethal1 = 0.0D;
    public double lethal2 = 0.0D;
    public double lethal_dmg = 0.0D;
    public boolean crit = false;
    public boolean shld = false;
    public boolean lethal = false;
    public boolean miss = false;

    public AttackInfo() {
    }
  }
}
