//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.conditions.ConditionPlayerState;
import l2.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.tables.LevelUpTable;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;

public class StatFunctions {
  public StatFunctions() {
  }

  public static void addPredefinedFuncs(Creature cha) {
    if (cha.isPlayer()) {
      cha.addStatFunc(StatFunctions.FuncMultRegenResting.getFunc(Stats.REGENERATE_CP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenStanding.getFunc(Stats.REGENERATE_CP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenRunning.getFunc(Stats.REGENERATE_CP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenResting.getFunc(Stats.REGENERATE_HP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenStanding.getFunc(Stats.REGENERATE_HP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenRunning.getFunc(Stats.REGENERATE_HP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenResting.getFunc(Stats.REGENERATE_MP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenStanding.getFunc(Stats.REGENERATE_MP_RATE));
      cha.addStatFunc(StatFunctions.FuncMultRegenRunning.getFunc(Stats.REGENERATE_MP_RATE));
      cha.addStatFunc(StatFunctions.FuncMaxCpAdd.func);
      cha.addStatFunc(StatFunctions.FuncMaxHpAdd.func);
      cha.addStatFunc(StatFunctions.FuncMaxMpAdd.func);
      cha.addStatFunc(StatFunctions.FuncMaxCpMul.func);
      cha.addStatFunc(StatFunctions.FuncMaxHpMul.func);
      cha.addStatFunc(StatFunctions.FuncMaxMpMul.func);
      cha.addStatFunc(StatFunctions.FuncAttackRange.func);
      cha.addStatFunc(StatFunctions.FuncHennaSTR.func);
      cha.addStatFunc(StatFunctions.FuncHennaDEX.func);
      cha.addStatFunc(StatFunctions.FuncHennaINT.func);
      cha.addStatFunc(StatFunctions.FuncHennaMEN.func);
      cha.addStatFunc(StatFunctions.FuncHennaCON.func);
      cha.addStatFunc(StatFunctions.FuncHennaWIT.func);
      cha.addStatFunc(StatFunctions.FuncInventory.func);
      cha.addStatFunc(StatFunctions.FuncWarehouse.func);
      cha.addStatFunc(StatFunctions.FuncTradeLimit.func);
      cha.addStatFunc(StatFunctions.FuncSDefPlayers.func);
      cha.addStatFunc(StatFunctions.FuncMaxHpLimit.func);
      cha.addStatFunc(StatFunctions.FuncMaxMpLimit.func);
      cha.addStatFunc(StatFunctions.FuncMaxCpLimit.func);
      cha.addStatFunc(StatFunctions.FuncRunSpdLimit.func);
      cha.addStatFunc(StatFunctions.FuncRunSpdLimit.func);
      cha.addStatFunc(StatFunctions.FuncPDefLimit.func);
      cha.addStatFunc(StatFunctions.FuncMDefLimit.func);
      cha.addStatFunc(StatFunctions.FuncPAtkLimit.func);
      cha.addStatFunc(StatFunctions.FuncMAtkLimit.func);
    }

    if (cha.isPlayer() || cha.isPet()) {
      cha.addStatFunc(StatFunctions.FuncPAtkMul.func);
      cha.addStatFunc(StatFunctions.FuncMAtkMul.func);
      cha.addStatFunc(StatFunctions.FuncPDefMul.func);
      cha.addStatFunc(StatFunctions.FuncMDefMul.func);
    }

    if (!cha.isPet()) {
      cha.addStatFunc(StatFunctions.FuncAccuracyAdd.func);
      cha.addStatFunc(StatFunctions.FuncEvasionAdd.func);
    }

    if (!cha.isPet() && !cha.isSummon()) {
      cha.addStatFunc(StatFunctions.FuncPAtkSpeedMul.func);
      cha.addStatFunc(StatFunctions.FuncMAtkSpeedMul.func);
      cha.addStatFunc(StatFunctions.FuncSDefInit.func);
      cha.addStatFunc(StatFunctions.FuncSDefAll.func);
    } else {
      cha.addStatFunc(StatFunctions.FuncMaxHpMul.func);
      cha.addStatFunc(StatFunctions.FuncMaxMpMul.func);
    }

    cha.addStatFunc(StatFunctions.FuncMoveSpeedMul.func);
    cha.addStatFunc(StatFunctions.FuncPAtkSpdLimit.func);
    cha.addStatFunc(StatFunctions.FuncMAtkSpdLimit.func);
    cha.addStatFunc(StatFunctions.FuncCAtkLimit.func);
    cha.addStatFunc(StatFunctions.FuncEvasionLimit.func);
    cha.addStatFunc(StatFunctions.FuncAccuracyLimit.func);
    cha.addStatFunc(StatFunctions.FuncCritLimit.func);
    cha.addStatFunc(StatFunctions.FuncMCritLimit.func);
    cha.addStatFunc(StatFunctions.FuncMCriticalRateMul.func);
    cha.addStatFunc(StatFunctions.FuncPCriticalRateMul.func);
    cha.addStatFunc(StatFunctions.FuncPDamageResists.func);
    cha.addStatFunc(StatFunctions.FuncMDamageResists.func);
    cha.addStatFunc(StatFunctions.FuncAttributeAttackInit.getFunc(Element.FIRE));
    cha.addStatFunc(StatFunctions.FuncAttributeAttackInit.getFunc(Element.WATER));
    cha.addStatFunc(StatFunctions.FuncAttributeAttackInit.getFunc(Element.EARTH));
    cha.addStatFunc(StatFunctions.FuncAttributeAttackInit.getFunc(Element.WIND));
    cha.addStatFunc(StatFunctions.FuncAttributeAttackInit.getFunc(Element.HOLY));
    cha.addStatFunc(StatFunctions.FuncAttributeAttackInit.getFunc(Element.UNHOLY));
    cha.addStatFunc(StatFunctions.FuncAttributeDefenceInit.getFunc(Element.FIRE));
    cha.addStatFunc(StatFunctions.FuncAttributeDefenceInit.getFunc(Element.WATER));
    cha.addStatFunc(StatFunctions.FuncAttributeDefenceInit.getFunc(Element.EARTH));
    cha.addStatFunc(StatFunctions.FuncAttributeDefenceInit.getFunc(Element.WIND));
    cha.addStatFunc(StatFunctions.FuncAttributeDefenceInit.getFunc(Element.HOLY));
    cha.addStatFunc(StatFunctions.FuncAttributeDefenceInit.getFunc(Element.UNHOLY));
  }

  private static class FuncAttributeDefenceInit extends Func {
    static final Func[] func;
    private Element element;

    static Func getFunc(Element element) {
      return func[element.getId()];
    }

    private FuncAttributeDefenceInit(Element element) {
      super(element.getDefence(), 1, (Object)null);
      this.element = element;
    }

    public void calc(Env env) {
      env.value += (double)env.character.getTemplate().baseAttributeDefence[this.element.getId()];
    }

    static {
      func = new StatFunctions.FuncAttributeDefenceInit[Element.VALUES.length];

      for(int i = 0; i < Element.VALUES.length; ++i) {
        func[i] = new StatFunctions.FuncAttributeDefenceInit(Element.VALUES[i]);
      }

    }
  }

  private static class FuncAttributeAttackInit extends Func {
    static final Func[] func;
    private Element element;

    static Func getFunc(Element element) {
      return func[element.getId()];
    }

    private FuncAttributeAttackInit(Element element) {
      super(element.getAttack(), 1, (Object)null);
      this.element = element;
    }

    public void calc(Env env) {
      env.value += (double)env.character.getTemplate().baseAttributeAttack[this.element.getId()];
    }

    static {
      func = new StatFunctions.FuncAttributeAttackInit[Element.VALUES.length];

      for(int i = 0; i < Element.VALUES.length; ++i) {
        func[i] = new StatFunctions.FuncAttributeAttackInit(Element.VALUES[i]);
      }

    }
  }

  private static class FuncMCritLimit extends Func {
    static final Func func = new StatFunctions.FuncMCritLimit();

    private FuncMCritLimit() {
      super(Stats.MCRITICAL_RATE, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MCRIT, env.value);
    }
  }

  private static class FuncCritLimit extends Func {
    static final Func func = new StatFunctions.FuncCritLimit();

    private FuncCritLimit() {
      super(Stats.CRITICAL_BASE, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_CRIT, env.value);
    }
  }

  private static class FuncAccuracyLimit extends Func {
    static final Func func = new StatFunctions.FuncAccuracyLimit();

    private FuncAccuracyLimit() {
      super(Stats.ACCURACY_COMBAT, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_ACCURACY, env.value);
    }
  }

  private static class FuncEvasionLimit extends Func {
    static final Func func = new StatFunctions.FuncEvasionLimit();

    private FuncEvasionLimit() {
      super(Stats.EVASION_RATE, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_EVASION, env.value);
    }
  }

  private static class FuncCAtkLimit extends Func {
    static final Func func = new StatFunctions.FuncCAtkLimit();

    private FuncCAtkLimit() {
      super(Stats.CRITICAL_DAMAGE, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_CRIT_DAM / 2.0D, env.value);
    }
  }

  private static class FuncMAtkSpdLimit extends Func {
    static final Func func = new StatFunctions.FuncMAtkSpdLimit();

    private FuncMAtkSpdLimit() {
      super(Stats.MAGIC_ATTACK_SPEED, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MATK_SPD, env.value);
    }
  }

  private static class FuncPAtkSpdLimit extends Func {
    static final Func func = new StatFunctions.FuncPAtkSpdLimit();

    private FuncPAtkSpdLimit() {
      super(Stats.POWER_ATTACK_SPEED, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_PATK_SPD, env.value);
    }
  }

  private static class FuncMAtkLimit extends Func {
    static final Func func = new StatFunctions.FuncMAtkLimit();

    private FuncMAtkLimit() {
      super(Stats.MAGIC_ATTACK, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MATK, env.value);
    }
  }

  private static class FuncPAtkLimit extends Func {
    static final Func func = new StatFunctions.FuncPAtkLimit();

    private FuncPAtkLimit() {
      super(Stats.POWER_ATTACK, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_PATK, env.value);
    }
  }

  private static class FuncMDefLimit extends Func {
    static final Func func = new StatFunctions.FuncMDefLimit();

    private FuncMDefLimit() {
      super(Stats.MAGIC_DEFENCE, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MDEF, env.value);
    }
  }

  private static class FuncPDefLimit extends Func {
    static final Func func = new StatFunctions.FuncPDefLimit();

    private FuncPDefLimit() {
      super(Stats.POWER_DEFENCE, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_PDEF, env.value);
    }
  }

  private static class FuncRunSpdLimit extends Func {
    static final Func func = new StatFunctions.FuncRunSpdLimit();

    private FuncRunSpdLimit() {
      super(Stats.RUN_SPEED, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MOVE, env.value);
    }
  }

  private static class FuncMaxCpLimit extends Func {
    static final Func func = new StatFunctions.FuncMaxCpLimit();

    private FuncMaxCpLimit() {
      super(Stats.MAX_CP, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MAX_CP, env.value);
    }
  }

  private static class FuncMaxMpLimit extends Func {
    static final Func func = new StatFunctions.FuncMaxMpLimit();

    private FuncMaxMpLimit() {
      super(Stats.MAX_MP, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MAX_MP, env.value);
    }
  }

  private static class FuncMaxHpLimit extends Func {
    static final Func func = new StatFunctions.FuncMaxHpLimit();

    private FuncMaxHpLimit() {
      super(Stats.MAX_HP, 256, (Object)null);
    }

    public void calc(Env env) {
      env.value = Math.min((double)Config.LIM_MAX_HP, env.value);
    }
  }

  private static class FuncSDefPlayers extends Func {
    static final StatFunctions.FuncSDefPlayers func = new StatFunctions.FuncSDefPlayers();

    private FuncSDefPlayers() {
      super(Stats.SHIELD_RATE, 32, (Object)null);
    }

    public void calc(Env env) {
      if (env.value != 0.0D) {
        Creature cha = env.character;
        ItemInstance shld = ((Player)cha).getInventory().getPaperdollItem(8);
        if (shld != null && shld.getItemType() == WeaponType.NONE) {
          env.value *= BaseStats.DEX.calcBonus(env.character);
        }
      }
    }
  }

  private static class FuncSDefAll extends Func {
    static final StatFunctions.FuncSDefAll func = new StatFunctions.FuncSDefAll();

    private FuncSDefAll() {
      super(Stats.SHIELD_RATE, 32, (Object)null);
    }

    public void calc(Env env) {
      if (env.value != 0.0D) {
        Creature target = env.target;
        if (target != null) {
          WeaponTemplate weapon = target.getActiveWeaponItem();
          if (weapon != null) {
            switch(weapon.getItemType()) {
              case BOW:
                env.value += 30.0D;
                break;
              case DAGGER:
                env.value += 12.0D;
            }
          }
        }

      }
    }
  }

  private static class FuncSDefInit extends Func {
    static final Func func = new StatFunctions.FuncSDefInit();

    private FuncSDefInit() {
      super(Stats.SHIELD_RATE, 1, (Object)null);
    }

    public void calc(Env env) {
      Creature cha = env.character;
      env.value = (double)cha.getTemplate().baseShldRate;
    }
  }

  private static class FuncTradeLimit extends Func {
    static final StatFunctions.FuncTradeLimit func = new StatFunctions.FuncTradeLimit();

    private FuncTradeLimit() {
      super(Stats.TRADE_LIMIT, 1, (Object)null);
    }

    public void calc(Env env) {
      Player activeChar = (Player)env.character;
      if (activeChar.getRace() == Race.dwarf) {
        if (activeChar.getLevel() < 40) {
          env.value = (double)Config.MAX_PVTSTORE_SLOTS_DWARF_FIRST_JOB;
        } else {
          env.value = (double)Config.MAX_PVTSTORE_SLOTS_DWARF;
        }
      } else {
        if (activeChar.getLevel() < 40) {
          env.value = (double)Config.MAX_PVTSTORE_SLOTS_OTHER_FIRST_JOB;
        }

        env.value = (double)Config.MAX_PVTSTORE_SLOTS_OTHER;
      }

    }
  }

  private static class FuncWarehouse extends Func {
    static final StatFunctions.FuncWarehouse func = new StatFunctions.FuncWarehouse();

    private FuncWarehouse() {
      super(Stats.STORAGE_LIMIT, 1, (Object)null);
    }

    public void calc(Env env) {
      Player player = (Player)env.character;
      if (player.getTemplate().race == Race.dwarf) {
        env.value = (double)Config.WAREHOUSE_SLOTS_DWARF;
      } else {
        env.value = (double)Config.WAREHOUSE_SLOTS_NO_DWARF;
      }

      env.value += (double)player.getExpandWarehouse();
    }
  }

  private static class FuncInventory extends Func {
    static final StatFunctions.FuncInventory func = new StatFunctions.FuncInventory();

    private FuncInventory() {
      super(Stats.INVENTORY_LIMIT, 1, (Object)null);
    }

    public void calc(Env env) {
      Player player = (Player)env.character;
      if (player.isGM()) {
        env.value = (double)Config.INVENTORY_MAXIMUM_GM;
      } else if (player.getTemplate().race == Race.dwarf) {
        env.value = (double)Config.INVENTORY_MAXIMUM_DWARF;
      } else {
        env.value = (double)Config.INVENTORY_MAXIMUM_NO_DWARF;
      }

      env.value += (double)player.getExpandInventory();
      env.value = Math.min(env.value, (double)Config.SERVICES_EXPAND_INVENTORY_MAX);
    }
  }

  private static class FuncMDamageResists extends Func {
    static final StatFunctions.FuncMDamageResists func = new StatFunctions.FuncMDamageResists();

    private FuncMDamageResists() {
      super(Stats.MAGIC_DAMAGE, 48, (Object)null);
    }

    public void calc(Env env) {
      if (env.target.isRaid() && Math.abs(env.character.getLevel() - env.target.getLevel()) > Config.RAID_MAX_LEVEL_DIFF) {
        env.value = 1.0D;
      } else {
        env.value = Formulas.calcDamageResists(env.skill, env.character, env.target, env.value);
      }
    }
  }

  private static class FuncPDamageResists extends Func {
    static final StatFunctions.FuncPDamageResists func = new StatFunctions.FuncPDamageResists();

    private FuncPDamageResists() {
      super(Stats.PHYSICAL_DAMAGE, 48, (Object)null);
    }

    public void calc(Env env) {
      if (env.target.isRaid() && env.character.getLevel() - env.target.getLevel() > Config.RAID_MAX_LEVEL_DIFF) {
        env.value = 1.0D;
      } else {
        WeaponTemplate weapon = env.character.getActiveWeaponItem();
        if (weapon == null) {
          env.value *= 0.01D * env.target.calcStat(Stats.FIST_WPN_VULNERABILITY, env.character, env.skill);
        } else if (weapon.getItemType().getDefence() != null) {
          env.value *= 0.01D * env.target.calcStat(weapon.getItemType().getDefence(), env.character, env.skill);
        }

        env.value = Formulas.calcDamageResists(env.skill, env.character, env.target, env.value);
      }
    }
  }

  private static class FuncMaxMpMul extends Func {
    static final StatFunctions.FuncMaxMpMul func = new StatFunctions.FuncMaxMpMul();

    private FuncMaxMpMul() {
      super(Stats.MAX_MP, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.MEN.calcBonus(env.character);
    }
  }

  private static class FuncMaxMpAdd extends Func {
    static final StatFunctions.FuncMaxMpAdd func = new StatFunctions.FuncMaxMpAdd();

    private FuncMaxMpAdd() {
      super(Stats.MAX_MP, 16, (Object)null);
    }

    public void calc(Env env) {
      env.value += LevelUpTable.getInstance().getMaxMP(env.character);
    }
  }

  private static class FuncMaxCpMul extends Func {
    static final StatFunctions.FuncMaxCpMul func = new StatFunctions.FuncMaxCpMul();

    private FuncMaxCpMul() {
      super(Stats.MAX_CP, 32, (Object)null);
    }

    public void calc(Env env) {
      double cpSSmod = 1.0D;
      int sealOwnedBy = SevenSigns.getInstance().getSealOwner(3);
      int playerCabal = SevenSigns.getInstance().getPlayerCabal((Player)env.character);
      if (sealOwnedBy != 0) {
        if (playerCabal == sealOwnedBy) {
          cpSSmod = 1.1D;
        } else {
          cpSSmod = 0.9D;
        }
      }

      env.value *= BaseStats.CON.calcBonus(env.character) * cpSSmod;
    }
  }

  private static class FuncMaxCpAdd extends Func {
    static final StatFunctions.FuncMaxCpAdd func = new StatFunctions.FuncMaxCpAdd();

    private FuncMaxCpAdd() {
      super(Stats.MAX_CP, 16, (Object)null);
    }

    public void calc(Env env) {
      env.value += env.value += LevelUpTable.getInstance().getMaxCP(env.character);
    }
  }

  private static class FuncMaxHpMul extends Func {
    static final StatFunctions.FuncMaxHpMul func = new StatFunctions.FuncMaxHpMul();

    private FuncMaxHpMul() {
      super(Stats.MAX_HP, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.CON.calcBonus(env.character);
    }
  }

  private static class FuncMaxHpAdd extends Func {
    static final StatFunctions.FuncMaxHpAdd func = new StatFunctions.FuncMaxHpAdd();

    private FuncMaxHpAdd() {
      super(Stats.MAX_HP, 16, (Object)null);
    }

    public void calc(Env env) {
      env.value += LevelUpTable.getInstance().getMaxHP(env.character);
    }
  }

  private static class FuncHennaWIT extends Func {
    static final StatFunctions.FuncHennaWIT func = new StatFunctions.FuncHennaWIT();

    private FuncHennaWIT() {
      super(Stats.STAT_WIT, 16, (Object)null);
    }

    public void calc(Env env) {
      Player pc = (Player)env.character;
      if (pc != null) {
        env.value = Math.max(1.0D, env.value + (double)pc.getHennaStatWIT());
      }

    }
  }

  private static class FuncHennaCON extends Func {
    static final StatFunctions.FuncHennaCON func = new StatFunctions.FuncHennaCON();

    private FuncHennaCON() {
      super(Stats.STAT_CON, 16, (Object)null);
    }

    public void calc(Env env) {
      Player pc = (Player)env.character;
      if (pc != null) {
        env.value = Math.max(1.0D, env.value + (double)pc.getHennaStatCON());
      }

    }
  }

  private static class FuncHennaMEN extends Func {
    static final StatFunctions.FuncHennaMEN func = new StatFunctions.FuncHennaMEN();

    private FuncHennaMEN() {
      super(Stats.STAT_MEN, 16, (Object)null);
    }

    public void calc(Env env) {
      Player pc = (Player)env.character;
      if (pc != null) {
        env.value = Math.max(1.0D, env.value + (double)pc.getHennaStatMEN());
      }

    }
  }

  private static class FuncHennaINT extends Func {
    static final StatFunctions.FuncHennaINT func = new StatFunctions.FuncHennaINT();

    private FuncHennaINT() {
      super(Stats.STAT_INT, 16, (Object)null);
    }

    public void calc(Env env) {
      Player pc = (Player)env.character;
      if (pc != null) {
        env.value = Math.max(1.0D, env.value + (double)pc.getHennaStatINT());
      }

    }
  }

  private static class FuncHennaDEX extends Func {
    static final StatFunctions.FuncHennaDEX func = new StatFunctions.FuncHennaDEX();

    private FuncHennaDEX() {
      super(Stats.STAT_DEX, 16, (Object)null);
    }

    public void calc(Env env) {
      Player pc = (Player)env.character;
      if (pc != null) {
        env.value = Math.max(1.0D, env.value + (double)pc.getHennaStatDEX());
      }

    }
  }

  private static class FuncHennaSTR extends Func {
    static final StatFunctions.FuncHennaSTR func = new StatFunctions.FuncHennaSTR();

    private FuncHennaSTR() {
      super(Stats.STAT_STR, 16, (Object)null);
    }

    public void calc(Env env) {
      Player pc = (Player)env.character;
      if (pc != null) {
        env.value = Math.max(1.0D, env.value + (double)pc.getHennaStatSTR());
      }

    }
  }

  private static class FuncMAtkSpeedMul extends Func {
    static final StatFunctions.FuncMAtkSpeedMul func = new StatFunctions.FuncMAtkSpeedMul();

    private FuncMAtkSpeedMul() {
      super(Stats.MAGIC_ATTACK_SPEED, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.WIT.calcBonus(env.character);
    }
  }

  private static class FuncPAtkSpeedMul extends Func {
    static final StatFunctions.FuncPAtkSpeedMul func = new StatFunctions.FuncPAtkSpeedMul();

    private FuncPAtkSpeedMul() {
      super(Stats.POWER_ATTACK_SPEED, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.DEX.calcBonus(env.character);
    }
  }

  private static class FuncMoveSpeedMul extends Func {
    static final StatFunctions.FuncMoveSpeedMul func = new StatFunctions.FuncMoveSpeedMul();

    private FuncMoveSpeedMul() {
      super(Stats.RUN_SPEED, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.DEX.calcBonus(env.character);
    }
  }

  private static class FuncPCriticalRateMul extends Func {
    static final StatFunctions.FuncPCriticalRateMul func = new StatFunctions.FuncPCriticalRateMul();

    private FuncPCriticalRateMul() {
      super(Stats.CRITICAL_BASE, 16, (Object)null);
    }

    public void calc(Env env) {
      if (!(env.character instanceof Summon)) {
        env.value *= BaseStats.DEX.calcBonus(env.character);
      }

      env.value *= 0.01D * env.character.calcStat(Stats.CRITICAL_RATE, env.target, env.skill);
    }
  }

  private static class FuncMCriticalRateMul extends Func {
    static final StatFunctions.FuncMCriticalRateMul func = new StatFunctions.FuncMCriticalRateMul();

    private FuncMCriticalRateMul() {
      super(Stats.MCRITICAL_RATE, 16, (Object)null);
    }

    public void calc(Env env) {
      env.value *= 0.1D * BaseStats.WIT.calcBonus(env.character);
    }
  }

  private static class FuncEvasionAdd extends Func {
    static final StatFunctions.FuncEvasionAdd func = new StatFunctions.FuncEvasionAdd();

    private FuncEvasionAdd() {
      super(Stats.EVASION_RATE, 16, (Object)null);
    }

    public void calc(Env env) {
      env.value += Math.sqrt((double)env.character.getDEX()) * 6.0D + (double)env.character.getLevel();
      if (env.character.getLevel() > 77) {
        env.value += (double)(env.character.getLevel() - 77);
      }

      if (env.character.getLevel() > 69) {
        env.value += (double)(env.character.getLevel() - 69);
      }

    }
  }

  private static class FuncAccuracyAdd extends Func {
    static final StatFunctions.FuncAccuracyAdd func = new StatFunctions.FuncAccuracyAdd();

    private FuncAccuracyAdd() {
      super(Stats.ACCURACY_COMBAT, 16, (Object)null);
    }

    public void calc(Env env) {
      if (!env.character.isPet()) {
        env.value += Math.sqrt((double)env.character.getDEX()) * 6.0D + (double)env.character.getLevel();
        if (env.character.isSummon()) {
          env.value += env.character.getLevel() < 60 ? 4.0D : 5.0D;
        }

        if (env.character.getLevel() > 77) {
          env.value += (double)(env.character.getLevel() - 77);
        }

        if (env.character.getLevel() > 69) {
          env.value += (double)(env.character.getLevel() - 69);
        }

      }
    }
  }

  private static class FuncAttackRange extends Func {
    static final StatFunctions.FuncAttackRange func = new StatFunctions.FuncAttackRange();

    private FuncAttackRange() {
      super(Stats.POWER_ATTACK_RANGE, 32, (Object)null);
    }

    public void calc(Env env) {
      WeaponTemplate weapon = env.character.getActiveWeaponItem();
      if (weapon != null) {
        env.value = (double)weapon.getAttackRange();
      }

    }
  }

  private static class FuncMDefMul extends Func {
    static final StatFunctions.FuncMDefMul func = new StatFunctions.FuncMDefMul();

    private FuncMDefMul() {
      super(Stats.MAGIC_DEFENCE, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.MEN.calcBonus(env.character) * env.character.getLevelMod();
    }
  }

  private static class FuncPDefMul extends Func {
    static final StatFunctions.FuncPDefMul func = new StatFunctions.FuncPDefMul();

    private FuncPDefMul() {
      super(Stats.POWER_DEFENCE, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= env.character.getLevelMod();
    }
  }

  private static class FuncMAtkMul extends Func {
    static final StatFunctions.FuncMAtkMul func = new StatFunctions.FuncMAtkMul();

    private FuncMAtkMul() {
      super(Stats.MAGIC_ATTACK, 32, (Object)null);
    }

    public void calc(Env env) {
      double ib = BaseStats.INT.calcBonus(env.character);
      double lvlb = env.character.getLevelMod();
      env.value *= lvlb * lvlb * ib * ib;
    }
  }

  private static class FuncPAtkMul extends Func {
    static final StatFunctions.FuncPAtkMul func = new StatFunctions.FuncPAtkMul();

    private FuncPAtkMul() {
      super(Stats.POWER_ATTACK, 32, (Object)null);
    }

    public void calc(Env env) {
      env.value *= BaseStats.STR.calcBonus(env.character) * env.character.getLevelMod();
    }
  }

  private static class FuncMultRegenRunning extends Func {
    static final StatFunctions.FuncMultRegenRunning[] func;

    static Func getFunc(Stats stat) {
      int pos = stat.ordinal();
      if (func[pos] == null) {
        func[pos] = new StatFunctions.FuncMultRegenRunning(stat);
      }

      return func[pos];
    }

    private FuncMultRegenRunning(Stats stat) {
      super(stat, 48, (Object)null);
      this.setCondition(new ConditionPlayerState(CheckPlayerState.RUNNING, true));
    }

    public void calc(Env env) {
      env.value *= 0.7D;
    }

    static {
      func = new StatFunctions.FuncMultRegenRunning[Stats.NUM_STATS];
    }
  }

  private static class FuncMultRegenStanding extends Func {
    static final StatFunctions.FuncMultRegenStanding[] func;

    static Func getFunc(Stats stat) {
      int pos = stat.ordinal();
      if (func[pos] == null) {
        func[pos] = new StatFunctions.FuncMultRegenStanding(stat);
      }

      return func[pos];
    }

    private FuncMultRegenStanding(Stats stat) {
      super(stat, 48, (Object)null);
      this.setCondition(new ConditionPlayerState(CheckPlayerState.STANDING, true));
    }

    public void calc(Env env) {
      env.value *= 1.1D;
    }

    static {
      func = new StatFunctions.FuncMultRegenStanding[Stats.NUM_STATS];
    }
  }

  private static class FuncMultRegenResting extends Func {
    static final StatFunctions.FuncMultRegenResting[] func;

    static Func getFunc(Stats stat) {
      int pos = stat.ordinal();
      if (func[pos] == null) {
        func[pos] = new StatFunctions.FuncMultRegenResting(stat);
      }

      return func[pos];
    }

    private FuncMultRegenResting(Stats stat) {
      super(stat, 48, (Object)null);
      this.setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
    }

    public void calc(Env env) {
      if (env.character.isPlayer() && env.character.getLevel() <= 40 && ((Player)env.character).getClassId().getLevel() < 3 && this.stat == Stats.REGENERATE_HP_RATE) {
        env.value *= 6.0D;
      } else {
        env.value *= 1.5D;
      }

    }

    static {
      func = new StatFunctions.FuncMultRegenResting[Stats.NUM_STATS];
    }
  }
}
