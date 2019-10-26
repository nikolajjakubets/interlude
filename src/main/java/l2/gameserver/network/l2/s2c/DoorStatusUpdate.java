//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;

public class DoorStatusUpdate extends L2GameServerPacket {
  private final int _doorObjId;
  private final boolean _isClosed;
  private final int _dmg;
  private final boolean _isAttakable;
  private final int _doorStaticId;
  private final int _curHp;
  private final int _maxHp;

  public DoorStatusUpdate(DoorInstance door, Player player) {
    this._doorObjId = door.getObjectId();
    this._doorStaticId = door.getDoorId();
    this._isClosed = !door.isOpen();
    this._isAttakable = door.isAutoAttackable(player);
    this._curHp = (int)door.getCurrentHp();
    this._maxHp = door.getMaxHp();
    this._dmg = door.getDamage();
  }

  protected void writeImpl() {
    this.writeC(77);
    this.writeD(this._doorObjId);
    this.writeD(this._isClosed);
    this.writeD(this._dmg);
    this.writeD(this._isAttakable);
    this.writeD(this._doorStaticId);
    this.writeD(this._maxHp);
    this.writeD(this._curHp);
  }
}
