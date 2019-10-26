//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExListPartyMatchingWaitingRoom;

public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket {
  private int _minLevel;
  private int _maxLevel;
  private int _page;
  private int[] _classes;

  public RequestListPartyMatchingWaitingRoom() {
  }

  protected void readImpl() {
    this._page = this.readD();
    this._minLevel = this.readD();
    this._maxLevel = this.readD();
    int size = this.readD();
    if (size > 127 || size < 0) {
      size = 0;
    }

    this._classes = new int[size];

    for(int i = 0; i < size; ++i) {
      this._classes[i] = this.readD();
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, this._minLevel, this._maxLevel, this._page, this._classes));
    }
  }
}
