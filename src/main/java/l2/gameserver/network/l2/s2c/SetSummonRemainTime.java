//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Summon;

public class SetSummonRemainTime extends L2GameServerPacket {
  private final int _maxFed;
  private final int _curFed;

  public SetSummonRemainTime(Summon summon) {
    this._curFed = summon.getCurrentFed();
    this._maxFed = summon.getMaxFed();
  }

  protected final void writeImpl() {
    this.writeC(209);
    this.writeD(this._maxFed);
    this.writeD(this._curFed);
  }
}
