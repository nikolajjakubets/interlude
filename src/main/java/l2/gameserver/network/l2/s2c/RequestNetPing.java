//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class RequestNetPing extends L2GameServerPacket {
  private final int timestamp;

  public RequestNetPing(int timestamp) {
    this.timestamp = timestamp;
  }

  protected void writeImpl() {
    this.writeC(211);
    this.writeD(this.timestamp);
  }
}
