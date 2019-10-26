//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelReady extends L2GameServerPacket {
  private int _duelType;

  public ExDuelReady(DuelEvent event) {
    this._duelType = event.getDuelType();
  }

  protected final void writeImpl() {
    this.writeEx(76);
    this.writeD(this._duelType);
  }
}
