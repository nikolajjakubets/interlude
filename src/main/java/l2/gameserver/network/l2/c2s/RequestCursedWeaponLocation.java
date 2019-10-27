//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.CursedWeapon;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExCursedWeaponLocation;
import l2.gameserver.network.l2.s2c.ExCursedWeaponLocation.CursedWeaponInfo;
import l2.gameserver.utils.Location;

public class RequestCursedWeaponLocation extends L2GameClientPacket {
  public RequestCursedWeaponLocation() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Creature activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      List<CursedWeaponInfo> list = new ArrayList<>();
      CursedWeapon[] var3 = CursedWeaponsManager.getInstance().getCursedWeapons();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        CursedWeapon cw = var3[var5];
        Location pos = cw.getWorldPosition();
        if (pos != null) {
          list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
        }
      }

      activeChar.sendPacket(new ExCursedWeaponLocation(list));
    }
  }
}
