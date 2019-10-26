//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;

public class ShowMiniMap extends L2GameServerPacket {
  private int _mapId;
  private int _period;

  public ShowMiniMap(Player player, int mapId) {
    this._mapId = mapId;
    this._period = SevenSigns.getInstance().getCurrentPeriod();
  }

  protected final void writeImpl() {
    this.writeC(157);
    this.writeD(this._mapId);
    this.writeC(this._period);
  }
}
