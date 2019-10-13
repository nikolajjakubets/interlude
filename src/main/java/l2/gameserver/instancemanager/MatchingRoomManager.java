//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.templates.mapregion.RestartArea;
import l2.gameserver.templates.mapregion.RestartPoint;
import org.apache.commons.lang3.ArrayUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

public class MatchingRoomManager {
  private static final MatchingRoomManager _instance = new MatchingRoomManager();
  private MatchingRoomManager.RoomsHolder[] _holder = new MatchingRoomManager.RoomsHolder[2];
  private Set<Player> _players = new CopyOnWriteArraySet();

  public static MatchingRoomManager getInstance() {
    return _instance;
  }

  public MatchingRoomManager() {
    this._holder[MatchingRoom.PARTY_MATCHING] = new MatchingRoomManager.RoomsHolder();
    this._holder[MatchingRoom.CC_MATCHING] = new MatchingRoomManager.RoomsHolder();
  }

  public void addToWaitingList(Player player) {
    this._players.add(player);
  }

  public void removeFromWaitingList(Player player) {
    this._players.remove(player);
  }

  public List<Player> getWaitingList(int minLevel, int maxLevel, int[] classes) {
    List<Player> res = new ArrayList();
    Iterator var5 = this._players.iterator();

    while(true) {
      Player $member;
      do {
        do {
          do {
            if (!var5.hasNext()) {
              return res;
            }

            $member = (Player)var5.next();
          } while($member.getLevel() < minLevel);
        } while($member.getLevel() > maxLevel);
      } while(classes.length != 0 && !ArrayUtils.contains(classes, $member.getClassId().getId()));

      res.add($member);
    }
  }

  public List<MatchingRoom> getMatchingRooms(int type, int region, boolean allLevels, Player activeChar) {
    List<MatchingRoom> res = new ArrayList();
    Iterator var6 = this._holder[type]._rooms.values().iterator();

    while(true) {
      MatchingRoom room;
      do {
        do {
          do {
            if (!var6.hasNext()) {
              return res;
            }

            room = (MatchingRoom)var6.next();
          } while(region > 0 && room.getLocationId() != region);
        } while(region == -2 && room.getLocationId() != getInstance().getLocation(activeChar));
      } while(!allLevels && (room.getMinLevel() > activeChar.getLevel() || room.getMaxLevel() < activeChar.getLevel()));

      res.add(room);
    }
  }

  public int addMatchingRoom(MatchingRoom r) {
    return this._holder[r.getType()].addRoom(r);
  }

  public void removeMatchingRoom(MatchingRoom r) {
    this._holder[r.getType()]._rooms.remove(r.getId());
  }

  public MatchingRoom getMatchingRoom(int type, int id) {
    return (MatchingRoom)this._holder[type]._rooms.get(id);
  }

  public int getLocation(Player player) {
    if (player == null) {
      return 0;
    } else {
      RestartArea ra = (RestartArea)MapRegionManager.getInstance().getRegionData(RestartArea.class, player);
      if (ra != null) {
        RestartPoint rp = (RestartPoint)ra.getRestartPoint().get(player.getRace());
        return rp.getBbs();
      } else {
        return 0;
      }
    }
  }

  private class RoomsHolder {
    private int _id;
    private IntObjectMap<MatchingRoom> _rooms;

    private RoomsHolder() {
      this._id = 1;
      this._rooms = new CHashIntObjectMap();
    }

    public int addRoom(MatchingRoom r) {
      int val = this._id++;
      this._rooms.put(val, r);
      return val;
    }
  }
}
