//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class AutoAttackStop extends L2GameServerPacket {
  private int _targetId;

  public AutoAttackStop(int targetId) {
    this._targetId = targetId;
  }

  protected final void writeImpl() {
    this.writeC(44);
    this.writeD(this._targetId);
  }
}
