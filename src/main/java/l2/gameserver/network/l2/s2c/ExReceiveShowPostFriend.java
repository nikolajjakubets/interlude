//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import l2.gameserver.model.Player;
import org.napile.primitive.maps.IntObjectMap;

public class ExReceiveShowPostFriend extends L2GameServerPacket {
  private IntObjectMap<String> _list;

  public ExReceiveShowPostFriend(Player player) {
    this._list = player.getPostFriends();
  }

  public void writeImpl() {
    this.writeEx(211);
    this.writeD(this._list.size());
    Iterator var1 = this._list.values().iterator();

    while(var1.hasNext()) {
      String t = (String)var1.next();
      this.writeS(t);
    }

  }
}
