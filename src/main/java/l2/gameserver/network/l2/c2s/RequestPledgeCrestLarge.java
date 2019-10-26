//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExPledgeCrestLarge;

public class RequestPledgeCrestLarge extends L2GameClientPacket {
  private int _crestId;

  public RequestPledgeCrestLarge() {
  }

  protected void readImpl() {
    this._crestId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._crestId != 0) {
        byte[] data = CrestCache.getInstance().getPledgeCrestLarge(this._crestId);
        if (data != null) {
          ExPledgeCrestLarge pcl = new ExPledgeCrestLarge(this._crestId, data);
          this.sendPacket(pcl);
        }

      }
    }
  }
}
