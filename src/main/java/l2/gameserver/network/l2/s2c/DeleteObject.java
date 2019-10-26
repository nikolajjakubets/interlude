//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class DeleteObject extends L2GameServerPacket {
  private int _objectId;

  public DeleteObject(GameObject obj) {
    this._objectId = obj.getObjectId();
  }

  protected final void writeImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && activeChar.getObjectId() != this._objectId) {
      this.writeC(18);
      this.writeD(this._objectId);
      this.writeD(1);
    }
  }

  public String getType() {
    return super.getType() + " " + GameObjectsStorage.findObject(this._objectId) + " (" + this._objectId + ")";
  }
}
