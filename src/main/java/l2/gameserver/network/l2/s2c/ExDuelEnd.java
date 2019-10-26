//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelEnd extends L2GameServerPacket {
  private int _duelType;

  public ExDuelEnd(DuelEvent e) {
    this._duelType = e.getDuelType();
  }

  protected final void writeImpl() {
    this.writeEx(78);
    this.writeD(this._duelType);
  }
}
