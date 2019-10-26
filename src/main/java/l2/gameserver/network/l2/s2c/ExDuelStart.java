//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelStart extends L2GameServerPacket {
  private int _duelType;

  public ExDuelStart(DuelEvent e) {
    this._duelType = e.getDuelType();
  }

  protected final void writeImpl() {
    this.writeEx(77);
    this.writeD(this._duelType);
  }
}
