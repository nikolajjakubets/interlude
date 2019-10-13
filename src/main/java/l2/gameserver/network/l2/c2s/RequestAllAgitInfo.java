//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExShowAgitInfo;

public class RequestAllAgitInfo extends L2GameClientPacket {
  public RequestAllAgitInfo() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    ((GameClient)this.getClient()).getActiveChar().sendPacket(new ExShowAgitInfo());
  }
}
