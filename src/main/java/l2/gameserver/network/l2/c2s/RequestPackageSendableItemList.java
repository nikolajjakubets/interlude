//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PackageSendableList;

public class RequestPackageSendableItemList extends L2GameClientPacket {
  private int _objectId;

  public RequestPackageSendableItemList() {
  }

  protected void readImpl() throws Exception {
    this._objectId = this.readD();
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      player.sendPacket(new PackageSendableList(this._objectId, player));
    }
  }
}
