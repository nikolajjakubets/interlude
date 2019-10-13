//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class OnlineStatus extends SendablePacket {
  private boolean _online;

  public OnlineStatus(boolean online) {
    this._online = online;
  }

  protected void writeImpl() {
    this.writeC(1);
    this.writeC(this._online ? 1 : 0);
  }
}
