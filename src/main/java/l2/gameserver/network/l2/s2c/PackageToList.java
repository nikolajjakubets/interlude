//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.model.Player;

public class PackageToList extends L2GameServerPacket {
  private Map<Integer, String> _characters = Collections.emptyMap();

  public PackageToList(Player player) {
    this._characters = player.getAccountChars();
  }

  protected void writeImpl() {
    this.writeC(194);
    this.writeD(this._characters.size());
    Iterator var1 = this._characters.entrySet().iterator();

    while(var1.hasNext()) {
      Entry<Integer, String> entry = (Entry)var1.next();
      this.writeD((Integer)entry.getKey());
      this.writeS((CharSequence)entry.getValue());
    }

  }
}
