//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class RequestTeleport extends L2GameClientPacket {
  private int unk;
  private int _type;
  private int unk2;
  private int unk3;
  private int unk4;

  public RequestTeleport() {
  }

  protected void readImpl() {
    this.unk = this.readD();
    this._type = this.readD();
    if (this._type == 2) {
      this.unk2 = this.readD();
      this.unk3 = this.readD();
    } else if (this._type == 3) {
      this.unk2 = this.readD();
      this.unk3 = this.readD();
      this.unk4 = this.readD();
    }

  }

  protected void runImpl() {
  }
}
