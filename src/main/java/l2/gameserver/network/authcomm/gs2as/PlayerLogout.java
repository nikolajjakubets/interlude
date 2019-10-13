//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class PlayerLogout extends SendablePacket {
  private String account;

  public PlayerLogout(String account) {
    this.account = account;
  }

  protected void writeImpl() {
    this.writeC(4);
    this.writeS(this.account);
  }
}
