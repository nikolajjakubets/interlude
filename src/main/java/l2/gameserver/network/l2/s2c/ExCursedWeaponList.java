//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.instancemanager.CursedWeaponsManager;

public class ExCursedWeaponList extends L2GameServerPacket {
  private int[] cursedWeapon_ids = CursedWeaponsManager.getInstance().getCursedWeaponsIds();

  public ExCursedWeaponList() {
  }

  protected final void writeImpl() {
    this.writeEx(69);
    this.writeDD(this.cursedWeapon_ids, true);
  }
}
