//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.GameObjectTasks.DeleteTask;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.taskmanager.EffectTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public final class TrapInstance extends NpcInstance {
  private final HardReference<? extends Creature> _ownerRef;
  private final Skill _skill;
  private ScheduledFuture<?> _targetTask;
  private ScheduledFuture<?> _destroyTask;
  private boolean _detected;

  public TrapInstance(int objectId, NpcTemplate template, Creature owner, Skill skill) {
    this(objectId, template, owner, skill, owner.getLoc());
  }

  public TrapInstance(int objectId, NpcTemplate template, Creature owner, Skill skill, Location loc) {
    super(objectId, template);
    this._ownerRef = owner.getRef();
    this._skill = skill;
    this.setReflection(owner.getReflection());
    this.setLevel(owner.getLevel());
    this.setTitle(owner.getName());
    this.setLoc(loc);
  }

  public boolean isTrap() {
    return true;
  }

  public Creature getOwner() {
    return (Creature)this._ownerRef.get();
  }

  protected void onSpawn() {
    super.onSpawn();
    this._destroyTask = ThreadPoolManager.getInstance().schedule(new DeleteTask(this), 120000L);
    this._targetTask = EffectTaskManager.getInstance().scheduleAtFixedRate(new TrapInstance.CastTask(this), 250L, 250L);
  }

  public void broadcastCharInfo() {
    if (this.isDetected()) {
      super.broadcastCharInfo();
    }
  }

  protected void onDelete() {
    Creature owner = this.getOwner();
    if (owner != null && owner.isPlayer()) {
      ((Player)owner).removeTrap(this);
    }

    if (this._destroyTask != null) {
      this._destroyTask.cancel(false);
    }

    this._destroyTask = null;
    if (this._targetTask != null) {
      this._targetTask.cancel(false);
    }

    this._targetTask = null;
    super.onDelete();
  }

  public boolean isDetected() {
    return this._detected;
  }

  public void setDetected(boolean detected) {
    this._detected = detected;
  }

  public int getPAtk(Creature target) {
    Creature owner = this.getOwner();
    return owner == null ? 0 : owner.getPAtk(target);
  }

  public int getMAtk(Creature target, Skill skill) {
    Creature owner = this.getOwner();
    return owner == null ? 0 : owner.getMAtk(target, skill);
  }

  public boolean hasRandomAnimation() {
    return false;
  }

  public boolean isAutoAttackable(Creature attacker) {
    return false;
  }

  public boolean isAttackable(Creature attacker) {
    return false;
  }

  public boolean isInvul() {
    return true;
  }

  public boolean isFearImmune() {
    return true;
  }

  public boolean isParalyzeImmune() {
    return true;
  }

  public boolean isLethalImmune() {
    return true;
  }

  public void showChatWindow(Player player, int val, Object... arg) {
  }

  public void showChatWindow(Player player, String filename, Object... replace) {
  }

  public void onBypassFeedback(Player player, String command) {
  }

  public void onAction(Player player, boolean shift) {
    if (player.getTarget() != this) {
      player.setTarget(this);
      if (player.getTarget() == this) {
        player.sendPacket(new MyTargetSelected(this.getObjectId(), player.getLevel()));
      }
    }

    player.sendActionFailed();
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    return !this.isDetected() && this.getOwner() != forPlayer ? Collections.emptyList() : Collections.singletonList(new NpcInfo(this, forPlayer));
  }

  private static class CastTask extends RunnableImpl {
    private HardReference<NpcInstance> _trapRef;

    public CastTask(TrapInstance trap) {
      this._trapRef = trap.getRef();
    }

    public void runImpl() throws Exception {
      TrapInstance trap = (TrapInstance)this._trapRef.get();
      if (trap != null) {
        Creature owner = trap.getOwner();
        if (owner != null) {
          Iterator var3 = trap.getAroundCharacters(200, 200).iterator();

          while(var3.hasNext()) {
            Creature target = (Creature)var3.next();
            if (target != owner && trap._skill.checkTarget(owner, target, (Creature)null, false, false) == null) {
              List<Creature> targets = new ArrayList();
              if (trap._skill.getTargetType() != SkillTargetType.TARGET_AREA) {
                targets.add(target);
              } else {
                Iterator var6 = trap.getAroundCharacters(trap._skill.getSkillRadius(), 128).iterator();

                while(var6.hasNext()) {
                  Creature t = (Creature)var6.next();
                  if (trap._skill.checkTarget(owner, t, (Creature)null, false, false) == null) {
                    targets.add(target);
                  }
                }
              }

              trap._skill.useSkill(trap, targets);
              if (target.isPlayer()) {
                target.sendMessage(new CustomMessage("common.Trap", target.getPlayer(), new Object[0]));
              }

              trap.deleteMe();
              break;
            }
          }

        }
      }
    }
  }
}
