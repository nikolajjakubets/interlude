//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.network.l2.s2c.AllianceCrest;

public class RequestAllyCrest extends L2GameClientPacket {
  private int _crestId;

  public RequestAllyCrest() {
  }

  protected void readImpl() {
    this._crestId = this.readD();
  }

  protected void runImpl() {
    if (this._crestId != 0) {
      byte[] data = CrestCache.getInstance().getAllyCrest(this._crestId);
      if (data != null) {
        AllianceCrest ac = new AllianceCrest(this._crestId, data);
        this.sendPacket(ac);
      }

    }
  }
}
