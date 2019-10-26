//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public final class EffectCharge extends Effect {
  public static final int MAX_CHARGE = 7;
  private final int _charges;
  private final boolean _fullCharge;

  public EffectCharge(Env env, EffectTemplate template) {
    super(env, template);
    this._charges = template.getParam().getInteger("charges", 7);
    this._fullCharge = template.getParam().getBool("fullCharge", false);
  }

  public void onStart() {
    super.onStart();
    if (this.getEffected().isPlayer()) {
      Player player = (Player)this.getEffected();
      if (player.getIncreasedForce() >= this._charges) {
        player.sendPacket(Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
      } else if (this._fullCharge) {
        player.setIncreasedForce(this._charges);
      } else {
        player.setIncreasedForce(player.getIncreasedForce() + 1);
      }
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
