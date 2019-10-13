//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

public class PetitionVote extends L2GameClientPacket {
  private int _type;
  private int _unk1;
  private String _petitionText;

  public PetitionVote() {
  }

  protected void runImpl() {
  }

  protected void readImpl() {
    this._type = this.readD();
    this._unk1 = this.readD();
    this._petitionText = this.readS(4096);
  }
}
