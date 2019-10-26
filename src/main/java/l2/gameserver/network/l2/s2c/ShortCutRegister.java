//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.network.l2.s2c.ShortCutPacket.ShortcutInfo;

public class ShortCutRegister extends ShortCutPacket {
  private ShortcutInfo _shortcutInfo;

  public ShortCutRegister(Player player, ShortCut sc) {
    this._shortcutInfo = convert(player, sc);
  }

  protected final void writeImpl() {
    this.writeC(68);
    this._shortcutInfo.write(this);
  }
}
