//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExFishingEnd extends L2GameServerPacket {
  private int _charId;
  private boolean _win;

  public ExFishingEnd(Player character, boolean win) {
    this._charId = character.getObjectId();
    this._win = win;
  }

  protected final void writeImpl() {
    this.writeEx(20);
    this.writeD(this._charId);
    this.writeC(this._win ? 1 : 0);
  }
}
