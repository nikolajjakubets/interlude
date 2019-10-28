//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.collections.LazyArrayList;
import l2.commons.geometry.Polygon;
import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.*;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.FeedableBeastInstance;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.FlyToLocation.FlyType;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.skills.skillclasses.DeathPenalty;
import l2.gameserver.skills.skillclasses.*;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.PositionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public abstract class Skill extends StatTemplate implements Cloneable {
  private static final Logger _log = LoggerFactory.getLogger(Skill.class);
  public static final Skill[] EMPTY_ARRAY = new Skill[0];
  protected EffectTemplate[] _effectTemplates;
  protected List<Integer> _teachers;
  protected List<ClassId> _canLearn;
  protected Skill.AddedSkill[] _addedSkills;
  protected final int[] _itemConsume;
  protected final int[] _itemConsumeId;
  protected final int _referenceItemId;
  protected final int _referenceItemMpConsume;
  public static final int SKILL_CRAFTING = 172;
  public static final int SKILL_POLEARM_MASTERY = 216;
  public static final int SKILL_CRYSTALLIZE = 248;
  public static final int SKILL_WEAPON_MAGIC_MASTERY1 = 249;
  public static final int SKILL_WEAPON_MAGIC_MASTERY2 = 250;
  public static final int SKILL_BLINDING_BLOW = 321;
  public static final int SKILL_STRIDER_ASSAULT = 325;
  public static final int SKILL_WYVERN_AEGIS = 327;
  public static final int SKILL_BLUFF = 358;
  public static final int SKILL_HEROIC_MIRACLE = 395;
  public static final int SKILL_HEROIC_BERSERKER = 396;
  public static final int SKILL_SOUL_MASTERY = 467;
  public static final int SKILL_TRANSFORM_DISPEL = 619;
  public static final int SKILL_FINAL_FLYING_FORM = 840;
  public static final int SKILL_AURA_BIRD_FALCON = 841;
  public static final int SKILL_AURA_BIRD_OWL = 842;
  public static final int SKILL_RECHARGE = 1013;
  public static final int SKILL_TRANSFER_PAIN = 1262;
  public static final int SKILL_FISHING_MASTERY = 1315;
  public static final int SKILL_NOBLESSE_BLESSING = 1323;
  public static final int SKILL_SUMMON_CP_POTION = 1324;
  public static final int SKILL_FORTUNE_OF_NOBLESSE = 1325;
  public static final int SKILL_HARMONY_OF_NOBLESSE = 1326;
  public static final int SKILL_SYMPHONY_OF_NOBLESSE = 1327;
  public static final int SKILL_HEROIC_VALOR = 1374;
  public static final int SKILL_HEROIC_GRANDEUR = 1375;
  public static final int SKILL_HEROIC_DREAD = 1376;
  public static final int SKILL_MYSTIC_IMMUNITY = 1411;
  public static final int SKILL_RAID_BLESSING = 2168;
  public static final int SKILL_HINDER_STRIDER = 4258;
  public static final int SKILL_WYVERN_BREATH = 4289;
  public static final int SKILL_RAID_CURSE = 4515;
  public static final int SKILL_CHARM_OF_COURAGE = 5041;
  protected boolean _isAltUse;
  protected boolean _isBehind;
  protected boolean _isCancelable;
  protected boolean _isCorpse;
  protected boolean _isCommon;
  protected boolean _isItemHandler;
  protected boolean _isOffensive;
  protected boolean _isPvpSkill;
  protected boolean _isNotUsedByAI;
  protected boolean _isFishingSkill;
  protected boolean _isPvm;
  protected boolean _isForceUse;
  protected boolean _isNewbie;
  protected boolean _isPreservedOnDeath;
  protected boolean _isHeroic;
  protected boolean _isSaveable;
  protected boolean _isSkillTimePermanent;
  protected boolean _isReuseDelayPermanent;
  protected boolean _isReflectable;
  protected boolean _isSuicideAttack;
  protected boolean _isShieldignore;
  protected boolean _isUndeadOnly;
  protected Skill.Ternary _isUseSS;
  protected boolean _isOverhit;
  protected boolean _isSoulBoost;
  protected boolean _isChargeBoost;
  protected boolean _isUsingWhileCasting;
  protected boolean _isIgnoreResists;
  protected boolean _isIgnoreInvul;
  protected boolean _isTrigger;
  protected boolean _isNotAffectedByMute;
  protected boolean _basedOnTargetDebuff;
  protected boolean _deathlink;
  protected boolean _hideStartMessage;
  protected boolean _hideUseMessage;
  protected boolean _skillInterrupt;
  protected boolean _flyingTransformUsage;
  protected boolean _canUseTeleport;
  protected boolean _isProvoke;
  protected boolean _isCubicSkill;
  protected boolean _isSelfDispellable;
  protected boolean _isSlotNone;
  protected boolean _isSharedClassReuse;
  protected Skill.SkillType _skillType;
  protected Skill.SkillOpType _operateType;
  protected Skill.SkillTargetType _targetType;
  protected Skill.SkillMagicType _magicType;
  protected SkillTrait _traitType;
  protected BaseStats _saveVs;
  protected Skill.SkillNextAction _skillNextAction;
  protected Element _element;
  protected FlyType _flyType;
  protected boolean _flyToBack;
  protected Condition[] _preCondition;
  protected int _id;
  protected int _level;
  protected int _baseLevel;
  protected int _displayId;
  protected int _displayLevel;
  protected int _activateRate;
  protected int _castRange;
  protected int _cancelTarget;
  protected int _coolTime;
  protected int _delayedEffect;
  protected int _effectPoint;
  protected int _energyConsume;
  protected int _elementPower;
  protected int _flyRadius;
  protected int _hitTime;
  protected int _hpConsume;
  protected int _levelModifier;
  protected int _magicLevel;
  protected int _matak;
  protected int _minPledgeClass;
  protected int _minRank;
  protected int _negatePower;
  protected int _negateSkill;
  protected int _npcId;
  protected int _numCharges;
  protected int _skillInterruptTime;
  protected int _skillRadius;
  protected int _effectiveRange;
  protected int _soulsConsume;
  protected int _symbolId;
  protected int _weaponsAllowed;
  protected int _enchantLevelCount;
  protected int _criticalRate;
  protected int _secondSkill;
  protected long _reuseDelay;
  protected double _power;
  protected double _powerPvP;
  protected double _powerPvE;
  protected double _mpConsume1;
  protected double _mpConsume2;
  protected double _lethal1;
  protected double _lethal2;
  protected double _absorbPart;
  protected String _name;
  protected String _baseValues;
  protected String _icon;
  public boolean _isStandart;
  private final int hashCode;

  protected Skill(StatsSet set) {
    this._effectTemplates = EffectTemplate.EMPTY_ARRAY;
    this._addedSkills = Skill.AddedSkill.EMPTY_ARRAY;
    this._isCubicSkill = false;
    this._preCondition = Condition.EMPTY_ARRAY;
    this._isStandart = false;
    this._id = set.getInteger("skill_id");
    this._level = set.getInteger("level");
    this._displayId = set.getInteger("displayId", this._id);
    this._displayLevel = set.getInteger("displayLevel", this._level);
    this._baseLevel = set.getInteger("base_level");
    this._name = set.getString("name");
    this._operateType = set.getEnum("operateType", SkillOpType.class);
    this._isNewbie = set.getBool("isNewbie", false);
    this._isSelfDispellable = set.getBool("isSelfDispellable", true);
    this._isPreservedOnDeath = set.getBool("isPreservedOnDeath", false);
    this._isHeroic = set.getBool("isHeroic", false);
    this._isAltUse = set.getBool("altUse", false);
    this._mpConsume1 = set.getInteger("mpConsume1", 0);
    this._mpConsume2 = set.getInteger("mpConsume2", 0);
    this._energyConsume = set.getInteger("energyConsume", 0);
    this._hpConsume = set.getInteger("hpConsume", 0);
    this._soulsConsume = set.getInteger("soulsConsume", 0);
    this._isSoulBoost = set.getBool("soulBoost", false);
    this._isChargeBoost = set.getBool("chargeBoost", false);
    this._isProvoke = set.getBool("provoke", false);
    this._isUsingWhileCasting = set.getBool("isUsingWhileCasting", false);
    this._matak = set.getInteger("mAtk", 0);
    this._isUseSS = Skill.Ternary.valueOf(set.getString("useSS", Skill.Ternary.DEFAULT.toString()).toUpperCase());
    this._magicLevel = set.getInteger("magicLevel", 0);
    this._castRange = set.getInteger("castRange", 40);
    this._effectiveRange = set.getInteger("effectiveRange", this._castRange + (this._castRange < 200 ? 400 : 500));
    this._baseValues = set.getString("baseValues", null);
    String s1 = set.getString("itemConsumeCount", "");
    String s2 = set.getString("itemConsumeId", "");
    String[] s;
    int id;
    if (s1.length() == 0) {
      this._itemConsume = new int[]{0};
    } else {
      s = s1.split(" ");
      this._itemConsume = new int[s.length];

      for (id = 0; id < s.length; ++id) {
        this._itemConsume[id] = Integer.parseInt(s[id]);
      }
    }

    if (s2.length() == 0) {
      this._itemConsumeId = new int[]{0};
    } else {
      s = s2.split(" ");
      this._itemConsumeId = new int[s.length];

      for (id = 0; id < s.length; ++id) {
        this._itemConsumeId[id] = Integer.parseInt(s[id]);
      }
    }

    this._referenceItemId = set.getInteger("referenceItemId", 0);
    this._referenceItemMpConsume = set.getInteger("referenceItemMpConsume", 0);
    this._isItemHandler = set.getBool("isHandler", false);
    this._isCommon = set.getBool("isCommon", false);
    this._isSaveable = set.getBool("isSaveable", true);
    this._coolTime = set.getInteger("coolTime", 0);
    this._skillInterruptTime = set.getInteger("hitCancelTime", 0);
    this._reuseDelay = set.getLong("reuseDelay", 0L);
    this._hitTime = set.getInteger("hitTime", 0);
    this._skillRadius = set.getInteger("skillRadius", 80);
    this._targetType = set.getEnum("target", SkillTargetType.class);
    this._magicType = set.getEnum("magicType", SkillMagicType.class, SkillMagicType.PHYSIC);
    this._traitType = set.getEnum("trait", SkillTrait.class, null);
    this._saveVs = set.getEnum("saveVs", BaseStats.class, null);
    this._hideStartMessage = set.getBool("isHideStartMessage", false);
    this._hideUseMessage = set.getBool("isHideUseMessage", false);
    this._isUndeadOnly = set.getBool("undeadOnly", false);
    this._isCorpse = set.getBool("corpse", false);
    this._power = set.getDouble("power", 0.0D);
    this._powerPvP = set.getDouble("powerPvP", 0.0D);
    this._powerPvE = set.getDouble("powerPvE", 0.0D);
    this._effectPoint = set.getInteger("effectPoint", 0);
    this._skillNextAction = Skill.SkillNextAction.valueOf(set.getString("nextAction", "DEFAULT").toUpperCase());
    this._skillType = set.getEnum("skillType", SkillType.class);
    this._isSuicideAttack = set.getBool("isSuicideAttack", false);
    this._isSkillTimePermanent = set.getBool("isSkillTimePermanent", false);
    this._isReuseDelayPermanent = set.getBool("isReuseDelayPermanent", false);
    this._deathlink = set.getBool("deathlink", false);
    this._basedOnTargetDebuff = set.getBool("basedOnTargetDebuff", false);
    this._isNotUsedByAI = set.getBool("isNotUsedByAI", false);
    this._isIgnoreResists = set.getBool("isIgnoreResists", false);
    this._isIgnoreInvul = set.getBool("isIgnoreInvul", false);
    this._isSharedClassReuse = set.getBool("isSharedClassReuse", false);
    this._isTrigger = set.getBool("isTrigger", false);
    this._isNotAffectedByMute = set.getBool("isNotAffectedByMute", false);
    this._flyingTransformUsage = set.getBool("flyingTransformUsage", false);
    this._canUseTeleport = set.getBool("canUseTeleport", true);
    if (NumberUtils.isNumber(set.getString("element", "NONE"))) {
      this._element = Element.getElementById(set.getInteger("element", -1));
    } else {
      this._element = Element.getElementByName(set.getString("element", "none").toUpperCase());
    }

    this._elementPower = set.getInteger("elementPower", 0);
    if (this._element != Element.NONE && this._elementPower == 0) {
      this._elementPower = 20;
    }

    this._activateRate = set.getInteger("activateRate", -1);
    this._levelModifier = set.getInteger("levelModifier", 1);
    this._isCancelable = set.getBool("cancelable", true);
    this._isReflectable = set.getBool("reflectable", true);
    this._isShieldignore = set.getBool("shieldignore", false);
    this._criticalRate = set.getInteger("criticalRate", 0);
    this._isOverhit = set.getBool("overHit", false);
    this._weaponsAllowed = set.getInteger("weaponsAllowed", 0);
    this._minPledgeClass = set.getInteger("minPledgeClass", 0);
    this._minRank = set.getInteger("minRank", 0);
    this._isOffensive = set.getBool("isOffensive", this._skillType.isOffensive());
    this._isPvpSkill = set.getBool("isPvpSkill", this._skillType.isPvpSkill());
    this._isFishingSkill = set.getBool("isFishingSkill", false);
    this._isPvm = set.getBool("isPvm", this._skillType.isPvM());
    this._isForceUse = set.getBool("isForceUse", false);
    this._isBehind = set.getBool("behind", false);
    this._symbolId = set.getInteger("symbolId", 0);
    this._npcId = set.getInteger("npcId", 0);
    this._flyType = FlyType.valueOf(set.getString("flyType", "NONE").toUpperCase());
    this._flyToBack = set.getBool("flyToBack", false);
    this._flyRadius = set.getInteger("flyRadius", 200);
    this._negateSkill = set.getInteger("negateSkill", 0);
    this._negatePower = set.getInteger("negatePower", 2147483647);
    this._numCharges = set.getInteger("num_charges", 0);
    this._delayedEffect = set.getInteger("delayedEffect", 0);
    this._cancelTarget = set.getInteger("cancelTarget", 0);
    this._skillInterrupt = set.getBool("skillInterrupt", false);
    this._lethal1 = set.getDouble("lethal1", 0.0D);
    this._lethal2 = set.getDouble("lethal2", 0.0D);
    this._absorbPart = set.getDouble("absorbPart", 0.0D);
    this._icon = set.getString("icon", "");
    this._secondSkill = set.getInteger("secondSkill", 0);
    this._isSlotNone = set.getBool("isIgnorBuffLimit", false);

    int level;
    StringTokenizer st;
    for (st = new StringTokenizer(set.getString("addSkills", ""), ";"); st.hasMoreTokens(); this._addedSkills = ArrayUtils.add(this._addedSkills, new AddedSkill(id, level))) {
      id = Integer.parseInt(st.nextToken());
      level = Integer.parseInt(st.nextToken());
      if (level == -1) {
        level = this._level;
      }
    }

    if (this._skillNextAction == Skill.SkillNextAction.DEFAULT) {
      switch (this._skillType) {
        case SOWING:
        case LETHAL_SHOT:
        case PDAM:
        case CPDAM:
        case SPOIL:
        case STUN:
          this._skillNextAction = Skill.SkillNextAction.ATTACK;
          break;
        default:
          this._skillNextAction = Skill.SkillNextAction.NONE;
      }
    }

    String canLearn = set.getString("canLearn", null);
    String teachers;
    if (canLearn == null) {
      this._canLearn = null;
    } else {
      this._canLearn = new ArrayList<>();
      st = new StringTokenizer(canLearn, " \r\n\t,;");

      while (st.hasMoreTokens()) {
        teachers = st.nextToken();
        this._canLearn.add(ClassId.valueOf(teachers));
      }
    }

    teachers = set.getString("teachers", null);
    if (teachers == null) {
      this._teachers = null;
    } else {
      this._teachers = new ArrayList<>();
      st = new StringTokenizer(teachers, " \r\n\t,;");

      while (st.hasMoreTokens()) {
        String npcid = st.nextToken();
        this._teachers.add(Integer.parseInt(npcid));
      }
    }

    this.hashCode = this._id * 1023 + this._level;
  }

  public final boolean getWeaponDependancy(Creature activeChar) {
    if (this._weaponsAllowed == 0) {
      return true;
    } else if (activeChar.getActiveWeaponInstance() != null && activeChar.getActiveWeaponItem() != null && (activeChar.getActiveWeaponItem().getItemType().mask() & (long) this._weaponsAllowed) != 0L) {
      return true;
    } else if (activeChar.getSecondaryWeaponInstance() != null && activeChar.getSecondaryWeaponItem() != null && (activeChar.getSecondaryWeaponItem().getItemType().mask() & (long) this._weaponsAllowed) != 0L) {
      return true;
    } else {
      activeChar.sendPacket((new SystemMessage(113)).addSkillName(this._displayId, this._displayLevel));
      return false;
    }
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    Player player = activeChar.getPlayer();
    if (activeChar.isDead()) {
      return false;
    } else if (target != null && activeChar.getReflection() != target.getReflection()) {
      activeChar.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
      return false;
    } else if (!this.getWeaponDependancy(activeChar)) {
      return false;
    } else if (activeChar.isUnActiveSkill(this._id)) {
      return false;
    } else if (first && activeChar.isSkillDisabled(this)) {
      activeChar.sendReuseMessage(this);
      return false;
    } else {
      if (first) {
        double mpConsume2 = this._mpConsume2;
        if (this.isMusic()) {
          mpConsume2 += (double) activeChar.getEffectList().getActiveMusicCount(this.getId()) * mpConsume2 / 2.0D;
          mpConsume2 = activeChar.calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, target, this);
        } else if (this.isMagic()) {
          mpConsume2 = activeChar.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, this);
        } else {
          mpConsume2 = activeChar.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, this);
        }

        if (activeChar.getCurrentMp() < this._mpConsume1 + mpConsume2) {
          activeChar.sendPacket(Msg.NOT_ENOUGH_MP);
          return false;
        }
      }

      if (activeChar.getCurrentHp() < (double) (this._hpConsume + 1)) {
        activeChar.sendPacket(Msg.NOT_ENOUGH_HP);
        return false;
      } else if (!this._isItemHandler && !this._isAltUse && activeChar.isMuted(this)) {
        return false;
      } else if (this._soulsConsume > activeChar.getConsumedSouls()) {
        activeChar.sendPacket(Msg.THERE_IS_NOT_ENOUGHT_SOUL);
        return false;
      } else {
        if (player != null) {
          if (player.isInFlyingTransform() && this._isItemHandler && !this.flyingTransformUsage()) {
            player.sendPacket((new SystemMessage(113)).addItemName(this.getItemConsumeId()[0]));
            return false;
          }

          if (player.isInBoat() && player.getBoat().isVehicle() && !(this instanceof FishingSkill) && !(this instanceof ReelingPumping)) {
            return false;
          }

          if (player.isInObserverMode()) {
            activeChar.sendPacket(Msg.OBSERVERS_CANNOT_PARTICIPATE);
            return false;
          }

          if (first && this._itemConsume[0] > 0) {
            for (int i = 0; i < this._itemConsume.length; ++i) {
              Object inv;
              if (activeChar instanceof Summon) {
                inv = player.getInventory();
              } else {
                inv = ((Playable) activeChar).getInventory();
              }

              ItemInstance requiredItems = ((Inventory) inv).getItemByItemId(this._itemConsumeId[i]);
              if (requiredItems == null || requiredItems.getCount() < (long) this._itemConsume[i]) {
                if (activeChar == player) {
                  player.sendPacket(this.isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : (new SystemMessage(113)).addSkillName(this.getDisplayId(), this.getDisplayLevel()));
                }

                return false;
              }
            }
          }

          if (player.isFishing() && !this.isFishingSkill() && !this.altUse() && !activeChar.isSummon() && !activeChar.isPet()) {
            if (activeChar == player) {
              player.sendPacket(Msg.ONLY_FISHING_SKILLS_ARE_AVAILABLE);
            }

            return false;
          }

          if (player.isOlyParticipant() && this.isOffensive() && !player.isOlyCompetitionStarted() && this.getId() != 347) {
            return false;
          }
        }

        if (this.getFlyType() == FlyType.NONE || this.getId() == 628 || this.getId() == 821 || !activeChar.isImmobilized() && !activeChar.isRooted()) {
          if (first && target != null && this.getFlyType() == FlyType.CHARGE && activeChar.isInRange(target.getLoc(), Math.min(150, this.getFlyRadius()))) {
            activeChar.getPlayer().sendPacket(Msg.THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED);
            return false;
          } else {
            SystemMsg msg = this.checkTarget(activeChar, target, target, forceUse, first);
            if (msg != null && activeChar.getPlayer() != null) {
              activeChar.getPlayer().sendPacket(msg);
              return false;
            } else if (this._preCondition.length == 0) {
              return true;
            } else {
              Env env = new Env();
              env.character = activeChar;
              env.skill = this;
              env.target = target;
              if (first) {
                Condition[] var16 = this._preCondition;
                int var10 = var16.length;

                for (Condition с : var16) {
                  if (!с.test(env)) {
                    SystemMsg cond_msg = с.getSystemMsg();
                    if (cond_msg != null) {
                      if (cond_msg.size() > 0) {
                        activeChar.sendPacket((new SystemMessage2(cond_msg)).addSkillName(this));
                      } else {
                        activeChar.sendPacket(cond_msg);
                      }
                    }

                    return false;
                  }
                }
              }

              return true;
            }
          }
        } else {
          activeChar.getPlayer().sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
          return false;
        }
      }
    }
  }

  public int getSecondSkill() {
    return this._secondSkill;
  }

  public SystemMsg checkTarget(Creature activeChar, Creature target, Creature aimingTarget, boolean forceUse, boolean first) {
    if ((target != activeChar || !this.isNotTargetAoE()) && (target != activeChar.getPet() || this._targetType != Skill.SkillTargetType.TARGET_PET_AURA)) {
      if (target == null || this.isOffensive() && target == activeChar) {
        return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
      } else if (activeChar.getReflection() != target.getReflection()) {
        return SystemMsg.CANNOT_SEE_TARGET;
      } else {
        if (target != activeChar && target == aimingTarget && this.getCastRange() > 0 && this.getCastRange() < 32767) {
          if (!GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying())) {
            return SystemMsg.CANNOT_SEE_TARGET;
          }

          if (!first) {
            int minRange = (int) ((double) Math.max(0, this.getEffectiveRange()) + activeChar.getMinDistance(target) + 16.0D);
            if (!activeChar.isInRange(target.getLoc(), minRange)) {
              return SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED;
            }
          }
        }

        if (this._skillType == Skill.SkillType.TAKECASTLE) {
          return null;
        } else {
          if (!first && target != activeChar && (this._targetType == Skill.SkillTargetType.TARGET_MULTIFACE || this._targetType == Skill.SkillTargetType.TARGET_MULTIFACE_AURA || this._targetType == Skill.SkillTargetType.TARGET_TUNNEL)) {
            if (this._isBehind) {
              if (PositionUtils.isFacing(activeChar, target, 120)) {
                return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
              }
            } else if (!PositionUtils.isFacing(activeChar, target, 60)) {
              return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
            }
          }

          if (target.isDead() != this._isCorpse && this._targetType != Skill.SkillTargetType.TARGET_AREA_AIM_CORPSE || this._isUndeadOnly && !target.isUndead()) {
            return SystemMsg.INVALID_TARGET;
          } else if (!this._isAltUse && this._targetType != Skill.SkillTargetType.TARGET_FEEDABLE_BEAST && this._targetType != Skill.SkillTargetType.TARGET_UNLOCKABLE && this._targetType != Skill.SkillTargetType.TARGET_CHEST) {
            Player player = activeChar.getPlayer();
            if (player != null) {
              Player pcTarget = target.getPlayer();
              if (pcTarget != null) {
                if (this.isPvM()) {
                  return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }

                if (player.isInZone(ZoneType.epic) != pcTarget.isInZone(ZoneType.epic)) {
                  return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }

                if ((player.isOlyParticipant() || !pcTarget.isOlyParticipant()) && (!player.isOlyParticipant() || pcTarget.isOlyParticipant()) && (!player.isOlyParticipant() || !pcTarget.isOlyParticipant() || player.getOlyParticipant().getCompetition() == pcTarget.getOlyParticipant().getCompetition())) {
                  if (this.isOffensive()) {
                    if (player.isOlyParticipant() && pcTarget.isOlyParticipant() && player.getOlyParticipant().getCompetition() != pcTarget.getOlyParticipant().getCompetition()) {
                      return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                    }

                    if (player.isOlyParticipant() && !player.isOlyCompetitionStarted()) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    if (player.isOlyParticipant() && player.getOlyParticipant() == pcTarget.getOlyParticipant()) {
                      return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                    }

                    if (pcTarget.isOlyParticipant() && pcTarget.isLooseOlyCompetition()) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    if (player.getTeam() != TeamType.NONE && player.getTeam() == pcTarget.getTeam()) {
                      return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                    }

                    if (this.isAoE() && this.getCastRange() < 32767 && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying())) {
                      return SystemMsg.CANNOT_SEE_TARGET;
                    }

                    if (activeChar.isInZoneBattle() != target.isInZoneBattle() && !player.getPlayerAccess().PeaceAttack) {
                      return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    }

                    if ((activeChar.isInZonePeace() || target.isInZonePeace()) && !player.getPlayerAccess().PeaceAttack) {
                      return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    }

                    if (this.isAoE() && player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    if (activeChar.isInZoneBattle()) {
                      if (!forceUse && !this.isForceUse() && player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                        return SystemMsg.INVALID_TARGET;
                      }

                      return null;
                    }

                    SystemMsg msg = null;
                    Iterator var9 = player.getEvents().iterator();

                    GlobalEvent e;
                    while (var9.hasNext()) {
                      e = (GlobalEvent) var9.next();
                      if ((msg = e.checkForAttack(target, activeChar, this, forceUse)) != null) {
                        return msg;
                      }
                    }

                    var9 = player.getEvents().iterator();

                    while (var9.hasNext()) {
                      e = (GlobalEvent) var9.next();
                      if (e.canAttack(target, activeChar, this, forceUse)) {
                        return null;
                      }
                    }

                    if (this.isProvoke()) {
                      if (!forceUse && player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                        return SystemMsg.INVALID_TARGET;
                      }

                      return null;
                    }

                    if (this.isPvpSkill() || !forceUse || this.isAoE()) {
                      if (player == pcTarget) {
                        return SystemMsg.INVALID_TARGET;
                      }

                      if (player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                        return SystemMsg.INVALID_TARGET;
                      }

                      if (player.getClan() != null && player.getClan() == pcTarget.getClan()) {
                        return SystemMsg.INVALID_TARGET;
                      }

                      if (Config.ALLY_ALLOW_BUFF_DEBUFFS && player.getAlliance() != null && player.getAlliance() == pcTarget.getAlliance()) {
                        return SystemMsg.INVALID_TARGET;
                      }
                    }

                    if (activeChar.isInZone(ZoneType.SIEGE) && target.isInZone(ZoneType.SIEGE)) {
                      return null;
                    }

                    if (activeChar.isInZone(ZoneType.fun) && target.isInZone(ZoneType.fun)) {
                      return null;
                    }

                    if (player.atMutualWarWith(pcTarget)) {
                      return null;
                    }

                    if (this.isForceUse()) {
                      return null;
                    }

                    if (pcTarget.getPvpFlag() != 0) {
                      return null;
                    }

                    if (pcTarget.getKarma() > 0) {
                      return null;
                    }

                    if (!forceUse || this.isPvpSkill() || this.isAoE() && aimingTarget != target) {
                      if (player.isCursedWeaponEquipped()) {
                        return null;
                      }

                      return SystemMsg.INVALID_TARGET;
                    }

                    return null;
                  }

                  if (pcTarget == player) {
                    return null;
                  }

                  if (player.isOlyParticipant() && player.getOlyParticipant().getCompetition() == pcTarget.getOlyParticipant().getCompetition() && player.getOlyParticipant() != pcTarget.getOlyParticipant()) {
                    if (player.getOlyParticipant().getCompetition().getType() == CompetitionType.TEAM_CLASS_FREE) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    if (!forceUse) {
                      return SystemMsg.INVALID_TARGET;
                    }
                  }

                  if (!activeChar.isInZoneBattle() && target.isInZoneBattle()) {
                    return SystemMsg.INVALID_TARGET;
                  }

                  if (!forceUse && !this.isForceUse()) {
                    if (player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                      return null;
                    }

                    if (player.getClan() != null && player.getClan() == pcTarget.getClan()) {
                      return null;
                    }

                    if (Config.ALLY_ALLOW_BUFF_DEBUFFS && player.getAlliance() != null && player.getAlliance() == pcTarget.getAlliance()) {
                      return null;
                    }

                    if (player.atMutualWarWith(pcTarget)) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    if (pcTarget.getPvpFlag() != 0) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    if (pcTarget.getKarma() > 0) {
                      return SystemMsg.INVALID_TARGET;
                    }

                    return null;
                  }

                  return null;
                }

                return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
              }
            }

            if (this.isAoE() && this.isOffensive() && this.getCastRange() < 32767 && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying())) {
              return SystemMsg.CANNOT_SEE_TARGET;
            } else if (!forceUse && !this.isForceUse() && !this.isOffensive() && target.isAutoAttackable(activeChar)) {
              return SystemMsg.INVALID_TARGET;
            } else if (!forceUse && !this.isForceUse() && this.isOffensive() && !target.isAutoAttackable(activeChar)) {
              return SystemMsg.INVALID_TARGET;
            } else if (!target.isAttackable(activeChar)) {
              return SystemMsg.INVALID_TARGET;
            } else {
              return null;
            }
          } else {
            return null;
          }
        }
      }
    } else {
      return null;
    }
  }

  public final Creature getAimingTarget(Creature activeChar, GameObject obj) {
    Creature target = obj != null && obj.isCreature() ? (Creature) obj : null;
    Creature activeCharPlayer = null;
    switch (this._targetType) {
      case TARGET_ALLY:
      case TARGET_CLAN:
      case TARGET_PARTY:
      case TARGET_CLAN_ONLY:
      case TARGET_SELF:
        return activeChar;
      case TARGET_AURA:
      case TARGET_COMMCHANNEL:
      case TARGET_MULTIFACE_AURA:
        return activeChar;
      case TARGET_HOLY:
        return target != null && activeChar.isPlayer() && target.isArtefact() ? target : null;
      case TARGET_FLAGPOLE:
        return activeChar;
      case TARGET_UNLOCKABLE:
        return (target == null || !target.isDoor()) && !(target instanceof ChestInstance) ? null : target;
      case TARGET_CHEST:
        return target instanceof ChestInstance ? target : null;
      case TARGET_FEEDABLE_BEAST:
        return target instanceof FeedableBeastInstance ? target : null;
      case TARGET_PET:
      case TARGET_PET_AURA:
        Creature activeCharPet = activeChar.getPet();
        return activeCharPet != null && activeCharPet.isDead() == this._isCorpse ? activeCharPet : null;
      case TARGET_OWNER:
        if (!activeChar.isSummon() && !activeChar.isPet()) {
          return null;
        }

        activeCharPlayer = activeChar.getPlayer();
        return activeCharPlayer != null && activeCharPlayer.isDead() == this._isCorpse ? activeCharPlayer : null;
      case TARGET_ENEMY_PET:
        if (activeCharPlayer != null && activeCharPlayer != activeChar.getPet() && activeCharPlayer.isPet()) {
          return activeCharPlayer;
        }

        return null;
      case TARGET_ENEMY_SUMMON:
        if (activeCharPlayer != null && activeCharPlayer != activeChar.getPet() && activeCharPlayer.isSummon()) {
          return activeCharPlayer;
        }

        return null;
      case TARGET_ENEMY_SERVITOR:
        if (activeCharPlayer != null && activeCharPlayer != activeChar.getPet() && activeCharPlayer instanceof Summon) {
          return activeCharPlayer;
        }

        return null;
      case TARGET_ONE:
        return activeCharPlayer == null || activeCharPlayer.isDead() != this._isCorpse || activeCharPlayer == activeChar && this.isOffensive() || this._isUndeadOnly && !activeCharPlayer.isUndead() ? null : activeCharPlayer;
      case TARGET_OTHER:
        return activeCharPlayer == null || activeCharPlayer == activeChar || activeCharPlayer.isDead() != this._isCorpse || this._isUndeadOnly && !activeCharPlayer.isUndead() ? null : activeCharPlayer;
      case TARGET_AREA:
      case TARGET_MULTIFACE:
      case TARGET_TUNNEL:
        return activeCharPlayer == null || activeCharPlayer.isDead() != this._isCorpse || activeCharPlayer == activeChar && this.isOffensive() || this._isUndeadOnly && !activeCharPlayer.isUndead() ? null : activeCharPlayer;
      case TARGET_AREA_AIM_CORPSE:
        return activeCharPlayer != null && activeCharPlayer.isDead() ? activeCharPlayer : null;
      case TARGET_CORPSE:
        if (activeCharPlayer != null && activeCharPlayer.isDead()) {
          if (activeCharPlayer.isSummon() && activeCharPlayer != activeChar.getPet()) {
            return activeCharPlayer;
          }

          return activeCharPlayer.isNpc() ? activeCharPlayer : null;
        }

        return null;
      case TARGET_CORPSE_PLAYER:
        return activeCharPlayer != null && activeCharPlayer.isPlayable() && activeCharPlayer.isDead() ? activeCharPlayer : null;
      case TARGET_SIEGE:
        return activeCharPlayer != null && !activeCharPlayer.isDead() && activeCharPlayer.isDoor() ? activeCharPlayer : null;
      default:
        activeChar.sendMessage("Target type of skill is not currently handled");
        return null;
    }
  }

  public List<Creature> getTargets(Creature activeChar, Creature aimingTarget, boolean forceUse) {
    LazyArrayList targets;
    if (this.oneTarget()) {
      targets = new LazyArrayList(1);
      targets.add(aimingTarget);
      return targets;
    } else {
      targets = new LazyArrayList();
      Iterator var5;
      switch (this._targetType) {
        case TARGET_ALLY:
        case TARGET_CLAN:
        case TARGET_PARTY:
        case TARGET_CLAN_ONLY:
          if (!activeChar.isMonster() && !activeChar.isSiegeGuard()) {
            Player player = activeChar.getPlayer();
            if (player != null) {
              Iterator var11 = World.getAroundPlayers(player, this._skillRadius, 600).iterator();

              while (true) {
                Player target;
                boolean check;
                do {
                  do {
                    if (!var11.hasNext()) {
                      this.addTargetAndPetToList(targets, player, player);
                      return targets;
                    }

                    target = (Player) var11.next();
                    check = false;
                    switch (this._targetType) {
                      case TARGET_ALLY:
                        check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getAllyId() != 0 && target.getAllyId() == player.getAllyId();
                        break;
                      case TARGET_CLAN:
                        check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getParty() != null && target.getParty() == player.getParty();
                        break;
                      case TARGET_PARTY:
                        check = player.getParty() != null && player.getParty() == target.getParty();
                        break;
                      case TARGET_CLAN_ONLY:
                        check = player.getClanId() != 0 && target.getClanId() == player.getClanId();
                    }
                  } while (!check);
                } while (player.isOlyParticipant() && target.isOlyParticipant() && player.getOlyParticipant() != target.getOlyParticipant());

                if (this.checkTarget(player, target, aimingTarget, forceUse, false) == null) {
                  this.addTargetAndPetToList(targets, player, target);
                }
              }
            }
          } else {
            targets.add(activeChar);
            var5 = World.getAroundCharacters(activeChar, this._skillRadius, 600).iterator();

            while (true) {
              Creature c;
              do {
                do {
                  if (!var5.hasNext()) {
                    return targets;
                  }

                  c = (Creature) var5.next();
                } while (c.isDead());
              } while (!c.isMonster() && !c.isSiegeGuard());

              targets.add(c);
            }
          }
        case TARGET_SELF:
        case TARGET_HOLY:
        case TARGET_FLAGPOLE:
        case TARGET_UNLOCKABLE:
        case TARGET_CHEST:
        case TARGET_FEEDABLE_BEAST:
        case TARGET_PET:
        case TARGET_OWNER:
        case TARGET_ENEMY_PET:
        case TARGET_ENEMY_SUMMON:
        case TARGET_ENEMY_SERVITOR:
        case TARGET_ONE:
        case TARGET_OTHER:
        default:
          break;
        case TARGET_AURA:
        case TARGET_MULTIFACE_AURA:
          this.addTargetsToList(targets, activeChar, activeChar, forceUse);
          break;
        case TARGET_COMMCHANNEL:
          if (activeChar.getPlayer() != null) {
            if (activeChar.getPlayer().isInParty()) {
              Player p;
              if (activeChar.getPlayer().getParty().isInCommandChannel()) {
                var5 = activeChar.getPlayer().getParty().getCommandChannel().iterator();

                while (var5.hasNext()) {
                  p = (Player) var5.next();
                  if (!p.isDead() && p.isInRange(activeChar, this._skillRadius == 0 ? 600L : (long) this._skillRadius)) {
                    targets.add(p);
                  }
                }

                this.addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
              } else {
                var5 = activeChar.getPlayer().getParty().getPartyMembers().iterator();

                while (var5.hasNext()) {
                  p = (Player) var5.next();
                  if (!p.isDead() && p.isInRange(activeChar, this._skillRadius == 0 ? 600L : (long) this._skillRadius)) {
                    targets.add(p);
                  }
                }

                this.addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
              }
            } else {
              targets.add(activeChar);
              this.addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
            }
          }
          break;
        case TARGET_PET_AURA:
          if (activeChar.getPet() != null) {
            this.addTargetsToList(targets, activeChar.getPet(), activeChar, forceUse);
          }
          break;
        case TARGET_AREA:
        case TARGET_MULTIFACE:
        case TARGET_TUNNEL:
        case TARGET_AREA_AIM_CORPSE:
          if (aimingTarget.isDead() == this._isCorpse && (!this._isUndeadOnly || aimingTarget.isUndead())) {
            targets.add(aimingTarget);
          }

          this.addTargetsToList(targets, aimingTarget, activeChar, forceUse);
      }

      return targets;
    }
  }

  private void addTargetAndPetToList(List<Creature> targets, Player actor, Player target) {
    if ((actor == target || actor.isInRange(target, this._skillRadius)) && target.isDead() == this._isCorpse) {
      targets.add(target);
    }

    Summon pet = target.getPet();
    if (pet != null && actor.isInRange(pet, this._skillRadius) && pet.isDead() == this._isCorpse) {
      targets.add(pet);
    }

  }

  private void addTargetsToList(List<Creature> targets, Creature aimingTarget, Creature activeChar, boolean forceUse) {
    int count = 0;
    Polygon terr = null;
    if (this._targetType == Skill.SkillTargetType.TARGET_TUNNEL) {
      int radius = 100;
      int zmin1 = activeChar.getZ() - 200;
      int zmax1 = activeChar.getZ() + 200;
      int zmin2 = aimingTarget.getZ() - 200;
      int zmax2 = aimingTarget.getZ() + 200;
      double angle = PositionUtils.convertHeadingToDegree(activeChar.getHeading());
      double radian1 = Math.toRadians(angle - 90.0D);
      double radian2 = Math.toRadians(angle + 90.0D);
      terr = new Polygon();
      terr.add(activeChar.getX() + (int) (Math.cos(radian1) * (double) radius), activeChar.getY() + (int) (Math.sin(radian1) * (double) radius));
      terr.add(activeChar.getX() + (int) (Math.cos(radian2) * (double) radius), activeChar.getY() + (int) (Math.sin(radian2) * (double) radius));
      terr.add(aimingTarget.getX() + (int) (Math.cos(radian2) * (double) radius), aimingTarget.getY() + (int) (Math.sin(radian2) * (double) radius));
      terr.add(aimingTarget.getX() + (int) (Math.cos(radian1) * (double) radius), aimingTarget.getY() + (int) (Math.sin(radian1) * (double) radius));
      terr.setZmin(Math.min(zmin1, zmin2)).setZmax(Math.max(zmax1, zmax2));
    }

    Iterator var18 = aimingTarget.getAroundCharacters(this._skillRadius, 300).iterator();

    while (var18.hasNext()) {
      Creature target = (Creature) var18.next();
      if ((terr == null || terr.isInside(target.getX(), target.getY(), target.getZ())) && target != null && activeChar != target && (activeChar.getPlayer() == null || activeChar.getPlayer() != target.getPlayer()) && this.checkTarget(activeChar, target, aimingTarget, forceUse, false) == null && (!activeChar.isNpc() || !target.isNpc())) {
        targets.add(target);
        ++count;
        if (this.isOffensive() && count >= 20 && !activeChar.isRaid()) {
          break;
        }
      }
    }

  }

  public final void getEffects(Creature effector, Creature effected, boolean calcChance, boolean applyOnCaster) {
    this.getEffects(effector, effected, calcChance, applyOnCaster, false);
  }

  public final void getEffects(Creature effector, Creature effected, boolean calcChance, boolean applyOnCaster, boolean skillReflected) {
    double timeMult = 1.0D;
    if (this.isMusic()) {
      timeMult = Config.SONGDANCETIME_MODIFIER;
    } else if (this.getId() >= 4342 && this.getId() <= 4360) {
      timeMult = Config.CLANHALL_BUFFTIME_MODIFIER;
    }

    this.getEffects(effector, effected, calcChance, applyOnCaster, 0L, timeMult, skillReflected);
  }

  public final void getEffects(final Creature effector, final Creature effected, final boolean calcChance, final boolean applyOnCaster, final long timeConst, final double timeMult, final boolean skillReflected) {
    if (!this.isPassive() && this.hasEffects() && effector != null && effected != null) {
      boolean isChg = false;
      if (this.getId() == 345 || this.getId() == 346 || this.getId() == 321 || this.getId() == 369 || this.getId() == 1231) {
        isChg = effected == effector;
      }

      if (!isChg && (effected.isEffectImmune() || effected.isInvul() && this.isOffensive())) {
        if (effector.isPlayer()) {
          effector.sendPacket((new SystemMessage(139)).addName(effected).addSkillName(this._displayId, this._displayLevel));
        }

      } else if (!effected.isDoor() && (!effected.isAlikeDead() || this.isPreservedOnDeath())) {
        ThreadPoolManager.getInstance().execute(new RunnableImpl() {
          public void runImpl() {
            boolean success = false;
            boolean skillMastery = false;
            int sps = effector.getChargedSpiritShot();
            if (effector.getSkillMastery(Skill.this.getId()) == 2) {
              skillMastery = true;
              effector.removeSkillMastery(Skill.this.getId());
            }

            EffectTemplate[] var4 = Skill.this.getEffectTemplates();
            int var5 = var4.length;

            label250:
            for (int var6 = 0; var6 < var5; ++var6) {
              EffectTemplate et = var4[var6];
              if (applyOnCaster == et._applyOnCaster && et._count != 0) {
                Creature character = !et._applyOnCaster && (!et._isReflectable || !skillReflected) ? effected : effector;
                List<Creature> targets = new LazyArrayList(1);
                targets.add(character);
                if (et._applyOnSummon && character.isPlayer()) {
                  Summon summon = character.getPlayer().getPet();
                  if (summon != null && summon.isSummon() && !Skill.this.isOffensive() && !Skill.this.isToggle() && !Skill.this.isCubicSkill()) {
                    targets.add(summon);
                  }
                }

                Iterator var23 = targets.iterator();

                while (true) {
                  label235:
                  while (true) {
                    Creature target;
                    do {
                      do {
                        if (!var23.hasNext()) {
                          continue label250;
                        }

                        target = (Creature) var23.next();
                      } while (target.isAlikeDead() && !Skill.this.isPreservedOnDeath());
                    } while (target.isRaid() && et.getEffectType().isRaidImmune());

                    if (et.getPeriod() > 0L && (effected.isBuffImmune() && !Skill.this.isOffensive() && effector != effected && !Config.BLOCK_BUFF_EXCLUDE.contains(Skill.this.getId()) || effected.isDebuffImmune() && Skill.this.isOffensive())) {
                      if (effector.isPlayer()) {
                        effector.sendPacket((new SystemMessage(139)).addName(effected).addSkillName(Skill.this._displayId, Skill.this._displayLevel));
                      }
                    } else if (!Skill.this.isBlockedByChar(target, et)) {
                      if (et._stackOrder == -1) {
                        if (!et._stackType.equals("none")) {
                          Iterator var12 = target.getEffectList().getAllEffects().iterator();

                          while (var12.hasNext()) {
                            Effect e = (Effect) var12.next();
                            if (e.getStackType().equalsIgnoreCase(et._stackType)) {
                              continue label235;
                            }
                          }
                        } else if (target.getEffectList().getEffectsBySkillId(Skill.this.getId()) != null) {
                          continue;
                        }
                      }

                      Env env = new Env(effector, target, Skill.this);
                      int chance = et.chance(Skill.this.getActivateRate());
                      if ((calcChance || chance >= 0) && !et._applyOnCaster) {
                        env.value = chance;
                        if (!Formulas.calcSkillSuccess(env, et, sps)) {
                          continue;
                        }
                      }

                      if (Skill.this._isReflectable && et._isReflectable && Skill.this.isOffensive() && target != effector && !effector.isTrap() && Rnd.chance(target.calcStat(Skill.this.isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0.0D, effector, Skill.this))) {
                        target.sendPacket((new SystemMessage(1998)).addName(effector));
                        effector.sendPacket((new SystemMessage(1999)).addName(target));
                        target = effector;
                        env.target = target;
                      }

                      if (success) {
                        env.value = 2.147483647E9D;
                      }

                      Effect ex = et.getEffect(env);
                      if (ex != null) {
                        if (chance > 0) {
                          success = true;
                        }

                        if (ex.isOneTime()) {
                          if (ex.checkCondition()) {
                            ex.onStart();
                            ex.onActionTime();
                            ex.onExit();
                          }
                        } else {
                          int count = et.getCount();
                          long period = et.getPeriod();
                          if (skillMastery) {
                            if (count > 1) {
                              count *= 2;
                            } else {
                              period *= 2L;
                            }
                          }

                          if (Config.CALC_EFFECT_TIME_YIELD_AND_RESIST && !et._applyOnCaster && Skill.this.isOffensive() && !Skill.this.isIgnoreResists() && !effector.isRaid()) {
                            double res = 0.0D;
                            Pair<Stats, Stats> resistAndPowerType = et.getEffectType().getResistAndPowerType();
                            if (resistAndPowerType != null) {
                              Stats resistType = resistAndPowerType.getLeft();
                              Stats powerType = resistAndPowerType.getRight();
                              if (resistType != null) {
                                res += effected.calcStat(resistType, effector, Skill.this);
                              }

                              if (powerType != null) {
                                res -= effector.calcStat(powerType, effected, Skill.this);
                              }
                            }

                            res += effected.calcStat(Stats.DEBUFF_RESIST, effector, Skill.this);
                            if (res != 0.0D) {
                              double mod = 1.0D + Math.abs(0.01D * res);
                              if (res > 0.0D) {
                                mod = 1.0D / mod;
                              }

                              if (count > 1) {
                                count = (int) Math.floor(Math.max((double) count * mod, 1.0D));
                              } else {
                                period = (long) Math.floor(Math.max((double) period * mod, 1.0D));
                              }
                            }
                          }

                          if (timeConst > 0L) {
                            if (count > 1) {
                              period = timeConst / (long) count;
                            } else {
                              period = timeConst;
                            }
                          } else if (timeMult > 1.0D) {
                            if (count > 1) {
                              count = (int) ((double) count * timeMult);
                            } else {
                              period = (long) ((double) period * timeMult);
                            }
                          }

                          Skill s = ex.getSkill();
                          if (s != null && Config.SKILL_DURATION_MOD.containsKey(s.getId())) {
                            int mtime = Config.SKILL_DURATION_MOD.get(s.getId());
                            if (s.getLevel() >= 100 && s.getLevel() < 140) {
                              if (count > 1) {
                                count = mtime;
                              } else {
                                period = mtime;
                              }
                            } else if (count > 1) {
                              count = mtime;
                            } else {
                              period = mtime;
                            }
                          }

                          ex.setCount(count);
                          ex.setPeriod(period);
                          ex.schedule();
                        }
                      }
                    }
                  }
                }
              }
            }

            if (calcChance) {
              if (success) {
                effector.sendPacket((new SystemMessage(1595)).addSkillName(Skill.this._displayId, Skill.this._displayLevel));
              } else {
                effector.sendPacket((new SystemMessage(1597)).addSkillName(Skill.this._displayId, Skill.this._displayLevel));
              }
            }

          }
        });
      }
    }
  }

  public final void attach(EffectTemplate effect) {
    this._effectTemplates = ArrayUtils.add(this._effectTemplates, effect);
  }

  public EffectTemplate[] getEffectTemplates() {
    return this._effectTemplates;
  }

  public boolean hasEffects() {
    return this._effectTemplates.length > 0;
  }

  public final Func[] getStatFuncs() {
    return this.getStatFuncs(this);
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      return this.hashCode() == obj.hashCode();
    }
  }

  public int hashCode() {
    return this.hashCode;
  }

  public final void attach(Condition c) {
    this._preCondition = ArrayUtils.add(this._preCondition, c);
  }

  public final boolean altUse() {
    return this._isAltUse;
  }

  public final boolean canTeachBy(int npcId) {
    return this._teachers == null || this._teachers.contains(npcId);
  }

  public final int getActivateRate() {
    return this._activateRate;
  }

  public Skill.AddedSkill[] getAddedSkills() {
    return this._addedSkills;
  }

  public final boolean getCanLearn(ClassId cls) {
    return this._canLearn == null || this._canLearn.contains(cls);
  }

  public final int getCastRange() {
    return this._castRange;
  }

  public int getEffectiveRange() {
    return this._effectiveRange;
  }

  public final int getAOECastRange() {
    return Math.max(this._castRange, this._skillRadius);
  }

  public final int getCoolTime() {
    return this._coolTime;
  }

  public boolean getCorpse() {
    return this._isCorpse;
  }

  public int getDelayedEffect() {
    return this._delayedEffect;
  }

  public final int getDisplayId() {
    return this._displayId;
  }

  public int getDisplayLevel() {
    return this._displayLevel;
  }

  public int getEffectPoint() {
    return this._effectPoint;
  }

  public Effect getSameByStackType(List<Effect> list) {
    EffectTemplate[] var3 = this.getEffectTemplates();
    int var4 = var3.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      EffectTemplate et = var3[var5];
      Effect ret;
      if (et != null && (ret = et.getSameByStackType(list)) != null) {
        return ret;
      }
    }

    return null;
  }

  public Effect getSameByStackType(EffectList list) {
    return this.getSameByStackType(list.getAllEffects());
  }

  public Effect getSameByStackType(Creature actor) {
    return this.getSameByStackType(actor.getEffectList().getAllEffects());
  }

  public final Element getElement() {
    return this._element;
  }

  public final int getElementPower() {
    return this._elementPower;
  }

  public Skill getFirstAddedSkill() {
    return this._addedSkills.length == 0 ? null : this._addedSkills[0].getSkill();
  }

  public int getFlyRadius() {
    return this._flyRadius;
  }

  public FlyType getFlyType() {
    return this._flyType;
  }

  public boolean isFlyToBack() {
    return this._flyToBack;
  }

  public final int getHitTime() {
    return this._hitTime;
  }

  public final int getHpConsume() {
    return this._hpConsume;
  }

  public int getId() {
    return this._id;
  }

  public void setId(int id) {
    this._id = id;
  }

  public final int[] getItemConsume() {
    return this._itemConsume;
  }

  public final int[] getItemConsumeId() {
    return this._itemConsumeId;
  }

  public final int getReferenceItemId() {
    return this._referenceItemId;
  }

  public final int getReferenceItemMpConsume() {
    return this._referenceItemMpConsume;
  }

  public final int getLevel() {
    return this._level;
  }

  public final int getBaseLevel() {
    return this._baseLevel;
  }

  public final void setBaseLevel(int baseLevel) {
    this._baseLevel = baseLevel;
  }

  public final int getLevelModifier() {
    return this._levelModifier;
  }

  public final int getMagicLevel() {
    return this._magicLevel;
  }

  public int getMatak() {
    return this._matak;
  }

  public int getMinPledgeClass() {
    return this._minPledgeClass;
  }

  public int getMinRank() {
    return this._minRank;
  }

  public final double getMpConsume() {
    return this._mpConsume1 + this._mpConsume2;
  }

  public final double getMpConsume1() {
    return this._mpConsume1;
  }

  public final double getMpConsume2() {
    return this._mpConsume2;
  }

  public final String getName() {
    return this._name;
  }

  public int getNegatePower() {
    return this._negatePower;
  }

  public int getNegateSkill() {
    return this._negateSkill;
  }

  public Skill.SkillNextAction getSkillNextAction() {
    return this._skillNextAction;
  }

  public int getNpcId() {
    return this._npcId;
  }

  public int getNumCharges() {
    return this._numCharges;
  }

  public final double getPower(Creature target) {
    if (target != null) {
      if (target.isPlayable()) {
        return this.getPowerPvP();
      }

      if (target.isMonster()) {
        return this.getPowerPvE();
      }
    }

    return this.getPower();
  }

  public final double getPower() {
    return this._power;
  }

  public final double getPowerPvP() {
    return this._powerPvP != 0.0D ? this._powerPvP : this._power;
  }

  public final double getPowerPvE() {
    return this._powerPvE != 0.0D ? this._powerPvE : this._power;
  }

  public final long getReuseDelay() {
    return this._reuseDelay;
  }

  public final void setReuseDelay(long newReuseDelay) {
    this._reuseDelay = newReuseDelay;
  }

  public final boolean getShieldIgnore() {
    return this._isShieldignore;
  }

  public final boolean isReflectable() {
    return this._isReflectable;
  }

  public final int getSkillInterruptTime() {
    return this._skillInterruptTime;
  }

  public final int getSkillRadius() {
    return this._skillRadius;
  }

  public final Skill.SkillType getSkillType() {
    return this._skillType;
  }

  public int getSoulsConsume() {
    return this._soulsConsume;
  }

  public int getSymbolId() {
    return this._symbolId;
  }

  public final Skill.SkillTargetType getTargetType() {
    return this._targetType;
  }

  public final SkillTrait getTraitType() {
    return this._traitType;
  }

  public final BaseStats getSaveVs() {
    return this._saveVs;
  }

  public final int getWeaponsAllowed() {
    return this._weaponsAllowed;
  }

  public double getLethal1() {
    return this._lethal1;
  }

  public double getLethal2() {
    return this._lethal2;
  }

  public String getBaseValues() {
    return this._baseValues;
  }

  public boolean isBlockedByChar(Creature effected, EffectTemplate et) {
    if (et.getAttachedFuncs() == null) {
      return false;
    } else {
      FuncTemplate[] var3 = et.getAttachedFuncs();
      int var4 = var3.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        FuncTemplate func = var3[var5];
        if (func != null && effected.checkBlockedStat(func._stat)) {
          return true;
        }
      }

      return false;
    }
  }

  public final boolean isCancelable() {
    return this._isCancelable && this.getSkillType() != Skill.SkillType.TRANSFORMATION && !this.isToggle();
  }

  public final boolean isCommon() {
    return this._isCommon;
  }

  public final int getCriticalRate() {
    return this._criticalRate;
  }

  public final boolean isHandler() {
    return this._isItemHandler;
  }

  public final boolean isMagic() {
    return this._magicType == Skill.SkillMagicType.MAGIC;
  }

  public final Skill.SkillMagicType getMagicType() {
    return this._magicType;
  }

  public final boolean isNewbie() {
    return this._isNewbie;
  }

  public final boolean isPreservedOnDeath() {
    return this._isPreservedOnDeath;
  }

  public final boolean isHeroic() {
    return this._isHeroic;
  }

  public final boolean isSelfDispellable() {
    return this._isSelfDispellable;
  }

  public void setOperateType(Skill.SkillOpType type) {
    this._operateType = type;
  }

  public final boolean isOverhit() {
    return this._isOverhit;
  }

  public final boolean isActive() {
    return this._operateType == Skill.SkillOpType.OP_ACTIVE;
  }

  public final boolean isPassive() {
    return this._operateType == Skill.SkillOpType.OP_PASSIVE;
  }

  public boolean isSaveable() {
    return (Config.ALT_SAVE_UNSAVEABLE || !this.isMusic() && !this._name.startsWith("Herb of")) && this._isSaveable;
  }

  public final boolean isSkillTimePermanent() {
    return this._isSkillTimePermanent || this._isItemHandler || this._name.contains("Talisman");
  }

  public final boolean isReuseDelayPermanent() {
    return this._isReuseDelayPermanent || this._isItemHandler;
  }

  public boolean isDeathlink() {
    return this._deathlink;
  }

  public boolean isBasedOnTargetDebuff() {
    return this._basedOnTargetDebuff;
  }

  public boolean isSoulBoost() {
    return this._isSoulBoost;
  }

  public boolean isChargeBoost() {
    return this._isChargeBoost;
  }

  public boolean isUsingWhileCasting() {
    return this._isUsingWhileCasting;
  }

  public boolean isBehind() {
    return this._isBehind;
  }

  public boolean isHideStartMessage() {
    return this._hideStartMessage;
  }

  public boolean isHideUseMessage() {
    return this._hideUseMessage;
  }

  public boolean isSSPossible() {
    return this._isUseSS == Skill.Ternary.TRUE || this._isUseSS == Skill.Ternary.DEFAULT && !this._isItemHandler && !this.isMusic() && this.isActive() && (this.getTargetType() != Skill.SkillTargetType.TARGET_SELF || this.isMagic());
  }

  public final boolean isSuicideAttack() {
    return this._isSuicideAttack;
  }

  public final boolean isToggle() {
    return this._operateType == Skill.SkillOpType.OP_TOGGLE;
  }

  public void setCastRange(int castRange) {
    this._castRange = castRange;
  }

  public void setDisplayLevel(int lvl) {
    this._displayLevel = lvl;
  }

  public void setHitTime(int hitTime) {
    this._hitTime = hitTime;
  }

  public void setHpConsume(int hpConsume) {
    this._hpConsume = hpConsume;
  }

  public void setMagicType(Skill.SkillMagicType type) {
    this._magicType = type;
  }

  public final void setMagicLevel(int newlevel) {
    this._magicLevel = newlevel;
  }

  public void setMpConsume1(double mpConsume1) {
    this._mpConsume1 = mpConsume1;
  }

  public void setMpConsume2(double mpConsume2) {
    this._mpConsume2 = mpConsume2;
  }

  public void setName(String name) {
    this._name = name;
  }

  public void setOverhit(boolean isOverhit) {
    this._isOverhit = isOverhit;
  }

  public final void setPower(double power) {
    this._power = power;
  }

  public void setSkillInterruptTime(int skillInterruptTime) {
    this._skillInterruptTime = skillInterruptTime;
  }

  public boolean isItemSkill() {
    return this._name.contains("Item Skill") || this._name.contains("Talisman");
  }

  public String toString() {
    return this._name + "[id=" + this._id + ",lvl=" + this._level + "]";
  }

  public abstract void useSkill(Creature var1, List<Creature> var2);

  public boolean isAoE() {
    switch (this._targetType) {
      case TARGET_AURA:
      case TARGET_MULTIFACE_AURA:
      case TARGET_PET_AURA:
      case TARGET_AREA:
      case TARGET_MULTIFACE:
      case TARGET_TUNNEL:
      case TARGET_AREA_AIM_CORPSE:
        return true;
      case TARGET_COMMCHANNEL:
      case TARGET_HOLY:
      case TARGET_FLAGPOLE:
      case TARGET_UNLOCKABLE:
      case TARGET_CHEST:
      case TARGET_FEEDABLE_BEAST:
      case TARGET_PET:
      case TARGET_OWNER:
      case TARGET_ENEMY_PET:
      case TARGET_ENEMY_SUMMON:
      case TARGET_ENEMY_SERVITOR:
      case TARGET_ONE:
      case TARGET_OTHER:
      default:
        return false;
    }
  }

  public boolean isNotTargetAoE() {
    switch (this._targetType) {
      case TARGET_ALLY:
      case TARGET_CLAN:
      case TARGET_PARTY:
      case TARGET_CLAN_ONLY:
      case TARGET_AURA:
      case TARGET_MULTIFACE_AURA:
        return true;
      case TARGET_SELF:
      case TARGET_COMMCHANNEL:
      default:
        return false;
    }
  }

  public boolean isOffensive() {
    return this._isOffensive;
  }

  public final boolean isForceUse() {
    return this._isForceUse;
  }

  public boolean isAI() {
    return this._skillType.isAI();
  }

  public boolean isPvM() {
    return this._isPvm;
  }

  public final boolean isPvpSkill() {
    return this._isPvpSkill;
  }

  public final boolean isFishingSkill() {
    return this._isFishingSkill;
  }

  public boolean isMusic() {
    return this._magicType == Skill.SkillMagicType.MUSIC;
  }

  public boolean isTrigger() {
    return this._isTrigger;
  }

  public boolean isSlotNone() {
    return this._isSlotNone;
  }

  public boolean oneTarget() {
    switch (this._targetType) {
      case TARGET_SELF:
      case TARGET_HOLY:
      case TARGET_FLAGPOLE:
      case TARGET_UNLOCKABLE:
      case TARGET_CHEST:
      case TARGET_FEEDABLE_BEAST:
      case TARGET_PET:
      case TARGET_OWNER:
      case TARGET_ENEMY_PET:
      case TARGET_ENEMY_SUMMON:
      case TARGET_ENEMY_SERVITOR:
      case TARGET_ONE:
      case TARGET_OTHER:
      case TARGET_CORPSE:
      case TARGET_CORPSE_PLAYER:
      case TARGET_SIEGE:
      case TARGET_ITEM:
      case TARGET_NONE:
        return true;
      case TARGET_AURA:
      case TARGET_COMMCHANNEL:
      case TARGET_MULTIFACE_AURA:
      case TARGET_PET_AURA:
      case TARGET_AREA:
      case TARGET_MULTIFACE:
      case TARGET_TUNNEL:
      case TARGET_AREA_AIM_CORPSE:
      default:
        return false;
    }
  }

  public int getCancelTarget() {
    return this._cancelTarget;
  }

  public boolean isSkillInterrupt() {
    return this._skillInterrupt;
  }

  public boolean isNotUsedByAI() {
    return this._isNotUsedByAI;
  }

  public boolean isIgnoreResists() {
    return this._isIgnoreResists;
  }

  public boolean isIgnoreInvul() {
    return this._isIgnoreInvul;
  }

  public boolean isSharedClassReuse() {
    return this._isSharedClassReuse;
  }

  public boolean isNotAffectedByMute() {
    return this._isNotAffectedByMute;
  }

  public boolean flyingTransformUsage() {
    return this._flyingTransformUsage;
  }

  public boolean canUseTeleport() {
    return this._canUseTeleport;
  }

  public int getEnchantLevelCount() {
    return this._enchantLevelCount;
  }

  public void setEnchantLevelCount(int count) {
    this._enchantLevelCount = count;
  }

  public boolean isClanSkill() {
    return this._id >= 370 && this._id <= 391 || this._id >= 611 && this._id <= 616;
  }

  public boolean isBaseTransformation() {
    return this._id >= 810 && this._id <= 813 || this._id >= 1520 && this._id <= 1522 || this._id == 538;
  }

  public boolean isSummonerTransformation() {
    return this._id >= 929 && this._id <= 931;
  }

  public double getSimpleDamage(Creature attacker, Creature target) {
    double pAtk;
    double pdef;
    double power;
    int ss;
    if (this.isMagic()) {
      pAtk = attacker.getMAtk(target, this);
      pdef = target.getMDef(null, this);
      power = this.getPower();
      ss = attacker.getChargedSpiritShot() > 0 && this.isSSPossible() ? attacker.getChargedSpiritShot() * 2 : 1;
      return 91.0D * power * Math.sqrt((double) ss * pAtk) / pdef;
    } else {
      pAtk = attacker.getPAtk(target);
      pdef = target.getPDef(attacker);
      power = this.getPower();
      ss = attacker.getChargedSoulShot() && this.isSSPossible() ? 2 : 1;
      return (double) ss * (pAtk + power) * 70.0D / pdef;
    }
  }

  public long getReuseForMonsters() {
    long min = 1000L;
    switch (this._skillType) {
      case DEBUFF:
      case PARALYZE:
      case NEGATE_STATS:
      case NEGATE_EFFECTS:
      case STEAL_BUFF:
        min = 10000L;
      case DOT:
      case MDOT:
      case POISON:
      case MANADAM:
      case DESTROY_SUMMON:
      case DRAIN:
      case DRAIN_SOUL:
      case LETHAL_SHOT:
      case MDAM:
      case PDAM:
      case CPDAM:
      case SOULSHOT:
      case SPIRITSHOT:
      case SPOIL:
      default:
        break;
      case MUTE:
      case ROOT:
      case SLEEP:
      case STUN:
        min = 5000L;
    }

    return Math.max(Math.max(this._hitTime + this._coolTime, this._reuseDelay), min);
  }

  public double getAbsorbPart() {
    return this._absorbPart;
  }

  public boolean isProvoke() {
    return this._isProvoke;
  }

  public String getIcon() {
    return this._icon;
  }

  public int getEnergyConsume() {
    return this._energyConsume;
  }

  public void setCubicSkill(boolean value) {
    this._isCubicSkill = value;
  }

  public boolean isCubicSkill() {
    return this._isCubicSkill;
  }

  public boolean isBlowSkill() {
    return false;
  }

  public enum SkillType {
    AGGRESSION(Aggression.class),
    AIEFFECTS(AIeffects.class),
    BALANCE(Balance.class),
    BEAST_FEED(BeastFeed.class),
    BLEED(Continuous.class),
    BUFF(Continuous.class),
    BUFF_CHARGER(BuffCharger.class),
    CALL(Call.class),
    CLAN_GATE(ClanGate.class),
    COMBATPOINTHEAL(CombatPointHeal.class),
    CONT(Toggle.class),
    CPDAM(CPDam.class),
    CPHOT(Continuous.class),
    CRAFT(Craft.class),
    DEATH_PENALTY(DeathPenalty.class),
    DEBUFF(Continuous.class),
    DELETE_HATE(DeleteHate.class),
    DELETE_HATE_OF_ME(DeleteHateOfMe.class),
    DESTROY_SUMMON(DestroySummon.class),
    DEFUSE_TRAP(DefuseTrap.class),
    DETECT_TRAP(DetectTrap.class),
    DISCORD(Continuous.class),
    DOT(Continuous.class),
    DRAIN(Drain.class),
    DRAIN_SOUL(DrainSoul.class),
    EFFECT(l2.gameserver.skills.skillclasses.Effect.class),
    EFFECTS_FROM_SKILLS(EffectsFromSkills.class),
    ENCHANT_ARMOR,
    ENCHANT_WEAPON,
    FEED_PET,
    FISHING(FishingSkill.class),
    HARDCODED(l2.gameserver.skills.skillclasses.Effect.class),
    HARVESTING(Harvesting.class),
    HEAL(Heal.class),
    HEAL_PERCENT(HealPercent.class),
    HOT(Continuous.class),
    LETHAL_SHOT(LethalShot.class),
    LUCK,
    MANADAM(ManaDam.class),
    MANAHEAL(ManaHeal.class),
    MANAHEAL_PERCENT(ManaHealPercent.class),
    MDAM(MDam.class),
    MDOT(Continuous.class),
    MPHOT(Continuous.class),
    MUTE(Disablers.class),
    NEGATE_EFFECTS(NegateEffects.class),
    NEGATE_STATS(NegateStats.class),
    ADD_PC_BANG(PcBangPointsAdd.class),
    NOTDONE,
    NOTUSED,
    PARALYZE(Disablers.class),
    PASSIVE,
    PDAM(PDam.class),
    PET_SUMMON(PetSummon.class),
    POISON(Continuous.class),
    PUMPING(ReelingPumping.class),
    RECALL(Recall.class),
    REELING(ReelingPumping.class),
    RESURRECT(Resurrect.class),
    RIDE(Ride.class),
    ROOT(Disablers.class),
    SHIFT_AGGRESSION(ShiftAggression.class),
    SSEED(SkillSeed.class),
    SLEEP(Disablers.class),
    SOULSHOT,
    SOWING(Sowing.class),
    SPHEAL(SPHeal.class),
    SPIRITSHOT,
    SPOIL(Spoil.class),
    STEAL_BUFF(StealBuff.class),
    STUN(Disablers.class),
    SUMMON(l2.gameserver.skills.skillclasses.Summon.class),
    SUMMON_FLAG(SummonSiegeFlag.class),
    SUMMON_ITEM(SummonItem.class),
    SWEEP(Sweep.class),
    TAKECASTLE(TakeCastle.class),
    TAMECONTROL(TameControl.class),
    TELEPORT_NPC(TeleportNpc.class),
    TRANSFORMATION(Transformation.class),
    UNLOCK(Unlock.class),
    WATCHER_GAZE(Continuous.class);

    private final Class<? extends Skill> clazz;

    SkillType() {
      this.clazz = Default.class;
    }

    SkillType(Class<? extends Skill> clazz) {
      this.clazz = clazz;
    }

    public Skill makeSkill(StatsSet set) {
      try {
        Constructor<? extends Skill> c = this.clazz.getConstructor(StatsSet.class);
        return c.newInstance(set);
      } catch (Exception var3) {
        Skill._log.error("", var3);
        throw new RuntimeException(var3);
      }
    }

    public final boolean isPvM() {
      switch (this) {
        case DISCORD:
          return true;
        default:
          return false;
      }
    }

    public boolean isAI() {
      switch (this) {
        case AGGRESSION:
        case AIEFFECTS:
        case SOWING:
        case DELETE_HATE:
        case DELETE_HATE_OF_ME:
          return true;
        default:
          return false;
      }
    }

    public final boolean isPvpSkill() {
      switch (this) {
        case AGGRESSION:
        case DELETE_HATE:
        case DELETE_HATE_OF_ME:
        case BLEED:
        case DEBUFF:
        case DOT:
        case MDOT:
        case MUTE:
        case PARALYZE:
        case POISON:
        case ROOT:
        case SLEEP:
        case MANADAM:
        case DESTROY_SUMMON:
        case NEGATE_STATS:
        case NEGATE_EFFECTS:
        case STEAL_BUFF:
          return true;
        case AIEFFECTS:
        case SOWING:
        default:
          return false;
      }
    }

    public boolean isOffensive() {
      switch (this) {
        case DISCORD:
        case AGGRESSION:
        case AIEFFECTS:
        case SOWING:
        case DELETE_HATE:
        case DELETE_HATE_OF_ME:
        case BLEED:
        case DEBUFF:
        case DOT:
        case MDOT:
        case MUTE:
        case PARALYZE:
        case POISON:
        case ROOT:
        case SLEEP:
        case MANADAM:
        case DESTROY_SUMMON:
        case STEAL_BUFF:
        case DRAIN:
        case DRAIN_SOUL:
        case LETHAL_SHOT:
        case MDAM:
        case PDAM:
        case CPDAM:
        case SOULSHOT:
        case SPIRITSHOT:
        case SPOIL:
        case STUN:
        case SWEEP:
        case HARVESTING:
        case TELEPORT_NPC:
          return true;
        case NEGATE_STATS:
        case NEGATE_EFFECTS:
        default:
          return false;
      }
    }
  }

  public enum SkillTargetType {
    TARGET_ALLY,
    TARGET_AREA,
    TARGET_AREA_AIM_CORPSE,
    TARGET_AURA,
    TARGET_PET_AURA,
    TARGET_CHEST,
    TARGET_FEEDABLE_BEAST,
    TARGET_CLAN,
    TARGET_CLAN_ONLY,
    TARGET_CORPSE,
    TARGET_CORPSE_PLAYER,
    TARGET_ENEMY_PET,
    TARGET_ENEMY_SUMMON,
    TARGET_ENEMY_SERVITOR,
    TARGET_FLAGPOLE,
    TARGET_COMMCHANNEL,
    TARGET_HOLY,
    TARGET_ITEM,
    TARGET_MULTIFACE,
    TARGET_MULTIFACE_AURA,
    TARGET_TUNNEL,
    TARGET_NONE,
    TARGET_ONE,
    TARGET_OTHER,
    TARGET_OWNER,
    TARGET_PARTY,
    TARGET_PET,
    TARGET_SELF,
    TARGET_SIEGE,
    TARGET_UNLOCKABLE;

    SkillTargetType() {
    }
  }

  public enum SkillMagicType {
    PHYSIC,
    MAGIC,
    SPECIAL,
    MUSIC;

    SkillMagicType() {
    }
  }

  public enum Ternary {
    TRUE,
    FALSE,
    DEFAULT;

    Ternary() {
    }
  }

  public enum SkillOpType {
    OP_ACTIVE,
    OP_PASSIVE,
    OP_TOGGLE;

    SkillOpType() {
    }
  }

  public enum SkillNextAction {
    ATTACK,
    CAST,
    DEFAULT,
    MOVE,
    NONE;

    SkillNextAction() {
    }
  }

  public static class AddedSkill {
    public static final Skill.AddedSkill[] EMPTY_ARRAY = new Skill.AddedSkill[0];
    public int id;
    public int level;
    private Skill _skill;

    public AddedSkill(int id, int level) {
      this.id = id;
      this.level = level;
    }

    public Skill getSkill() {
      if (this._skill == null) {
        this._skill = SkillTable.getInstance().getInfo(this.id, this.level);
      }

      return this._skill;
    }
  }
}
