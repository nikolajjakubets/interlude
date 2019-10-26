//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExCursedWeaponList;

public class RequestCursedWeaponList extends L2GameClientPacket {
  public RequestCursedWeaponList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Creature activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.sendPacket(new ExCursedWeaponList());
    }
  }
}
