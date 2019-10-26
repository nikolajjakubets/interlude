//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class PetDelete extends L2GameServerPacket {
  private int _petId;
  private int _petnum;

  public PetDelete(int petId, int petnum) {
    this._petId = petId;
    this._petnum = petnum;
  }

  protected final void writeImpl() {
    this.writeC(182);
    this.writeD(this._petId);
    this.writeD(this._petnum);
  }
}
