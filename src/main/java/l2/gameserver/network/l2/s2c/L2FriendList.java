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
  private List<L2FriendList.FriendInfo> _list;

  public L2FriendList(Player player) {
    Map<Integer, Friend> list = player.getFriendList().getList();
    this._list = new ArrayList<>(list.size());

    for (Entry<Integer, Friend> integerFriendEntry : list.entrySet()) {
      FriendInfo f = new FriendInfo();
      f._objectId = integerFriendEntry.getKey();
      f._name = integerFriendEntry.getValue().getName();
      f._online = integerFriendEntry.getValue().isOnline();
      this._list.add(f);
    }

  }

  protected final void writeImpl() {
    this.writeC(250);
    this.writeD(this._list.size());

    for (FriendInfo friendInfo : this._list) {
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
