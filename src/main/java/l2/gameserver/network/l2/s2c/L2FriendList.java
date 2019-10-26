//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Friend;

public class L2FriendList extends L2GameServerPacket {
  private List<L2FriendList.FriendInfo> _list = Collections.emptyList();

  public L2FriendList(Player player) {
    Map<Integer, Friend> list = player.getFriendList().getList();
    this._list = new ArrayList(list.size());
    Iterator var3 = list.entrySet().iterator();

    while(var3.hasNext()) {
      Entry<Integer, Friend> entry = (Entry)var3.next();
      L2FriendList.FriendInfo f = new L2FriendList.FriendInfo();
      f._objectId = (Integer)entry.getKey();
      f._name = ((Friend)entry.getValue()).getName();
      f._online = ((Friend)entry.getValue()).isOnline();
      this._list.add(f);
    }

  }

  protected final void writeImpl() {
    this.writeC(250);
    this.writeD(this._list.size());
    Iterator var1 = this._list.iterator();

    while(var1.hasNext()) {
      L2FriendList.FriendInfo friendInfo = (L2FriendList.FriendInfo)var1.next();
      this.writeD(0);
      this.writeS(friendInfo._name);
      this.writeD(friendInfo._online ? 1 : 0);
      this.writeD(friendInfo._objectId);
    }

  }

  private static class FriendInfo {
    private int _objectId;
    private String _name;
    private boolean _online;

    private FriendInfo() {
    }
  }
}
