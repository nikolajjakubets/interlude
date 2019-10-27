//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectAddSkills;
import l2.gameserver.skills.effects.EffectAgathionRes;
import l2.gameserver.skills.effects.EffectAggression;
import l2.gameserver.skills.effects.EffectBetray;
import l2.gameserver.skills.effects.EffectBlessNoblesse;
import l2.gameserver.skills.effects.EffectBlockStat;
import l2.gameserver.skills.effects.EffectBluff;
import l2.gameserver.skills.effects.EffectBuff;
import l2.gameserver.skills.effects.EffectBuffImmunity;
import l2.gameserver.skills.effects.EffectCPDamPercent;
import l2.gameserver.skills.effects.EffectCallSkills;
import l2.gameserver.skills.effects.EffectCharge;
import l2.gameserver.skills.effects.EffectCharmOfCourage;
import l2.gameserver.skills.effects.EffectCombatPointHealOverTime;
import l2.gameserver.skills.effects.EffectConsumeSoulsOverTime;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.skills.effects.EffectCurseOfLifeFlow;
import l2.gameserver.skills.effects.EffectDamOverTime;
import l2.gameserver.skills.effects.EffectDamOverTimeLethal;
import l2.gameserver.skills.effects.EffectDebuffImmunity;
import l2.gameserver.skills.effects.EffectDestroySummon;
import l2.gameserver.skills.effects.EffectDisarm;
import l2.gameserver.skills.effects.EffectDiscord;
import l2.gameserver.skills.effects.EffectDispelEffects;
import l2.gameserver.skills.effects.EffectEnervation;
import l2.gameserver.skills.effects.EffectFakeDeath;
import l2.gameserver.skills.effects.EffectFear;
import l2.gameserver.skills.effects.EffectGrow;
import l2.gameserver.skills.effects.EffectHPDamPercent;
import l2.gameserver.skills.effects.EffectHate;
import l2.gameserver.skills.effects.EffectHeal;
import l2.gameserver.skills.effects.EffectHealBlock;
import l2.gameserver.skills.effects.EffectHealCPPercent;
import l2.gameserver.skills.effects.EffectHealOverTime;
import l2.gameserver.skills.effects.EffectHealPercent;
import l2.gameserver.skills.effects.EffectImmobilize;
import l2.gameserver.skills.effects.EffectInterrupt;
import l2.gameserver.skills.effects.EffectInvisible;
import l2.gameserver.skills.effects.EffectInvulnerable;
import l2.gameserver.skills.effects.EffectInvulnerableHeal;
import l2.gameserver.skills.effects.EffectLDManaDamOverTime;
import l2.gameserver.skills.effects.EffectLockInventory;
import l2.gameserver.skills.effects.EffectMPDamPercent;
import l2.gameserver.skills.effects.EffectManaDamOverTime;
import l2.gameserver.skills.effects.EffectManaHeal;
import l2.gameserver.skills.effects.EffectManaHealOverTime;
import l2.gameserver.skills.effects.EffectManaHealPercent;
import l2.gameserver.skills.effects.EffectMeditation;
import l2.gameserver.skills.effects.EffectMute;
import l2.gameserver.skills.effects.EffectMuteAll;
import l2.gameserver.skills.effects.EffectMuteAttack;
import l2.gameserver.skills.effects.EffectMutePhisycal;
import l2.gameserver.skills.effects.EffectNegateEffects;
import l2.gameserver.skills.effects.EffectNegateMusic;
import l2.gameserver.skills.effects.EffectParalyze;
import l2.gameserver.skills.effects.EffectPetrification;
import l2.gameserver.skills.effects.EffectRandomHate;
import l2.gameserver.skills.effects.EffectRelax;
import l2.gameserver.skills.effects.EffectRemoveTarget;
import l2.gameserver.skills.effects.EffectRoot;
import l2.gameserver.skills.effects.EffectSalvation;
import l2.gameserver.skills.effects.EffectServitorShare;
import l2.gameserver.skills.effects.EffectSilentMove;
import l2.gameserver.skills.effects.EffectSkillSeed;
import l2.gameserver.skills.effects.EffectSleep;
import l2.gameserver.skills.effects.EffectStun;
import l2.gameserver.skills.effects.EffectSymbol;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.skills.effects.EffectTransformation;
import l2.gameserver.skills.effects.EffectUnAggro;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import org.apache.commons.lang3.tuple.Pair;

public enum EffectType {
  AddSkills(EffectAddSkills.class, null, false),
  AgathionResurrect(EffectAgathionRes.class, null, true),
  Aggression(EffectAggression.class, null, true),
  Betray(EffectBetray.class, null, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  BlessNoblesse(EffectBlessNoblesse.class, null, true),
  BlockStat(EffectBlockStat.class, null, true),
  Buff(EffectBuff.class, null, false),
  BuffImmunity(EffectBuffImmunity.class, null, false),
  Bluff(EffectBluff.class, AbnormalEffect.NULL, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  DebuffImmunity(EffectDebuffImmunity.class, null, true),
  DispelEffects(EffectDispelEffects.class, null, Pair.of(Stats.CANCEL_RESIST, Stats.CANCEL_POWER), true),
  CallSkills(EffectCallSkills.class, null, false),
  CombatPointHealOverTime(EffectCombatPointHealOverTime.class, null, true),
  ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, null, true),
  Charge(EffectCharge.class, null, false),
  CharmOfCourage(EffectCharmOfCourage.class, null, true),
  CPDamPercent(EffectCPDamPercent.class, null, true),
  Cubic(EffectCubic.class, null, true),
  DamOverTime(EffectDamOverTime.class, null, false),
  DamOverTimeLethal(EffectDamOverTimeLethal.class, null, false),
  DestroySummon(EffectDestroySummon.class, null, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  Disarm(EffectDisarm.class, null, true),
  Discord(EffectDiscord.class, AbnormalEffect.CONFUSED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  Enervation(EffectEnervation.class, null, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), false),
  FakeDeath(EffectFakeDeath.class, null, true),
  Fear(EffectFear.class, AbnormalEffect.AFFRAID, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  Grow(EffectGrow.class, AbnormalEffect.GROW, false),
  Hate(EffectHate.class, null, false),
  Heal(EffectHeal.class, null, false),
  HealBlock(EffectHealBlock.class, null, true),
  HealCPPercent(EffectHealCPPercent.class, null, true),
  HealOverTime(EffectHealOverTime.class, null, false),
  HealPercent(EffectHealPercent.class, null, false),
  HPDamPercent(EffectHPDamPercent.class, null, true),
  IgnoreSkill(EffectBuff.class, null, false),
  Immobilize(EffectImmobilize.class, null, true),
  Interrupt(EffectInterrupt.class, null, true),
  Invulnerable(EffectInvulnerable.class, null, false),
  InvulnerableHeal(EffectInvulnerableHeal.class, null, false),
  Invisible(EffectInvisible.class, null, false),
  LockInventory(EffectLockInventory.class, null, false),
  CurseOfLifeFlow(EffectCurseOfLifeFlow.class, null, true),
  LDManaDamOverTime(EffectLDManaDamOverTime.class, null, true),
  ManaDamOverTime(EffectManaDamOverTime.class, null, true),
  ManaHeal(EffectManaHeal.class, null, false),
  ManaHealOverTime(EffectManaHealOverTime.class, null, false),
  ManaHealPercent(EffectManaHealPercent.class, null, false),
  Meditation(EffectMeditation.class, null, false),
  MPDamPercent(EffectMPDamPercent.class, null, true),
  Mute(EffectMute.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  MuteAll(EffectMuteAll.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  MuteAttack(EffectMuteAttack.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  MutePhisycal(EffectMutePhisycal.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  NegateEffects(EffectNegateEffects.class, null, false),
  NegateMusic(EffectNegateMusic.class, null, false),
  Paralyze(EffectParalyze.class, AbnormalEffect.HOLD_1, Pair.of(Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER), true),
  Petrification(EffectPetrification.class, AbnormalEffect.HOLD_2, Pair.of(Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER), true),
  RandomHate(EffectRandomHate.class, null, true),
  Relax(EffectRelax.class, null, true),
  RemoveTarget(EffectRemoveTarget.class, null, true),
  Root(EffectRoot.class, AbnormalEffect.ROOT, Pair.of(Stats.ROOT_RESIST, Stats.ROOT_POWER), true),
  Salvation(EffectSalvation.class, null, true),
  ServitorShare(EffectServitorShare.class, null, true),
  SilentMove(EffectSilentMove.class, AbnormalEffect.STEALTH, true),
  SkillSeed(EffectSkillSeed.class, null, true),
  Sleep(EffectSleep.class, AbnormalEffect.SLEEP, Pair.of(Stats.SLEEP_RESIST, Stats.SLEEP_POWER), true),
  Stun(EffectStun.class, AbnormalEffect.STUN, Pair.of(Stats.STUN_RESIST, Stats.STUN_POWER), true),
  Symbol(EffectSymbol.class, null, false),
  Transformation(EffectTransformation.class, null, true),
  UnAggro(EffectUnAggro.class, null, true),
  Vitality(EffectBuff.class, AbnormalEffect.VITALITY, true),
  Poison(EffectDamOverTime.class, null, Pair.of(Stats.POISON_RESIST, Stats.POISON_POWER), false),
  PoisonLethal(EffectDamOverTimeLethal.class, null, Pair.of(Stats.POISON_RESIST, Stats.POISON_POWER), false),
  Bleed(EffectDamOverTime.class, null, Pair.of(Stats.BLEED_RESIST, Stats.BLEED_POWER), false),
  Debuff(EffectBuff.class, null, false),
  WatcherGaze(EffectBuff.class, null, false),
  AbsorbDamageToEffector(EffectBuff.class, null, false),
  AbsorbDamageToSummon(EffectLDManaDamOverTime.class, null, true);

  private final Constructor<? extends Effect> _constructor;
  private final AbnormalEffect _abnormal;
  private final Pair<Stats, Stats> _resistAndPowerType;
  private final boolean _isRaidImmune;

  EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, boolean isRaidImmune) {
    try {
      this._constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
    } catch (NoSuchMethodException var7) {
      throw new Error(var7);
    }

    this._abnormal = abnormal;
    this._resistAndPowerType = null;
    this._isRaidImmune = isRaidImmune;
  }

  EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, Pair<Stats, Stats> resistAndPowerType, boolean isRaidImmune) {
    try {
      this._constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
    } catch (NoSuchMethodException var8) {
      throw new Error(var8);
    }

    this._abnormal = abnormal;
    this._resistAndPowerType = resistAndPowerType;
    this._isRaidImmune = isRaidImmune;
  }

  public AbnormalEffect getAbnormal() {
    return this._abnormal;
  }

  public Pair<Stats, Stats> getResistAndPowerType() {
    return this._resistAndPowerType;
  }

  public boolean isRaidImmune() {
    return this._isRaidImmune;
  }

  public Effect makeEffect(Env env, EffectTemplate template) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    return this._constructor.newInstance(env, template);
  }
}
