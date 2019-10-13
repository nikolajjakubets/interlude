//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.collections.LazyArrayList;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.listener.Listener;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.commons.util.concurrent.atomic.AtomicState;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.NextAction;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.geodata.GeoMove;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.GameObjectTasks.AltMagicUseTask;
import l2.gameserver.model.GameObjectTasks.CastEndTimeTask;
import l2.gameserver.model.GameObjectTasks.HitTask;
import l2.gameserver.model.GameObjectTasks.MagicLaunchedTask;
import l2.gameserver.model.GameObjectTasks.MagicUseTask;
import l2.gameserver.model.GameObjectTasks.NotifyAITask;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.model.reference.L2Reference;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.Attack;
import l2.gameserver.network.l2.s2c.AutoAttackStart;
import l2.gameserver.network.l2.s2c.AutoAttackStop;
import l2.gameserver.network.l2.s2c.ChangeMoveType;
import l2.gameserver.network.l2.s2c.CharMoveToLocation;
import l2.gameserver.network.l2.s2c.FlyToLocation;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillCanceled;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.MoveToPawn;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.SetupGauge;
import l2.gameserver.network.l2.s2c.StatusUpdate;
import l2.gameserver.network.l2.s2c.StopMove;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.TeleportToLocation;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.stats.Calculator;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.StatFunctions;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.Formulas.AttackInfo;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.taskmanager.RegenTaskManager;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Creature extends GameObject {
  private static final Logger _log = LoggerFactory.getLogger(Creature.class);
  public static final double HEADINGS_IN_PI = 10430.378350470453D;
  private Skill _castingSkill;
  private long _castInterruptTime;
  private long _animationEndTime;
  public int _scheduledCastInterval;
  public Future<?> _skillTask;
  public Future<?> _skillLaunchedTask;
  private Future<?> _stanceTask;
  private Runnable _stanceTaskRunnable;
  private long _stanceEndTime;
  public static final int CLIENT_BAR_SIZE = 352;
  private int _lastCpBarUpdate = -1;
  private int _lastHpBarUpdate = -1;
  private int _lastMpBarUpdate = -1;
  protected double _currentCp = 0.0D;
  protected double _currentHp = 1.0D;
  protected double _currentMp = 1.0D;
  protected boolean _isAttackAborted;
  protected long _attackEndTime;
  protected long _attackReuseEndTime;
  private int _poleAttackCount = 0;
  private static final double[] POLE_VAMPIRIC_MOD = new double[]{1.0D, 0.9D, 0.0D, 7.0D, 0.2D, 0.01D};
  protected final Map<Integer, Skill> _skills = new ConcurrentSkipListMap();
  protected Map<TriggerType, Set<TriggerInfo>> _triggers;
  protected IntObjectMap<TimeStamp> _skillReuses = new CHashIntObjectMap();
  protected volatile EffectList _effectList;
  protected volatile CharStatsChangeRecorder<? extends Creature> _statsRecorder;
  private List<Stats> _blockedStats;
  private int _abnormalEffects;
  private int _abnormalEffects2;
  private int _abnormalEffects3;
  protected AtomicBoolean isDead = new AtomicBoolean();
  protected AtomicBoolean isTeleporting = new AtomicBoolean();
  private Map<Integer, Integer> _skillMastery;
  protected boolean _isInvul;
  private boolean _isBlessedByNoblesse;
  private boolean _isSalvation;
  private boolean _meditated;
  private boolean _lockedTarget;
  private boolean _blocked;
  private AtomicState _afraid = new AtomicState();
  private AtomicState _muted = new AtomicState();
  private AtomicState _pmuted = new AtomicState();
  private AtomicState _amuted = new AtomicState();
  private AtomicState _paralyzed = new AtomicState();
  private AtomicState _rooted = new AtomicState();
  private AtomicState _sleeping = new AtomicState();
  private AtomicState _stunned = new AtomicState();
  private AtomicState _immobilized = new AtomicState();
  private AtomicState _confused = new AtomicState();
  private AtomicState _frozen = new AtomicState();
  private AtomicState _healBlocked = new AtomicState();
  private AtomicState _damageBlocked = new AtomicState();
  private AtomicState _buffImmunity = new AtomicState();
  private AtomicState _debuffImmunity = new AtomicState();
  private AtomicState _effectImmunity = new AtomicState();
  private AtomicState _weaponEquipBlocked = new AtomicState();
  private boolean _flying;
  private boolean _running;
  protected Creature.MoveActionBase moveAction = null;
  private final Lock moveLock = new ReentrantLock();
  private Future<?> _moveTask;
  private Runnable _moveTaskRunnable;
  private volatile HardReference<? extends GameObject> target = HardReferences.emptyRef();
  private volatile HardReference<? extends Creature> castingTarget = HardReferences.emptyRef();
  private volatile HardReference<? extends Creature> _aggressionTarget = HardReferences.emptyRef();
  private int _heading;
  private final Calculator[] _calculators;
  protected CharTemplate _template;
  protected CharTemplate _baseTemplate;
  protected volatile CharacterAI _ai;
  protected String _name;
  protected String _title;
  protected TeamType _team;
  private boolean _isRegenerating;
  private final Lock regenLock;
  private Future<?> _regenTask;
  private Runnable _regenTaskRunnable;
  private AtomicReference<Zone[]> _zonesRef;
  protected volatile CharListenerList listeners;
  private List<Player> _statusListeners;
  private final Lock statusListenersLock;
  protected Long _storedId;
  protected HardReference<? extends Creature> reference;
  private Location _flyLoc;
  private TIntHashSet _unActiveSkills;

  public int getActingRange() {
    return 150;
  }

  public final Long getStoredId() {
    return this._storedId;
  }

  public Creature(int objectId, CharTemplate template) {
    super(objectId);
    this._team = TeamType.NONE;
    this.regenLock = new ReentrantLock();
    this._zonesRef = new AtomicReference(Zone.EMPTY_L2ZONE_ARRAY);
    this.statusListenersLock = new ReentrantLock();
    this._unActiveSkills = new TIntHashSet();
    this._template = template;
    this._baseTemplate = template;
    this._calculators = new Calculator[Stats.NUM_STATS];
    StatFunctions.addPredefinedFuncs(this);
    this.reference = new L2Reference(this);
    this._storedId = GameObjectsStorage.put(this);
  }

  public HardReference<? extends Creature> getRef() {
    return this.reference;
  }

  public boolean isAttackAborted() {
    return this._isAttackAborted;
  }

  public final void abortAttack(boolean force, boolean message) {
    if (this.isAttackingNow()) {
      this._attackEndTime = 0L;
      if (force) {
        this._isAttackAborted = true;
      }

      this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      if (this.isPlayer() && message) {
        this.sendActionFailed();
        this.sendPacket((IStaticPacket)(new SystemMessage(158)));
      }
    }

  }

  public final void abortCast(boolean force, boolean message) {
    if (this.isCastingNow() && (force || this.canAbortCast())) {
      Skill castingSkill = this._castingSkill;
      Future<?> skillTask = this._skillTask;
      Future<?> skillLaunchedTask = this._skillLaunchedTask;
      this.finishFly();
      this.clearCastVars();
      if (skillTask != null) {
        skillTask.cancel(false);
      }

      if (skillLaunchedTask != null) {
        skillLaunchedTask.cancel(false);
      }

      if (castingSkill != null) {
        if (castingSkill.isUsingWhileCasting()) {
          Creature target = this.getAI().getAttackTarget();
          if (target != null) {
            target.getEffectList().stopEffect(castingSkill.getId());
          }
        }

        this.removeSkillMastery(castingSkill.getId());
      }

      this.broadcastPacket(new MagicSkillCanceled(this));
      this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      if (this.isPlayer() && message) {
        this.sendPacket((IStaticPacket)Msg.CASTING_HAS_BEEN_INTERRUPTED);
      }
    }

  }

  public final boolean canAbortCast() {
    return this._castInterruptTime >= System.currentTimeMillis();
  }

  public boolean absorbAndReflect(Creature target, Skill skill, double damage, boolean sendMessage) {
    if (target.isDead()) {
      return false;
    } else {
      boolean bow = this.getActiveWeaponItem() != null && this.getActiveWeaponItem().getItemType() == WeaponType.BOW;
      double value = 0.0D;
      if (skill != null && skill.isMagic()) {
        value = target.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0.0D, this, skill);
      } else if (skill != null && skill.getCastRange() <= 200) {
        value = target.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0.0D, this, skill);
      } else if (skill == null && !bow) {
        value = target.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0.0D, this, (Skill)null);
      }

      if (value > 0.0D && Rnd.chance(value)) {
        this.reduceCurrentHp(damage, target, (Skill)null, true, true, false, false, false, false, true);
        return true;
      } else {
        if (skill != null && skill.isMagic()) {
          value = target.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0.0D, this, skill);
        } else if (skill != null && skill.getCastRange() <= 200) {
          value = target.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0.0D, this, skill);
        } else if (skill == null && !bow) {
          value = target.calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0.0D, this, (Skill)null);
        }

        double poleMod;
        if (value > 0.0D && target.getCurrentHp() + target.getCurrentCp() > damage) {
          poleMod = value / 100.0D * damage;
          this.reduceCurrentHp(poleMod, target, (Skill)null, true, true, false, false, false, false, sendMessage);
          if (sendMessage && target.isPlayable()) {
            target.sendPacket((IStaticPacket)(new SystemMessage(35)).addNumber((int)poleMod));
          }
        }

        if (skill == null && !bow) {
          damage = (double)((int)(damage - target.getCurrentCp()));
          if (damage <= 0.0D) {
            return false;
          } else {
            poleMod = this._poleAttackCount < POLE_VAMPIRIC_MOD.length ? POLE_VAMPIRIC_MOD[this._poleAttackCount] : 0.0D;
            double absorb = poleMod * this.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0.0D, target, (Skill)null);
            if (absorb > 0.0D && !target.isDamageBlocked()) {
              double limit = this.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)this.getMaxHp() / 100.0D;
              if (this.getCurrentHp() < limit) {
                this.setCurrentHp(Math.min(this._currentHp + damage * absorb * Config.ALT_ABSORB_DAMAGE_MODIFIER / 100.0D, limit), false);
              }
            }

            return false;
          }
        } else {
          return false;
        }
      }
    }
  }

  public double absorbToEffector(Creature attacker, double damage) {
    double transferToEffectorDam = this.calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0.0D);
    if (transferToEffectorDam > 0.0D) {
      Effect effect = this.getEffectList().getEffectByType(EffectType.AbsorbDamageToEffector);
      if (effect == null) {
        return damage;
      } else {
        Creature effector = effect.getEffector();
        if (effector != this && !effector.isDead() && this.isInRange(effector, 1200L)) {
          Player thisPlayer = this.getPlayer();
          Player effectorPlayer = effector.getPlayer();
          if (thisPlayer != null && effectorPlayer != null) {
            if (thisPlayer != effectorPlayer && (!thisPlayer.isOnline() || !thisPlayer.isInParty() || thisPlayer.getParty() != effectorPlayer.getParty())) {
              return damage;
            } else {
              double transferDamage = damage * transferToEffectorDam * 0.01D;
              damage -= transferDamage;
              effector.reduceCurrentHp(transferDamage, effector, (Skill)null, false, false, !attacker.isPlayable(), false, true, false, true);
              return damage;
            }
          } else {
            return damage;
          }
        } else {
          return damage;
        }
      }
    } else {
      return damage;
    }
  }

  public double absorbToSummon(Creature attacker, double damage) {
    double transferToSummonDam = this.calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0.0D);
    if (transferToSummonDam > 0.0D) {
      Summon summon = this.getPet();
      double transferDamage = damage * transferToSummonDam * 0.01D;
      if (summon != null && !summon.isDead() && summon.getCurrentHp() >= transferDamage) {
        if (summon.isSummon() && summon.isInRangeZ(this, 1200L)) {
          damage -= transferDamage;
          summon.reduceCurrentHp(transferDamage, summon, (Skill)null, false, false, false, false, true, false, true);
        }
      } else {
        this.getEffectList().stopEffects(EffectType.AbsorbDamageToSummon);
      }
    }

    return damage;
  }

  public void addBlockStats(List<Stats> stats) {
    if (this._blockedStats == null) {
      this._blockedStats = new ArrayList();
    }

    this._blockedStats.addAll(stats);
  }

  public Skill addSkill(Skill newSkill) {
    if (newSkill == null) {
      return null;
    } else {
      Skill oldSkill = (Skill)this._skills.get(newSkill.getId());
      if (oldSkill != null && oldSkill.getLevel() == newSkill.getLevel()) {
        return newSkill;
      } else {
        this._skills.put(newSkill.getId(), newSkill);
        if (oldSkill != null) {
          this.removeStatsOwner(oldSkill);
          this.removeTriggers(oldSkill);
        }

        this.addTriggers(newSkill);
        this.addStatFuncs(newSkill.getStatFuncs());
        return oldSkill;
      }
    }
  }

  public Calculator[] getCalculators() {
    return this._calculators;
  }

  public final void addStatFunc(Func f) {
    if (f != null) {
      int stat = f.stat.ordinal();
      synchronized(this._calculators) {
        if (this._calculators[stat] == null) {
          this._calculators[stat] = new Calculator(f.stat, this);
        }

        this._calculators[stat].addFunc(f);
      }
    }
  }

  public final void addStatFuncs(Func[] funcs) {
    Func[] var2 = funcs;
    int var3 = funcs.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Func f = var2[var4];
      this.addStatFunc(f);
    }

  }

  public final void removeStatFunc(Func f) {
    if (f != null) {
      int stat = f.stat.ordinal();
      synchronized(this._calculators) {
        if (this._calculators[stat] != null) {
          this._calculators[stat].removeFunc(f);
        }

      }
    }
  }

  public final void removeStatFuncs(Func[] funcs) {
    Func[] var2 = funcs;
    int var3 = funcs.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Func f = var2[var4];
      this.removeStatFunc(f);
    }

  }

  public final void removeStatsOwner(Object owner) {
    synchronized(this._calculators) {
      for(int i = 0; i < this._calculators.length; ++i) {
        if (this._calculators[i] != null) {
          this._calculators[i].removeOwner(owner);
        }
      }

    }
  }

  public void altOnMagicUseTimer(Creature aimingTarget, Skill skill) {
    if (!this.isAlikeDead()) {
      List<Creature> targets = skill.getTargets(this, aimingTarget, true);
      double mpConsume2 = skill.getMpConsume2();
      if (mpConsume2 > 0.0D) {
        if (this._currentMp < mpConsume2) {
          this.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
          return;
        }

        if (skill.isMagic()) {
          this.reduceCurrentMp(this.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill), (Creature)null);
        } else {
          this.reduceCurrentMp(this.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill), (Creature)null);
        }
      }

      this.callSkill(skill, targets, false);
      this.broadcastPacket(new MagicSkillLaunched(this, skill, targets));
    }
  }

  public void altUseSkill(Skill skill, Creature target) {
    if (skill != null) {
      int magicId = skill.getId();
      if (!this.isUnActiveSkill(magicId)) {
        if (this.isSkillDisabled(skill)) {
          this.sendReuseMessage(skill);
        } else {
          if (target == null) {
            target = skill.getAimingTarget(this, this.getTarget());
            if (target == null) {
              return;
            }
          }

          this.getListeners().onMagicUse(skill, target, true);
          double mpConsume1 = skill.getMpConsume1();
          if (mpConsume1 > 0.0D) {
            if (this._currentMp < mpConsume1) {
              this.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
              return;
            }

            this.reduceCurrentMp(mpConsume1, (Creature)null);
          }

          int[] itemConsume = skill.getItemConsume();
          int level;
          if (itemConsume[0] > 0) {
            for(level = 0; level < itemConsume.length; ++level) {
              if (!this.consumeItem(skill.getItemConsumeId()[level], (long)itemConsume[level])) {
                this.sendPacket((IStaticPacket)(skill.isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : (new SystemMessage(113)).addSkillName(skill.getDisplayId(), skill.getDisplayLevel())));
                return;
              }
            }
          }

          if (skill.getReferenceItemId() <= 0 || this.consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume())) {
            if (skill.getSoulsConsume() > this.getConsumedSouls()) {
              this.sendPacket((IStaticPacket)Msg.THERE_IS_NOT_ENOUGHT_SOUL);
            } else if (skill.getEnergyConsume() > this.getAgathionEnergy()) {
              this.sendPacket((IStaticPacket)SystemMsg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY);
            } else {
              if (skill.getSoulsConsume() > 0) {
                this.setConsumedSouls(this.getConsumedSouls() - skill.getSoulsConsume(), (NpcInstance)null);
              }

              if (skill.getEnergyConsume() > 0) {
                this.setAgathionEnergy(this.getAgathionEnergy() - skill.getEnergyConsume());
              }

              level = Math.max(1, this.getSkillDisplayLevel(magicId));
              Formulas.calcSkillMastery(skill, this);
              long reuseDelay = Formulas.calcSkillReuseDelay(this, skill);
              if (!skill.isToggle()) {
                this.broadcastPacket(new MagicSkillUse(this, target, skill.getDisplayId(), level, skill.getHitTime(), reuseDelay));
              }

              if (!skill.isHideUseMessage()) {
                if (skill.getSkillType() == SkillType.PET_SUMMON) {
                  this.sendPacket((IStaticPacket)(new SystemMessage(547)));
                } else if (!skill.isHandler()) {
                  this.sendPacket((IStaticPacket)(new SystemMessage(46)).addSkillName(magicId, level));
                } else {
                  this.sendPacket((IStaticPacket)(new SystemMessage(46)).addItemName(skill.getItemConsumeId()[0]));
                }
              }

              if (!skill.isHandler()) {
                this.disableSkill(skill, reuseDelay);
              }

              ThreadPoolManager.getInstance().schedule(new AltMagicUseTask(this, target, skill), (long)skill.getHitTime());
            }
          }
        }
      }
    }
  }

  public void sendReuseMessage(Skill skill) {
  }

  public void broadcastPacket(L2GameServerPacket... packets) {
    this.sendPacket((IStaticPacket[])packets);
    this.broadcastPacketToOthers(packets);
  }

  public void broadcastPacket(List<L2GameServerPacket> packets) {
    this.sendPacket(packets);
    this.broadcastPacketToOthers(packets);
  }

  public void broadcastPacketToOthers(L2GameServerPacket... packets) {
    if (this.isVisible() && packets.length != 0) {
      List<Player> players = World.getAroundPlayers(this);

      for(int i = 0; i < players.size(); ++i) {
        Player target = (Player)players.get(i);
        target.sendPacket(packets);
      }

    }
  }

  public void broadcastPacketToOthers(List<L2GameServerPacket> packets) {
    if (this.isVisible() && !packets.isEmpty()) {
      List<Player> players = World.getAroundPlayers(this);

      for(int i = 0; i < players.size(); ++i) {
        Player target = (Player)players.get(i);
        target.sendPacket(packets);
      }

    }
  }

  public void broadcastToStatusListeners(L2GameServerPacket... packets) {
    if (this.isVisible() && packets.length != 0) {
      this.statusListenersLock.lock();

      try {
        if (this._statusListeners != null && !this._statusListeners.isEmpty()) {
          for(int i = 0; i < this._statusListeners.size(); ++i) {
            Player player = (Player)this._statusListeners.get(i);
            player.sendPacket(packets);
          }

          return;
        }
      } finally {
        this.statusListenersLock.unlock();
      }

    }
  }

  public void addStatusListener(Player cha) {
    if (cha != this) {
      this.statusListenersLock.lock();

      try {
        if (this._statusListeners == null) {
          this._statusListeners = new LazyArrayList();
        }

        if (!this._statusListeners.contains(cha)) {
          this._statusListeners.add(cha);
        }
      } finally {
        this.statusListenersLock.unlock();
      }

    }
  }

  public void removeStatusListener(Creature cha) {
    this.statusListenersLock.lock();

    try {
      if (this._statusListeners != null) {
        this._statusListeners.remove(cha);
        return;
      }
    } finally {
      this.statusListenersLock.unlock();
    }

  }

  public void clearStatusListeners() {
    this.statusListenersLock.lock();

    try {
      if (this._statusListeners == null) {
        return;
      }

      this._statusListeners.clear();
    } finally {
      this.statusListenersLock.unlock();
    }

  }

  public StatusUpdate makeStatusUpdate(int... fields) {
    StatusUpdate su = new StatusUpdate(this.getObjectId());
    int[] var3 = fields;
    int var4 = fields.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int field = var3[var5];
      switch(field) {
        case 9:
          su.addAttribute(field, (int)this.getCurrentHp());
          break;
        case 10:
          su.addAttribute(field, this.getMaxHp());
          break;
        case 11:
          su.addAttribute(field, (int)this.getCurrentMp());
          break;
        case 12:
          su.addAttribute(field, this.getMaxMp());
        case 13:
        case 14:
        case 15:
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        default:
          break;
        case 26:
          su.addAttribute(field, this.getPvpFlag());
          break;
        case 27:
          su.addAttribute(field, this.getKarma());
          break;
        case 33:
          su.addAttribute(field, (int)this.getCurrentCp());
          break;
        case 34:
          su.addAttribute(field, this.getMaxCp());
      }
    }

    return su;
  }

  public void broadcastStatusUpdate() {
    if (this.needStatusUpdate()) {
      StatusUpdate su = this.makeStatusUpdate(10, 12, 9, 11);
      this.broadcastToStatusListeners(su);
    }
  }

  public int calcHeading(int x_dest, int y_dest) {
    return (int)(Math.atan2((double)(this.getY() - y_dest), (double)(this.getX() - x_dest)) * 10430.378350470453D) + '耀';
  }

  public final double calcStat(Stats stat, double init) {
    return this.calcStat(stat, init, (Creature)null, (Skill)null);
  }

  public final double calcStat(Stats stat, double init, Creature target, Skill skill) {
    int id = stat.ordinal();
    Calculator c = this._calculators[id];
    if (c == null) {
      return init;
    } else {
      Env env = new Env();
      env.character = this;
      env.target = target;
      env.skill = skill;
      env.value = init;
      c.calc(env);
      return env.value;
    }
  }

  public final double calcStat(Stats stat, Creature target, Skill skill) {
    Env env = new Env(this, target, skill);
    env.value = stat.getInit();
    int id = stat.ordinal();
    Calculator c = this._calculators[id];
    if (c != null) {
      c.calc(env);
    }

    return env.value;
  }

  public int calculateAttackDelay() {
    return Formulas.calcPAtkSpd((double)this.getPAtkSpd());
  }

  public void callSkill(Skill skill, List<Creature> targets, boolean useActionSkills) {
    try {
      if (useActionSkills && !skill.isUsingWhileCasting() && this._triggers != null) {
        if (skill.isOffensive()) {
          if (skill.isMagic()) {
            this.useTriggers(this.getTarget(), TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, (Skill)null, skill, 0.0D);
          } else {
            this.useTriggers(this.getTarget(), TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, (Skill)null, skill, 0.0D);
          }
        } else if (Config.BUFF_STICK_FOR_ALL || skill.isMagic()) {
          boolean targetSelf = skill.isAoE() || skill.isNotTargetAoE() || skill.getTargetType() == SkillTargetType.TARGET_SELF;
          this.useTriggers((GameObject)(targetSelf ? this : this.getTarget()), TriggerType.SUPPORT_MAGICAL_SKILL_USE, (Skill)null, skill, 0.0D);
        }
      }

      Player pl = this.getPlayer();
      Iterator itr = targets.iterator();

      while(true) {
        while(itr.hasNext()) {
          Creature target = (Creature)itr.next();
          if (skill.isOffensive() && target.isInvul()) {
            Player pcTarget = target.getPlayer();
            if ((!skill.isIgnoreInvul() || pcTarget != null && pcTarget.isGM()) && !target.isArtefact()) {
              itr.remove();
              continue;
            }
          }

          Effect ie = target.getEffectList().getEffectByType(EffectType.IgnoreSkill);
          if (ie != null && ArrayUtils.contains(ie.getTemplate().getParam().getIntegerArray("skillId"), skill.getId())) {
            itr.remove();
          } else {
            target.getListeners().onMagicHit(skill, this);
            if (pl != null && target != null && target.isNpc()) {
              NpcInstance npc = (NpcInstance)target;
              List<QuestState> ql = pl.getQuestsForEvent(npc, QuestEventType.MOB_TARGETED_BY_SKILL);
              if (ql != null) {
                Iterator var10 = ql.iterator();

                while(var10.hasNext()) {
                  QuestState qs = (QuestState)var10.next();
                  qs.getQuest().notifySkillUse(npc, skill, qs);
                }
              }
            }

            if (skill.getNegateSkill() > 0) {
              Iterator var15 = target.getEffectList().getAllEffects().iterator();

              label111:
              while(true) {
                Effect e;
                Skill efs;
                do {
                  do {
                    do {
                      if (!var15.hasNext()) {
                        break label111;
                      }

                      e = (Effect)var15.next();
                      efs = e.getSkill();
                    } while(efs.getId() != skill.getNegateSkill());
                  } while(!e.isCancelable());
                } while(skill.getNegatePower() > 0 && efs.getPower() > (double)skill.getNegatePower());

                e.exit();
              }
            }

            if (skill.getCancelTarget() > 0 && Rnd.chance(skill.getCancelTarget()) && (target.getCastingSkill() == null || target.getCastingSkill().getSkillType() != SkillType.TAKECASTLE) && !target.isRaid()) {
              target.abortAttack(true, true);
              target.abortCast(true, true);
              target.setTarget((GameObject)null);
            }
          }
        }

        if (skill.isOffensive()) {
          this.startAttackStanceTask();
        }

        if (!skill.isNotTargetAoE() || !skill.isOffensive() || targets.size() != 0) {
          skill.getEffects(this, this, false, true);
        }

        skill.useSkill(this, targets);
        break;
      }
    } catch (Exception var12) {
      _log.error("", var12);
    }

  }

  public void useTriggers(GameObject target, TriggerType type, Skill ex, Skill owner, double damage) {
    if (this._triggers != null) {
      Set<TriggerInfo> SkillsOnSkillAttack = (Set)this._triggers.get(type);
      if (SkillsOnSkillAttack != null) {
        Iterator var8 = SkillsOnSkillAttack.iterator();

        while(var8.hasNext()) {
          TriggerInfo t = (TriggerInfo)var8.next();
          if (t.getSkill() != ex) {
            this.useTriggerSkill(target == null ? this.getTarget() : target, (List)null, t, owner, damage);
          }
        }
      }

    }
  }

  public void useTriggerSkill(GameObject target, List<Creature> targets, TriggerInfo trigger, Skill owner, double damage) {
    Skill skill = trigger.getSkill();
    if (skill.getReuseDelay() <= 0L || !this.isSkillDisabled(skill)) {
      Creature aimTarget = skill.getAimingTarget(this, target);
      Creature realTarget = target != null && target.isCreature() ? (Creature)target : null;
      if (Rnd.chance(trigger.getChance()) && trigger.checkCondition(this, realTarget, aimTarget, owner, damage) && skill.checkCondition(this, aimTarget, false, true, true)) {
        if (targets == null) {
          targets = skill.getTargets(this, aimTarget, false);
        }

        int displayId = 0;
        int displayLevel = 0;
        if (skill.hasEffects()) {
          displayId = skill.getEffectTemplates()[0]._displayId;
          displayLevel = skill.getEffectTemplates()[0]._displayLevel;
        }

        if (displayId == 0) {
          displayId = skill.getDisplayId();
        }

        if (displayLevel == 0) {
          displayLevel = skill.getDisplayLevel();
        }

        if (trigger.getType() != TriggerType.SUPPORT_MAGICAL_SKILL_USE) {
          Iterator var12 = targets.iterator();

          while(var12.hasNext()) {
            Creature cha = (Creature)var12.next();
            this.broadcastPacket(new MagicSkillUse(this, cha, displayId, displayLevel, 0, 0L));
          }
        }

        this.callSkill(skill, targets, false);
        this.disableSkill(skill, skill.getReuseDelay());
      }

    }
  }

  public boolean checkBlockedStat(Stats stat) {
    return this._blockedStats != null && this._blockedStats.contains(stat);
  }

  public boolean checkReflectSkill(Creature attacker, Skill skill) {
    if (!skill.isReflectable()) {
      return false;
    } else if (!this.isInvul() && !attacker.isInvul() && skill.isOffensive()) {
      if (skill.isMagic() && skill.getSkillType() != SkillType.MDAM) {
        return false;
      } else if (Rnd.chance(this.calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0.0D, attacker, skill))) {
        this.sendPacket((IStaticPacket)(new SystemMessage(1998)).addName(attacker));
        attacker.sendPacket((IStaticPacket)(new SystemMessage(1999)).addName(this));
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public void doCounterAttack(Skill skill, Creature attacker, boolean blow) {
    if (!this.isDead()) {
      if (!this.isDamageBlocked() && !attacker.isDamageBlocked()) {
        if (skill != null && !skill.hasEffects() && !skill.isMagic() && skill.isOffensive() && skill.getCastRange() <= 200) {
          if (Rnd.chance(this.calcStat(Stats.COUNTER_ATTACK, 0.0D, attacker, skill))) {
            double damage = (double)(1189 * this.getPAtk(attacker) / Math.max(attacker.getPDef(this), 1));
            attacker.sendPacket((IStaticPacket)(new SystemMessage(1997)).addName(this));
            if (blow) {
              this.sendPacket((IStaticPacket)(new SystemMessage(1997)).addName(this));
              this.sendPacket((IStaticPacket)(new SystemMessage(35)).addNumber((long)damage));
              attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
            } else {
              this.sendPacket((IStaticPacket)(new SystemMessage(1997)).addName(this));
            }

            this.sendPacket((IStaticPacket)(new SystemMessage(35)).addNumber((long)damage));
            attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
          }

        }
      }
    }
  }

  public void disableSkill(Skill skill, long delay) {
    this._skillReuses.put(skill.hashCode(), new TimeStamp(skill, delay));
  }

  public abstract boolean isAutoAttackable(Creature var1);

  public void doAttack(Creature target) {
    if (target != null && !this.isAMuted() && !this.isAttackingNow() && !this.isAlikeDead() && !target.isAlikeDead() && this.isInRange(target, 2048L) && (!this.isPlayer() || !this.getPlayer().isInMountTransform())) {
      this.getListeners().onAttack(target);
      if (Config.ALT_TELEPORT_PROTECTION && this.isPlayer()) {
        Player player = this.getPlayer();
        if (player.getAfterTeleportPortectionTime() > System.currentTimeMillis()) {
          player.setAfterTeleportPortectionTime(0L);
          player.sendMessage(new CustomMessage("alt.teleport_protect_gonna", player, new Object[0]));
        }
      }

      int sAtk = Math.max(this.calculateAttackDelay(), Config.MIN_ATK_DELAY);
      int ssGrade = 0;
      int reuse = sAtk;
      this._attackEndTime = (long)sAtk + System.currentTimeMillis() - (long)Config.ATTACK_END_DELAY;
      this._isAttackAborted = false;
      WeaponTemplate weaponItem = this.getActiveWeaponItem();
      if (weaponItem != null) {
        reuse = sAtk + (int)((float)weaponItem.getAttackReuseDelay() / ((float)this.getPAtkSpd() / 333.0F));
        if (this.isPlayer() && weaponItem.getAttackReuseDelay() > 0 && reuse > 0) {
          this.sendPacket((IStaticPacket)(new SetupGauge(this, 1, reuse)));
          this._attackReuseEndTime = (long)reuse + System.currentTimeMillis() - (long)Config.ATTACK_END_DELAY;
        }

        ssGrade = weaponItem.getCrystalType().gradeOrd();
      }

      Attack attack = new Attack(this, target, this.getChargedSoulShot(), ssGrade);
      this.setHeading(PositionUtils.calculateHeadingFrom(this, target));
      int hitDelay = reuse / 2;
      if (weaponItem == null) {
        this.doAttackHitSimple(attack, target, 1.0D, !this.isPlayer(), (long)hitDelay, true);
      } else {
        switch(weaponItem.getItemType()) {
          case BOW:
            this.doAttackHitByBow(attack, target, (long)hitDelay);
            break;
          case POLE:
            this.doAttackHitByPole(attack, target, (long)hitDelay);
            break;
          case DUAL:
          case DUALFIST:
            this.doAttackHitByDual(attack, target, (long)hitDelay);
            break;
          default:
            this.doAttackHitSimple(attack, target, 1.0D, true, (long)hitDelay, true);
        }
      }

      if (attack.hasHits()) {
        this.broadcastPacket(attack);
      }

    }
  }

  private void doAttackHitSimple(Attack attack, Creature target, double multiplier, boolean unchargeSS, long hitDelay, boolean notify) {
    int damage1 = 0;
    boolean shld1 = false;
    boolean crit1 = false;
    boolean miss1 = Formulas.calcHitMiss(this, target);
    if (!miss1) {
      AttackInfo info = Formulas.calcPhysDam(this, target, (Skill)null, false, false, attack._soulshot, false);
      damage1 = (int)(info.damage * multiplier);
      shld1 = info.shld;
      crit1 = info.crit;
    }

    ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, unchargeSS, notify, hitDelay), hitDelay);
    attack.addHit(target, damage1, miss1, crit1, shld1);
  }

  private void doAttackHitByBow(Attack attack, Creature target, long hitDelay) {
    WeaponTemplate activeWeapon = this.getActiveWeaponItem();
    if (activeWeapon != null) {
      int damage1 = 0;
      boolean shld1 = false;
      boolean crit1 = false;
      boolean miss1 = Formulas.calcHitMiss(this, target);
      if (Config.ALT_CONSUME_ARROWS) {
        this.reduceArrowCount();
      }

      if (!miss1) {
        AttackInfo info = Formulas.calcPhysDam(this, target, (Skill)null, false, false, attack._soulshot, false);
        damage1 = (int)info.damage;
        shld1 = info.shld;
        crit1 = info.crit;
      }

      ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, true, hitDelay), hitDelay);
      attack.addHit(target, damage1, miss1, crit1, shld1);
    }
  }

  private void doAttackHitByDual(Attack attack, Creature target, long hitDelay) {
    int damage1 = 0;
    int damage2 = 0;
    boolean shld1 = false;
    boolean shld2 = false;
    boolean crit1 = false;
    boolean crit2 = false;
    boolean miss1 = Formulas.calcHitMiss(this, target);
    boolean miss2 = Formulas.calcHitMiss(this, target);
    AttackInfo info;
    if (!miss1) {
      info = Formulas.calcPhysDam(this, target, (Skill)null, true, false, attack._soulshot, false);
      damage1 = (int)info.damage;
      shld1 = info.shld;
      crit1 = info.crit;
    }

    if (!miss2) {
      info = Formulas.calcPhysDam(this, target, (Skill)null, true, false, attack._soulshot, false);
      damage2 = (int)info.damage;
      shld2 = info.shld;
      crit2 = info.crit;
    }

    ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, false), hitDelay / 2L);
    ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage2, crit2, miss2, attack._soulshot, shld2, false, true, hitDelay), hitDelay);
    attack.addHit(target, damage1, miss1, crit1, shld1);
    attack.addHit(target, damage2, miss2, crit2, shld2);
  }

  private void doAttackHitByPole(Attack attack, Creature target, long hitDelay) {
    int angle = (int)this.calcStat(Stats.POLE_ATTACK_ANGLE, 90.0D, target, (Skill)null);
    int range = (int)this.calcStat(Stats.POWER_ATTACK_RANGE, (double)this.getTemplate().baseAtkRange, target, (Skill)null);
    int attackcountmax = (int)Math.round(this.calcStat(Stats.POLE_TARGET_COUNT, 0.0D, target, (Skill)null));
    if (this.isBoss()) {
      attackcountmax += 27;
    } else if (this.isRaid()) {
      attackcountmax += 12;
    } else if (this.isMonster() && this.getLevel() > 0) {
      attackcountmax = (int)((double)attackcountmax + (double)this.getLevel() / 7.5D);
    }

    double mult = 1.0D;
    this._poleAttackCount = 1;
    if (!this.isInZonePeace()) {
      Iterator var10 = this.getAroundCharacters(range, 200).iterator();

      while(var10.hasNext()) {
        Creature t = (Creature)var10.next();
        if (this._poleAttackCount > attackcountmax) {
          break;
        }

        if (t != target && !t.isDead() && PositionUtils.isFacing(this, t, angle) && t.isAutoAttackable(this)) {
          this.doAttackHitSimple(attack, t, mult, false, hitDelay, false);
          mult *= Config.ALT_POLE_DAMAGE_MODIFIER;
          ++this._poleAttackCount;
        }
      }
    }

    this._poleAttackCount = 0;
    this.doAttackHitSimple(attack, target, 1.0D, true, hitDelay, true);
  }

  public long getAnimationEndTime() {
    return this._animationEndTime;
  }

  public void doCast(Skill skill, Creature target, boolean forceUse) {
    if (skill != null) {
      int[] itemConsume = skill.getItemConsume();
      int magicId;
      if (itemConsume[0] > 0) {
        for(magicId = 0; magicId < itemConsume.length; ++magicId) {
          if (!this.consumeItem(skill.getItemConsumeId()[magicId], (long)itemConsume[magicId])) {
            this.sendPacket((IStaticPacket)(skill.isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : (new SystemMessage(113)).addSkillName(skill.getId(), skill.getLevel())));
            return;
          }
        }
      }

      if (skill.getReferenceItemId() <= 0 || this.consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume())) {
        magicId = skill.getId();
        if (target == null) {
          target = skill.getAimingTarget(this, this.getTarget());
        }

        if (target != null) {
          this.getListeners().onMagicUse(skill, target, false);
          if (this != target) {
            this.setHeading(PositionUtils.calculateHeadingFrom(this, target));
          }

          int level = Math.max(1, this.getSkillDisplayLevel(magicId));
          int skillTime = skill.isSkillTimePermanent() ? skill.getHitTime() : Formulas.calcMAtkSpd(this, skill, (double)skill.getHitTime());
          int skillInterruptTime = skill.getSkillInterruptTime();
          int minCastTime = Math.min(Config.SKILLS_CAST_TIME_MIN, skill.getHitTime());
          if (skillTime < minCastTime) {
            skillTime = minCastTime;
            skillInterruptTime = 0;
          }

          this._animationEndTime = System.currentTimeMillis() + (long)skillTime;
          if (skill.isMagic() && !skill.isSkillTimePermanent() && this.getChargedSpiritShot() > 0) {
            skillTime = (int)(0.7D * (double)skillTime);
            skillInterruptTime = (int)(0.7D * (double)skillInterruptTime);
          }

          Formulas.calcSkillMastery(skill, this);
          long reuseDelay = Math.max(0L, Formulas.calcSkillReuseDelay(this, skill));
          this.broadcastPacket(new MagicSkillUse(this, target, skill, skillTime, reuseDelay));
          if (!skill.isHandler()) {
            this.disableSkill(skill, reuseDelay);
          }

          if (this.isPlayer()) {
            if (skill.getSkillType() == SkillType.PET_SUMMON) {
              this.sendPacket((IStaticPacket)Msg.SUMMON_A_PET);
            } else if (!skill.isHandler()) {
              this.sendPacket((IStaticPacket)(new SystemMessage(46)).addSkillName(magicId, level));
            } else {
              this.sendPacket((IStaticPacket)(new SystemMessage(46)).addItemName(skill.getItemConsumeId()[0]));
            }
          }

          if (skill.getTargetType() == SkillTargetType.TARGET_HOLY) {
            target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);
          }

          double mpConsume1 = skill.isUsingWhileCasting() ? skill.getMpConsume() : skill.getMpConsume1();
          if (mpConsume1 > 0.0D) {
            if (this._currentMp < mpConsume1) {
              this.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
              this.onCastEndTime();
              return;
            }

            this.reduceCurrentMp(mpConsume1, (Creature)null);
          }

          this._flyLoc = null;
          switch(skill.getFlyType()) {
            case DUMMY:
            case CHARGE:
              Location flyLoc = this.getFlyLocation(target, skill);
              if (flyLoc == null) {
                this._animationEndTime = 0L;
                this.sendPacket((IStaticPacket)SystemMsg.CANNOT_SEE_TARGET);
                return;
              } else {
                this._flyLoc = flyLoc;
                this.broadcastPacket(new FlyToLocation(this, flyLoc, skill.getFlyType()));
              }
            default:
              this._castingSkill = skill;
              int skillLaunchTime = skillInterruptTime > 0 ? Math.max(0, skillTime - skillInterruptTime) : 0;
              this._castInterruptTime = System.currentTimeMillis() + (long)skillLaunchTime;
              this.setCastingTarget(target);
              if (skill.isUsingWhileCasting()) {
                this.callSkill(skill, skill.getTargets(this, target, forceUse), true);
              }

              this._scheduledCastInterval = skillTime;
              if (skillTime > 333 && this.isPlayer()) {
                this.sendPacket((IStaticPacket)(new SetupGauge(this, 0, skillTime)));
              }

              this.scheduleSkillLaunchedTask(forceUse, skillLaunchTime);
              this.scheduleSkillUseTask(forceUse, skillTime);
          }
        }
      }
    }
  }

  protected void scheduleSkillLaunchedTask(boolean forceUse, int skillLaunchTime) {
    this._skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse), (long)skillLaunchTime);
  }

  protected void scheduleSkillUseTask(boolean forceUse, int skillTime) {
    this._skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse), (long)skillTime);
  }

  public void clearCastVars() {
    this._animationEndTime = 0L;
    this._castInterruptTime = 0L;
    this._castingSkill = null;
    this._skillTask = null;
    this._skillLaunchedTask = null;
    this._flyLoc = null;
  }

  private Location getFlyLocation(GameObject target, Skill skill) {
    if (target != null && target != this) {
      double radian;
      Location loc;
      if (skill.isFlyToBack()) {
        radian = PositionUtils.convertHeadingToRadian(target.getHeading());
        loc = new Location(target.getX() + (int)(Math.sin(radian) * 40.0D), target.getY() - (int)(Math.cos(radian) * 40.0D), target.getZ());
      } else {
        radian = Math.atan2((double)(this.getY() - target.getY()), (double)(this.getX() - target.getX()));
        loc = new Location(target.getX() + (int)Math.round(Math.cos(radian) * 40.0D), target.getY() + (int)Math.round(Math.sin(radian) * 40.0D), target.getZ());
      }

      if (!this.isFlying()) {
        loc.correctGeoZ();
        if (!GeoEngine.canMoveToCoord(this.getX(), this.getY(), this.getZ(), loc.x, loc.y, loc.z, this.getGeoIndex())) {
          loc = target.getLoc();
          if (!GeoEngine.canMoveToCoord(this.getX(), this.getY(), this.getZ(), loc.x, loc.y, loc.z, this.getGeoIndex())) {
            return null;
          }
        }
      } else {
        if (this.isPlayer() && ((Player)this).isInFlyingTransform() && (loc.z <= 0 || loc.z >= 6000)) {
          return null;
        }

        if (GeoEngine.moveCheckInAir(this.getX(), this.getY(), this.getZ(), loc.x, loc.y, loc.z, this.getColRadius(), this.getGeoIndex()) == null) {
          return null;
        }
      }

      return loc;
    } else {
      double radian = PositionUtils.convertHeadingToRadian(this.getHeading());
      int x1 = -((int)(Math.sin(radian) * (double)skill.getFlyRadius()));
      int y1 = (int)(Math.cos(radian) * (double)skill.getFlyRadius());
      return this.isFlying() ? GeoEngine.moveCheckInAir(this.getX(), this.getY(), this.getZ(), this.getX() + x1, this.getY() + y1, this.getZ(), this.getColRadius(), this.getGeoIndex()) : GeoEngine.moveCheck(this.getX(), this.getY(), this.getZ(), this.getX() + x1, this.getY() + y1, this.getGeoIndex());
    }
  }

  public final void doDie(Creature killer) {
    if (this.isDead.compareAndSet(false, true)) {
      this.onDeath(killer);
    }
  }

  protected void onDeath(Creature killer) {
    if (killer != null) {
      Player killerPlayer = killer.getPlayer();
      if (killerPlayer != null) {
        killerPlayer.getListeners().onKillIgnorePetOrSummon(this);
      }

      killer.getListeners().onKill(this);
      if (this.isPlayer() && killer.isPlayable()) {
        this._currentCp = 0.0D;
      }
    }

    this.setTarget((GameObject)null);
    this.stopMove();
    this.stopAttackStanceTask();
    this.stopRegeneration();
    this._currentHp = 0.0D;
    Effect e;
    Iterator var4;
    if (!this.isBlessedByNoblesse() && !this.isSalvation()) {
      if (Config.ALT_PASSIVE_NOBLESS_ID == 0 || this.getKnownSkill(Config.ALT_PASSIVE_NOBLESS_ID) == null) {
        var4 = this.getEffectList().getAllEffects().iterator();

        while(var4.hasNext()) {
          e = (Effect)var4.next();
          if (e.getEffectType() != EffectType.Transformation && !e.getSkill().isPreservedOnDeath()) {
            e.exit();
          }
        }
      }
    } else {
      if (this.isSalvation() && this.isPlayer() && !this.getPlayer().isOlyParticipant() && !this.getPlayer().isResurectProhibited()) {
        this.getPlayer().reviveRequest(this.getPlayer(), 100.0D, false, 0);
      }

      var4 = this.getEffectList().getAllEffects().iterator();

      label66:
      while(true) {
        do {
          if (!var4.hasNext()) {
            break label66;
          }

          e = (Effect)var4.next();
        } while(e.getEffectType() != EffectType.BlessNoblesse && e.getSkill().getId() != 1325 && e.getSkill().getId() != 2168);

        e.exit();
      }
    }

    ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_DEAD, killer, (Object)null));
    this.getListeners().onDeath(killer);
    this.updateEffectIcons();
    this.updateStats();
    this.broadcastStatusUpdate();
  }

  protected void onRevive() {
  }

  public void enableSkill(Skill skill) {
    this._skillReuses.remove(skill.hashCode());
  }

  public int getAbnormalEffect() {
    return this._abnormalEffects;
  }

  public int getAbnormalEffect2() {
    return this._abnormalEffects2;
  }

  public int getAbnormalEffect3() {
    return this._abnormalEffects3;
  }

  public int getAccuracy() {
    return (int)this.calcStat(Stats.ACCURACY_COMBAT, 0.0D, (Creature)null, (Skill)null);
  }

  public Collection<Skill> getAllSkills() {
    return this._skills.values();
  }

  public final Skill[] getAllSkillsArray() {
    Collection<Skill> vals = this._skills.values();
    return (Skill[])vals.toArray(new Skill[vals.size()]);
  }

  public final double getAttackSpeedMultiplier() {
    return 1.1D * (double)this.getPAtkSpd() / (double)this.getTemplate().basePAtkSpd;
  }

  public int getBuffLimit() {
    return (int)this.calcStat(Stats.BUFF_LIMIT, (double)Config.ALT_BUFF_LIMIT, (Creature)null, (Skill)null);
  }

  public Skill getCastingSkill() {
    return this._castingSkill;
  }

  public int getCON() {
    return (int)this.calcStat(Stats.STAT_CON, (double)this._template.baseCON, (Creature)null, (Skill)null);
  }

  public int getCriticalHit(Creature target, Skill skill) {
    return (int)this.calcStat(Stats.CRITICAL_BASE, (double)this._template.baseCritRate, target, skill);
  }

  public double getMagicCriticalRate(Creature target, Skill skill) {
    return this.calcStat(Stats.MCRITICAL_RATE, target, skill);
  }

  public final double getCurrentCp() {
    return this._currentCp;
  }

  public final double getCurrentCpRatio() {
    return this.getCurrentCp() / (double)this.getMaxCp();
  }

  public final double getCurrentCpPercents() {
    return this.getCurrentCpRatio() * 100.0D;
  }

  public final boolean isCurrentCpFull() {
    return this.getCurrentCp() >= (double)this.getMaxCp();
  }

  public final boolean isCurrentCpZero() {
    return this.getCurrentCp() < 1.0D;
  }

  public final double getCurrentHp() {
    return this._currentHp;
  }

  public final double getCurrentHpRatio() {
    return this.getCurrentHp() / (double)this.getMaxHp();
  }

  public final double getCurrentHpPercents() {
    return this.getCurrentHpRatio() * 100.0D;
  }

  public final boolean isCurrentHpFull() {
    return this.getCurrentHp() >= (double)this.getMaxHp();
  }

  public final boolean isCurrentHpZero() {
    return this.getCurrentHp() < 1.0D;
  }

  public final double getCurrentMp() {
    return this._currentMp;
  }

  public final double getCurrentMpRatio() {
    return this.getCurrentMp() / (double)this.getMaxMp();
  }

  public final double getCurrentMpPercents() {
    return this.getCurrentMpRatio() * 100.0D;
  }

  public final boolean isCurrentMpFull() {
    return this.getCurrentMp() >= (double)this.getMaxMp();
  }

  public final boolean isCurrentMpZero() {
    return this.getCurrentMp() < 1.0D;
  }

  public int getDEX() {
    return (int)this.calcStat(Stats.STAT_DEX, (double)this._template.baseDEX, (Creature)null, (Skill)null);
  }

  public int getEvasionRate(Creature target) {
    return (int)this.calcStat(Stats.EVASION_RATE, 0.0D, target, (Skill)null);
  }

  public int getINT() {
    return (int)this.calcStat(Stats.STAT_INT, (double)this._template.baseINT, (Creature)null, (Skill)null);
  }

  public List<Creature> getAroundCharacters(int radius, int height) {
    return !this.isVisible() ? Collections.emptyList() : World.getAroundCharacters(this, radius, height);
  }

  public List<NpcInstance> getAroundNpc(int range, int height) {
    return !this.isVisible() ? Collections.emptyList() : World.getAroundNpc(this, range, height);
  }

  public boolean knowsObject(GameObject obj) {
    return World.getAroundObjectById(this, obj.getObjectId()) != null;
  }

  public final Skill getKnownSkill(int skillId) {
    return (Skill)this._skills.get(skillId);
  }

  public final int getMagicalAttackRange(Skill skill) {
    return skill != null ? (int)this.calcStat(Stats.MAGIC_ATTACK_RANGE, (double)skill.getCastRange(), (Creature)null, skill) : this.getTemplate().baseAtkRange;
  }

  public final int getMagicalAttackRange(double base, Skill skill) {
    return skill != null ? (int)this.calcStat(Stats.MAGIC_ATTACK_RANGE, base, (Creature)null, skill) : this.getTemplate().baseAtkRange;
  }

  public int getMAtk(Creature target, Skill skill) {
    return skill != null && skill.getMatak() > 0 ? skill.getMatak() : (int)this.calcStat(Stats.MAGIC_ATTACK, (double)this._template.baseMAtk, target, skill);
  }

  public int getMAtkSpd() {
    return (int)this.calcStat(Stats.MAGIC_ATTACK_SPEED, (double)this._template.baseMAtkSpd, (Creature)null, (Skill)null);
  }

  public final int getMaxCp() {
    return (int)this.calcStat(Stats.MAX_CP, this._template.baseCpMax, (Creature)null, (Skill)null);
  }

  public int getMaxHp() {
    return (int)this.calcStat(Stats.MAX_HP, this._template.baseHpMax, (Creature)null, (Skill)null);
  }

  public int getMaxMp() {
    return (int)this.calcStat(Stats.MAX_MP, this._template.baseMpMax, (Creature)null, (Skill)null);
  }

  public int getMDef(Creature target, Skill skill) {
    return Math.max((int)this.calcStat(Stats.MAGIC_DEFENCE, (double)this._template.baseMDef, target, skill), 1);
  }

  public int getMEN() {
    return (int)this.calcStat(Stats.STAT_MEN, (double)this._template.baseMEN, (Creature)null, (Skill)null);
  }

  public double getMinDistance(GameObject obj) {
    double distance = this.getTemplate().collisionRadius;
    if (obj != null && obj.isCreature()) {
      distance += ((Creature)obj).getTemplate().collisionRadius;
    }

    return distance;
  }

  public double getMovementSpeedMultiplier() {
    return (double)this.getRunSpeed() * 1.0D / (double)this._template.baseRunSpd;
  }

  public int getMoveSpeed() {
    return this.isRunning() ? this.getRunSpeed() : this.getWalkSpeed();
  }

  public String getName() {
    return StringUtils.defaultString(this._name);
  }

  public int getPAtk(Creature target) {
    return (int)this.calcStat(Stats.POWER_ATTACK, (double)this._template.basePAtk, target, (Skill)null);
  }

  public int getPAtkSpd() {
    return (int)this.calcStat(Stats.POWER_ATTACK_SPEED, (double)this._template.basePAtkSpd, (Creature)null, (Skill)null);
  }

  public int getPDef(Creature target) {
    return (int)this.calcStat(Stats.POWER_DEFENCE, (double)this._template.basePDef, target, (Skill)null);
  }

  public int getPhysicalAttackRange() {
    WeaponTemplate weaponItem = this.getActiveWeaponItem();
    return weaponItem == null ? (int)this.calcStat(Stats.POWER_ATTACK_RANGE, (double)this.getTemplate().baseAtkRange, (Creature)null, (Skill)null) : (int)this.calcStat(Stats.POWER_ATTACK_RANGE, (double)weaponItem.getAttackRange(), (Creature)null, (Skill)null);
  }

  /** @deprecated */
  @Deprecated
  public final int getRandomDamage() {
    WeaponTemplate weaponItem = this.getActiveWeaponItem();
    return weaponItem == null ? 5 + (int)Math.sqrt((double)this.getLevel()) : weaponItem.getRandomDamage();
  }

  public double getReuseModifier(Creature target) {
    return this.calcStat(Stats.ATK_REUSE, 1.0D, target, (Skill)null);
  }

  public int getRunSpeed() {
    return this.getSpeed(this._template.baseRunSpd);
  }

  public final int getShldDef() {
    return this.isPlayer() ? (int)this.calcStat(Stats.SHIELD_DEFENCE, 0.0D, (Creature)null, (Skill)null) : (int)this.calcStat(Stats.SHIELD_DEFENCE, (double)this._template.baseShldDef, (Creature)null, (Skill)null);
  }

  public final int getSkillDisplayLevel(Integer skillId) {
    Skill skill = (Skill)this._skills.get(skillId);
    return skill == null ? -1 : skill.getDisplayLevel();
  }

  public final int getSkillLevel(Integer skillId) {
    return this.getSkillLevel(skillId, -1);
  }

  public final int getSkillLevel(Integer skillId, int def) {
    Skill skill = (Skill)this._skills.get(skillId);
    return skill == null ? def : skill.getLevel();
  }

  public int getSkillMastery(Integer skillId) {
    if (this._skillMastery == null) {
      return 0;
    } else {
      Integer val = (Integer)this._skillMastery.get(skillId);
      return val == null ? 0 : val;
    }
  }

  public void removeSkillMastery(Integer skillId) {
    if (this._skillMastery != null) {
      this._skillMastery.remove(skillId);
    }

  }

  public int getSpeed(int baseSpeed) {
    return this.isInWater() ? this.getSwimSpeed() : (int)this.calcStat(Stats.RUN_SPEED, (double)baseSpeed, (Creature)null, (Skill)null);
  }

  public int getSTR() {
    return (int)this.calcStat(Stats.STAT_STR, (double)this._template.baseSTR, (Creature)null, (Skill)null);
  }

  public int getSwimSpeed() {
    return (int)this.calcStat(Stats.RUN_SPEED, (double)Config.SWIMING_SPEED, (Creature)null, (Skill)null);
  }

  public GameObject getTarget() {
    return (GameObject)this.target.get();
  }

  public final int getTargetId() {
    GameObject target = this.getTarget();
    return target == null ? -1 : target.getObjectId();
  }

  public CharTemplate getTemplate() {
    return this._template;
  }

  public CharTemplate getBaseTemplate() {
    return this._baseTemplate;
  }

  public String getTitle() {
    return StringUtils.defaultString(this._title);
  }

  public final int getWalkSpeed() {
    return this.isInWater() ? this.getSwimSpeed() : this.getSpeed(this._template.baseWalkSpd);
  }

  public int getWIT() {
    return (int)this.calcStat(Stats.STAT_WIT, (double)this._template.baseWIT, (Creature)null, (Skill)null);
  }

  public double headingToRadians(int heading) {
    return (double)(heading - '耀') / 10430.378350470453D;
  }

  public boolean isAlikeDead() {
    return this.isDead();
  }

  public final boolean isAttackingNow() {
    return this._attackEndTime > System.currentTimeMillis();
  }

  public final boolean isBlessedByNoblesse() {
    return this._isBlessedByNoblesse;
  }

  public final boolean isSalvation() {
    return this._isSalvation;
  }

  public boolean isEffectImmune() {
    return this._effectImmunity.get();
  }

  public boolean isBuffImmune() {
    return this._buffImmunity.get();
  }

  public boolean isDebuffImmune() {
    return this._debuffImmunity.get();
  }

  public boolean isDead() {
    return this._currentHp < 0.5D || this.isDead.get();
  }

  public final boolean isFlying() {
    return this._flying;
  }

  public final boolean isInCombat() {
    return System.currentTimeMillis() < this._stanceEndTime;
  }

  public boolean isInvul() {
    return this._isInvul;
  }

  public boolean isMageClass() {
    return this.getTemplate().baseMAtk > 3;
  }

  public final boolean isRunning() {
    return this._running;
  }

  public boolean isSkillDisabled(Skill skill) {
    TimeStamp sts = (TimeStamp)this._skillReuses.get(skill.hashCode());
    if (sts == null) {
      return false;
    } else if (sts.hasNotPassed()) {
      return true;
    } else {
      this._skillReuses.remove(skill.hashCode());
      return false;
    }
  }

  public final boolean isTeleporting() {
    return this.isTeleporting.get();
  }

  public Location getDestination() {
    return this.moveAction != null && this.moveAction instanceof Creature.MoveToLocationAction ? ((Creature.MoveToLocationAction)this.moveAction).moveTo().clone() : null;
  }

  public boolean isMoving() {
    Creature.MoveActionBase theMoveActionBase = this.moveAction;
    return theMoveActionBase != null && !theMoveActionBase.isFinished();
  }

  public boolean isFollowing() {
    Creature.MoveActionBase theMoveActionBase = this.moveAction;
    return theMoveActionBase != null && theMoveActionBase instanceof Creature.MoveToRelativeAction && !theMoveActionBase.isFinished();
  }

  public int maxZDiff() {
    Creature.MoveActionBase theMoveActionBase = this.moveAction;
    if (theMoveActionBase != null) {
      Location moveFrom = theMoveActionBase.moveFrom();
      Location moveTo = theMoveActionBase.moveTo();
      if (moveFrom.getZ() > moveTo.getZ()) {
        int maxZDiff = moveFrom.getZ() - moveTo.getZ();
        return maxZDiff;
      }
    }

    return Config.MAX_Z_DIFF;
  }

  public Creature getFollowTarget() {
    Creature.MoveActionBase moveAction = this.moveAction;
    if (moveAction != null && moveAction instanceof Creature.MoveToRelativeAction && !moveAction.isFinished()) {
      Creature.MoveToRelativeAction mtra = (Creature.MoveToRelativeAction)moveAction;
      GameObject target = mtra.getTarget();
      if (target != null && target instanceof Creature) {
        return (Creature)target;
      }
    }

    return null;
  }

  protected Creature.MoveActionBase createMoveToRelative(GameObject pawn, int indent, int range, boolean pathfinding) {
    return new Creature.MoveToRelativeAction(this, pawn, !Config.ALLOW_GEODATA, indent, range, pathfinding);
  }

  protected Creature.MoveActionBase createMoveToLocation(Location dest, int indent, boolean pathFind) {
    return new Creature.MoveToLocationAction(this, this.getLoc(), dest, this.isInBoat() || this.isBoat() || !Config.ALLOW_GEODATA, indent, pathFind);
  }

  public boolean moveToLocation(Location loc, int offset, boolean pathfinding) {
    return this.moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding);
  }

  public boolean moveToLocation(int toX, int toY, int toZ, int indent, boolean pathfinding) {
    this.moveLock.lock();

    boolean var8;
    try {
      indent = Math.max(indent, 0);
      Location worldTo = new Location(toX, toY, toZ);
      Creature.MoveActionBase prevMoveAction = this.moveAction;
      if (prevMoveAction == null || !(prevMoveAction instanceof Creature.MoveToLocationAction) || !((Creature.MoveToLocationAction)prevMoveAction).isSameDest(worldTo)) {
        if (this.isMovementDisabled()) {
          this.getAI().setNextAction(NextAction.MOVE, new Location(toX, toY, toZ), indent, pathfinding, false);
          this.sendActionFailed();
          var8 = false;
          return var8;
        }

        this.getAI().clearNextAction();
        if (this.isPlayer()) {
          Player player = this.getPlayer();
          this.getAI().changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, (Object)null, (Object)null);
          if (Config.ALT_TELEPORT_PROTECTION && this.isPlayer() && player.getAfterTeleportPortectionTime() > System.currentTimeMillis()) {
            player.setAfterTeleportPortectionTime(0L);
            player.sendMessage(new CustomMessage("alt.teleport_protect_gonna", player, new Object[0]));
          }
        }

        this.stopMove(false, false);
        Creature.MoveActionBase mtla = this.createMoveToLocation(worldTo, indent, pathfinding);
        this.moveAction = mtla;
        boolean var9;
        if (!mtla.start()) {
          this.moveAction = null;
          this.sendActionFailed();
          var9 = false;
          return var9;
        }

        mtla.scheduleNextTick();
        var9 = true;
        return var9;
      }

      this.sendActionFailed();
      var8 = false;
    } finally {
      this.moveLock.unlock();
    }

    return var8;
  }

  public boolean moveToRelative(GameObject pawn, int indent, int range) {
    return this.moveToRelative(pawn, indent, range, Config.ALLOW_PAWN_PATHFIND);
  }

  public boolean moveToRelative(GameObject pawn, int indent, int range, boolean pathfinding) {
    this.moveLock.lock();

    boolean var7;
    try {
      if (this.isMovementDisabled() || pawn == null || this.isInBoat()) {
        boolean var11 = false;
        return var11;
      }

      Creature.MoveActionBase prevMoveAction = this.moveAction;
      if (prevMoveAction != null && prevMoveAction instanceof Creature.MoveToRelativeAction && !prevMoveAction.isFinished() && ((Creature.MoveToRelativeAction)prevMoveAction).isSameTarget(pawn)) {
        this.sendActionFailed();
        boolean var13 = false;
        return var13;
      }

      range = Math.max(range, 10);
      indent = Math.min(indent, range);
      this.getAI().clearNextAction();
      if (this.isPlayer()) {
        Player player = this.getPlayer();
        if (Config.ALT_TELEPORT_PROTECTION && this.isPlayer() && player.getAfterTeleportPortectionTime() > System.currentTimeMillis()) {
          player.setAfterTeleportPortectionTime(0L);
          player.sendMessage(new CustomMessage("alt.teleport_protect_gonna", player, new Object[0]));
        }
      }

      this.stopMove(false, false);
      Creature.MoveActionBase mtra = this.createMoveToRelative(pawn, indent, range, pathfinding);
      this.moveAction = mtra;
      if (mtra.start()) {
        mtra.scheduleNextTick();
        var7 = true;
        return var7;
      }

      this.moveAction = null;
      this.sendActionFailed();
      var7 = false;
    } finally {
      this.moveLock.unlock();
    }

    return var7;
  }

  private void broadcastMove() {
    this.validateLocation(this.isPlayer() ? 2 : 1);
    this.broadcastPacket(this.movePacket());
  }

  public void stopMove() {
    this.stopMove(true, true);
  }

  public void stopMove(boolean validate) {
    this.stopMove(true, validate);
  }

  public void stopMove(boolean stop, boolean validate) {
    this.stopMove(stop, validate, true);
  }

  public void stopMove(boolean stop, boolean validate, boolean action) {
    if (this.isMoving()) {
      this.moveLock.lock();

      try {
        if (this.isMoving()) {
          if (action && this.moveAction != null && !this.moveAction.isFinished()) {
            this.moveAction.interrupt();
            this.moveAction = null;
          }

          if (this._moveTask != null) {
            this._moveTask.cancel(false);
            this._moveTask = null;
          }

          if (validate) {
            this.validateLocation(this.isPlayer() ? 2 : 1);
          }

          if (stop) {
            this.broadcastPacket(this.stopMovePacket());
          }

          return;
        }
      } finally {
        this.moveLock.unlock();
      }

    }
  }

  public int getWaterZ() {
    if (!this.isInWater()) {
      return -2147483648;
    } else {
      int waterZ = -2147483648;
      Zone[] zones = (Zone[])this._zonesRef.get();
      Zone[] var3 = zones;
      int var4 = zones.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        Zone zone = var3[var5];
        if (zone.getType() == ZoneType.water && (waterZ == -2147483648 || waterZ < zone.getTerritory().getZmax())) {
          waterZ = zone.getTerritory().getZmax();
        }
      }

      return waterZ;
    }
  }

  protected L2GameServerPacket stopMovePacket() {
    return new StopMove(this);
  }

  public L2GameServerPacket movePacket() {
    Creature.MoveActionBase moveAction = this.moveAction;
    return (L2GameServerPacket)(moveAction != null ? moveAction.movePacket() : new CharMoveToLocation(this));
  }

  public boolean updateZones() {
    if (this.isInObserverMode()) {
      return false;
    } else {
      Zone[] regionZones = this.isVisible() ? this.getCurrentRegion().getZones() : Zone.EMPTY_L2ZONE_ARRAY;
      Zone[] currZones = (Zone[])this._zonesRef.get();
      Zone[] newZones = currZones;
      Zone zone;
      int newZoneIdx;
      if (currZones.length > 0) {
        for(newZoneIdx = 0; newZoneIdx < currZones.length; ++newZoneIdx) {
          zone = currZones[newZoneIdx];
          if (!ArrayUtils.contains(regionZones, zone) || !zone.checkIfInZone(this.getX(), this.getY(), this.getZ(), this.getReflection())) {
            newZones = (Zone[])ArrayUtils.removeElement(newZones, zone);
          }
        }
      }

      if (regionZones.length > 0) {
        for(newZoneIdx = 0; newZoneIdx < regionZones.length; ++newZoneIdx) {
          zone = regionZones[newZoneIdx];
          if (!ArrayUtils.contains(currZones, zone) && zone.checkIfInZone(this.getX(), this.getY(), this.getZ(), this.getReflection())) {
            newZones = (Zone[])ArrayUtils.add(newZones, zone);
          }
        }
      }

      if (currZones != newZones && this._zonesRef.compareAndSet(currZones, newZones)) {
        for(newZoneIdx = 0; newZoneIdx < currZones.length; ++newZoneIdx) {
          zone = currZones[newZoneIdx];
          if (!ArrayUtils.contains(newZones, zone)) {
            zone.doLeave(this);
          }
        }

        for(newZoneIdx = 0; newZoneIdx < newZones.length; ++newZoneIdx) {
          zone = newZones[newZoneIdx];
          if (!ArrayUtils.contains(currZones, zone)) {
            zone.doEnter(this);
          }
        }

        return true;
      } else {
        return false;
      }
    }
  }

  public boolean isInZonePeace() {
    return this.isInZone(ZoneType.peace_zone) && !this.isInZoneBattle();
  }

  public boolean isInZoneBattle() {
    return this.isInZone(ZoneType.battle_zone);
  }

  public boolean isInWater() {
    return this.isInZone(ZoneType.water) && !this.isInBoat() && !this.isBoat() && !this.isFlying();
  }

  public boolean isInZone(ZoneType type) {
    Zone[] zones = (Zone[])this._zonesRef.get();
    Zone[] var3 = zones;
    int var4 = zones.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      Zone zone = var3[var5];
      if (zone.getType() == type) {
        return true;
      }
    }

    return false;
  }

  public boolean isInZone(String name) {
    Zone[] zones = (Zone[])this._zonesRef.get();
    Zone[] var3 = zones;
    int var4 = zones.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      Zone zone = var3[var5];
      if (zone.getName().equals(name)) {
        return true;
      }
    }

    return false;
  }

  public boolean isInZone(Zone zone) {
    Zone[] zones = (Zone[])this._zonesRef.get();
    return ArrayUtils.contains(zones, zone);
  }

  public Zone getZone(ZoneType type) {
    Zone[] zones = (Zone[])this._zonesRef.get();
    Zone[] var3 = zones;
    int var4 = zones.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      Zone zone = var3[var5];
      if (zone.getType() == type) {
        return zone;
      }
    }

    return null;
  }

  public Location getRestartPoint() {
    Zone[] zones = (Zone[])this._zonesRef.get();
    Zone[] var2 = zones;
    int var3 = zones.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Zone zone = var2[var4];
      if (zone.getRestartPoints() != null) {
        ZoneType type = zone.getType();
        if (type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy) {
          return zone.getSpawn();
        }
      }
    }

    return null;
  }

  public Location getPKRestartPoint() {
    Zone[] zones = (Zone[])this._zonesRef.get();
    Zone[] var2 = zones;
    int var3 = zones.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Zone zone = var2[var4];
      if (zone.getRestartPoints() != null) {
        ZoneType type = zone.getType();
        if (type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy) {
          return zone.getPKSpawn();
        }
      }
    }

    return null;
  }

  public int getGeoZ(Location loc) {
    return !this.isFlying() && !this.isInWater() && !this.isInBoat() && !this.isBoat() && !this.isDoor() ? super.getGeoZ(loc) : loc.z;
  }

  protected boolean needStatusUpdate() {
    if (!this.isVisible()) {
      return false;
    } else {
      boolean result = false;
      int bar = (int)(this.getCurrentHp() * 352.0D / (double)this.getMaxHp());
      if (bar == 0 || bar != this._lastHpBarUpdate) {
        this._lastHpBarUpdate = bar;
        result = true;
      }

      bar = (int)(this.getCurrentMp() * 352.0D / (double)this.getMaxMp());
      if (bar == 0 || bar != this._lastMpBarUpdate) {
        this._lastMpBarUpdate = bar;
        result = true;
      }

      if (this.isPlayer()) {
        bar = (int)(this.getCurrentCp() * 352.0D / (double)this.getMaxCp());
        if (bar == 0 || bar != this._lastCpBarUpdate) {
          this._lastCpBarUpdate = bar;
          result = true;
        }
      }

      return result;
    }
  }

  public void onForcedAttack(Player player, boolean shift) {
    player.sendPacket(new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()));
    if (this.isAttackable(player) && !player.isConfused() && !player.isBlocked()) {
      player.getAI().Attack(this, true, shift);
    } else {
      player.sendActionFailed();
    }
  }

  public void onHitTimer(Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS) {
    if (this.isAlikeDead()) {
      this.sendActionFailed();
    } else if (!target.isDead() && this.isInRange(target, 2000L)) {
      if (this.isPlayable() && target.isPlayable() && this.isInZoneBattle() != target.isInZoneBattle()) {
        Player player = this.getPlayer();
        if (player != null) {
          player.sendPacket(Msg.INVALID_TARGET);
          player.sendActionFailed();
        }

      } else {
        target.getListeners().onAttackHit(this);
        if (!miss && target.isPlayer() && (this.isCursedWeaponEquipped() || this.getActiveWeaponInstance() != null && this.getActiveWeaponInstance().isHeroWeapon() && target.isCursedWeaponEquipped())) {
          target.setCurrentCp(0.0D);
        }

        if (target.isStunned() && Formulas.calcStunBreak(crit)) {
          target.getEffectList().stopEffects(EffectType.Stun);
        }

        this.displayGiveDamageMessage(target, damage, crit, miss, shld, false);
        ThreadPoolManager.getInstance().execute(new NotifyAITask(target, CtrlEvent.EVT_ATTACKED, this, damage));
        boolean checkPvP = this.checkPvP(target, (Skill)null);
        if (!miss && damage > 0) {
          target.reduceCurrentHp((double)damage, this, (Skill)null, true, true, false, true, false, false, true);
          if (!target.isDead()) {
            if (crit) {
              this.useTriggers(target, TriggerType.CRIT, (Skill)null, (Skill)null, (double)damage);
            }

            this.useTriggers(target, TriggerType.ATTACK, (Skill)null, (Skill)null, (double)damage);
            if (Formulas.calcCastBreak(target, crit)) {
              target.abortCast(false, true);
            }
          }

          if (soulshot && unchargeSS) {
            this.unChargeShots(false);
          }
        }

        if (miss) {
          target.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, (Skill)null, (Skill)null, (double)damage);
        }

        this.startAttackStanceTask();
        if (checkPvP) {
          this.startPvPFlag(target);
        }

      }
    } else {
      this.sendActionFailed();
    }
  }

  public void onMagicUseTimer(Creature aimingTarget, Skill skill, boolean forceUse) {
    this._castInterruptTime = 0L;
    if (skill.isUsingWhileCasting()) {
      aimingTarget.getEffectList().stopEffect(skill.getId());
      this.onCastEndTime();
    } else {
      if (!skill.isOffensive() && this.getAggressionTarget() != null) {
        forceUse = true;
      }

      if (!skill.checkCondition(this, aimingTarget, forceUse, false, false)) {
        if (skill.getSkillType() == SkillType.PET_SUMMON && this.isPlayer()) {
          this.getPlayer().setPetControlItem((ItemInstance)null);
        }

        this.onCastEndTime();
      } else {
        List<Creature> targets = skill.getTargets(this, aimingTarget, forceUse);
        int hpConsume = skill.getHpConsume();
        if (hpConsume > 0) {
          this.setCurrentHp(Math.max(0.0D, this._currentHp - (double)hpConsume), false);
        }

        double mpConsume2 = skill.getMpConsume2();
        if (mpConsume2 > 0.0D) {
          if (skill.isMusic()) {
            mpConsume2 += (double)this.getEffectList().getActiveMusicCount(skill.getId()) * mpConsume2 / 2.0D;
            mpConsume2 = this.calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
          } else if (skill.isMagic()) {
            mpConsume2 = this.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
          } else {
            mpConsume2 = this.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
          }

          if (this._currentMp < mpConsume2 && this.isPlayable()) {
            this.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
            this.onCastEndTime();
            return;
          }

          this.reduceCurrentMp(mpConsume2, (Creature)null);
        }

        this.callSkill(skill, targets, true);
        if (skill.getNumCharges() > 0) {
          this.setIncreasedForce(this.getIncreasedForce() - skill.getNumCharges());
        }

        if (skill.isSoulBoost()) {
          this.setConsumedSouls(this.getConsumedSouls() - Math.min(this.getConsumedSouls(), 5), (NpcInstance)null);
        } else if (skill.getSoulsConsume() > 0) {
          this.setConsumedSouls(this.getConsumedSouls() - skill.getSoulsConsume(), (NpcInstance)null);
        }

        switch(skill.getFlyType()) {
          case THROW_UP:
          case THROW_HORIZONTAL:
            Iterator var9 = targets.iterator();

            while(var9.hasNext()) {
              Creature target = (Creature)var9.next();
              Location flyLoc = this.getFlyLocation((GameObject)null, skill);
              target.setLoc(flyLoc);
              this.broadcastPacket(new FlyToLocation(target, flyLoc, skill.getFlyType()));
            }
          default:
            int skillCoolTime = Formulas.calcMAtkSpd(this, skill, (double)skill.getCoolTime());
            if (skillCoolTime > 0) {
              ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(this), (long)skillCoolTime);
            } else {
              this.onCastEndTime();
            }

        }
      }
    }
  }

  public void onCastEndTime() {
    this.finishFly();
    Skill cs = this.getCastingSkill();
    Creature ct = this.getCastingTarget();
    this.clearCastVars();
    this.getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING, cs, ct);
  }

  private void finishFly() {
    Location flyLoc = this._flyLoc;
    this._flyLoc = null;
    if (flyLoc != null) {
      this.setLoc(flyLoc);
      this.validateLocation(1);
    }

  }

  public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
    if (attacker != null && !this.isDead() && (!attacker.isDead() || isDot)) {
      if (!this.isDamageBlocked() || !transferDamage) {
        if (this.isDamageBlocked() && attacker != this) {
          if (sendMessage) {
            attacker.sendPacket((IStaticPacket)Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
          }

        } else {
          if (canReflect) {
            if (attacker.absorbAndReflect(this, skill, damage, sendMessage)) {
              return;
            }

            damage = this.absorbToEffector(attacker, damage);
            damage = this.absorbToSummon(attacker, damage);
          }

          this.getListeners().onCurrentHpDamage(damage, attacker, skill);
          if (attacker != this) {
            if (sendMessage) {
              this.displayReceiveDamageMessage(attacker, (int)damage);
            }

            if (!isDot) {
              this.useTriggers(attacker, TriggerType.RECEIVE_DAMAGE, (Skill)null, (Skill)null, damage);
            }
          }

          this.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
        }
      }
    }
  }

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    if (awake && this.isSleeping()) {
      this.getEffectList().stopEffects(EffectType.Sleep);
    }

    if (attacker != this || skill != null && skill.isOffensive()) {
      if (this.isMeditated()) {
        Effect effect = this.getEffectList().getEffectByType(EffectType.Meditation);
        if (effect != null) {
          this.getEffectList().stopEffect(effect.getSkill());
        }
      }

      this.startAttackStanceTask();
      this.checkAndRemoveInvisible();
      if (this.getCurrentHp() - damage < 0.5D) {
        this.useTriggers(attacker, TriggerType.DIE, (Skill)null, (Skill)null, damage);
      }
    }

    if (!this.isPlayer() || !this.getPlayer().isGM() || !this.getPlayer().isUndying() || damage + 0.5D < this.getCurrentHp()) {
      this.setCurrentHp(Math.max(this.getCurrentHp() - damage, 0.0D), false);
      if (this.getCurrentHp() < 0.5D) {
        this.doDie(attacker);
      }

    }
  }

  public void reduceCurrentMp(double i, Creature attacker) {
    this.reduceCurrentMp(i, attacker, false);
  }

  public void reduceCurrentMp(double i, Creature attacker, boolean sendMessage) {
    if (attacker != null && attacker != this) {
      if (this.isSleeping()) {
        this.getEffectList().stopEffects(EffectType.Sleep);
      }

      if (this.isMeditated()) {
        Effect effect = this.getEffectList().getEffectByType(EffectType.Meditation);
        if (effect != null) {
          this.getEffectList().stopEffect(effect.getSkill());
        }
      }
    }

    if (this.isDamageBlocked() && attacker != null && attacker != this) {
      attacker.sendPacket((IStaticPacket)Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
    } else {
      if (attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - this.getLevel()) > 10) {
        if (attacker.getKarma() > 0 && this.getEffectList().getEffectsBySkillId(5182) != null && !this.isInZone(ZoneType.SIEGE)) {
          return;
        }

        if (this.getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.SIEGE)) {
          return;
        }
      }

      this.getListeners().onCurrentMpReduce(i, attacker);
      if (sendMessage) {
        int msgMp = (int)Math.min(this._currentMp, i);
        this.sendPacket((IStaticPacket)(new SystemMessage(1866)).addNumber(msgMp));
        if (attacker != null && attacker.isPlayer()) {
          attacker.sendPacket((IStaticPacket)(new SystemMessage(1867)).addNumber(msgMp));
        }
      }

      i = Math.max(0.0D, this._currentMp - i);
      this.setCurrentMp(i);
      if (attacker != null && attacker != this) {
        this.startAttackStanceTask();
      }

    }
  }

  public double relativeSpeed(GameObject target) {
    return (double)this.getMoveSpeed() - (double)target.getMoveSpeed() * Math.cos(this.headingToRadians(this.getHeading()) - this.headingToRadians(target.getHeading()));
  }

  public void removeAllSkills() {
    Skill[] var1 = this.getAllSkillsArray();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Skill s = var1[var3];
      this.removeSkill(s);
    }

  }

  public void removeBlockStats(List<Stats> stats) {
    if (this._blockedStats != null) {
      this._blockedStats.removeAll(stats);
      if (this._blockedStats.isEmpty()) {
        this._blockedStats = null;
      }
    }

  }

  public Skill removeSkill(Skill skill) {
    return skill == null ? null : this.removeSkillById(skill.getId());
  }

  public Skill removeSkillById(Integer id) {
    Skill oldSkill = (Skill)this._skills.remove(id);
    if (oldSkill != null) {
      this.removeTriggers(oldSkill);
      this.removeStatsOwner(oldSkill);
      if (Config.ALT_DELETE_SA_BUFFS && (oldSkill.isItemSkill() || oldSkill.isHandler())) {
        List<Effect> effects = this.getEffectList().getEffectsBySkill(oldSkill);
        if (effects != null) {
          Iterator var4 = effects.iterator();

          while(var4.hasNext()) {
            Effect effect = (Effect)var4.next();
            effect.exit();
          }
        }

        Summon pet = this.getPet();
        if (pet != null) {
          effects = pet.getEffectList().getEffectsBySkill(oldSkill);
          if (effects != null) {
            Iterator var8 = effects.iterator();

            while(var8.hasNext()) {
              Effect effect = (Effect)var8.next();
              effect.exit();
            }
          }
        }
      }
    }

    return oldSkill;
  }

  public void addTriggers(StatTemplate f) {
    if (!f.getTriggerList().isEmpty()) {
      Iterator var2 = f.getTriggerList().iterator();

      while(var2.hasNext()) {
        TriggerInfo t = (TriggerInfo)var2.next();
        this.addTrigger(t);
      }

    }
  }

  public void addTrigger(TriggerInfo t) {
    if (this._triggers == null) {
      this._triggers = new ConcurrentHashMap();
    }

    Set<TriggerInfo> hs = (Set)this._triggers.get(t.getType());
    if (hs == null) {
      hs = new CopyOnWriteArraySet();
      this._triggers.put(t.getType(), hs);
    }

    ((Set)hs).add(t);
    if (t.getType() == TriggerType.ADD) {
      this.useTriggerSkill(this, (List)null, t, (Skill)null, 0.0D);
    }

  }

  public void removeTriggers(StatTemplate f) {
    if (this._triggers != null && !f.getTriggerList().isEmpty()) {
      Iterator var2 = f.getTriggerList().iterator();

      while(var2.hasNext()) {
        TriggerInfo t = (TriggerInfo)var2.next();
        this.removeTrigger(t);
      }

    }
  }

  public void removeTrigger(TriggerInfo t) {
    if (this._triggers != null) {
      Set<TriggerInfo> hs = (Set)this._triggers.get(t.getType());
      if (hs != null) {
        hs.remove(t);
      }
    }
  }

  public void sendActionFailed() {
    this.sendPacket((IStaticPacket)ActionFail.STATIC);
  }

  public boolean hasAI() {
    return this._ai != null;
  }

  public CharacterAI getAI() {
    if (this._ai == null) {
      synchronized(this) {
        if (this._ai == null) {
          this._ai = new CharacterAI(this);
        }
      }
    }

    return this._ai;
  }

  public void setAI(CharacterAI newAI) {
    if (newAI != null) {
      CharacterAI oldAI = this._ai;
      synchronized(this) {
        this._ai = newAI;
      }

      if (oldAI != null && oldAI.isActive()) {
        oldAI.stopAITask();
        newAI.startAITask();
        newAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      }

    }
  }

  public final void setCurrentHp(double newHp, boolean canRessurect, boolean sendInfo) {
    int maxHp = this.getMaxHp();
    newHp = Math.min((double)maxHp, Math.max(0.0D, newHp));
    if (this._currentHp != newHp) {
      if (newHp < 0.5D || !this.isDead() || canRessurect) {
        double hpStart = this._currentHp;
        this._currentHp = newHp;
        if (this.isDead.compareAndSet(true, false)) {
          this.onRevive();
        }

        this.checkHpMessages(hpStart, this._currentHp);
        if (sendInfo) {
          this.broadcastStatusUpdate();
          this.sendChanges();
        }

        if (this._currentHp < (double)maxHp) {
          this.startRegeneration();
        }

      }
    }
  }

  public final void setCurrentHp(double newHp, boolean canRessurect) {
    this.setCurrentHp(newHp, canRessurect, true);
  }

  public final void setCurrentMp(double newMp, boolean sendInfo) {
    int maxMp = this.getMaxMp();
    newMp = Math.min((double)maxMp, Math.max(0.0D, newMp));
    if (this._currentMp != newMp) {
      if (newMp < 0.5D || !this.isDead()) {
        this._currentMp = newMp;
        if (sendInfo) {
          this.broadcastStatusUpdate();
          this.sendChanges();
        }

        if (this._currentMp < (double)maxMp) {
          this.startRegeneration();
        }

      }
    }
  }

  public final void setCurrentMp(double newMp) {
    this.setCurrentMp(newMp, true);
  }

  public final void setCurrentCp(double newCp, boolean sendInfo) {
    if (this.isPlayer()) {
      int maxCp = this.getMaxCp();
      newCp = Math.min((double)maxCp, Math.max(0.0D, newCp));
      if (this._currentCp != newCp) {
        if (newCp < 0.5D || !this.isDead()) {
          this._currentCp = newCp;
          if (sendInfo) {
            this.broadcastStatusUpdate();
            this.sendChanges();
          }

          if (this._currentCp < (double)maxCp) {
            this.startRegeneration();
          }

        }
      }
    }
  }

  public final void setCurrentCp(double newCp) {
    this.setCurrentCp(newCp, true);
  }

  public void setCurrentHpMp(double newHp, double newMp, boolean canRessurect) {
    int maxHp = this.getMaxHp();
    int maxMp = this.getMaxMp();
    newHp = Math.min((double)maxHp, Math.max(0.0D, newHp));
    newMp = Math.min((double)maxMp, Math.max(0.0D, newMp));
    if (this._currentHp != newHp || this._currentMp != newMp) {
      if (newHp < 0.5D || !this.isDead() || canRessurect) {
        double hpStart = this._currentHp;
        this._currentHp = newHp;
        this._currentMp = newMp;
        if (this.isDead.compareAndSet(true, false)) {
          this.onRevive();
        }

        this.checkHpMessages(hpStart, this._currentHp);
        this.broadcastStatusUpdate();
        this.sendChanges();
        if (this._currentHp < (double)maxHp || this._currentMp < (double)maxMp) {
          this.startRegeneration();
        }

      }
    }
  }

  public void setCurrentHpMp(double newHp, double newMp) {
    this.setCurrentHpMp(newHp, newMp, false);
  }

  public final void setFlying(boolean mode) {
    this._flying = mode;
  }

  public final int getHeading() {
    return this._heading;
  }

  public void setHeading(int heading) {
    this._heading = heading;
  }

  public final void setIsTeleporting(boolean value) {
    this.isTeleporting.compareAndSet(!value, value);
  }

  public final void setName(String name) {
    this._name = name;
  }

  public Creature getCastingTarget() {
    return (Creature)this.castingTarget.get();
  }

  public void setCastingTarget(Creature target) {
    if (target == null) {
      this.castingTarget = HardReferences.emptyRef();
    } else {
      this.castingTarget = target.getRef();
    }

  }

  public final void setRunning() {
    if (!this._running) {
      this._running = true;
      this.broadcastPacket(new ChangeMoveType(this));
    }

  }

  public void setSkillMastery(Integer skill, int mastery) {
    if (this._skillMastery == null) {
      this._skillMastery = new HashMap();
    }

    this._skillMastery.put(skill, mastery);
  }

  public void setAggressionTarget(Creature target) {
    if (target == null) {
      this._aggressionTarget = HardReferences.emptyRef();
    } else {
      this._aggressionTarget = target.getRef();
    }

  }

  public Creature getAggressionTarget() {
    return (Creature)this._aggressionTarget.get();
  }

  public void setTarget(GameObject object) {
    if (object != null && !object.isVisible()) {
      object = null;
    }

    if (object == null) {
      this.target = HardReferences.emptyRef();
    } else {
      this.target = object.getRef();
    }

  }

  public void setTitle(String title) {
    this._title = title;
  }

  public void setWalking() {
    if (this._running) {
      this._running = false;
      this.broadcastPacket(new ChangeMoveType(this));
    }

  }

  public void startAbnormalEffect(AbnormalEffect ae) {
    if (ae == AbnormalEffect.NULL) {
      this._abnormalEffects = AbnormalEffect.NULL.getMask();
      this._abnormalEffects2 = AbnormalEffect.NULL.getMask();
      this._abnormalEffects3 = AbnormalEffect.NULL.getMask();
    } else if (ae.isSpecial()) {
      this._abnormalEffects2 |= ae.getMask();
    } else if (ae.isEvent()) {
      this._abnormalEffects3 |= ae.getMask();
    } else {
      this._abnormalEffects |= ae.getMask();
    }

    this.sendChanges();
  }

  public void startAttackStanceTask() {
    this.startAttackStanceTask0();
  }

  protected void startAttackStanceTask0() {
    if (this.isInCombat()) {
      this._stanceEndTime = System.currentTimeMillis() + 15000L;
    } else {
      this._stanceEndTime = System.currentTimeMillis() + 15000L;
      this.broadcastPacket(new AutoAttackStart(this.getObjectId()));
      Future<?> task = this._stanceTask;
      if (task != null) {
        task.cancel(false);
      }

      this._stanceTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(this._stanceTaskRunnable == null ? (this._stanceTaskRunnable = new Creature.AttackStanceTask((SyntheticClass_1)null)) : this._stanceTaskRunnable, 1000L, 1000L);
    }
  }

  public void stopAttackStanceTask() {
    this._stanceEndTime = 0L;
    Future<?> task = this._stanceTask;
    if (task != null) {
      task.cancel(false);
      this._stanceTask = null;
      this.broadcastPacket(new AutoAttackStop(this.getObjectId()));
    }

  }

  protected void stopRegeneration() {
    this.regenLock.lock();

    try {
      if (this._isRegenerating) {
        this._isRegenerating = false;
        if (this._regenTask != null) {
          this._regenTask.cancel(false);
          this._regenTask = null;
        }
      }
    } finally {
      this.regenLock.unlock();
    }

  }

  protected void startRegeneration() {
    if (this.isVisible() && !this.isDead() && this.getRegenTick() != 0L) {
      if (!this._isRegenerating) {
        this.regenLock.lock();

        try {
          if (!this._isRegenerating) {
            this._isRegenerating = true;
            this._regenTask = RegenTaskManager.getInstance().scheduleAtFixedRate(this._regenTaskRunnable == null ? (this._regenTaskRunnable = new Creature.RegenTask((SyntheticClass_1)null)) : this._regenTaskRunnable, 0L, this.getRegenTick());
          }
        } finally {
          this.regenLock.unlock();
        }

      }
    }
  }

  public long getRegenTick() {
    return 3000L;
  }

  public void stopAbnormalEffect(AbnormalEffect ae) {
    if (ae.isSpecial()) {
      this._abnormalEffects2 &= ~ae.getMask();
    }

    if (ae.isEvent()) {
      this._abnormalEffects3 &= ~ae.getMask();
    } else {
      this._abnormalEffects &= ~ae.getMask();
    }

    this.sendChanges();
  }

  public void block() {
    this._blocked = true;
  }

  public void unblock() {
    this._blocked = false;
  }

  public boolean startConfused() {
    return this._confused.getAndSet(true);
  }

  public boolean stopConfused() {
    return this._confused.setAndGet(false);
  }

  public boolean startFear() {
    return this._afraid.getAndSet(true);
  }

  public boolean stopFear() {
    return this._afraid.setAndGet(false);
  }

  public boolean startMuted() {
    return this._muted.getAndSet(true);
  }

  public boolean stopMuted() {
    return this._muted.setAndGet(false);
  }

  public boolean startPMuted() {
    return this._pmuted.getAndSet(true);
  }

  public boolean stopPMuted() {
    return this._pmuted.setAndGet(false);
  }

  public boolean startAMuted() {
    return this._amuted.getAndSet(true);
  }

  public boolean stopAMuted() {
    return this._amuted.setAndGet(false);
  }

  public boolean startRooted() {
    return this._rooted.getAndSet(true);
  }

  public boolean stopRooted() {
    return this._rooted.setAndGet(false);
  }

  public boolean startSleeping() {
    return this._sleeping.getAndSet(true);
  }

  public boolean stopSleeping() {
    return this._sleeping.setAndGet(false);
  }

  public boolean startStunning() {
    return this._stunned.getAndSet(true);
  }

  public boolean stopStunning() {
    return this._stunned.setAndGet(false);
  }

  public boolean startParalyzed() {
    return this._paralyzed.getAndSet(true);
  }

  public boolean stopParalyzed() {
    return this._paralyzed.setAndGet(false);
  }

  public boolean startImmobilized() {
    return this._immobilized.getAndSet(true);
  }

  public boolean stopImmobilized() {
    return this._immobilized.setAndGet(false);
  }

  public boolean startHealBlocked() {
    return this._healBlocked.getAndSet(true);
  }

  public boolean stopHealBlocked() {
    return this._healBlocked.setAndGet(false);
  }

  public boolean startDamageBlocked() {
    return this._damageBlocked.getAndSet(true);
  }

  public boolean stopDamageBlocked() {
    return this._damageBlocked.setAndGet(false);
  }

  public boolean startBuffImmunity() {
    return this._buffImmunity.getAndSet(true);
  }

  public boolean stopBuffImmunity() {
    return this._buffImmunity.setAndGet(false);
  }

  public boolean startDebuffImmunity() {
    return this._debuffImmunity.getAndSet(true);
  }

  public boolean stopDebuffImmunity() {
    return this._debuffImmunity.setAndGet(false);
  }

  public boolean startEffectImmunity() {
    return this._effectImmunity.getAndSet(true);
  }

  public boolean stopEffectImmunity() {
    return this._effectImmunity.setAndGet(false);
  }

  public boolean startWeaponEquipBlocked() {
    return this._weaponEquipBlocked.getAndSet(true);
  }

  public boolean stopWeaponEquipBlocked() {
    return this._weaponEquipBlocked.getAndSet(false);
  }

  public boolean startFrozen() {
    return this._frozen.getAndSet(true);
  }

  public boolean stopFrozen() {
    return this._frozen.setAndGet(false);
  }

  public void setMeditated(boolean value) {
    this._meditated = value;
  }

  public final void setIsBlessedByNoblesse(boolean value) {
    this._isBlessedByNoblesse = value;
  }

  public final void setIsSalvation(boolean value) {
    this._isSalvation = value;
  }

  public void setIsInvul(boolean value) {
    this._isInvul = value;
  }

  public void setLockedTarget(boolean value) {
    this._lockedTarget = value;
  }

  public boolean isConfused() {
    return this._confused.get();
  }

  public boolean isAfraid() {
    return this._afraid.get();
  }

  public boolean isBlocked() {
    return this._blocked;
  }

  public boolean isMuted(Skill skill) {
    if (skill != null && !skill.isNotAffectedByMute()) {
      return this.isMMuted() && skill.isMagic() || this.isPMuted() && !skill.isMagic();
    } else {
      return false;
    }
  }

  public boolean isPMuted() {
    return this._pmuted.get();
  }

  public boolean isMMuted() {
    return this._muted.get();
  }

  public boolean isAMuted() {
    return this._amuted.get();
  }

  public boolean isRooted() {
    return this._rooted.get();
  }

  public boolean isSleeping() {
    return this._sleeping.get();
  }

  public boolean isStunned() {
    return this._stunned.get();
  }

  public boolean isMeditated() {
    return this._meditated;
  }

  public boolean isWeaponEquipBlocked() {
    return this._weaponEquipBlocked.get();
  }

  public boolean isParalyzed() {
    return this._paralyzed.get();
  }

  public boolean isFrozen() {
    return this._frozen.get();
  }

  public boolean isImmobilized() {
    return this._immobilized.get() || this.getRunSpeed() < 1;
  }

  public boolean isHealBlocked() {
    return this.isAlikeDead() || this._healBlocked.get();
  }

  public boolean isDamageBlocked() {
    return this.isInvul() || this._damageBlocked.get();
  }

  public boolean isCastingNow() {
    return this._skillTask != null;
  }

  public boolean isLockedTarget() {
    return this._lockedTarget;
  }

  public boolean isMovementDisabled() {
    return this.isBlocked() || this.isRooted() || this.isImmobilized() || this.isAlikeDead() || this.isStunned() || this.isSleeping() || this.isParalyzed() || this.isAttackingNow() || this.isCastingNow() || this.isFrozen();
  }

  public boolean isActionsDisabled() {
    return this.isBlocked() || this.isAlikeDead() || this.isStunned() || this.isSleeping() || this.isParalyzed() || this.isAttackingNow() || this.isCastingNow() || this.isFrozen();
  }

  public boolean isPotionsDisabled() {
    return this.isActionsDisabled() || this.isStunned() || this.isSleeping() || this.isParalyzed() || this.isAlikeDead() || this.isAfraid();
  }

  public final boolean isAttackingDisabled() {
    return this._attackReuseEndTime > System.currentTimeMillis();
  }

  public boolean isOutOfControl() {
    return this.isBlocked() || this.isConfused() || this.isAfraid() || this.isFrozen();
  }

  public void teleToLocation(Location loc) {
    this.teleToLocation(loc.x, loc.y, loc.z, this.getReflection());
  }

  public void teleToLocation(Location loc, int refId) {
    this.teleToLocation(loc.x, loc.y, loc.z, refId);
  }

  public void teleToLocation(Location loc, Reflection r) {
    this.teleToLocation(loc.x, loc.y, loc.z, r);
  }

  public void teleToLocation(int x, int y, int z) {
    this.teleToLocation(x, y, z, this.getReflection());
  }

  public void checkAndRemoveInvisible() {
    InvisibleType invisibleType = this.getInvisibleType();
    if (invisibleType == InvisibleType.EFFECT) {
      this.getEffectList().stopEffects(EffectType.Invisible);
    }

  }

  public void teleToLocation(int x, int y, int z, int refId) {
    Reflection r = ReflectionManager.getInstance().get(refId);
    if (r != null) {
      this.teleToLocation(x, y, z, r);
    }
  }

  public void teleToLocation(int x, int y, int z, Reflection r) {
    if (this.isTeleporting.compareAndSet(false, true)) {
      this.abortCast(true, false);
      if (!this.isLockedTarget()) {
        this.setTarget((GameObject)null);
      }

      this.stopMove(true, true, false);
      if (!this.isBoat() && !this.isFlying() && !World.isWater(new Location(x, y, z), r)) {
        z = GeoEngine.getHeight(x, y, z, r.getGeoIndex());
      }

      Player player;
      if (this.isPlayer() && DimensionalRiftManager.getInstance().checkIfInRiftZone(this.getLoc(), true)) {
        player = (Player)this;
        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
          Location newCoords = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
          x = newCoords.x;
          y = newCoords.y;
          z = newCoords.z;
          player.getParty().getDimensionalRift().usedTeleport(player);
        }
      }

      if (this.isPlayer()) {
        player = (Player)this;
        player.getListeners().onTeleport(x, y, z, r);
        this.decayMe();
        this.setXYZ(x, y, z);
        this.setReflection(r);
        player.setLastClientPosition((Location)null);
        player.setLastServerPosition((Location)null);
        player.sendPacket(new TeleportToLocation(player, x, y, z));
      } else {
        this.setXYZ(x, y, z);
        this.setReflection(r);
        this.broadcastPacket(new TeleportToLocation(this, x, y, z));
        this.onTeleported();
      }

    }
  }

  public boolean onTeleported() {
    return this.isTeleporting.compareAndSet(true, false);
  }

  public void sendMessage(CustomMessage message) {
  }

  public String toString() {
    return this.getClass().getSimpleName() + "[" + this.getObjectId() + "]";
  }

  public double getColRadius() {
    return this.getTemplate().collisionRadius;
  }

  public double getColHeight() {
    return this.getTemplate().collisionHeight;
  }

  public EffectList getEffectList() {
    if (this._effectList == null) {
      synchronized(this) {
        if (this._effectList == null) {
          this._effectList = new EffectList(this);
        }
      }
    }

    return this._effectList;
  }

  public boolean paralizeOnAttack(Creature attacker) {
    int max_attacker_level = 65535;
    MonsterInstance leader;
    if (this.isRaid() || this.isMinion() && (leader = ((MinionInstance)this).getLeader()) != null && leader.isRaid()) {
      max_attacker_level = this.getLevel() + Config.RAID_MAX_LEVEL_DIFF;
    } else if (this.isNpc()) {
      int max_level_diff = ((NpcInstance)this).getParameter("ParalizeOnAttack", -1000);
      if (max_level_diff != -1000) {
        max_attacker_level = this.getLevel() + max_level_diff;
      }
    }

    return attacker.getLevel() > max_attacker_level;
  }

  protected void onDelete() {
    GameObjectsStorage.remove(this._storedId);
    this.getEffectList().stopAllEffects();
    super.onDelete();
  }

  public void addExpAndSp(long exp, long sp) {
  }

  public void broadcastCharInfo() {
  }

  public void checkHpMessages(double currentHp, double newHp) {
  }

  public boolean checkPvP(Creature target, Skill skill) {
    return false;
  }

  public boolean consumeItem(int itemConsumeId, long itemCount) {
    return true;
  }

  public boolean consumeItemMp(int itemId, int mp) {
    return true;
  }

  public boolean isFearImmune() {
    return false;
  }

  public boolean isLethalImmune() {
    return this.getMaxHp() >= 50000;
  }

  public boolean getChargedSoulShot() {
    return false;
  }

  public int getChargedSpiritShot() {
    return 0;
  }

  public int getIncreasedForce() {
    return 0;
  }

  public int getConsumedSouls() {
    return 0;
  }

  public int getAgathionEnergy() {
    return 0;
  }

  public void setAgathionEnergy(int val) {
  }

  public int getKarma() {
    return 0;
  }

  public double getLevelMod() {
    return 1.0D;
  }

  public int getNpcId() {
    return 0;
  }

  public Summon getPet() {
    return null;
  }

  public int getPvpFlag() {
    return 0;
  }

  public void setTeam(TeamType t) {
    this._team = t;
    this.sendChanges();
  }

  public TeamType getTeam() {
    return this._team;
  }

  public boolean isUndead() {
    return false;
  }

  public boolean isParalyzeImmune() {
    return false;
  }

  public void reduceArrowCount() {
  }

  public void sendChanges() {
    this.getStatsRecorder().sendChanges();
  }

  public void sendMessage(String message) {
  }

  public void sendPacket(IStaticPacket mov) {
  }

  public void sendPacket(IStaticPacket... mov) {
  }

  public void sendPacket(List<? extends IStaticPacket> mov) {
  }

  public void setIncreasedForce(int i) {
  }

  public void setConsumedSouls(int i, NpcInstance monster) {
  }

  public void startPvPFlag(Creature target) {
  }

  public boolean unChargeShots(boolean spirit) {
    return false;
  }

  public void updateEffectIcons() {
  }

  protected void refreshHpMpCp() {
    int maxHp = this.getMaxHp();
    int maxMp = this.getMaxMp();
    int maxCp = this.isPlayer() ? this.getMaxCp() : 0;
    if (this._currentHp > (double)maxHp) {
      this.setCurrentHp((double)maxHp, false);
    }

    if (this._currentMp > (double)maxMp) {
      this.setCurrentMp((double)maxMp, false);
    }

    if (this._currentCp > (double)maxCp) {
      this.setCurrentCp((double)maxCp, false);
    }

    if (this._currentHp < (double)maxHp || this._currentMp < (double)maxMp || this._currentCp < (double)maxCp) {
      this.startRegeneration();
    }

  }

  public void updateStats() {
    this.refreshHpMpCp();
    this.sendChanges();
  }

  public void setOverhitAttacker(Creature attacker) {
  }

  public void setOverhitDamage(double damage) {
  }

  public boolean isCursedWeaponEquipped() {
    return false;
  }

  public boolean isHero() {
    return false;
  }

  public int getAccessLevel() {
    return 0;
  }

  public Clan getClan() {
    return null;
  }

  public double getRateAdena() {
    return 1.0D;
  }

  public double getRateItems() {
    return 1.0D;
  }

  public double getRateExp() {
    return 1.0D;
  }

  public double getRateSp() {
    return 1.0D;
  }

  public double getRateSpoil() {
    return 1.0D;
  }

  public int getFormId() {
    return 0;
  }

  public boolean isNameAbove() {
    return true;
  }

  public void setLoc(Location loc) {
    this.setXYZ(loc.x, loc.y, loc.z);
  }

  public void setLoc(Location loc, boolean MoveTask) {
    this.setXYZ(loc.x, loc.y, loc.z, MoveTask);
  }

  public void setXYZ(int x, int y, int z) {
    this.setXYZ(x, y, z, false);
  }

  public void setXYZ(int x, int y, int z, boolean MoveTask) {
    if (!MoveTask) {
      this.stopMove();
    }

    this.moveLock.lock();

    try {
      super.setXYZ(x, y, z);
    } finally {
      this.moveLock.unlock();
    }

    this.updateZones();
  }

  protected void onSpawn() {
    super.onSpawn();
    this.updateStats();
    this.updateZones();
  }

  public void spawnMe(Location loc) {
    if (loc.h > 0) {
      this.setHeading(loc.h);
    }

    try {
      super.spawnMe(loc);
    } catch (Exception var3) {
      var3.printStackTrace();
    }

  }

  protected void onDespawn() {
    if (!this.isLockedTarget()) {
      this.setTarget((GameObject)null);
    }

    this.stopMove();
    this.stopAttackStanceTask();
    this.stopRegeneration();
    this.updateZones();
    this.clearStatusListeners();
    super.onDespawn();
  }

  public final void doDecay() {
    if (this.isDead()) {
      this.onDecay();
    }
  }

  protected void onDecay() {
    this.decayMe();
  }

  public void validateLocation(int broadcast) {
    L2GameServerPacket sp = new ValidateLocation(this);
    if (broadcast == 0) {
      this.sendPacket((IStaticPacket)sp);
    } else if (broadcast == 1) {
      this.broadcastPacket(sp);
    } else {
      this.broadcastPacketToOthers(sp);
    }

  }

  public void addUnActiveSkill(Skill skill) {
    if (skill != null && !this.isUnActiveSkill(skill.getId())) {
      this.removeStatsOwner(skill);
      this.removeTriggers(skill);
      this._unActiveSkills.add(skill.getId());
    }
  }

  public void removeUnActiveSkill(Skill skill) {
    if (skill != null && this.isUnActiveSkill(skill.getId())) {
      this.addStatFuncs(skill.getStatFuncs());
      this.addTriggers(skill);
      this._unActiveSkills.remove(skill.getId());
    }
  }

  public boolean isUnActiveSkill(int id) {
    return this._unActiveSkills.contains(id);
  }

  public abstract int getLevel();

  public abstract ItemInstance getActiveWeaponInstance();

  public abstract WeaponTemplate getActiveWeaponItem();

  public abstract ItemInstance getSecondaryWeaponInstance();

  public abstract WeaponTemplate getSecondaryWeaponItem();

  public CharListenerList getListeners() {
    if (this.listeners == null) {
      synchronized(this) {
        if (this.listeners == null) {
          this.listeners = new CharListenerList(this);
        }
      }
    }

    return this.listeners;
  }

  public <T extends Listener<Creature>> boolean addListener(T listener) {
    return this.getListeners().add(listener);
  }

  public <T extends Listener<Creature>> boolean removeListener(T listener) {
    return this.getListeners().remove(listener);
  }

  public CharStatsChangeRecorder<? extends Creature> getStatsRecorder() {
    if (this._statsRecorder == null) {
      synchronized(this) {
        if (this._statsRecorder == null) {
          this._statsRecorder = new CharStatsChangeRecorder(this);
        }
      }
    }

    return this._statsRecorder;
  }

  public boolean isCreature() {
    return true;
  }

  public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
    if (miss && target.isPlayer() && !target.isDamageBlocked()) {
      target.sendPacket((IStaticPacket)(new SystemMessage(42)).addName(this));
    }

  }

  public void displayReceiveDamageMessage(Creature attacker, int damage) {
  }

  public Collection<TimeStamp> getSkillReuses() {
    return this._skillReuses.values();
  }

  public TimeStamp getSkillReuse(Skill skill) {
    return (TimeStamp)this._skillReuses.get(skill.hashCode());
  }

  private class RegenTask implements Runnable {
    private RegenTask() {
    }

    public void run() {
      if (!Creature.this.isAlikeDead() && Creature.this.getRegenTick() != 0L) {
        double hpStart = Creature.this._currentHp;
        int maxHp = Creature.this.getMaxHp();
        int maxMp = Creature.this.getMaxMp();
        int maxCp = Creature.this.isPlayer() ? Creature.this.getMaxCp() : 0;
        double addHp = 0.0D;
        double addMp = 0.0D;
        Creature.this.regenLock.lock();

        try {
          if (Creature.this._currentHp < (double)maxHp) {
            addHp += Formulas.calcHpRegen(Creature.this);
          }

          if (Creature.this._currentMp < (double)maxMp) {
            addMp += Formulas.calcMpRegen(Creature.this);
          }

          if (Creature.this.isPlayer() && Config.REGEN_SIT_WAIT) {
            Player pl = (Player)Creature.this;
            if (pl.isSitting()) {
              pl.updateWaitSitTime();
              if (pl.getWaitSitTime() > 5) {
                addHp += (double)pl.getWaitSitTime();
                addMp += (double)pl.getWaitSitTime();
              }
            }
          } else if (Creature.this.isRaid() && Creature.this.getLevel() >= Config.RATE_MOD_MIN_LEVEL_LIMIT && Creature.this.getLevel() <= Config.RATE_MOD_MAX_LEVEL_LIMIT) {
            addHp *= Config.RATE_RAID_REGEN;
            addMp *= Config.RATE_RAID_REGEN;
          }

          Creature var10000 = Creature.this;
          var10000._currentHp += Math.max(0.0D, Math.min(addHp, Creature.this.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)maxHp / 100.0D - Creature.this._currentHp));
          var10000 = Creature.this;
          var10000._currentMp += Math.max(0.0D, Math.min(addMp, Creature.this.calcStat(Stats.MP_LIMIT, (Creature)null, (Skill)null) * (double)maxMp / 100.0D - Creature.this._currentMp));
          Creature.this._currentHp = Math.min((double)maxHp, Creature.this._currentHp);
          Creature.this._currentMp = Math.min((double)maxMp, Creature.this._currentMp);
          if (Creature.this.isPlayer()) {
            var10000 = Creature.this;
            var10000._currentCp += Math.max(0.0D, Math.min(Formulas.calcCpRegen(Creature.this), Creature.this.calcStat(Stats.CP_LIMIT, (Creature)null, (Skill)null) * (double)maxCp / 100.0D - Creature.this._currentCp));
            Creature.this._currentCp = Math.min((double)maxCp, Creature.this._currentCp);
          }

          if (Creature.this._currentHp == (double)maxHp && Creature.this._currentMp == (double)maxMp && Creature.this._currentCp == (double)maxCp) {
            Creature.this.stopRegeneration();
          }
        } finally {
          Creature.this.regenLock.unlock();
        }

        Creature.this.broadcastStatusUpdate();
        Creature.this.sendChanges();
        Creature.this.checkHpMessages(hpStart, Creature.this._currentHp);
      }
    }
  }

  private class AttackStanceTask extends RunnableImpl {
    private AttackStanceTask() {
    }

    public void runImpl() throws Exception {
      if (!Creature.this.isInCombat()) {
        Creature.this.stopAttackStanceTask();
      }

    }
  }

  public static class MoveToRelativeAction extends Creature.MoveToAction {
    private final HardReference<? extends GameObject> targetRef;
    private Location prevTargetLoc;
    private boolean isRelativeMoveEnabled;
    private final int range;

    protected MoveToRelativeAction(Creature actor, GameObject target, boolean ignoreGeo, int indent, int range, boolean pathFind) {
      super(actor, ignoreGeo, indent, pathFind);
      this.targetRef = target.getRef();
      this.prevTargetLoc = target.getLoc().clone();
      this.range = Math.max(range, indent + 16);
      this.isRelativeMoveEnabled = false;
    }

    private GameObject getTarget() {
      return (GameObject)this.targetRef.get();
    }

    public boolean isSameTarget(GameObject target) {
      return this.getTarget() == target;
    }

    public boolean start() {
      if (!super.start()) {
        return false;
      } else {
        Creature actor = this.getActor();
        GameObject target = this.getTarget();
        if (actor != null && target != null) {
          Location actorLoc = actor.getLoc();
          Location pawnLoc = target.getLoc().clone();
          if (!this.buildPathLines(actorLoc, pawnLoc)) {
            return false;
          } else {
            this.prevTargetLoc = pawnLoc.clone();
            return !this.onEnd();
          }
        } else {
          return false;
        }
      }
    }

    protected boolean isPathRebuildRequired() {
      Creature actor = this.getActor();
      GameObject target = this.getTarget();
      if (actor != null && target != null) {
        Location targetLoc = target.getLoc();
        if (!this.isRelativeMoveEnabled) {
          return false;
        } else {
          return !this.prevTargetLoc.equalsGeo(targetLoc);
        }
      } else {
        return true;
      }
    }

    protected boolean onEnd() {
      Creature actor = this.getActor();
      GameObject target = this.getTarget();
      if (actor != null && target != null) {
        int remainingLinesCount = this.remainingLinesCount();
        if (remainingLinesCount > 1) {
          if (!this.pollPathLine()) {
            this.onFinish(false, false);
            return true;
          }
        } else {
          if (remainingLinesCount != 1) {
            this.onFinish(true, false);
            return true;
          }

          this.isRelativeMoveEnabled = true;
          if (this.isPathRebuildRequired()) {
            if (this.isArrived()) {
              this.onFinish(true, false);
              return true;
            }

            Location actorLoc = actor.getLoc();
            Location targetLoc = this.getImpliedTargetLoc();
            if (!this.buildPathLines(actorLoc, targetLoc)) {
              this.onFinish(false, false);
              return true;
            }

            if (!this.pollPathLine()) {
              this.onFinish(false, false);
              return true;
            }

            this.prevTargetLoc = targetLoc.clone();
          } else if (!this.pollPathLine()) {
            this.onFinish(false, false);
            return true;
          }
        }

        actor.broadcastMove();
        return false;
      } else {
        return true;
      }
    }

    protected boolean isArrived() {
      Creature actor = this.getActor();
      GameObject target = this.getTarget();
      if (actor != null && target != null) {
        if (target.isCreature() && ((Creature)target).isMoving()) {
          int threshold = this.indent + 16;
          return this.includeMoveZ() ? target.isInRangeZ(actor, (long)threshold) : target.isInRange(actor, (long)threshold);
        } else {
          return this.includeMoveZ() ? target.isInRangeZ(actor, (long)(this.indent + 16)) : target.isInRange(actor, (long)(this.indent + 16));
        }
      } else {
        return false;
      }
    }

    private Location getImpliedTargetLoc() {
      Creature actor = this.getActor();
      GameObject targetObj = this.getTarget();
      if (actor != null && targetObj != null) {
        if (!targetObj.isCreature()) {
          return targetObj.getLoc();
        } else {
          Creature target = (Creature)targetObj;
          Location loc = targetObj.getLoc();
          return !target.isMoving() ? loc : GeoMove.getIntersectPoint(actor.getLoc(), loc, target.getMoveSpeed(), Math.max(128, Config.MOVE_TASK_QUANTUM_PC / 2));
        }
      } else {
        return null;
      }
    }

    protected boolean onTick(double done) {
      if (!super.onTick(done)) {
        return false;
      } else {
        Creature actor = this.getActor();
        GameObject target = this.getTarget();
        if (actor != null && target != null) {
          if (done < 1.0D) {
            if (this.isPathRebuildRequired()) {
              Location actorLoc = actor.getLoc();
              Location pawnLoc = this.getImpliedTargetLoc();
              if (actor.isPlayer() && actor.getPlayer().getNetConnection() != null) {
                int pawnClippingRange = actor.getPlayer().getNetConnection().getPawnClippingRange();
                if (actorLoc.distance3D(pawnLoc) > (double)pawnClippingRange) {
                  this.onFinish(false, false);
                  return false;
                }
              }

              if (!this.buildPathLines(actorLoc, pawnLoc)) {
                this.onFinish(false, false);
                return false;
              }

              if (!this.pollPathLine()) {
                this.onFinish(false, false);
                return false;
              }

              this.prevTargetLoc = pawnLoc.clone();
            } else if (this.isRelativeMoveEnabled && this.isArrived()) {
              this.onFinish(true, false);
              return false;
            }
          }

          return true;
        } else {
          return false;
        }
      }
    }

    protected void onFinish(boolean finishedWell, boolean isInterrupted) {
      Creature actor = this.getActor();
      GameObject target = this.getTarget();
      if (!this.isFinished() && actor != null && target != null) {
        if (isInterrupted) {
          this.setIsFinished(true);
        } else {
          actor.stopMove(!(target instanceof StaticObjectInstance) && !target.isDoor(), false, false);
          boolean succeed = false;
          if (finishedWell) {
            succeed = (this.includeMoveZ() ? actor.getRealDistance3D(target) : actor.getRealDistance(target)) <= (double)(this.range + 16);
          }

          this.setIsFinished(true);
          if (succeed) {
            ThreadPoolManager.getInstance().execute(new NotifyAITask(actor, CtrlEvent.EVT_ARRIVED_TARGET));
          } else {
            ThreadPoolManager.getInstance().execute(new NotifyAITask(actor, CtrlEvent.EVT_ARRIVED_BLOCKED, actor.getLoc()));
          }

        }
      }
    }

    protected boolean isRelativeMove() {
      return this.isRelativeMoveEnabled;
    }

    public L2GameServerPacket movePacket() {
      Creature actor = this.getActor();
      if (actor == null) {
        return null;
      } else {
        GameObject target = this.getTarget();
        if (this.isRelativeMove()) {
          return target == null ? null : new MoveToPawn(actor, target, this.indent);
        } else {
          return new CharMoveToLocation(actor, actor.getLoc(), this.moveTo.clone());
        }
      }
    }
  }

  public static class MoveToLocationAction extends Creature.MoveToAction {
    private final Location dst;
    private final Location src;

    public MoveToLocationAction(Creature actor, Location moveFrom, Location moveTo, boolean ignoreGeo, int indent, boolean pathFind) {
      super(actor, ignoreGeo, indent, pathFind);
      this.src = moveFrom.clone();
      this.dst = moveTo.clone();
    }

    public MoveToLocationAction(Creature actor, Location dest, int indent, boolean pathFind) {
      this(actor, actor.getLoc(), dest, actor.isBoat() || actor.isInBoat(), indent, pathFind);
    }

    public boolean isSameDest(Location to) {
      return this.dst.equalsGeo(to);
    }

    public boolean start() {
      if (!super.start()) {
        return false;
      } else if (!this.buildPathLines(this.src, this.dst)) {
        return false;
      } else {
        return !this.onEnd();
      }
    }

    protected boolean onEnd() {
      Creature actor = this.getActor();
      if (actor == null) {
        return true;
      } else if (!this.pollPathLine()) {
        this.onFinish(true, false);
        return true;
      } else {
        actor.broadcastMove();
        return false;
      }
    }

    protected void onFinish(boolean finishedWell, boolean isInterrupted) {
      Creature actor = this.getActor();
      if (!this.isFinished() && actor != null) {
        if (isInterrupted) {
          this.setIsFinished(true);
        } else {
          if (finishedWell) {
            ThreadPoolManager.getInstance().execute(new NotifyAITask(actor, CtrlEvent.EVT_ARRIVED));
          } else {
            actor.stopMove(true, true, false);
            ThreadPoolManager.getInstance().execute(new NotifyAITask(actor, CtrlEvent.EVT_ARRIVED_BLOCKED, actor.getLoc()));
          }

          super.onFinish(finishedWell, isInterrupted);
        }
      }
    }

    public L2GameServerPacket movePacket() {
      Creature actor = this.getActor();
      return actor != null ? new CharMoveToLocation(actor, actor.getLoc(), this.moveTo.clone()) : null;
    }

    protected boolean isRelativeMove() {
      return false;
    }
  }

  public abstract static class MoveToAction extends Creature.MoveActionBase {
    protected final int indent;
    protected final boolean pathFind;
    protected final boolean ignoreGeo;
    protected Queue<List<Location>> geoPathLines;
    protected List<Location> currentGeoPathLine;
    protected Location moveFrom;
    protected Location moveTo;
    protected double prevMoveLen;
    protected boolean prevIncZ;

    protected MoveToAction(Creature actor, boolean ignoreGeo, int indent, boolean pathFind) {
      super(actor);
      this.indent = indent;
      this.pathFind = pathFind;
      this.ignoreGeo = ignoreGeo;
      this.geoPathLines = new LinkedList();
      this.currentGeoPathLine = Collections.emptyList();
      this.moveFrom = actor.getLoc();
      this.moveTo = actor.getLoc();
      this.prevMoveLen = 0.0D;
      this.prevIncZ = false;
    }

    protected boolean buildPathLines(Location pathFrom, Location pathTo) {
      Creature actor = this.getActor();
      if (actor == null) {
        return false;
      } else {
        LinkedList<List<Location>> geoPathLines = new LinkedList();
        if (!GeoMove.buildGeoPath(geoPathLines, pathFrom.clone().world2geo(), pathTo.clone().world2geo(), actor.getGeoIndex(), (int)actor.getColRadius(), (int)actor.getColHeight(), this.indent, this.pathFind && !this.ignoreGeo && !this.isRelativeMove(), this.isForPlayable(), actor.isFlying(), actor.isInWater(), actor.getWaterZ(), this.ignoreGeo)) {
          return false;
        } else {
          this.geoPathLines.clear();
          this.geoPathLines.addAll(geoPathLines);
          return true;
        }
      }
    }

    protected boolean pollPathLine() {
      if ((this.currentGeoPathLine = (List)this.geoPathLines.poll()) != null) {
        Creature actor = this.getActor();
        this.moveFrom = ((Location)this.currentGeoPathLine.get(0)).clone().geo2world();
        this.moveTo = ((Location)this.currentGeoPathLine.get(this.currentGeoPathLine.size() - 1)).clone().geo2world();
        this.prevIncZ = this.includeMoveZ();
        this.prevMoveLen = PositionUtils.calculateDistance(this.moveFrom, this.moveTo, this.prevIncZ);
        this.setPassDist(0.0D);
        this.setPrevTick(System.currentTimeMillis());
        if (this.prevMoveLen > 16.0D) {
          actor.setHeading(PositionUtils.calculateHeadingFrom(this.moveFrom.getX(), this.moveFrom.getY(), this.moveTo.getX(), this.moveTo.getY()));
        }

        return true;
      } else {
        return false;
      }
    }

    protected int remainingLinesCount() {
      return this.geoPathLines.size();
    }

    protected abstract boolean isRelativeMove();

    protected boolean calcMidDest(Creature creature, Location result, boolean includeZ, double done, double pass, double len) {
      if (this.currentGeoPathLine == null) {
        return false;
      } else {
        Location currLoc = creature.getLoc();
        if (len >= 16.0D && done != 0.0D && pass != 0.0D && !this.currentGeoPathLine.isEmpty()) {
          int lastIdx = this.currentGeoPathLine.size() - 1;
          result.set(this.moveFrom).indent(this.moveTo, (int)(pass + 0.5D), includeZ).setZ(((Location)this.currentGeoPathLine.get(Math.min(lastIdx, (int)((double)lastIdx * done + 0.5D)))).getZ());
          if (!result.equalsGeo(currLoc) && !this.ignoreGeo && Config.ALLOW_GEODATA) {
            return includeZ ? true : GeoEngine.canMoveToCoord(currLoc.getX(), currLoc.getY(), currLoc.getZ(), result.getX(), result.getY(), result.getZ(), creature.getGeoIndex());
          } else {
            return true;
          }
        } else {
          result.set(currLoc);
          return true;
        }
      }
    }

    public Location moveFrom() {
      return this.moveFrom;
    }

    public Location moveTo() {
      return this.moveTo;
    }

    protected double getMoveLen() {
      boolean incZ = this.includeMoveZ();
      if (incZ != this.prevIncZ) {
        this.prevMoveLen = PositionUtils.calculateDistance(this.moveFrom, this.moveTo, incZ);
        this.prevIncZ = incZ;
      }

      return this.prevMoveLen;
    }
  }

  protected abstract static class MoveActionBase {
    private final HardReference<? extends Creature> actorRef;
    private final boolean isForPlayable;
    private long prevTick;
    private int prevSpeed;
    private double passDist;
    protected volatile boolean isFinished = false;

    public MoveActionBase(Creature actor) {
      this.actorRef = actor.getRef();
      this.isForPlayable = actor.isPlayable();
      this.prevTick = 0L;
      this.prevSpeed = 0;
      this.passDist = 0.0D;
      this.isFinished = false;
    }

    protected boolean isForPlayable() {
      return this.isForPlayable;
    }

    protected Creature getActor() {
      return (Creature)this.actorRef.get();
    }

    protected void setIsFinished(boolean isFinished) {
      this.isFinished = isFinished;
    }

    public boolean isFinished() {
      return this.isFinished;
    }

    protected long getPrevTick() {
      return this.prevTick;
    }

    protected void setPrevTick(long prevTick) {
      this.prevTick = prevTick;
    }

    protected int getPrevSpeed() {
      return this.prevSpeed;
    }

    protected void setPrevSpeed(int prevSpeed) {
      this.prevSpeed = prevSpeed;
    }

    protected double getPassDist() {
      return this.passDist;
    }

    protected void setPassDist(double passDist) {
      this.passDist = passDist;
    }

    public boolean start() {
      Creature actor = this.getActor();
      if (actor == null) {
        return false;
      } else {
        this.setPrevTick(System.currentTimeMillis());
        this.setPrevSpeed(actor.getMoveSpeed());
        this.setPassDist(0.0D);
        this.setIsFinished(false);
        return this.weightCheck(actor);
      }
    }

    public abstract Location moveFrom();

    public abstract Location moveTo();

    protected double getMoveLen() {
      return PositionUtils.calculateDistance(this.moveFrom(), this.moveTo(), this.includeMoveZ());
    }

    protected boolean includeMoveZ() {
      Creature actor = this.getActor();
      return actor == null || actor.isInWater() || actor.isFlying() || actor.isBoat() || actor.isInBoat();
    }

    public int getNextTickInterval() {
      return !this.isForPlayable() ? Math.min(Config.MOVE_TASK_QUANTUM_NPC, (int)(1000.0D * (this.getMoveLen() - this.getPassDist()) / (double)Math.max(this.getPrevSpeed(), 1))) : Math.min(Config.MOVE_TASK_QUANTUM_PC, (int)(1000.0D * (this.getMoveLen() - this.getPassDist()) / (double)Math.max(this.getPrevSpeed(), 1)));
    }

    protected boolean onEnd() {
      return true;
    }

    protected void onFinish(boolean finishedWell, boolean isInterrupted) {
      this.setIsFinished(true);
    }

    public void interrupt() {
      this.tick();
      this.onFinish(false, true);
    }

    protected boolean onTick(double done) {
      Creature actor = this.getActor();
      if (actor == null) {
        this.onFinish(false, true);
        return false;
      } else {
        return true;
      }
    }

    public boolean scheduleNextTick() {
      Creature actor = this.getActor();
      if (actor == null) {
        return false;
      } else {
        Runnable r = actor._moveTaskRunnable;
        Creature.CreatureMoveActionTask r;
        actor._moveTaskRunnable = r = new Creature.CreatureMoveActionTask(actor);
        actor._moveTask = ThreadPoolManager.getInstance().schedule(r, (long)this.getNextTickInterval());
        return true;
      }
    }

    public boolean tick() {
      Creature actor = this.getActor();
      if (actor == null) {
        return false;
      } else {
        actor.moveLock.lock();

        boolean var2;
        try {
          var2 = this.tickImpl(actor);
        } finally {
          actor.moveLock.unlock();
        }

        return var2;
      }
    }

    private boolean tickImpl(Creature actor) {
      if (this.isFinished()) {
        return false;
      } else if (actor.moveAction != this) {
        this.setIsFinished(true);
        return false;
      } else if (actor.isMovementDisabled()) {
        this.onFinish(false, false);
        return false;
      } else {
        int currSpeed = actor.getMoveSpeed();
        if (currSpeed <= 0) {
          this.onFinish(false, false);
          return false;
        } else {
          long now = System.currentTimeMillis();
          float delta = (float)(now - this.getPrevTick()) / 1000.0F;
          boolean includeMoveZ = this.includeMoveZ();
          double passLen = this.getPassDist();
          passLen += (double)delta * ((double)Math.max(this.getPrevSpeed() + currSpeed, 2) / 2.0D);
          this.setPrevTick(now);
          this.setPrevSpeed(currSpeed);
          this.setPassDist(passLen);
          double len = this.getMoveLen();
          double done = Math.max(0.0D, Math.min(passLen / Math.max(len, 1.0D), 1.0D));
          Location currLoc = actor.getLoc();
          Location newLoc = currLoc.clone();
          if (!this.calcMidDest(actor, newLoc, includeMoveZ, done, passLen, len)) {
            this.onFinish(false, false);
            return false;
          } else {
            if (!includeMoveZ) {
            }

            actor.setLoc(newLoc, true);
            if (done == 1.0D) {
              return !this.onEnd();
            } else if (!this.onTick(done)) {
              this.setIsFinished(true);
              return false;
            } else {
              return true;
            }
          }
        }
      }
    }

    protected boolean weightCheck(Creature creature) {
      if (!creature.isPlayer()) {
        return true;
      } else if (creature.getPlayer().getCurrentLoad() >= 2 * creature.getPlayer().getMaxLoad()) {
        creature.sendPacket((IStaticPacket)(new SystemMessage(555)));
        return false;
      } else {
        return true;
      }
    }

    protected boolean calcMidDest(Creature creature, Location result, boolean includeZ, double done, double pass, double len) {
      result.set(this.moveTo().clone().indent(this.moveFrom(), (int)Math.round(len - pass), creature.isFlying() || creature.isInWater())).correctGeoZ();
      return true;
    }

    public abstract L2GameServerPacket movePacket();
  }

  protected static class CreatureMoveActionTask extends RunnableImpl {
    private final HardReference<? extends Creature> _creatureRef;

    public CreatureMoveActionTask(Creature creature) {
      this._creatureRef = creature.getRef();
    }

    public void runImpl() throws Exception {
      Creature actor = (Creature)this._creatureRef.get();
      if (actor != null) {
        actor.moveLock.lock();

        try {
          Creature.MoveActionBase moveActionBase = actor.moveAction;
          if (actor._moveTaskRunnable == this && moveActionBase != null && !moveActionBase.isFinished() && moveActionBase.tickImpl(actor) && actor._moveTaskRunnable == this) {
            moveActionBase.scheduleNextTick();
          }
        } finally {
          actor.moveLock.unlock();
        }

      }
    }
  }
}
