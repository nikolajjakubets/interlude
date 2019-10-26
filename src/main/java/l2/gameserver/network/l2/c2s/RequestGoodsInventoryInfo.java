//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestGoodsInventoryInfo extends L2GameClientPacket {
  public RequestGoodsInventoryInfo() {
  }

  protected void readImpl() throws Exception {
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      ;
    }
  }
}
