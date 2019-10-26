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
  AddSkills(EffectAddSkills.class, (AbnormalEffect)null, false),
  AgathionResurrect(EffectAgathionRes.class, (AbnormalEffect)null, true),
  Aggression(EffectAggression.class, (AbnormalEffect)null, true),
  Betray(EffectBetray.class, (AbnormalEffect)null, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  BlessNoblesse(EffectBlessNoblesse.class, (AbnormalEffect)null, true),
  BlockStat(EffectBlockStat.class, (AbnormalEffect)null, true),
  Buff(EffectBuff.class, (AbnormalEffect)null, false),
  BuffImmunity(EffectBuffImmunity.class, (AbnormalEffect)null, false),
  Bluff(EffectBluff.class, AbnormalEffect.NULL, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  DebuffImmunity(EffectDebuffImmunity.class, (AbnormalEffect)null, true),
  DispelEffects(EffectDispelEffects.class, (AbnormalEffect)null, Pair.of(Stats.CANCEL_RESIST, Stats.CANCEL_POWER), true),
  CallSkills(EffectCallSkills.class, (AbnormalEffect)null, false),
  CombatPointHealOverTime(EffectCombatPointHealOverTime.class, (AbnormalEffect)null, true),
  ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, (AbnormalEffect)null, true),
  Charge(EffectCharge.class, (AbnormalEffect)null, false),
  CharmOfCourage(EffectCharmOfCourage.class, (AbnormalEffect)null, true),
  CPDamPercent(EffectCPDamPercent.class, (AbnormalEffect)null, true),
  Cubic(EffectCubic.class, (AbnormalEffect)null, true),
  DamOverTime(EffectDamOverTime.class, (AbnormalEffect)null, false),
  DamOverTimeLethal(EffectDamOverTimeLethal.class, (AbnormalEffect)null, false),
  DestroySummon(EffectDestroySummon.class, (AbnormalEffect)null, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  Disarm(EffectDisarm.class, (AbnormalEffect)null, true),
  Discord(EffectDiscord.class, AbnormalEffect.CONFUSED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  Enervation(EffectEnervation.class, (AbnormalEffect)null, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), false),
  FakeDeath(EffectFakeDeath.class, (AbnormalEffect)null, true),
  Fear(EffectFear.class, AbnormalEffect.AFFRAID, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  Grow(EffectGrow.class, AbnormalEffect.GROW, false),
  Hate(EffectHate.class, (AbnormalEffect)null, false),
  Heal(EffectHeal.class, (AbnormalEffect)null, false),
  HealBlock(EffectHealBlock.class, (AbnormalEffect)null, true),
  HealCPPercent(EffectHealCPPercent.class, (AbnormalEffect)null, true),
  HealOverTime(EffectHealOverTime.class, (AbnormalEffect)null, false),
  HealPercent(EffectHealPercent.class, (AbnormalEffect)null, false),
  HPDamPercent(EffectHPDamPercent.class, (AbnormalEffect)null, true),
  IgnoreSkill(EffectBuff.class, (AbnormalEffect)null, false),
  Immobilize(EffectImmobilize.class, (AbnormalEffect)null, true),
  Interrupt(EffectInterrupt.class, (AbnormalEffect)null, true),
  Invulnerable(EffectInvulnerable.class, (AbnormalEffect)null, false),
  InvulnerableHeal(EffectInvulnerableHeal.class, (AbnormalEffect)null, false),
  Invisible(EffectInvisible.class, (AbnormalEffect)null, false),
  LockInventory(EffectLockInventory.class, (AbnormalEffect)null, false),
  CurseOfLifeFlow(EffectCurseOfLifeFlow.class, (AbnormalEffect)null, true),
  LDManaDamOverTime(EffectLDManaDamOverTime.class, (AbnormalEffect)null, true),
  ManaDamOverTime(EffectManaDamOverTime.class, (AbnormalEffect)null, true),
  ManaHeal(EffectManaHeal.class, (AbnormalEffect)null, false),
  ManaHealOverTime(EffectManaHealOverTime.class, (AbnormalEffect)null, false),
  ManaHealPercent(EffectManaHealPercent.class, (AbnormalEffect)null, false),
  Meditation(EffectMeditation.class, (AbnormalEffect)null, false),
  MPDamPercent(EffectMPDamPercent.class, (AbnormalEffect)null, true),
  Mute(EffectMute.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  MuteAll(EffectMuteAll.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  MuteAttack(EffectMuteAttack.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  MutePhisycal(EffectMutePhisycal.class, AbnormalEffect.MUTED, Pair.of(Stats.MENTAL_RESIST, Stats.MENTAL_POWER), true),
  NegateEffects(EffectNegateEffects.class, (AbnormalEffect)null, false),
  NegateMusic(EffectNegateMusic.class, (AbnormalEffect)null, false),
  Paralyze(EffectParalyze.class, AbnormalEffect.HOLD_1, Pair.of(Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER), true),
  Petrification(EffectPetrification.class, AbnormalEffect.HOLD_2, Pair.of(Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER), true),
  RandomHate(EffectRandomHate.class, (AbnormalEffect)null, true),
  Relax(EffectRelax.class, (AbnormalEffect)null, true),
  RemoveTarget(EffectRemoveTarget.class, (AbnormalEffect)null, true),
  Root(EffectRoot.class, AbnormalEffect.ROOT, Pair.of(Stats.ROOT_RESIST, Stats.ROOT_POWER), true),
  Salvation(EffectSalvation.class, (AbnormalEffect)null, true),
  ServitorShare(EffectServitorShare.class, (AbnormalEffect)null, true),
  SilentMove(EffectSilentMove.class, AbnormalEffect.STEALTH, true),
  SkillSeed(EffectSkillSeed.class, (AbnormalEffect)null, true),
  Sleep(EffectSleep.class, AbnormalEffect.SLEEP, Pair.of(Stats.SLEEP_RESIST, Stats.SLEEP_POWER), true),
  Stun(EffectStun.class, AbnormalEffect.STUN, Pair.of(Stats.STUN_RESIST, Stats.STUN_POWER), true),
  Symbol(EffectSymbol.class, (AbnormalEffect)null, false),
  Transformation(EffectTransformation.class, (AbnormalEffect)null, true),
  UnAggro(EffectUnAggro.class, (AbnormalEffect)null, true),
  Vitality(EffectBuff.class, AbnormalEffect.VITALITY, true),
  Poison(EffectDamOverTime.class, (AbnormalEffect)null, Pair.of(Stats.POISON_RESIST, Stats.POISON_POWER), false),
  PoisonLethal(EffectDamOverTimeLethal.class, (AbnormalEffect)null, Pair.of(Stats.POISON_RESIST, Stats.POISON_POWER), false),
  Bleed(EffectDamOverTime.class, (AbnormalEffect)null, Pair.of(Stats.BLEED_RESIST, Stats.BLEED_POWER), false),
  Debuff(EffectBuff.class, (AbnormalEffect)null, false),
  WatcherGaze(EffectBuff.class, (AbnormalEffect)null, false),
  AbsorbDamageToEffector(EffectBuff.class, (AbnormalEffect)null, false),
  AbsorbDamageToSummon(EffectLDManaDamOverTime.class, (AbnormalEffect)null, true);

  private final Constructor<? extends Effect> _constructor;
  private final AbnormalEffect _abnormal;
  private final Pair<Stats, Stats> _resistAndPowerType;
  private final boolean _isRaidImmune;

  private EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, boolean isRaidImmune) {
    try {
      this._constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
    } catch (NoSuchMethodException var7) {
      throw new Error(var7);
    }

    this._abnormal = abnormal;
    this._resistAndPowerType = null;
    this._isRaidImmune = isRaidImmune;
  }

  private EffectType(Class<? extends Effect> clazz, AbnormalEffect abnormal, Pair<Stats, Stats> resistAndPowerType, boolean isRaidImmune) {
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
    return (Effect)this._constructor.newInstance(env, template);
  }
}
