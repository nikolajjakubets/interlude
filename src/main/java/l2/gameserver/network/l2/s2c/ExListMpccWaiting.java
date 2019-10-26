//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;

public class ExListMpccWaiting extends L2GameServerPacket {
  private static final int PAGE_SIZE = 10;
  private int _fullSize;
  private List<MatchingRoom> _list;

  public ExListMpccWaiting(Player player, int page, int location, boolean allLevels) {
    int first = (page - 1) * 10;
    int firstNot = page * 10;
    int i = 0;
    Collection<MatchingRoom> all = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.CC_MATCHING, location, allLevels, player);
    this._fullSize = all.size();
    this._list = new ArrayList(10);
    Iterator var9 = all.iterator();

    while(var9.hasNext()) {
      MatchingRoom c = (MatchingRoom)var9.next();
      if (i >= first && i < firstNot) {
        this._list.add(c);
        ++i;
      }
    }

  }

  public void writeImpl() {
    this.writeEx(156);
    this.writeD(this._fullSize);
    this.writeD(this._list.size());
    Iterator var1 = this._list.iterator();

    while(var1.hasNext()) {
      MatchingRoom room = (MatchingRoom)var1.next();
      this.writeD(room.getId());
      this.writeS(room.getTopic());
      this.writeD(room.getPlayers().size());
      this.writeD(room.getMinLevel());
      this.writeD(room.getMaxLevel());
      this.writeD(1);
      this.writeD(room.getMaxMembersSize());
      Player leader = room.getLeader();
      this.writeS(leader == null ? "" : leader.getName());
    }

  }
}
