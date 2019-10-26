//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExUISetting extends L2GameServerPacket {
  private final byte[] data;

  public ExUISetting(Player player) {
    this.data = player.getKeyBindings();
  }

  protected void writeImpl() {
    this.writeEx(112);
    this.writeD(this.data.length);
    this.writeB(this.data);
  }
}
