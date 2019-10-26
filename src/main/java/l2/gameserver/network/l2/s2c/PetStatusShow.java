//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Summon;

public class PetStatusShow extends L2GameServerPacket {
  private int _summonType;

  public PetStatusShow(Summon summon) {
    this._summonType = summon.getSummonType();
  }

  protected final void writeImpl() {
    this.writeC(176);
    this.writeD(this._summonType);
  }
}
