//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.listener.actor.OnAttackListener;
import l2.gameserver.listener.actor.OnMagicUseListener;
import l2.gameserver.network.l2.s2c.AbnormalStatusUpdate;
import l2.gameserver.network.l2.s2c.ExOlympiadSpelledInfo;
import l2.gameserver.network.l2.s2c.PartySpelled;
import l2.gameserver.network.l2.s2c.ShortBuffStatusUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncOwner;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.EffectTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Effect extends RunnableImpl implements Comparable<Effect>, FuncOwner {
  protected static final Logger _log = LoggerFactory.getLogger(Effect.class);
  public static final Effect[] EMPTY_L2EFFECT_ARRAY = new Effect[0];
  public static int SUSPENDED = -1;
  public static int STARTING = 0;
  public static int STARTED = 1;
  public static int ACTING = 2;
  public static int FINISHING = 3;
  public static int FINISHED = 4;
  protected final Creature _effector;
  protected final Creature _effected;
  protected final Skill _skill;
  protected final int _displayId;
  protected final int _displayLevel;
  private final double _value;
  private final AtomicInteger _state;
  private int _count;
  private long _period;
  private long _startTimeMillis;
  private long _duration;
  private boolean _inUse = false;
  private Effect _next = null;
  private boolean _active = false;
  protected final EffectTemplate _template;
  private Future<?> _effectTask;
  private final Effect.EEffectSlot _eEffSlot;
  private Effect.ActionDispelListener _listener;

  protected Effect(Env env, EffectTemplate template) {
    this._skill = env.skill;
    this._effector = env.character;
    this._effected = env.target;
    this._template = template;
    this._value = template._value;
    this._count = template.getCount();
    this._period = template.getPeriod();
    this._duration = this._period * (long)this._count;
    this._displayId = template._displayId != 0 ? template._displayId : this._skill.getDisplayId();
    this._displayLevel = template._displayLevel != 0 ? template._displayLevel : this._skill.getDisplayLevel();
    this._state = new AtomicInteger(STARTING);
    if (this._skill.isOffensive()) {
      this._eEffSlot = Effect.EEffectSlot.EFFECT_SLOT_DEBUFF;
    } else {
      this._eEffSlot = Effect.EEffectSlot.EFFECT_SLOT_NORMAL;
    }

  }

  public long getPeriod() {
    return this._period;
  }

  public void setPeriod(long time) {
    this._period = time;
    this._duration = this._period * (long)this._count;
  }

  public int getCount() {
    return this._count;
  }

  public void setCount(int count) {
    this._count = count;
    this._duration = this._period * (long)this._count;
  }

  public boolean isOneTime() {
    return this._period == 0L;
  }

  public long getStartTime() {
    return this._startTimeMillis == 0L ? System.currentTimeMillis() : this._startTimeMillis;
  }

  public long getTime() {
    return System.currentTimeMillis() - this.getStartTime();
  }

  public long getDuration() {
    return this._duration;
  }

  public int getTimeLeft() {
    return (int)((this.getDuration() - this.getTime()) / 1000L);
  }

  public boolean isTimeLeft() {
    return this.getDuration() - this.getTime() > 0L;
  }

  public boolean isInUse() {
    return this._inUse;
  }

  public void setInUse(boolean inUse) {
    this._inUse = inUse;
  }

  public boolean isActive() {
    return this._active;
  }

  public void setActive(boolean set) {
    this._active = set;
  }

  public EffectTemplate getTemplate() {
    return this._template;
  }

  public Effect.EEffectSlot getEffectSlot() {
    return this._eEffSlot;
  }

  public String getStackType() {
    return this.getTemplate()._stackType;
  }

  public String getStackType2() {
    return this.getTemplate()._stackType2;
  }

  public boolean isStackTypeMatch(String... params) {
    String thisStackType = this.getStackType();
    String thisStackType2 = this.getStackType2();

    for(int paramIdx = 0; paramIdx < params.length; ++paramIdx) {
      if (params[paramIdx].equalsIgnoreCase(thisStackType)) {
        return true;
      }

      if (params[paramIdx].equalsIgnoreCase(thisStackType2)) {
        return true;
      }
    }

    return false;
  }

  public boolean isStackTypeMatch(Effect param) {
    return this.isStackTypeMatch(param.getStackType()) || this.isStackTypeMatch(param.getStackType2());
  }

  public int getStackOrder() {
    return this.getTemplate()._stackOrder;
  }

  public Skill getSkill() {
    return this._skill;
  }

  public Creature getEffector() {
    return this._effector;
  }

  public Creature getEffected() {
    return this._effected;
  }

  public double calc() {
    return this._value;
  }

  public boolean isEnded() {
    return this.isFinished() || this.isFinishing();
  }

  public boolean isFinishing() {
    return this.getState() == FINISHING;
  }

  public boolean isFinished() {
    return this.getState() == FINISHED;
  }

  private int getState() {
    return this._state.get();
  }

  private boolean setState(int oldState, int newState) {
    return this._state.compareAndSet(oldState, newState);
  }

  public boolean checkCondition() {
    return true;
  }

  protected void onStart() {
    this.getEffected().addStatFuncs(this.getStatFuncs());
    this.getEffected().addTriggers(this.getTemplate());
    if (this.getTemplate()._abnormalEffect != AbnormalEffect.NULL) {
      this.getEffected().startAbnormalEffect(this.getTemplate()._abnormalEffect);
    } else if (this.getEffectType().getAbnormal() != null) {
      this.getEffected().startAbnormalEffect(this.getEffectType().getAbnormal());
    }

    if (this.getTemplate()._abnormalEffect2 != AbnormalEffect.NULL) {
      this.getEffected().startAbnormalEffect(this.getTemplate()._abnormalEffect2);
    }

    if (this.getTemplate()._abnormalEffect3 != AbnormalEffect.NULL) {
      this.getEffected().startAbnormalEffect(this.getTemplate()._abnormalEffect3);
    }

    if (this._template._cancelOnAction) {
      this.getEffected().addListener(this._listener = new Effect.ActionDispelListener());
    }

    if (this.getEffected().isPlayer() && !this.getSkill().canUseTeleport()) {
      this.getEffected().getPlayer().getPlayerAccess().UseTeleport = false;
    }

  }

  protected abstract boolean onActionTime();

  protected void onExit() {
    this.getEffected().removeStatsOwner(this);
    this.getEffected().removeTriggers(this.getTemplate());
    if (this.getTemplate()._abnormalEffect != AbnormalEffect.NULL) {
      this.getEffected().stopAbnormalEffect(this.getTemplate()._abnormalEffect);
    } else if (this.getEffectType().getAbnormal() != null) {
      this.getEffected().stopAbnormalEffect(this.getEffectType().getAbnormal());
    }

    if (this.getTemplate()._abnormalEffect2 != AbnormalEffect.NULL) {
      this.getEffected().stopAbnormalEffect(this.getTemplate()._abnormalEffect2);
    }

    if (this.getTemplate()._abnormalEffect3 != AbnormalEffect.NULL) {
      this.getEffected().stopAbnormalEffect(this.getTemplate()._abnormalEffect3);
    }

    if (this._template._cancelOnAction) {
      this.getEffected().removeListener(this._listener);
    }

    if (this.getEffected().isPlayer() && this.isStackTypeMatch("HpRecoverCast")) {
      this.getEffected().sendPacket(new ShortBuffStatusUpdate());
    }

    if (this.getEffected().isPlayer() && !this.getSkill().canUseTeleport() && !this.getEffected().getPlayer().getPlayerAccess().UseTeleport) {
      this.getEffected().getPlayer().getPlayerAccess().UseTeleport = true;
    }

  }

  private void stopEffectTask() {
    if (this._effectTask != null) {
      this._effectTask.cancel(false);
    }

  }

  private void startEffectTask() {
    if (this._effectTask == null) {
      this._startTimeMillis = System.currentTimeMillis();
      this._effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, this._period, this._period);
    }

  }

  public final void schedule() {
    Creature effected = this.getEffected();
    if (effected != null) {
      if (this.checkCondition()) {
        this.getEffected().getEffectList().addEffect(this);
      }
    }
  }

  private final void suspend() {
    if (this.setState(STARTING, SUSPENDED)) {
      this.startEffectTask();
    } else if (this.setState(STARTED, SUSPENDED) || this.setState(ACTING, SUSPENDED)) {
      synchronized(this) {
        if (this.isInUse()) {
          this.setInUse(false);
          this.setActive(false);
          this.onExit();
        }
      }

      this.getEffected().getEffectList().removeEffect(this);
    }

  }

  public final void start() {
    if (this.setState(STARTING, STARTED)) {
      synchronized(this) {
        if (this.isInUse()) {
          this.setActive(true);
          this.onStart();
          this.startEffectTask();
        }
      }
    }

    this.run();
  }

  public final void runImpl() throws Exception {
    if (this.setState(STARTED, ACTING)) {
      if (!this.getSkill().isHideStartMessage() && !this.getSkill().isToggle() && this.getEffected().getEffectList().getEffectsCountForSkill(this.getSkill().getId()) == 1) {
        this.getEffected().sendPacket((new SystemMessage(110)).addSkillName(this._displayId, this._displayLevel));
      }

      if (this.getSkill().getSecondSkill() > 0) {
        SkillTable.getInstance().getInfo(this.getSkill().getSecondSkill(), 1).getEffects(this._effector, this._effected, false, false);
      }

    } else if (this.getState() == SUSPENDED) {
      if (this.isTimeLeft()) {
        --this._count;
        if (this.isTimeLeft()) {
          return;
        }
      }

      this.exit();
    } else {
      if (this.getState() == ACTING && this.isTimeLeft()) {
        --this._count;
        if ((!this.isActive() || this.onActionTime()) && this.isTimeLeft()) {
          return;
        }
      }

      if (this.setState(ACTING, FINISHING)) {
        this.setInUse(false);
      }

      if (this.setState(FINISHING, FINISHED)) {
        synchronized(this) {
          this.setActive(false);
          this.stopEffectTask();
          this.onExit();
        }

        Effect next = this.getNext();
        if (next != null && next.setState(SUSPENDED, STARTING)) {
          next.schedule();
        }

        if (this.getSkill().getDelayedEffect() > 0) {
          SkillTable.getInstance().getInfo(this.getSkill().getDelayedEffect(), 1).getEffects(this._effector, this._effected, false, false);
        }

        boolean msg = !this.isHidden() && this.getEffected().getEffectList().getEffectsCountForSkill(this.getSkill().getId()) == 1;
        this.getEffected().getEffectList().removeEffect(this);
        if (msg) {
          this.getEffected().sendPacket((new SystemMessage(92)).addSkillName(this._displayId, this._displayLevel));
        }
      }

    }
  }

  public void exit() {
    Effect next = this.getNext();
    if (next != null) {
      next.exit();
    }

    this.removeNext();
    if (this.setState(STARTING, FINISHED)) {
      this.getEffected().getEffectList().removeEffect(this);
    } else if (this.setState(SUSPENDED, FINISHED)) {
      this.stopEffectTask();
    } else if (this.setState(STARTED, FINISHED) || this.setState(ACTING, FINISHED)) {
      synchronized(this) {
        if (this.isInUse()) {
          this.setInUse(false);
          this.setActive(false);
          this.stopEffectTask();
          this.onExit();
        }
      }

      this.getEffected().getEffectList().removeEffect(this);
    }

  }

  private boolean scheduleNext(Effect e) {
    if (e != null && !e.isEnded()) {
      Effect next = this.getNext();
      if (next != null && !next.maybeScheduleNext(e)) {
        return false;
      } else {
        this._next = e;
        return true;
      }
    } else {
      return false;
    }
  }

  public Effect getNext() {
    return this._next;
  }

  private void removeNext() {
    this._next = null;
  }

  public boolean maybeScheduleNext(Effect newEffect) {
    if (newEffect.getStackOrder() < this.getStackOrder()) {
      if (newEffect.getTimeLeft() > this.getTimeLeft()) {
        newEffect.suspend();
        this.scheduleNext(newEffect);
      }

      return false;
    } else {
      if (newEffect.getTimeLeft() >= this.getTimeLeft()) {
        if (this.getNext() != null && this.getNext().getTimeLeft() > newEffect.getTimeLeft()) {
          newEffect.scheduleNext(this.getNext());
          this.removeNext();
        }

        this.exit();
      } else {
        this.suspend();
        newEffect.scheduleNext(this);
      }

      return true;
    }
  }

  public Func[] getStatFuncs() {
    return this.getTemplate().getStatFuncs(this);
  }

  public void addIcon(AbnormalStatusUpdate mi) {
    if (this.isActive() && !this.isHidden()) {
      int duration = this._skill.isToggle() ? -1 : this.getTimeLeft();
      mi.addEffect(this._displayId, this._displayLevel, duration);
    }
  }

  public void addPartySpelledIcon(PartySpelled ps) {
    if (this.isActive() && !this.isHidden()) {
      int duration = this._skill.isToggle() ? -1 : this.getTimeLeft();
      ps.addPartySpelledEffect(this._displayId, this._displayLevel, duration);
    }
  }

  public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfo os) {
    if (this.isActive() && !this.isHidden()) {
      int duration = this._skill.isToggle() ? -1 : this.getTimeLeft();
      os.addSpellRecivedPlayer(player);
      os.addEffect(this._displayId, this._displayLevel, duration);
    }
  }

  protected int getLevel() {
    return this._skill.getLevel();
  }

  public EffectType getEffectType() {
    return this.getTemplate()._effectType;
  }

  public boolean isHidden() {
    return this._displayId < 0;
  }

  public int compareTo(Effect obj) {
    return obj.equals(this) ? 0 : 1;
  }

  public boolean isSaveable() {
    return this._template.isSaveable(this.getSkill().isSaveable()) && this.getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME;
  }

  public int getDisplayId() {
    return this._displayId;
  }

  public int getDisplayLevel() {
    return this._displayLevel;
  }

  public boolean isCancelable() {
    return this._template.isCancelable(this.getSkill().isCancelable());
  }

  public String toString() {
    return "Skill: " + this._skill + ", state: " + this.getState() + ", inUse: " + this._inUse + ", active : " + this._active;
  }

  public boolean isFuncEnabled() {
    return this.isInUse();
  }

  public boolean overrideLimits() {
    return false;
  }

  public boolean isOffensive() {
    return this._template.isOffensive(this.getSkill().isOffensive());
  }

  private class ActionDispelListener implements OnAttackListener, OnMagicUseListener {
    private ActionDispelListener() {
    }

    public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt) {
      Effect.this.exit();
    }

    public void onAttack(Creature actor, Creature target) {
      Effect.this.exit();
    }
  }

  public static enum EEffectSlot {
    EFFECT_SLOT_NORMAL,
    EFFECT_SLOT_DEBUFF;

    public static final Effect.EEffectSlot[] VALUES = values();

    private EEffectSlot() {
    }
  }
}
