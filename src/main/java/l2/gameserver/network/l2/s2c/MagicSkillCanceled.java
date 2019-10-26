//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;

public class MagicSkillCanceled extends L2GameServerPacket {
  private int _casterId;
  private final int _casterX;
  private final int _casterY;

  public MagicSkillCanceled(Creature caster) {
    this._casterId = caster.getObjectId();
    this._casterX = caster.getX();
    this._casterY = caster.getY();
  }

  protected final void writeImpl() {
    this.writeC(73);
    this.writeD(this._casterId);
  }

  public L2GameServerPacket packet(Player player) {
    if (player != null && !player.isInObserverMode()) {
      if (player.buffAnimRange() < 0) {
        return null;
      } else if (player.buffAnimRange() == 0) {
        return this._casterId == player.getObjectId() ? super.packet(player) : null;
      } else {
        return player.getDistance(this._casterX, this._casterY) < (double)player.buffAnimRange() ? super.packet(player) : null;
      }
    } else {
      return super.packet(player);
    }
  }
}
