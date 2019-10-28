//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.text.StrBuilder;

import java.text.NumberFormat;
import java.util.Locale;

public class WhoAmI implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"whoami", "whoiam"};

  public WhoAmI() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player player, String args) {
    Creature target = null;
    if (!Config.SERVICES_WHOIAM_COMMAND_ENABLE && !player.isGM()) {
      return true;
    } else {
      double hpRegen = Formulas.calcHpRegen(player);
      double cpRegen = Formulas.calcCpRegen(player);
      double mpRegen = Formulas.calcMpRegen(player);
      double hpDrain = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0.0D, (Creature) target, (Skill) null);
      double hpGain = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0D, (Creature) target, (Skill) null);
      double mpGain = player.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0D, (Creature) target, (Skill) null);
      double critPerc = 2.0D * player.calcStat(Stats.CRITICAL_DAMAGE, (Creature) target, (Skill) null);
      double critStatic = player.calcStat(Stats.CRITICAL_DAMAGE_STATIC, (Creature) target, (Skill) null);
      double mCritRate = player.calcStat(Stats.MCRITICAL_RATE, (Creature) target, (Skill) null);
      double blowRate = player.calcStat(Stats.FATALBLOW_RATE, (Creature) target, (Skill) null);
      ItemInstance shld = player.getSecondaryWeaponInstance();
      boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;
      double shieldDef = shield ? player.calcStat(Stats.SHIELD_DEFENCE, (double) player.getTemplate().baseShldDef, (Creature) target, (Skill) null) : 0.0D;
      double shieldRate = shield ? player.calcStat(Stats.SHIELD_RATE, (Creature) target, (Skill) null) : 0.0D;
      double xpRate = player.getRateExp();
      double spRate = player.getRateSp();
      double dropRate = player.getRateItems();
      double adenaRate = player.getRateAdena();
      double spoilRate = player.getRateSpoil();
      double fireResist = player.calcStat(Element.FIRE.getDefence(), 0.0D, (Creature) target, (Skill) null);
      double windResist = player.calcStat(Element.WIND.getDefence(), 0.0D, (Creature) target, (Skill) null);
      double waterResist = player.calcStat(Element.WATER.getDefence(), 0.0D, (Creature) target, (Skill) null);
      double earthResist = player.calcStat(Element.EARTH.getDefence(), 0.0D, (Creature) target, (Skill) null);
      double holyResist = player.calcStat(Element.HOLY.getDefence(), 0.0D, (Creature) target, (Skill) null);
      double unholyResist = player.calcStat(Element.UNHOLY.getDefence(), 0.0D, (Creature) target, (Skill) null);
      double bleedPower = player.calcStat(Stats.BLEED_POWER, (Creature) target, (Skill) null);
      double bleedResist = player.calcStat(Stats.BLEED_RESIST, (Creature) target, (Skill) null);
      double poisonPower = player.calcStat(Stats.POISON_POWER, (Creature) target, (Skill) null);
      double poisonResist = player.calcStat(Stats.POISON_RESIST, (Creature) target, (Skill) null);
      double stunPower = player.calcStat(Stats.STUN_POWER, (Creature) target, (Skill) null);
      double stunResist = player.calcStat(Stats.STUN_RESIST, (Creature) target, (Skill) null);
      double rootPower = player.calcStat(Stats.ROOT_POWER, (Creature) target, (Skill) null);
      double rootResist = player.calcStat(Stats.ROOT_RESIST, (Creature) target, (Skill) null);
      double sleepPower = player.calcStat(Stats.SLEEP_POWER, (Creature) target, (Skill) null);
      double sleepResist = player.calcStat(Stats.SLEEP_RESIST, (Creature) target, (Skill) null);
      double paralyzePower = player.calcStat(Stats.PARALYZE_POWER, (Creature) target, (Skill) null);
      double paralyzeResist = player.calcStat(Stats.PARALYZE_RESIST, (Creature) target, (Skill) null);
      double mentalPower = player.calcStat(Stats.MENTAL_POWER, (Creature) target, (Skill) null);
      double mentalResist = player.calcStat(Stats.MENTAL_RESIST, (Creature) target, (Skill) null);
      double debuffPower = player.calcStat(Stats.DEBUFF_POWER, (Creature) target, (Skill) null);
      double debuffResist = player.calcStat(Stats.DEBUFF_RESIST, (Creature) target, (Skill) null);
      double cancelPower = player.calcStat(Stats.CANCEL_POWER, (Creature) target, (Skill) null);
      double cancelResist = player.calcStat(Stats.CANCEL_RESIST, (Creature) target, (Skill) null);
      double swordResist = 100.0D - player.calcStat(Stats.SWORD_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double dualResist = 100.0D - player.calcStat(Stats.DUAL_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double bluntResist = 100.0D - player.calcStat(Stats.BLUNT_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double daggerResist = 100.0D - player.calcStat(Stats.DAGGER_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double bowResist = 100.0D - player.calcStat(Stats.BOW_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double poleResist = 100.0D - player.calcStat(Stats.POLE_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double fistResist = 100.0D - player.calcStat(Stats.FIST_WPN_VULNERABILITY, (Creature) target, (Skill) null);
      double critChanceResist = 100.0D - player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, (Creature) target, (Skill) null);
      double critDamResistStatic = player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, (Creature) target, (Skill) null);
      double critDamResist = 100.0D - 100.0D * (player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1.0D, (Creature) target, (Skill) null) - critDamResistStatic);
      double SkillPower = player.calcStat(Stats.SKILL_POWER, 1.0D, (Creature) target, (Skill) null);
      double PvPPhysDmg = player.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1.0D, (Creature) target, (Skill) null);
      double PvPSkillDmg = player.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1.0D, (Creature) target, (Skill) null);
      double MagicPvPSkillDmg = player.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0D, (Creature) target, (Skill) null);
      double pSkillEvas = player.calcStat(Stats.PSKILL_EVASION, (Creature) null, (Skill) null);
      double reflectDam = player.calcStat(Stats.REFLECT_DAMAGE_PERCENT, (Creature) target, (Skill) null);
      double reflectSMagic = player.calcStat(Stats.REFLECT_MAGIC_SKILL, (Creature) target, (Skill) null);
      double reflectSPhys = player.calcStat(Stats.REFLECT_PHYSIC_SKILL, (Creature) target, (Skill) null);
      double meleePhysRes = player.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, (Creature) target, (Skill) null);
      double pReuse = player.calcStat(Stats.PHYSIC_REUSE_RATE, (Creature) target, (Skill) null);
      double mReuse = player.calcStat(Stats.MAGIC_REUSE_RATE, (Creature) target, (Skill) null);
      String dialog = HtmCache.getInstance().getNotNull("command/whoami.htm", player);
      NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
      df.setMaximumFractionDigits(1);
      df.setMinimumFractionDigits(1);
      StrBuilder sb = new StrBuilder(dialog);
      sb.replaceFirst("%hpRegen%", df.format(hpRegen));
      sb.replaceFirst("%cpRegen%", df.format(cpRegen));
      sb.replaceFirst("%mpRegen%", df.format(mpRegen));
      sb.replaceFirst("%hpDrain%", df.format(hpDrain));
      sb.replaceFirst("%hpGain%", df.format(hpGain));
      sb.replaceFirst("%mpGain%", df.format(mpGain));
      sb.replaceFirst("%critPerc%", df.format(critPerc));
      sb.replaceFirst("%critStatic%", df.format(critStatic));
      sb.replaceFirst("%mCritRate%", df.format(mCritRate));
      sb.replaceFirst("%blowRate%", df.format(blowRate));
      sb.replaceFirst("%shieldDef%", df.format(shieldDef));
      sb.replaceFirst("%shieldRate%", df.format(shieldRate));
      sb.replaceFirst("%xpRate%", df.format(xpRate));
      sb.replaceFirst("%spRate%", df.format(spRate));
      sb.replaceFirst("%dropRate%", df.format(dropRate));
      sb.replaceFirst("%adenaRate%", df.format(adenaRate));
      sb.replaceFirst("%spoilRate%", df.format(spoilRate));
      sb.replaceFirst("%fireResist%", df.format(fireResist));
      sb.replaceFirst("%windResist%", df.format(windResist));
      sb.replaceFirst("%waterResist%", df.format(waterResist));
      sb.replaceFirst("%earthResist%", df.format(earthResist));
      sb.replaceFirst("%holyResist%", df.format(holyResist));
      sb.replaceFirst("%darkResist%", df.format(unholyResist));
      sb.replaceFirst("%bleedPower%", df.format(bleedPower));
      sb.replaceFirst("%bleedResist%", df.format(bleedResist));
      sb.replaceFirst("%poisonPower%", df.format(poisonPower));
      sb.replaceFirst("%poisonResist%", df.format(poisonResist));
      sb.replaceFirst("%stunPower%", df.format(stunPower));
      sb.replaceFirst("%stunResist%", df.format(stunResist));
      sb.replaceFirst("%rootPower%", df.format(rootPower));
      sb.replaceFirst("%rootResist%", df.format(rootResist));
      sb.replaceFirst("%sleepPower%", df.format(sleepPower));
      sb.replaceFirst("%sleepResist%", df.format(sleepResist));
      sb.replaceFirst("%paralyzePower%", df.format(paralyzePower));
      sb.replaceFirst("%paralyzeResist%", df.format(paralyzeResist));
      sb.replaceFirst("%mentalPower%", df.format(mentalPower));
      sb.replaceFirst("%mentalResist%", df.format(mentalResist));
      sb.replaceFirst("%debuffPower%", df.format(debuffPower));
      sb.replaceFirst("%debuffResist%", df.format(debuffResist));
      sb.replaceFirst("%cancelPower%", df.format(cancelPower));
      sb.replaceFirst("%cancelResist%", df.format(cancelResist));
      sb.replaceFirst("%swordResist%", df.format(swordResist));
      sb.replaceFirst("%dualResist%", df.format(dualResist));
      sb.replaceFirst("%bluntResist%", df.format(bluntResist));
      sb.replaceFirst("%daggerResist%", df.format(daggerResist));
      sb.replaceFirst("%bowResist%", df.format(bowResist));
      sb.replaceFirst("%fistResist%", df.format(fistResist));
      sb.replaceFirst("%poleResist%", df.format(poleResist));
      sb.replaceFirst("%critChanceResist%", df.format(critChanceResist));
      sb.replaceFirst("%critDamResist%", df.format(critDamResist));
      sb.replaceFirst("%SkillPower%", df.format(SkillPower));
      sb.replaceFirst("%PvPPhysDmg%", df.format(PvPPhysDmg));
      sb.replaceFirst("%PvPSkillDmg%", df.format(PvPSkillDmg));
      sb.replaceFirst("%MagicPvPSkillDmg%", df.format(MagicPvPSkillDmg));
      sb.replaceFirst("%pSkillEvas%", df.format(pSkillEvas));
      sb.replaceFirst("%reflectDam%", df.format(reflectDam));
      sb.replaceFirst("%reflectSMagic%", df.format(reflectSMagic));
      sb.replaceFirst("%reflectSPhys%", df.format(reflectSPhys));
      sb.replaceFirst("%meleePhysRes%", df.format(meleePhysRes));
      sb.replaceFirst("%pReuse%", df.format(pReuse));
      sb.replaceFirst("%mReuse%", df.format(mReuse));
      NpcHtmlMessage msg = new NpcHtmlMessage(0);
      msg.setHtml(Strings.bbParse(sb.toString()));
      player.sendPacket(msg);
      return false;
    }
  }
}
