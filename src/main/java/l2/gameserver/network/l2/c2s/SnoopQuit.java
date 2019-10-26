//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class SnoopQuit extends L2GameClientPacket {
  private int _snoopID;

  public SnoopQuit() {
  }

  protected void readImpl() {
    this._snoopID = this.readD();
  }

  protected void runImpl() {
  }
}
