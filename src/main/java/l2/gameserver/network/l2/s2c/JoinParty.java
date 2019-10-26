//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class JoinParty extends L2GameServerPacket {
  public static final L2GameServerPacket SUCCESS = new JoinParty(1);
  public static final L2GameServerPacket FAIL = new JoinParty(0);
  private int _response;

  public JoinParty(int response) {
    this._response = response;
  }

  protected final void writeImpl() {
    this.writeC(58);
    this.writeD(this._response);
  }
}
