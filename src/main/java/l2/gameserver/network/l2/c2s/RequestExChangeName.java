//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestExChangeName extends L2GameClientPacket {
  public RequestExChangeName() {
  }

  protected void readImpl() {
    int unk1 = this.readD();
    String name = this.readS();
    int unk2 = this.readD();
  }

  protected void runImpl() {
  }
}
