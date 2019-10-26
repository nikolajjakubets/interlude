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

public class FriendList extends L2GameServerPacket {
  private List<FriendList.FriendInfo> _friends = Collections.emptyList();

  public FriendList(Player player) {
    Map<Integer, Friend> friends = player.getFriendList().getList();
    this._friends = new ArrayList(friends.size());
    Iterator var3 = friends.entrySet().iterator();

    while(var3.hasNext()) {
      Entry<Integer, Friend> entry = (Entry)var3.next();
      Friend friend = (Friend)entry.getValue();
      FriendList.FriendInfo f = new FriendList.FriendInfo();
      f.name = friend.getName();
      f.classId = friend.getClassId();
      f.objectId = (Integer)entry.getKey();
      f.level = friend.getLevel();
      f.online = friend.isOnline();
      this._friends.add(f);
    }

  }

  protected void writeImpl() {
    this.writeC(250);
    this.writeD(this._friends.size());
    Iterator var1 = this._friends.iterator();

    while(var1.hasNext()) {
      FriendList.FriendInfo f = (FriendList.FriendInfo)var1.next();
      this.writeD(f.objectId);
      this.writeS(f.name);
      this.writeD(f.online);
      this.writeD(f.online ? f.objectId : 0);
    }

  }

  private class FriendInfo {
    private String name;
    private int objectId;
    private boolean online;
    private int level;
    private int classId;

    private FriendInfo() {
    }
  }
}
