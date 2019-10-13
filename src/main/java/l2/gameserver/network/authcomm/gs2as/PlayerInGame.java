//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class PlayerInGame extends SendablePacket {
  private String account;

  public PlayerInGame(String account) {
    this.account = account;
  }

  protected void writeImpl() {
    this.writeC(3);
    this.writeS(this.account);
  }
}
