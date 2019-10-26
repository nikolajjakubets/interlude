//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class AutoAttackStart extends L2GameServerPacket {
  private int _targetId;

  public AutoAttackStart(int targetId) {
    this._targetId = targetId;
  }

  protected final void writeImpl() {
    this.writeC(43);
    this.writeD(this._targetId);
  }
}
