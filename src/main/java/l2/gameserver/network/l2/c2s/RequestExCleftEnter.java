//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestExCleftEnter extends L2GameClientPacket {
  private int unk;

  public RequestExCleftEnter() {
  }

  protected void readImpl() {
    this.unk = this.readD();
  }

  protected void runImpl() {
  }
}
