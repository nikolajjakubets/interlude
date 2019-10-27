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

public class ListPartyWaiting extends L2GameServerPacket {
  private Collection<MatchingRoom> _rooms;
  private int _fullSize;

  public ListPartyWaiting(int region, boolean allLevels, int page, Player activeChar) {
    int first = (page - 1) * 64;
    int firstNot = page * 64;
    this._rooms = new ArrayList<>();
    int i = 0;
    List<MatchingRoom> temp = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, region, allLevels, activeChar);
    this._fullSize = temp.size();
    Iterator var9 = temp.iterator();

    while(var9.hasNext()) {
      MatchingRoom room = (MatchingRoom)var9.next();
      if (i >= first && i < firstNot) {
        this._rooms.add(room);
        ++i;
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(150);
    this.writeD(this._fullSize);
    this.writeD(this._rooms.size());
    Iterator var1 = this._rooms.iterator();

    while(var1.hasNext()) {
      MatchingRoom room = (MatchingRoom)var1.next();
      this.writeD(room.getId());
      this.writeS(room.getLeader() == null ? "None" : room.getLeader().getName());
      this.writeD(room.getLocationId());
      this.writeD(room.getMinLevel());
      this.writeD(room.getMaxLevel());
      this.writeD(room.getMaxMembersSize());
      this.writeS(room.getTopic());
      Collection<Player> players = room.getPlayers();
      this.writeD(players.size());
      Iterator var4 = players.iterator();

      while(var4.hasNext()) {
        Player player = (Player)var4.next();
        this.writeD(player.getClassId().getId());
        this.writeS(player.getName());
      }
    }

  }
}
