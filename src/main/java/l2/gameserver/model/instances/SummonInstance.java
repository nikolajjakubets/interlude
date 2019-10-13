//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.concurrent.Future;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SetSummonRemainTime;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.templates.npc.NpcTemplate;

public class SummonInstance extends Summon {
  public final int CYCLE = 5000;
  private int _summonSkillId;
  private double _expPenalty = 0.0D;
  private int _itemConsumeIdInTime;
  private int _itemConsumeCountInTime;
  private int _itemConsumeDelay;
  private Future<?> _disappearTask;
  private int _consumeCountdown;
  private int _lifetimeCountdown;
  private int _maxLifetime;

  public SummonInstance(int objectId, NpcTemplate template, Player owner, int lifetime, int consumeid, int consumecount, int consumedelay, Skill skill) {
    super(objectId, template, owner);
    this.setName(template.name);
    this._lifetimeCountdown = this._maxLifetime = lifetime;
    this._itemConsumeIdInTime = consumeid;
    this._itemConsumeCountInTime = consumecount;
    this._consumeCountdown = this._itemConsumeDelay = consumedelay;
    this._summonSkillId = skill.getDisplayId();
    this._disappearTask = ThreadPoolManager.getInstance().schedule(new SummonInstance.Lifetime(), 5000L);
  }

  public HardReference<SummonInstance> getRef() {
    return super.getRef();
  }

  public final int getLevel() {
    return this.getTemplate() != null ? this.getTemplate().level : 0;
  }

  public int getSummonType() {
    return 1;
  }

  public int getCurrentFed() {
    return this._lifetimeCountdown;
  }

  public int getMaxFed() {
    return this._maxLifetime;
  }

  public void setExpPenalty(double expPenalty) {
    this._expPenalty = expPenalty;
  }

  public double getExpPenalty() {
    return this._expPenalty;
  }

  protected void onDeath(Creature killer) {
    super.onDeath(killer);
    this.saveEffects();
    if (this._disappearTask != null) {
      this._disappearTask.cancel(false);
      this._disappearTask = null;
    }

  }

  public int getItemConsumeIdInTime() {
    return this._itemConsumeIdInTime;
  }

  public int getItemConsumeCountInTime() {
    return this._itemConsumeCountInTime;
  }

  public int getItemConsumeDelay() {
    return this._itemConsumeDelay;
  }

  protected synchronized void stopDisappear() {
    if (this._disappearTask != null) {
      this._disappearTask.cancel(false);
      this._disappearTask = null;
    }

  }

  public void unSummon() {
    this.stopDisappear();
    super.unSummon();
  }

  public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
    Player owner = this.getPlayer();
    if (owner != null) {
      if (crit) {
        owner.sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
      }

      if (miss) {
        owner.sendPacket(new SystemMessage(43));
      } else if (!target.isInvul()) {
        owner.sendPacket((new SystemMessage(35)).addNumber(damage));
      }

    }
  }

  public void displayReceiveDamageMessage(Creature attacker, int damage) {
    Player owner = this.getPlayer();
    owner.sendPacket((new SystemMessage(36)).addName(attacker).addNumber((long)damage));
  }

  public int getEffectIdentifier() {
    return this._summonSkillId;
  }

  public boolean isSummon() {
    return true;
  }

  public long getWearedMask() {
    return WeaponType.SWORD.mask();
  }

  class Lifetime extends RunnableImpl {
    Lifetime() {
    }

    public void runImpl() throws Exception {
      Player owner = SummonInstance.this.getPlayer();
      if (owner == null) {
        SummonInstance.this._disappearTask = null;
        SummonInstance.this.unSummon();
      } else {
        int usedtime = SummonInstance.this.isInCombat() ? 5000 : 1250;
        SummonInstance.this._lifetimeCountdown = SummonInstance.this._lifetimeCountdown - usedtime;
        if (SummonInstance.this._lifetimeCountdown <= 0) {
          owner.sendPacket(Msg.SERVITOR_DISAPPEASR_BECAUSE_THE_SUMMONING_TIME_IS_OVER);
          SummonInstance.this._disappearTask = null;
          SummonInstance.this.unSummon();
        } else {
          SummonInstance.this._consumeCountdown = SummonInstance.this._consumeCountdown - usedtime;
          if (SummonInstance.this._itemConsumeIdInTime > 0 && SummonInstance.this._itemConsumeCountInTime > 0 && SummonInstance.this._consumeCountdown <= 0) {
            if (owner.getInventory().destroyItemByItemId(SummonInstance.this.getItemConsumeIdInTime(), (long)SummonInstance.this.getItemConsumeCountInTime())) {
              SummonInstance.this._consumeCountdown = SummonInstance.this._itemConsumeDelay;
              owner.sendPacket((new SystemMessage(1029)).addItemName(SummonInstance.this.getItemConsumeIdInTime()));
            } else {
              owner.sendPacket(Msg.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_WILL_DISAPPEAR);
              SummonInstance.this.unSummon();
            }
          }

          owner.sendPacket(new SetSummonRemainTime(SummonInstance.this));
          SummonInstance.this._disappearTask = ThreadPoolManager.getInstance().schedule(this, 5000L);
        }
      }
    }
  }
}
