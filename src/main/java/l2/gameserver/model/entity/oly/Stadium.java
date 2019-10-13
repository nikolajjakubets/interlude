//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import java.util.ArrayList;
import java.util.Iterator;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class Stadium extends Reflection {
  public static final int OLYMPIAD_HOST = 36402;
  private Location _observe_loc;
  private int _stadium_id;
  private boolean _isFree = true;
  private static final int[] EMPTY_VISITORS = new int[0];

  public Stadium(int id, int ozid, Location observe_loc) {
    InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(ozid);
    this.init(instantZone);
    this.setName("OlyStadium-" + id);
    this._stadium_id = id;
    this._observe_loc = observe_loc;
    this.setCollapseIfEmptyTime(0);
    this._isFree = true;
  }

  public boolean isFree() {
    return this._isFree;
  }

  public void setFree(boolean val) {
    this._isFree = val;
  }

  public void clear() {
    ArrayList<Player> teleport_list = new ArrayList();
    ArrayList<GameObject> delete_list = new ArrayList();
    this.lock.lock();

    Iterator var3;
    GameObject o;
    try {
      var3 = this._objects.iterator();

      while(var3.hasNext()) {
        o = (GameObject)var3.next();
        if (o != null && !(o instanceof DoorInstance) && (!o.isNpc() || ((NpcInstance)o).getNpcId() != 36402)) {
          if (o.isPlayer() && !o.getPlayer().isOlyObserver()) {
            teleport_list.add((Player)o);
          } else if (!o.isPlayer()) {
            delete_list.add(o);
          }
        }
      }
    } finally {
      this.lock.unlock();
    }

    var3 = teleport_list.iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      if (player.getParty() != null && this.equals(player.getParty().getReflection())) {
        player.getParty().setReflection((Reflection)null);
      }

      if (this.equals(player.getReflection())) {
        if (this.getReturnLoc() != null) {
          player.teleToLocation(this.getReturnLoc(), 0);
        } else {
          player.teleToClosestTown();
        }
      }
    }

    var3 = delete_list.iterator();

    while(var3.hasNext()) {
      o = (GameObject)var3.next();
      o.deleteMe();
    }

  }

  public final int getStadiumId() {
    return this._stadium_id;
  }

  public Location getLocForParticipant(Participant part) {
    return Location.findPointToStay((Location)this.getInstancedZone().getTeleportCoords().get(part.getSide() - 1), 50, 50, this.getGeoIndex());
  }

  public Location getObservingLoc() {
    return this._observe_loc;
  }

  public int getObserverCount() {
    int cnt = 0;
    Iterator var2 = this.getPlayers().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      if (player.isOlyObserver()) {
        ++cnt;
      }
    }

    return cnt;
  }

  public void setZonesActive(boolean active) {
    Iterator var2 = this.getZones().iterator();

    while(var2.hasNext()) {
      Zone zone = (Zone)var2.next();
      zone.setActive(active);
    }

  }

  public boolean isStatic() {
    return true;
  }

  public int[] getVisitors() {
    return EMPTY_VISITORS;
  }
}
