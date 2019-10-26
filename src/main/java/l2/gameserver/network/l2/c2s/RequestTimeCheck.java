//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestTimeCheck extends L2GameClientPacket {
  private int unk;
  private int unk2;

  public RequestTimeCheck() {
  }

  protected void readImpl() {
    this.unk = this.readD();
    this.unk2 = this.readD();
  }

  protected void runImpl() {
  }
}
