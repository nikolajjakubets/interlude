//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PledgeCrest;

public class RequestPledgeCrest extends L2GameClientPacket {
  private int _crestId;

  public RequestPledgeCrest() {
  }

  protected void readImpl() {
    this._crestId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._crestId != 0) {
        byte[] data = CrestCache.getInstance().getPledgeCrest(this._crestId);
        if (data != null) {
          PledgeCrest pc = new PledgeCrest(this._crestId, data);
          this.sendPacket(pc);
        }

      }
    }
  }
}
