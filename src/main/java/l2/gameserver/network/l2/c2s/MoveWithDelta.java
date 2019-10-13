//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class MoveWithDelta extends L2GameClientPacket {
  private int _dx;
  private int _dy;
  private int _dz;

  public MoveWithDelta() {
  }

  protected void readImpl() {
    this._dx = this.readD();
    this._dy = this.readD();
    this._dz = this.readD();
  }

  protected void runImpl() {
  }
}
