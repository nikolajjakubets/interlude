//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestSendMsnChatLog extends L2GameClientPacket {
  private int unk3;
  private String unk;
  private String unk2;

  public RequestSendMsnChatLog() {
  }

  protected void runImpl() {
  }

  protected void readImpl() {
    this.unk = this.readS();
    this.unk2 = this.readS();
    this.unk3 = this.readD();
  }
}
