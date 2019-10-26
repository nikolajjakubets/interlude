//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.s2c.FinishRotating;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.StartRotating;
import l2.gameserver.stats.Env;

public final class EffectBluff extends Effect {
  public EffectBluff(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return this.getEffected().isNpc() && !this.getEffected().isMonster() ? false : super.checkCondition();
  }

  public void onStart() {
    this.getEffected().broadcastPacket(new L2GameServerPacket[]{new StartRotating(this.getEffected(), this.getEffected().getHeading(), 1, 65535)});
    this.getEffected().broadcastPacket(new L2GameServerPacket[]{new FinishRotating(this.getEffected(), this.getEffector().getHeading(), 65535)});
    this.getEffected().setHeading(this.getEffector().getHeading());
  }

  public boolean isHidden() {
    return true;
  }

  public boolean onActionTime() {
    return false;
  }
}
