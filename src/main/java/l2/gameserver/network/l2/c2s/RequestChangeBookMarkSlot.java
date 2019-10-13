//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestChangeBookMarkSlot extends L2GameClientPacket {
  private int slot_old;
  private int slot_new;

  public RequestChangeBookMarkSlot() {
  }

  protected void readImpl() {
    this.slot_old = this.readD();
    this.slot_new = this.readD();
  }

  protected void runImpl() {
  }
}
