//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.s2c.ExSendManorList;

public class RequestManorList extends L2GameClientPacket {
  public RequestManorList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    this.sendPacket(new ExSendManorList());
  }
}
