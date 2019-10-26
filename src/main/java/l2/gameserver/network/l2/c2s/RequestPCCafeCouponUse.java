//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestPCCafeCouponUse extends L2GameClientPacket {
  private String _unknown;

  public RequestPCCafeCouponUse() {
  }

  protected void readImpl() {
    this._unknown = this.readS();
  }

  protected void runImpl() {
  }
}
