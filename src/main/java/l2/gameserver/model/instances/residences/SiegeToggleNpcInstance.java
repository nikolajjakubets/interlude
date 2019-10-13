//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances.residences;

import java.util.Set;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.templates.npc.NpcTemplate;

public abstract class SiegeToggleNpcInstance extends NpcInstance {
  private NpcInstance _fakeInstance;
  private int _maxHp;

  public SiegeToggleNpcInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this.setHasChatWindow(false);
  }

  public void setMaxHp(int maxHp) {
    this._maxHp = maxHp;
  }

  public void setZoneList(Set<String> set) {
  }

  public void register(Spawner spawn) {
  }

  public void initFake(int fakeNpcId) {
    this._fakeInstance = NpcHolder.getInstance().getTemplate(fakeNpcId).getNewInstance();
    this._fakeInstance.setCurrentHpMp(1.0D, (double)this._fakeInstance.getMaxMp());
    this._fakeInstance.setHasChatWindow(false);
  }

  public abstract void onDeathImpl(Creature var1);

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    this.setCurrentHp(Math.max(this.getCurrentHp() - damage, 0.0D), false);
    if (this.getCurrentHp() < 0.5D) {
      this.doDie(attacker);
      this.onDeathImpl(attacker);
      this.decayMe();
      this._fakeInstance.spawnMe(this.getLoc());
    }

  }

  public boolean isAutoAttackable(Creature attacker) {
    if (attacker == null) {
      return false;
    } else {
      Player player = attacker.getPlayer();
      if (player == null) {
        return false;
      } else {
        SiegeEvent<?, ?> siegeEvent = (SiegeEvent)this.getEvent(SiegeEvent.class);
        return siegeEvent != null && siegeEvent.isInProgress();
      }
    }
  }

  public boolean isAttackable(Creature attacker) {
    return this.isAutoAttackable(attacker);
  }

  public boolean isInvul() {
    return false;
  }

  public boolean hasRandomAnimation() {
    return false;
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

  public void decayFake() {
    this._fakeInstance.decayMe();
  }

  public int getMaxHp() {
    return this._maxHp;
  }

  protected void onDecay() {
    this.decayMe();
    this._spawnAnimation = 2;
  }

  public Clan getClan() {
    return null;
  }
}
