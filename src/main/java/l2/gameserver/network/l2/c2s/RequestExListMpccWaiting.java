//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExListMpccWaiting;

public class RequestExListMpccWaiting extends L2GameClientPacket {
  private int _listId;
  private int _locationId;
  private boolean _allLevels;

  public RequestExListMpccWaiting() {
  }

  protected void readImpl() throws Exception {
    this._listId = this.readD();
    this._locationId = this.readD();
    this._allLevels = this.readD() == 1;
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      player.sendPacket(new ExListMpccWaiting(player, this._listId, this._locationId, this._allLevels));
    }
  }
}
