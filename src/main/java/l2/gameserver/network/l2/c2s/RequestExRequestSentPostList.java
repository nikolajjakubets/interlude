//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExShowSentPostList;

public class RequestExRequestSentPostList extends L2GameClientPacket {
  public RequestExRequestSentPostList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player cha = ((GameClient)this.getClient()).getActiveChar();
    if (cha != null) {
      cha.sendPacket(new ExShowSentPostList(cha));
    }

  }
}
