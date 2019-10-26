//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.NpcString;

public class ExSendUIEvent extends NpcStringContainer {
  private int _objectId;
  private boolean _isHide;
  private boolean _isIncrease;
  private int _startTime;
  private int _endTime;

  public ExSendUIEvent(Player player, boolean isHide, boolean isIncrease, int startTime, int endTime, String... params) {
    this(player, isHide, isIncrease, startTime, endTime, NpcString.NONE, params);
  }

  public ExSendUIEvent(Player player, boolean isHide, boolean isIncrease, int startTime, int endTime, NpcString npcString, String... params) {
    super(npcString, params);
    this._objectId = player.getObjectId();
    this._isHide = isHide;
    this._isIncrease = isIncrease;
    this._startTime = startTime;
    this._endTime = endTime;
  }

  protected void writeImpl() {
    this.writeC(254);
    this.writeH(142);
    this.writeD(this._objectId);
    this.writeD(this._isHide ? 1 : 0);
    this.writeD(0);
    this.writeD(0);
    this.writeS(this._isIncrease ? "1" : "0");
    this.writeS(String.valueOf(this._startTime / 60));
    this.writeS(String.valueOf(this._startTime % 60));
    this.writeS(String.valueOf(this._endTime / 60));
    this.writeS(String.valueOf(this._endTime % 60));
    this.writeElements();
  }
}
