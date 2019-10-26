//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExClosePartyRoom extends L2GameServerPacket {
  public static L2GameServerPacket STATIC = new ExClosePartyRoom();

  public ExClosePartyRoom() {
  }

  protected void writeImpl() {
    this.writeEx(9);
  }
}
