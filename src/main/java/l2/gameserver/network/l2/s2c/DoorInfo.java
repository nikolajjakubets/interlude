//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;

public class DoorInfo extends L2GameServerPacket {
  private int _id;
  private int obj_id;
  private final boolean _isAttakable;
  private int unk1;
  private int _isOpened;
  private int _curHP;
  private int _maxHp;
  private int _isVis;
  private int _damage;

  public DoorInfo(DoorInstance door, Player player) {
    this._id = door.getDoorId();
    this.obj_id = door.getObjectId();
    this.unk1 = 1;
    this._isOpened = door.isOpen() ? 0 : 1;
    this._curHP = (int)door.getCurrentHp();
    this._maxHp = door.getMaxHp();
    this._isVis = door.isHPVisible() ? 1 : 0;
    this._damage = door.getDamage();
    this._isAttakable = door.isAutoAttackable(player);
  }

  protected final void writeImpl() {
    this.writeC(76);
    this.writeD(this.obj_id);
    this.writeD(this._id);
    this.writeD(this._isAttakable);
    this.writeD(this.unk1);
    this.writeD(this._isOpened);
    this.writeD(this._curHP);
    this.writeD(this._maxHp);
    this.writeD(this._isVis);
    this.writeD(this._damage);
  }
}
