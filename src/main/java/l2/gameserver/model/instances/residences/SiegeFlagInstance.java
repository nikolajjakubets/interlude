//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances.residences;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.npc.NpcTemplate;

public class SiegeFlagInstance extends NpcInstance {
  private SiegeClanObject _owner;
  private long _lastAnnouncedAttackedTime = 0L;

  public SiegeFlagInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this.setHasChatWindow(false);
  }

  public String getName() {
    return this._owner.getClan().getName();
  }

  public Clan getClan() {
    return this._owner.getClan();
  }

  public String getTitle() {
    return "";
  }

  public boolean isAutoAttackable(Creature attacker) {
    Player player = attacker.getPlayer();
    if (player != null && !this.isInvul()) {
      Clan clan = player.getClan();
      return clan == null || this._owner.getClan() != clan;
    } else {
      return false;
    }
  }

  public boolean isAttackable(Creature attacker) {
    return true;
  }

  protected void onDeath(Creature killer) {
    this._owner.setFlag((NpcInstance)null);
    super.onDeath(killer);
  }

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    if (System.currentTimeMillis() - this._lastAnnouncedAttackedTime > 120000L) {
      this._lastAnnouncedAttackedTime = System.currentTimeMillis();
      this._owner.getClan().broadcastToOnlineMembers(new IStaticPacket[]{SystemMsg.YOUR_BASE_IS_BEING_ATTACKED});
    }

    super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
  }

  public boolean hasRandomAnimation() {
    return false;
  }

  public boolean isInvul() {
    return this._isInvul;
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

  public boolean isHealBlocked() {
    return true;
  }

  public boolean isEffectImmune() {
    return true;
  }

  public void setClan(SiegeClanObject owner) {
    this._owner = owner;
  }
}
