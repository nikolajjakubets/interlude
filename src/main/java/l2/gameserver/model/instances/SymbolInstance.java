//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.GameObjectTasks.DeleteTask;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.taskmanager.EffectTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;

public class SymbolInstance extends NpcInstance {
  private final Creature _owner;
  private final Skill _skill;
  private ScheduledFuture<?> _targetTask;
  private ScheduledFuture<?> _destroyTask;

  public SymbolInstance(int objectId, NpcTemplate template, Creature owner, Skill skill) {
    super(objectId, template);
    this._owner = owner;
    this._skill = skill;
    this.setReflection(owner.getReflection());
    this.setLevel(owner.getLevel());
    this.setTitle(owner.getName());
  }

  public Creature getOwner() {
    return this._owner;
  }

  protected void onSpawn() {
    super.onSpawn();
    this._destroyTask = ThreadPoolManager.getInstance().schedule(new DeleteTask(this), 120000L);
    this._targetTask = EffectTaskManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        Iterator var1 = SymbolInstance.this.getAroundCharacters(200, 200).iterator();

        while(true) {
          Creature target;
          do {
            if (!var1.hasNext()) {
              return;
            }

            target = (Creature)var1.next();
          } while(SymbolInstance.this._skill.checkTarget(SymbolInstance.this._owner, target, (Creature)null, false, false) != null);

          List<Creature> targets = new ArrayList();
          if (!SymbolInstance.this._skill.isAoE()) {
            targets.add(target);
          } else {
            Iterator var4 = SymbolInstance.this.getAroundCharacters(SymbolInstance.this._skill.getSkillRadius(), 128).iterator();

            while(var4.hasNext()) {
              Creature t = (Creature)var4.next();
              if (SymbolInstance.this._skill.checkTarget(SymbolInstance.this._owner, t, (Creature)null, false, false) == null) {
                targets.add(target);
              }
            }
          }

          SymbolInstance.this._skill.useSkill(SymbolInstance.this, targets);
        }
      }
    }, 1000L, Rnd.get(4000L, 7000L));
  }

  protected void onDelete() {
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
    player.sendActionFailed();
  }

  public Clan getClan() {
    return null;
  }
}
