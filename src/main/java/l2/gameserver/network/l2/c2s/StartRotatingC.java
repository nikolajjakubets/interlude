//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.StartRotating;

public class StartRotatingC extends L2GameClientPacket {
  private int _degree;
  private int _side;

  public StartRotatingC() {
  }

  protected void readImpl() {
    this._degree = this.readD();
    this._side = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setHeading(this._degree);
      activeChar.broadcastPacket(new L2GameServerPacket[]{new StartRotating(activeChar, this._degree, this._side, 0)});
    }
  }
}
