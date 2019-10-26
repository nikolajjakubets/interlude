//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestSEKCustom extends L2GameClientPacket {
  private int SlotNum;
  private int Direction;

  public RequestSEKCustom() {
  }

  protected void readImpl() {
    this.SlotNum = this.readD();
    this.Direction = this.readD();
  }

  protected void runImpl() {
  }
}
