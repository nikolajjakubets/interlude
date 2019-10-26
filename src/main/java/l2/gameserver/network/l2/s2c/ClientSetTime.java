//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.GameTimeController;

public class ClientSetTime extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC = new ClientSetTime();

  public ClientSetTime() {
  }

  protected final void writeImpl() {
    this.writeC(236);
    this.writeD(GameTimeController.getInstance().getGameTime());
    this.writeD(6);
  }
}
