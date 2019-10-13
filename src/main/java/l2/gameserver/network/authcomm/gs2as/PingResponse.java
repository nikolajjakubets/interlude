//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class PingResponse extends SendablePacket {
  public PingResponse() {
  }

  protected void writeImpl() {
    this.writeC(255);
    this.writeQ(System.currentTimeMillis());
  }
}
