//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class ChangeMoveType extends L2GameServerPacket {
  public static int WALK = 0;
  public static int RUN = 1;
  private int _chaId;
  private boolean _running;

  public ChangeMoveType(Creature cha) {
    this._chaId = cha.getObjectId();
    this._running = cha.isRunning();
  }

  protected final void writeImpl() {
    this.writeC(46);
    this.writeD(this._chaId);
    this.writeD(this._running ? 1 : 0);
    this.writeD(0);
  }
}
