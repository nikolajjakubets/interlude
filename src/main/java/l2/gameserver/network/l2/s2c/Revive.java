//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.GameObject;

public class Revive extends L2GameServerPacket {
  private int _objectId;

  public Revive(GameObject obj) {
    this._objectId = obj.getObjectId();
  }

  protected final void writeImpl() {
    this.writeC(7);
    this.writeD(this._objectId);
  }
}
