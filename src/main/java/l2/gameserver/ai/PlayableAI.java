//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.*;
import l2.gameserver.model.Skill.SkillNextAction;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;

@Slf4j
public class PlayableAI extends CharacterAI {
  private volatile int thinking = 0;
  protected Object _intention_arg0 = null;
  protected Object _intention_arg1 = null;
  protected Skill _skill;
  private NextAction _nextAction;
  private Object _nextAction_arg0;
  private Object _nextAction_arg1;
  private boolean _nextAction_arg2;
  private boolean _nextAction_arg3;
  protected boolean _forceUse;
  private boolean _dontMove;
  protected ScheduledFuture<?> _followTask;

  public PlayableAI(Playable actor) {
    super(actor);
  }

  public void changeIntention(CtrlIntention intention, Object arg0, Object arg1) {
    super.changeIntention(intention, arg0, arg1);
    this._intention_arg0 = arg0;
    this._intention_arg1 = arg1;
  }

  public void setIntention(CtrlIntention intention, Object arg0, Object arg1) {
    this._intention_arg0 = null;
    this._intention_arg1 = null;
    super.setIntention(intention, arg0, arg1);
  }

  protected void onIntentionCast(Skill skill, Creature target) {
    this._skill = skill;
    super.onIntentionCast(skill, target);
  }

  public NextAction getNextAction() {
    return this._nextAction;
  }

  public boolean isIntendingInteract(GameObject withTarget) {
    return this.getIntention() == CtrlIntention.AI_INTENTION_INTERACT && this._intention_arg0 == withTarget;
  }

  public void setNextAction(NextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3) {
    this._nextAction = action;
    this._nextAction_arg0 = arg0;
    this._nextAction_arg1 = arg1;
    this._nextAction_arg2 = arg2;
    this._nextAction_arg3 = arg3;
  }

  public boolean setNextIntention() {
    NextAction nextAction = this._nextAction;
    Object nextAction_arg0 = this._nextAction_arg0;
    Object nextAction_arg1 = this._nextAction_arg1;
    boolean nextAction_arg2 = this._nextAction_arg2;
    boolean nextAction_arg3 = this._nextAction_arg3;
    Playable actor = this.getActor();
    if (nextAction != null && !actor.isActionsDisabled()) {
      Creature target;
      GameObject object;
      switch (nextAction) {
        case ATTACK:
          if (nextAction_arg0 == null) {
            return false;
          }

          target = (Creature) nextAction_arg0;
          this._forceUse = nextAction_arg2;
          this._dontMove = nextAction_arg3;
          this.clearNextAction();
          this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
          break;
        case CAST:
          if (nextAction_arg0 != null && nextAction_arg1 != null) {
            Skill skill = (Skill) nextAction_arg0;
            target = (Creature) nextAction_arg1;
            this._forceUse = nextAction_arg2;
            this._dontMove = nextAction_arg3;
            this.clearNextAction();
            if (!skill.checkCondition(actor, target, this._forceUse, this._dontMove, true)) {
              if (skill.getSkillNextAction() == SkillNextAction.ATTACK && !actor.equals(target) && !this._forceUse) {
                this.setNextAction(NextAction.ATTACK, target, null, false, false);
                return this.setNextIntention();
              }

              return false;
            }

            this.setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
            break;
          }

          return false;
        case MOVE:
          if (nextAction_arg0 != null && nextAction_arg1 != null) {
            Location loc = (Location) nextAction_arg0;
            Integer offset = (Integer) nextAction_arg1;
            this.clearNextAction();
            actor.moveToLocation(loc, offset, nextAction_arg2);
            break;
          }

          return false;
        case REST:
          actor.sitDown(null);
          break;
        case INTERACT:
          if (nextAction_arg0 == null) {
            return false;
          }

          object = (GameObject) nextAction_arg0;
          this.clearNextAction();
          this.onIntentionInteract(object);
          break;
        case EQUIP:
          if (nextAction_arg0 == null) {
            return false;
          }

          ItemInstance item = (ItemInstance) nextAction_arg0;
          item.getTemplate().getHandler().useItem(this.getActor(), item, nextAction_arg2);
          this.clearNextAction();
          break;
        case PICKUP:
          if (nextAction_arg0 == null) {
            return false;
          }

          object = (GameObject) nextAction_arg0;
          this.clearNextAction();
          this.onIntentionPickUp(object);
          break;
        default:
          return false;
      }

      return true;
    } else {
      return false;
    }
  }

  public void clearNextAction() {
    this._nextAction = null;
    this._nextAction_arg0 = null;
    this._nextAction_arg1 = null;
    this._nextAction_arg2 = false;
    this._nextAction_arg3 = false;
  }

  protected void onEvtFinishCasting(Skill skill, Creature target) {
    if (!this.setNextIntention()) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

  }

  protected void onEvtReadyToAct() {
    if (!this.setNextIntention()) {
      this.onEvtThink();
    }

  }

  protected void onEvtArrived() {
    if (!this.setNextIntention()) {
      if (this.getIntention() != CtrlIntention.AI_INTENTION_INTERACT && this.getIntention() != CtrlIntention.AI_INTENTION_PICK_UP) {
        this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
      } else {
        this.onEvtThink();
      }
    }

  }

  protected void onEvtArrivedTarget() {
    switch (this.getIntention()) {
      case AI_INTENTION_ATTACK:
        this.thinkAttack(false);
        break;
      case AI_INTENTION_CAST:
        this.thinkCast(false);
        break;
      case AI_INTENTION_FOLLOW:
        this.thinkFollow();
        break;
      default:
        this.onEvtThink();
    }

  }

  protected final void onEvtThink() {
    Playable actor = this.getActor();
    if (!actor.isActionsDisabled()) {
      try {
        if (this.thinking++ <= 1) {
          switch (this.getIntention()) {
            case AI_INTENTION_ATTACK:
              this.thinkAttack(true);
              return;
            case AI_INTENTION_CAST:
              this.thinkCast(true);
              return;
            case AI_INTENTION_FOLLOW:
              this.thinkFollow();
              return;
            case AI_INTENTION_ACTIVE:
              this.thinkActive();
              return;
            case AI_INTENTION_PICK_UP:
              this.thinkPickUp();
              return;
            case AI_INTENTION_INTERACT:
              this.thinkInteract();
              return;
            default:
          }
        }
      } catch (Exception e) {
        log.error("onEvtThink: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
      } finally {
        --this.thinking;
      }

    }
  }

  protected void thinkActive() {
  }

  protected static boolean isThinkImplyZ(Playable actor, GameObject target) {
    if (!actor.isFlying() && !actor.isInWater()) {
      if (target != null) {
        if (target.isDoor()) {
          return false;
        }

        if (target.isCreature()) {
          Creature creature = (Creature) target;
          return creature.isInWater() || creature.isFlying();
        }
      }

      return false;
    } else {
      return true;
    }
  }

  protected void thinkAttack(boolean checkRange) {
    final Playable actor = this.getActor();
    Player player = actor.getPlayer();
    if (player == null) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    } else if (!actor.isActionsDisabled() && !actor.isAttackingDisabled()) {
      boolean isPosessed = actor instanceof Summon && ((Summon) actor).isDepressed();
      final Creature attack_target = this.getAttackTarget();
      if (attack_target != null && !attack_target.isDead()) {
        label132:
        {
          if (!isPosessed) {
            if (this._forceUse) {
              if (!attack_target.isAttackable(actor)) {
                break label132;
              }
            } else if (!attack_target.isAutoAttackable(actor)) {
              break label132;
            }
          }

          if (!checkRange) {
            this.clientStopMoving();
            actor.doAttack(attack_target);
            return;
          }

          int clientClipRange = player.getNetConnection() != null ? player.getNetConnection().getPawnClippingRange() : GameClient.DEFAULT_PAWN_CLIPPING_RANGE;
          int collisions = (int) (actor.getColRadius() + attack_target.getColRadius());
          boolean incZ = isThinkImplyZ(actor, attack_target);
          int dist = (int) (!incZ ? actor.getDistance(attack_target) : actor.getDistance3D(attack_target)) - collisions;
          boolean useActAsAtkRange = attack_target.isDoor();
          int atkRange = !useActAsAtkRange ? actor.getPhysicalAttackRange() : attack_target.getActingRange();
          boolean canSee = dist < clientClipRange && GeoEngine.canSeeTarget(actor, attack_target, incZ);
          if (canSee || atkRange <= 256 && Math.abs(actor.getZ() - attack_target.getZ()) <= 256) {
            if (dist <= atkRange) {
              if (!canSee) {
                actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
                return;
              }

              this.clientStopMoving(false);
              actor.doAttack(attack_target);
            } else if (!this._dontMove) {
              final int moveIndent = getIndentRange(atkRange) + (!useActAsAtkRange ? collisions : 0);
              final int moveRange = Math.max(moveIndent, atkRange + (!useActAsAtkRange ? collisions : 0));
              ThreadPoolManager.getInstance().execute(new RunnableImpl() {
                public void runImpl() throws Exception {
                  actor.moveToRelative(attack_target, moveIndent, moveRange);
                }
              });
            } else {
              actor.sendActionFailed();
            }

            return;
          }

          actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
          this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
          actor.sendActionFailed();
          return;
        }
      }

      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      actor.sendActionFailed();
    } else {
      actor.sendActionFailed();
    }
  }

  protected void thinkCast(boolean checkRange) {
    final Playable actor = this.getActor();
    final Creature target = this.getAttackTarget();
    if (this._skill.getSkillType() != SkillType.CRAFT && !this._skill.isToggle()) {
      if (target != null && (target.isDead() == this._skill.getCorpse() || this._skill.isNotTargetAoE())) {
        if (!checkRange) {
          if (this._skill.getSkillNextAction() == SkillNextAction.ATTACK && !actor.equals(target) && !this._forceUse) {
            this.setNextAction(NextAction.ATTACK, target, null, false, false);
          } else {
            this.clearNextAction();
          }

          this.clientStopMoving();
          if (this._skill.checkCondition(actor, target, this._forceUse, this._dontMove, true)) {
            actor.doCast(this._skill, target, this._forceUse);
          } else {
            this.setNextIntention();
            if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
              this.thinkAttack(true);
            }
          }

        } else {
          int collisions = (int) (actor.getColRadius() + target.getColRadius());
          boolean incZ = isThinkImplyZ(actor, target);
          int dist = (int) (!incZ ? actor.getDistance(target) : actor.getDistance3D(target)) - collisions;
          boolean useActAsCastRange = target.isDoor();
          int castRange = Math.max(16, actor.getMagicalAttackRange(this._skill));
          boolean canSee;
          if (this._skill.getSkillType() == SkillType.TAKECASTLE) {
            canSee = true;
          } else {
            canSee = GeoEngine.canSeeTarget(actor, target, actor.isFlying());
          }

          boolean noRangeSkill = this._skill.getCastRange() == 32767;
          if (!noRangeSkill && !canSee && (castRange > 256 || Math.abs(actor.getZ() - target.getZ()) > 256)) {
            actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
          } else {
            if (!noRangeSkill && dist > castRange) {
              if (!this._dontMove) {
                final int moveIndent = getIndentRange(castRange) + (!useActAsCastRange ? collisions : 0);
                final int moveRange = Math.max(moveIndent, castRange + (!useActAsCastRange ? collisions : 0));
                ThreadPoolManager.getInstance().execute(new RunnableImpl() {
                  public void runImpl() throws Exception {
                    actor.moveToRelative(target, moveIndent, moveRange);
                  }
                });
              } else {
                actor.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
                this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
              }
            } else {
              if (!noRangeSkill && !canSee) {
                actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
                return;
              }

              if (this._skill.getSkillNextAction() == SkillNextAction.ATTACK && !actor.equals(target) && !this._forceUse) {
                this.setNextAction(NextAction.ATTACK, target, null, false, false);
              } else {
                this.clearNextAction();
              }

              if (this._skill.checkCondition(actor, target, this._forceUse, this._dontMove, true)) {
                this.clientStopMoving(false);
                actor.doCast(this._skill, target, this._forceUse);
              } else {
                this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
              }
            }

          }
        }
      } else {
        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        actor.sendActionFailed();
      }
    } else {
      if (this._skill.checkCondition(actor, target, this._forceUse, this._dontMove, true)) {
        actor.doCast(this._skill, target, this._forceUse);
      }

    }
  }

  protected void thinkPickUp() {
    final Playable actor = this.getActor();
    final GameObject target = (GameObject) this._intention_arg0;
    if (target == null) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    } else {
      if (actor.isInRange(target, target.getActingRange() + 16) && Math.abs(actor.getZ() - target.getZ()) < 64) {
        if (actor.isPlayer() || actor.isPet()) {
          actor.doPickupItem(target);
        }

        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      } else {
        final Location moveToLoc = new Location(target.getX() & -8, target.getY() & -8, target.getZ());
        ThreadPoolManager.getInstance().execute(new RunnableImpl() {
          public void runImpl() {
            actor.moveToLocation(moveToLoc, 0, true);
            PlayableAI.this.setNextAction(NextAction.PICKUP, target, null, false, false);
          }
        });
      }

    }
  }

  protected void thinkInteract() {
    final Playable actor = this.getActor();
    final GameObject target = (GameObject) this._intention_arg0;
    if (target == null) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    } else {
      boolean incZ = isThinkImplyZ(actor, target);
      int dist = (int) (!incZ ? actor.getDistance(target) : actor.getDistance3D(target));
      final int actRange = target.getActingRange();
      if (dist <= actRange) {
        if (actor.isPlayer()) {
          ((Player) actor).doInteract(target);
        }

        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      } else {
        final int moveIndent = getIndentRange(actRange);
        ThreadPoolManager.getInstance().execute(new RunnableImpl() {
          public void runImpl() throws Exception {
            actor.moveToRelative(target, moveIndent, actRange);
          }
        });
        this.setNextAction(NextAction.INTERACT, target, null, false, false);
      }

    }
  }

  protected void thinkFollow() {
    Playable actor = this.getActor();
    Creature target = (Creature) this._intention_arg0;
    if (target != null && !target.isAlikeDead()) {
      if (actor.isFollowing() && actor.getFollowTarget() == target) {
        this.clientActionFailed();
      } else {
        int clientClipRange = actor.getPlayer() != null && actor.getPlayer().getNetConnection() != null ? actor.getPlayer().getNetConnection().getPawnClippingRange() : GameClient.DEFAULT_PAWN_CLIPPING_RANGE;
        int collisions = (int) (actor.getColRadius() + target.getColRadius());
        boolean incZ = isThinkImplyZ(actor, target);
        int dist = (int) (!incZ ? actor.getDistance(target) : actor.getDistance3D(target)) - collisions;
        int followRange = Config.FOLLOW_ARRIVE_DISTANCE;
        if (dist > clientClipRange) {
          this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
          this.clientActionFailed();
          this.clientStopMoving();
        } else {
          if (dist <= followRange + 16 || actor.isMovementDisabled()) {
            this.clientActionFailed();
          }

          if (this._followTask != null) {
            this._followTask.cancel(false);
            this._followTask = null;
          }

          this._followTask = this.scheduleThinkFollowTask();
        }
      }
    } else {
      this.clientActionFailed();
    }
  }

  protected ScheduledFuture<?> scheduleThinkFollowTask() {
    return ThreadPoolManager.getInstance().schedule(new PlayableAI.ThinkFollow(this.getActor()), Config.MOVE_TASK_QUANTUM_PC);
  }

  protected void onIntentionInteract(GameObject target) {
    Playable actor = this.getActor();
    if (actor.isActionsDisabled()) {
      this.setNextAction(NextAction.INTERACT, target, null, false, false);
      this.clientActionFailed();
    } else {
      this.clearNextAction();
      this.changeIntention(CtrlIntention.AI_INTENTION_INTERACT, target, null);
      this.onEvtThink();
    }
  }

  protected void onIntentionPickUp(GameObject object) {
    Playable actor = this.getActor();
    if (actor.isActionsDisabled()) {
      this.setNextAction(NextAction.PICKUP, object, null, false, false);
      this.clientActionFailed();
    } else {
      this.clearNextAction();
      this.changeIntention(CtrlIntention.AI_INTENTION_PICK_UP, object, null);
      this.onEvtThink();
    }
  }

  protected void onEvtDead(Creature killer) {
    this.clearNextAction();
    super.onEvtDead(killer);
  }

  protected void onEvtFakeDeath() {
    this.clearNextAction();
    super.onEvtFakeDeath();
  }

  public void lockTarget(Creature target) {
    Playable actor = this.getActor();
    if (target != null && !target.isDead()) {
      if (actor.getAggressionTarget() == null) {
        GameObject actorStoredTarget = actor.getTarget();
        actor.setAggressionTarget(target);
        actor.setTarget(target);
        this.clearNextAction();
        if (actorStoredTarget != target) {
          actor.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
        }
      }
    } else {
      actor.setAggressionTarget(null);
    }

  }

  public void Attack(GameObject target, boolean forceUse, boolean dontMove) {
    Playable actor = this.getActor();
    if (!target.isCreature() || !actor.isActionsDisabled() && !actor.isAttackingDisabled()) {
      this._dontMove = dontMove;
      this._forceUse = forceUse;
      this.clearNextAction();
      this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    } else {
      this.setNextAction(NextAction.ATTACK, target, null, forceUse, false);
      actor.sendActionFailed();
    }
  }

  public void Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
    Playable actor = this.getActor();
    if (!skill.altUse() && !skill.isToggle()) {
      if (actor.isActionsDisabled()) {
        this.setNextAction(NextAction.CAST, skill, target, forceUse, dontMove);
        this.clientActionFailed();
      } else {
        this._forceUse = forceUse;
        this._dontMove = dontMove;
        this.clearNextAction();
        this.setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
      }
    } else {
      if (!skill.isToggle() && !skill.isHandler() || !actor.isOutOfControl() && !actor.isStunned() && !actor.isSleeping() && !actor.isParalyzed() && !actor.isAlikeDead()) {
        actor.altUseSkill(skill, target);
      } else {
        this.clientActionFailed();
      }

    }
  }

  public Playable getActor() {
    return (Playable) super.getActor();
  }

  protected void onEvtForgetObject(GameObject object) {
    super.onEvtForgetObject(object);
    if (this.getIntention() == CtrlIntention.AI_INTENTION_FOLLOW && this._intention_arg0 == object) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

  }

  protected static class ThinkFollow extends RunnableImpl {
    private final HardReference<? extends Playable> _actorRef;

    public ThinkFollow(Playable actor) {
      this._actorRef = (HardReference<? extends Playable>) actor.getRef();
    }

    public void runImpl() throws Exception {
      Playable actor = this._actorRef.get();
      if (actor != null) {
        PlayableAI actorAI = (PlayableAI) actor.getAI();
        if (actorAI.getIntention() == CtrlIntention.AI_INTENTION_FOLLOW) {
          Creature target = (Creature) actorAI._intention_arg0;
          if (target != null && !target.isAlikeDead()) {
            int clientClipRange = actor.getPlayer() != null && actor.getPlayer().getNetConnection() != null ? actor.getPlayer().getNetConnection().getPawnClippingRange() : GameClient.DEFAULT_PAWN_CLIPPING_RANGE;
            int collisions = (int) (actor.getColRadius() + target.getColRadius());
            boolean incZ = PlayableAI.isThinkImplyZ(actor, target);
            int dist = (int) (!incZ ? actor.getDistance(target) : actor.getDistance3D(target)) - collisions;
            int followIndent = Math.min(clientClipRange, target.getActingRange());
            int followRange = Config.FOLLOW_ARRIVE_DISTANCE;
            if (dist <= clientClipRange && dist <= 2 << World.SHIFT_BY) {
              Player player = actor.getPlayer();
              if (player == null || player.isLogoutStarted() || (actor.isPet() || actor.isSummon()) && player.getPet() != actor) {
                actorAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                actorAI.clientStopMoving();
              } else {
                if (dist > followRange && (!actor.isFollowing() || actor.getFollowTarget() != target)) {
                  actor.moveToRelative(target, followIndent + collisions, followRange + collisions);
                }

                actorAI._followTask = actorAI.scheduleThinkFollowTask();
              }
            } else {
              actorAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
              actorAI.clientStopMoving();
            }
          } else {
            actorAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
          }
        }
      }
    }
  }
}
