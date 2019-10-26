//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExUISetting;

public class RequestSaveKeyMapping extends L2GameClientPacket {
  private byte[] _data;

  public RequestSaveKeyMapping() {
  }

  protected void readImpl() {
    int length = this.readD();
    if (length <= this._buf.remaining() && length <= 32767 && length >= 0) {
      this._data = new byte[length];
      this.readB(this._data);
    } else {
      this._data = null;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._data != null) {
      activeChar.setKeyBindings(this._data);
      activeChar.sendPacket(new ExUISetting(activeChar));
    }
  }
}
