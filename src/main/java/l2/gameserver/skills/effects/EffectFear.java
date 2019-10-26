//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Env;
import l2.gameserver.utils.PositionUtils;

public final class EffectFear extends Effect {
  public static final double FEAR_RANGE = 2600.0D;

  public EffectFear(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    if (this._effected.isFearImmune()) {
      this.getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
      return false;
    } else {
      Player player = this._effected.getPlayer();
      if (player != null) {
        SiegeEvent<?, ?> siegeEvent = (SiegeEvent)player.getEvent(SiegeEvent.class);
        if (this._effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance)this._effected)) {
          this.getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
          return false;
        }
      }

      if (this._effected.isInZonePeace()) {
        this.getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
        return false;
      } else {
        return super.checkCondition();
      }
    }
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.startFear()) {
      this._effected.abortAttack(true, true);
      this._effected.abortCast(true, true);
      this._effected.stopMove();
    }

    double angle = Math.toRadians(PositionUtils.calculateAngleFrom(this._effector, this._effected));
    int oldX = this._effected.getX();
    int oldY = this._effected.getY();
    int x = oldX + (int)(2600.0D * Math.cos(angle));
    int y = oldY + (int)(2600.0D * Math.sin(angle));
    this._effected.setRunning();
    this._effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, this._effected.getZ(), x, y, this._effected.getGeoIndex()), 0, false);
  }

  public void onExit() {
    super.onExit();
    this._effected.stopFear();
    this._effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
  }

  public boolean onActionTime() {
    return false;
  }
}
