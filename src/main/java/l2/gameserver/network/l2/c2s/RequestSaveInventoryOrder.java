//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestSaveInventoryOrder extends L2GameClientPacket {
  int[][] _items;

  public RequestSaveInventoryOrder() {
  }

  protected void readImpl() {
    int size = this.readD();
    if (size > 125) {
      size = 125;
    }

    if (size * 8 <= this._buf.remaining() && size >= 1) {
      this._items = new int[size][2];

      for(int i = 0; i < size; ++i) {
        this._items[i][0] = this.readD();
        this._items[i][1] = this.readD();
      }

    } else {
      this._items = (int[][])null;
    }
  }

  protected void runImpl() {
    if (this._items != null) {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
        activeChar.getInventory().sort(this._items);
      }
    }
  }
}
