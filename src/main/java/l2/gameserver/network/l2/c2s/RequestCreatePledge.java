//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestCreatePledge extends L2GameClientPacket {
  private String _pledgename;

  public RequestCreatePledge() {
  }

  protected void readImpl() {
    this._pledgename = this.readS(64);
  }

  protected void runImpl() {
  }
}
