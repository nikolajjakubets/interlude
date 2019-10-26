//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.network.l2.GameClient;

public class RequestSetAllyCrest extends L2GameClientPacket {
  private int _length;
  private byte[] _data;

  public RequestSetAllyCrest() {
  }

  protected void readImpl() {
    this._length = this.readD();
    if (this._length == 192 && this._length == this._buf.remaining()) {
      this._data = new byte[this._length];
      this.readB(this._data);
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Alliance ally = activeChar.getAlliance();
      if (ally != null && activeChar.isAllyLeader()) {
        int crestId = 0;
        if (this._data != null && CrestCache.isValidCrestData(this._data)) {
          crestId = CrestCache.getInstance().saveAllyCrest(ally.getAllyId(), this._data);
        } else if (ally.hasAllyCrest()) {
          CrestCache.getInstance().removeAllyCrest(ally.getAllyId());
        }

        ally.setAllyCrestId(crestId);
        ally.broadcastAllyStatus();
      }

    }
  }
}
