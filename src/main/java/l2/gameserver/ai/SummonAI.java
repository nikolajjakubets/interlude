//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.*;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;

import java.util.concurrent.ScheduledFuture;

public class SummonAI extends PlayableAI {
  private HardReference<Playable> _runAwayTargetRef = HardReferences.emptyRef();

  public SummonAI(Summon actor) {
    super(actor);
  }

  protected void thinkActive() {
    Summon actor = this.getActor();
    this.clearNextAction();
    if (actor.isDepressed()) {
      this.setAttackTarget(actor.getPlayer());
      this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
      this.thinkAttack(true);
    } else if (actor.isFollowMode()) {
      this.changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.getPlayer(), null);
      this.thinkFollow();
    }

    super.thinkActive();
  }

  protected void thinkAttack(boolean checkRange) {
    Summon actor = this.getActor();
    if (actor.isDepressed()) {
      this.setAttackTarget(actor.getPlayer());
    }

    super.thinkAttack(checkRange);
  }

  private void tryRunAway() {
    Summon actor = this.getActor();
    if (!actor.isDead() && !actor.isDepressed()) {
      Player owner = actor.getPlayer();
      Playable runAwayTarget = this._runAwayTargetRef.get();
      if (owner != null && runAwayTarget != null && !owner.isDead() && !owner.isOutOfControl()) {
        if (runAwayTarget.isInCombat() && actor.getDistance(runAwayTarget) < (double)actor.getActingRange()) {
          int radius = getIndentRange(actor.getActingRange());
          Location ownerLoc = owner.getLoc();
          Location targetLoc = runAwayTarget.getLoc();
          double radian = PositionUtils.convertHeadingToRadian(('耀' + PositionUtils.getHeadingTo(ownerLoc, targetLoc)) % '\uffff');
          Location ne = (new Location((int)(0.5D + (double)ownerLoc.getX() + (double)radius * Math.sin(radian)), (int)(0.5D + (double)ownerLoc.getY() + (double)radius * Math.cos(radian)), ownerLoc.getZ())).correctGeoZ();
          actor.moveToLocation(ne, 0, true);
          return;
        }

        this._runAwayTargetRef = HardReferences.emptyRef();
      } else {
        this._runAwayTargetRef = HardReferences.emptyRef();
      }
    }

  }

  protected void onEvtArrived() {
    if (!this.setNextIntention()) {
      if (this.getIntention() != CtrlIntention.AI_INTENTION_INTERACT && this.getIntention() != CtrlIntention.AI_INTENTION_PICK_UP && this.getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
        this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
      } else {
        this.onEvtThink();
      }
    }

  }

  protected void onEvtAttacked(Creature attacker, int damage) {
    Summon actor = this.getActor();
    if (attacker != null && actor.getPlayer().isDead() && !actor.isDepressed()) {
      this.Attack(attacker, false, false);
    }

    if (attacker != null && attacker.isPlayable()) {
      this._runAwayTargetRef = (HardReference<Playable>) attacker.getRef();
    }

    super.onEvtAttacked(attacker, damage);
  }

  public Summon getActor() {
    return (Summon)super.getActor();
  }

  protected ScheduledFuture<?> scheduleThinkFollowTask() {
    return ThreadPoolManager.getInstance().schedule(new SummonAI.ThinkFollowForSummon(this.getActor()), Config.MOVE_TASK_QUANTUM_NPC);
  }

  protected static class ThinkFollowForSummon extends RunnableImpl {
    private final HardReference<? extends Summon> _actorRef;

    public ThinkFollowForSummon(Summon actor) {
      this._actorRef = (HardReference<? extends Summon>) actor.getRef();
    }

    public void runImpl() throws Exception {
      Summon actor = this._actorRef.get();
      if (actor != null) {
        SummonAI actorAI = actor.getAI();
        if (actorAI.getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
          if (actorAI.getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
            actor.setFollowMode(false);
          }

        } else {
          Creature target = (Creature)actorAI._intention_arg0;
          if (target != null && !target.isAlikeDead()) {
            int clientClipRange = actor.getPlayer() != null && actor.getPlayer().getNetConnection() != null ? actor.getPlayer().getNetConnection().getPawnClippingRange() : GameClient.DEFAULT_PAWN_CLIPPING_RANGE;
            int collisions = (int)(actor.getColRadius() + target.getColRadius());
            boolean incZ = PlayableAI.isThinkImplyZ(actor, target);
            int dist = (int)(!incZ ? actor.getDistance(target) : actor.getDistance3D(target)) - collisions;
            int followIndent = Math.min(clientClipRange, target.getActingRange());
            int followRange = actor.getActingRange();
            if (dist <= clientClipRange && dist <= 2 << World.SHIFT_BY) {
              Player player = actor.getPlayer();
              if (player != null && !player.isLogoutStarted() && (!actor.isPet() && !actor.isSummon() || player.getPet() == actor)) {
                if (dist > followRange) {
                  if (!actor.isFollowing() || actor.getFollowTarget() != target) {
                    actor.moveToRelative(target, CharacterAI.getIndentRange(followIndent), followRange);
                  }
                } else {
                  actorAI.tryRunAway();
                }

                actorAI._followTask = actorAI.scheduleThinkFollowTask();
              } else {
                actorAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                actorAI.clientStopMoving();
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
