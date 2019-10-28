//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.SystemMessage;

import java.util.List;

public class GameObjectTasks {
  public GameObjectTasks() {
  }

  public static class NotifyAITask extends RunnableImpl {
    private final CtrlEvent _evt;
    private final Object _agr0;
    private final Object _agr1;
    private final HardReference<? extends Creature> _charRef;

    public NotifyAITask(Creature cha, CtrlEvent evt, Object agr0, Object agr1) {
      this._charRef = cha.getRef();
      this._evt = evt;
      this._agr0 = agr0;
      this._agr1 = agr1;
    }

    public NotifyAITask(Creature cha, CtrlEvent evt, Object arg0) {
      this(cha, evt, arg0, null);
    }

    public NotifyAITask(Creature cha, CtrlEvent evt) {
      this(cha, evt, null, null);
    }

    public void runImpl() {
      Creature character = this._charRef.get();
      if (character != null && character.hasAI()) {
        character.getAI().notifyEvent(this._evt, this._agr0, this._agr1);
      }
    }
  }

  public static class MagicLaunchedTask extends RunnableImpl {
    public boolean _forceUse;
    private final HardReference<? extends Creature> _charRef;

    public MagicLaunchedTask(Creature cha, boolean forceUse) {
      this._charRef = cha.getRef();
      this._forceUse = forceUse;
    }

    public void runImpl() {
      Creature character = this._charRef.get();
      if (character != null) {
        Skill castingSkill = character.getCastingSkill();
        Creature castingTarget = character.getCastingTarget();
        if (castingSkill != null && castingTarget != null) {
          if (!castingSkill.checkCondition(character, castingTarget, this._forceUse, false, false)) {
            character.abortCast(true, false);
          } else {
            List<Creature> targets = castingSkill.getTargets(character, castingTarget, this._forceUse);
            character.broadcastPacket(new MagicSkillLaunched(character, castingSkill, targets));
          }
        } else {
          character.clearCastVars();
        }
      }
    }
  }

  public static class MagicUseTask extends RunnableImpl {
    public boolean _forceUse;
    private final HardReference<? extends Creature> _charRef;

    public MagicUseTask(Creature cha, boolean forceUse) {
      this._charRef = cha.getRef();
      this._forceUse = forceUse;
    }

    public void runImpl() {
      Creature character = this._charRef.get();
      if (character != null) {
        Skill castingSkill = character.getCastingSkill();
        Creature castingTarget = character.getCastingTarget();
        if (castingSkill != null && castingTarget != null) {
          character.onMagicUseTimer(castingTarget, castingSkill, this._forceUse);
        } else {
          character.clearCastVars();
        }
      }
    }
  }

  public static class ActReadyTask extends RunnableImpl {
    private final HardReference<? extends Creature> _charRef;

    public ActReadyTask(Creature cha) {
      this._charRef = cha.getRef();
    }

    public void runImpl() throws Exception {
      Creature character = this._charRef.get();
      if (character != null) {
        character.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
      }
    }
  }

  public static class HitTask extends RunnableImpl {
    boolean _crit;
    boolean _miss;
    boolean _shld;
    boolean _soulshot;
    boolean _unchargeSS;
    boolean _notify;
    int _damage;
    long _actDelay;
    private final HardReference<? extends Creature> _charRef;
    private final HardReference<? extends Creature> _targetRef;

    public HitTask(Creature cha, Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS, boolean notify) {
      this._charRef = cha.getRef();
      this._targetRef = target.getRef();
      this._damage = damage;
      this._crit = crit;
      this._shld = shld;
      this._miss = miss;
      this._soulshot = soulshot;
      this._unchargeSS = unchargeSS;
      this._notify = notify;
      this._actDelay = 0L;
    }

    public HitTask(Creature cha, Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS, boolean notify, long actDelay) {
      this._charRef = cha.getRef();
      this._targetRef = target.getRef();
      this._damage = damage;
      this._crit = crit;
      this._shld = shld;
      this._miss = miss;
      this._soulshot = soulshot;
      this._unchargeSS = unchargeSS;
      this._notify = notify;
      this._actDelay = actDelay;
    }

    public void runImpl() {
      Creature character;
      Creature target;
      if ((character = this._charRef.get()) != null && (target = this._targetRef.get()) != null) {
        if (!character.isAttackAborted()) {
          character.onHitTimer(target, this._damage, this._crit, this._miss, this._soulshot, this._shld, this._unchargeSS);
          if (this._notify) {
            if (this._actDelay > 0L) {
              ThreadPoolManager.getInstance().schedule(new GameObjectTasks.ActReadyTask(character), this._actDelay);
            } else {
              character.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
            }
          }

        }
      }
    }
  }

  public static class CastEndTimeTask extends RunnableImpl {
    private final HardReference<? extends Creature> _charRef;

    public CastEndTimeTask(Creature character) {
      this._charRef = character.getRef();
    }

    public void runImpl() {
      Creature character = this._charRef.get();
      if (character != null) {
        character.onCastEndTime();
      }
    }
  }

  public static class AltMagicUseTask extends RunnableImpl {
    public final Skill _skill;
    private final HardReference<? extends Creature> _charRef;
    private final HardReference<? extends Creature> _targetRef;

    public AltMagicUseTask(Creature character, Creature target, Skill skill) {
      this._charRef = character.getRef();
      this._targetRef = target.getRef();
      this._skill = skill;
    }

    public void runImpl() {
      Creature cha;
      Creature target;
      if ((cha = this._charRef.get()) != null && (target = this._targetRef.get()) != null) {
        cha.altOnMagicUseTimer(target, this._skill);
      }
    }
  }

  public static class EndCustomHeroTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public EndCustomHeroTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        if (player.getVar("CustomHeroEndTime") != null && !HeroController.getInstance().isCurrentHero(player)) {
          player.setHero(false);
          HeroController.removeSkills(player);
          player.broadcastUserInfo(true);
        }
      }
    }
  }

  public static class EndStandUpTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public EndStandUpTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        player.sittingTaskLaunched = false;
        player.setSitting(false);
        if (!player.getAI().setNextIntention()) {
          player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }

      }
    }
  }

  public static class EndSitDownTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public EndSitDownTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        player.sittingTaskLaunched = false;
        player.getAI().clearNextAction();
      }
    }
  }

  public static class UnJailTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public UnJailTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        player.unblock();
        player.standUp();
        player.teleToLocation(17817, 170079, -3530, ReflectionManager.DEFAULT);
      }
    }
  }

  public static class KickTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public KickTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        player.setOfflineMode(false);
        player.kick();
      }
    }
  }

  public static class WaterTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public WaterTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        if (!player.isDead() && player.isInWater()) {
          double reduceHp = player.getMaxHp() < 100 ? 1.0D : (double)(player.getMaxHp() / 100);
          player.reduceCurrentHp(reduceHp, player, null, false, false, true, false, false, false, false);
          player.sendPacket((new SystemMessage(297)).addNumber((long)reduceHp));
        } else {
          player.stopWaterTask();
        }
      }
    }
  }

  public static class HourlyTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public HourlyTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        int hoursInGame = player.getHoursInGame();
        player.sendPacket((new SystemMessage(764)).addNumber(hoursInGame));
      }
    }
  }

  public static class PvPFlagTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public PvPFlagTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        long diff = Math.abs(System.currentTimeMillis() - player.getlastPvpAttack());
        if (diff > (long)(Config.PVP_TIME + Config.PVP_BLINKING_UNFLAG_TIME)) {
          player.stopPvPFlag();
        } else if (diff > (long)Config.PVP_TIME) {
          player.updatePvPFlag(2);
        } else {
          player.updatePvPFlag(1);
        }

      }
    }
  }

  public static class SoulConsumeTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public SoulConsumeTask(Player player) {
      this._playerRef = (HardReference<Player>) player.getRef();
    }

    public void runImpl() {
      Player player = this._playerRef.get();
      if (player != null) {
        player.setConsumedSouls(player.getConsumedSouls() + 1, null);
      }
    }
  }

  public static class DeleteTask extends RunnableImpl {
    private final HardReference<? extends Creature> _ref;

    public DeleteTask(Creature c) {
      this._ref = c.getRef();
    }

    public void runImpl() {
      Creature c = this._ref.get();
      if (c != null) {
        c.deleteMe();
      }

    }
  }
}
