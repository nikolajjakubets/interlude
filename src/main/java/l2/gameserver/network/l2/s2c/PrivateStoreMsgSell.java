//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import org.apache.commons.lang3.StringUtils;

public class PrivateStoreMsgSell extends L2GameServerPacket {
  private final int _objId;
  private final String _name;

  public PrivateStoreMsgSell(Player player) {
    this._objId = player.getObjectId();
    this._name = StringUtils.defaultString(player.getSellStoreName());
  }

  protected final void writeImpl() {
    this.writeC(156);
    this.writeD(this._objId);
    this.writeS(this._name);
  }
}
