//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.utils.Location;

public class ExCursedWeaponLocation extends L2GameServerPacket {
  private List<ExCursedWeaponLocation.CursedWeaponInfo> _cursedWeaponInfo;

  public ExCursedWeaponLocation(List<ExCursedWeaponLocation.CursedWeaponInfo> cursedWeaponInfo) {
    this._cursedWeaponInfo = cursedWeaponInfo;
  }

  protected final void writeImpl() {
    this.writeEx(70);
    if (this._cursedWeaponInfo.isEmpty()) {
      this.writeD(0);
    } else {
      this.writeD(this._cursedWeaponInfo.size());
      Iterator var1 = this._cursedWeaponInfo.iterator();

      while(var1.hasNext()) {
        ExCursedWeaponLocation.CursedWeaponInfo w = (ExCursedWeaponLocation.CursedWeaponInfo)var1.next();
        this.writeD(w._id);
        this.writeD(w._status);
        this.writeD(w._pos.x);
        this.writeD(w._pos.y);
        this.writeD(w._pos.z);
      }
    }

  }

  public static class CursedWeaponInfo {
    public Location _pos;
    public int _id;
    public int _status;

    public CursedWeaponInfo(Location p, int ID, int status) {
      this._pos = p;
      this._id = ID;
      this._status = status;
    }
  }
}
