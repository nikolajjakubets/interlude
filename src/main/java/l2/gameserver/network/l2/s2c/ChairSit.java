//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.StaticObjectInstance;

public class ChairSit extends L2GameServerPacket {
  private int _objectId;
  private int _staticObjectId;

  public ChairSit(Player player, StaticObjectInstance throne) {
    this._objectId = player.getObjectId();
    this._staticObjectId = throne.getUId();
  }

  protected final void writeImpl() {
    this.writeC(225);
    this.writeD(this._objectId);
    this.writeD(this._staticObjectId);
  }
}
