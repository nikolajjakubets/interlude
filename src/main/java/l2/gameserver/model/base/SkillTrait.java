//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public enum SkillTrait {
  NONE,
  BLEED {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.BLEED_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return env.character.calcStat(Stats.BLEED_POWER, env.target, env.skill);
    }
  },
  BOSS,
  DEATH,
  DERANGEMENT {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.MENTAL_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return Math.min(40.0D, env.character.calcStat(Stats.MENTAL_POWER, env.target, env.skill) + calcEnchantMod(env));
    }
  },
  ETC,
  GUST,
  HOLD {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.ROOT_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return env.character.calcStat(Stats.ROOT_POWER, env.target, env.skill);
    }
  },
  PARALYZE {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.PARALYZE_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return env.character.calcStat(Stats.PARALYZE_POWER, env.target, env.skill);
    }
  },
  PHYSICAL_BLOCKADE,
  POISON {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.POISON_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return env.character.calcStat(Stats.POISON_POWER, env.target, env.skill);
    }
  },
  SHOCK {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.STUN_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return Math.min(40.0D, env.character.calcStat(Stats.STUN_POWER, env.target, env.skill) + calcEnchantMod(env));
    }
  },
  SLEEP {
    public final double calcVuln(Env env) {
      return env.target.calcStat(Stats.SLEEP_RESIST, env.character, env.skill);
    }

    public final double calcProf(Env env) {
      return env.character.calcStat(Stats.SLEEP_POWER, env.target, env.skill);
    }
  },
  VALAKAS;

  private SkillTrait() {
  }

  public double calcVuln(Env env) {
    return 0.0D;
  }

  public double calcProf(Env env) {
    return 0.0D;
  }

  public static double calcEnchantMod(Env env) {
    int enchantLevel = env.skill.getDisplayLevel();
    if (enchantLevel <= 100) {
      return 0.0D;
    } else {
      enchantLevel %= 100;
      return env.skill.getEnchantLevelCount() == 15 ? (double)(enchantLevel * 2) : (double)enchantLevel;
    }
  }
}
