//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.FinishRotating;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class FinishRotatingC extends L2GameClientPacket {
  private int _degree;
  private int _unknown;

  public FinishRotatingC() {
  }

  protected void readImpl() {
    this._degree = this.readD();
    this._unknown = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.broadcastPacket(new L2GameServerPacket[]{new FinishRotating(activeChar, this._degree, 0)});
    }
  }
}
