//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.FriendList;

public class RequestExFriendListForPostBox extends L2GameClientPacket {
  public RequestExFriendListForPostBox() {
  }

  protected void readImpl() throws Exception {
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      player.sendPacket(new FriendList(player));
    }
  }
}
