//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.model.Player;
import l2.gameserver.templates.InstantZone;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class InstantZoneHolder extends AbstractHolder {
  private static final InstantZoneHolder _instance = new InstantZoneHolder();
  private IntObjectMap<InstantZone> _zones = new HashIntObjectMap();

  public InstantZoneHolder() {
  }

  public static InstantZoneHolder getInstance() {
    return _instance;
  }

  public void addInstantZone(InstantZone zone) {
    this._zones.put(zone.getId(), zone);
  }

  public InstantZone getInstantZone(int id) {
    return (InstantZone)this._zones.get(id);
  }

  private SchedulingPattern getResetReuseById(int id) {
    InstantZone zone = this.getInstantZone(id);
    return zone == null ? null : zone.getResetReuse();
  }

  public int getMinutesToNextEntrance(int id, Player player) {
    SchedulingPattern resetReuse = this.getResetReuseById(id);
    if (resetReuse == null) {
      return 0;
    } else {
      Long time = null;
      if (this.getSharedReuseInstanceIds(id) != null && !this.getSharedReuseInstanceIds(id).isEmpty()) {
        List<Long> reuses = new ArrayList();
        Iterator var6 = this.getSharedReuseInstanceIds(id).iterator();

        while(var6.hasNext()) {
          int i = (Integer)var6.next();
          if (player.getInstanceReuse(i) != null) {
            reuses.add(player.getInstanceReuse(i));
          }
        }

        if (!reuses.isEmpty()) {
          Collections.sort(reuses);
          time = (Long)reuses.get(reuses.size() - 1);
        }
      } else {
        time = player.getInstanceReuse(id);
      }

      return time == null ? 0 : (int)Math.max((resetReuse.next(time) - System.currentTimeMillis()) / 60000L, 0L);
    }
  }

  public List<Integer> getSharedReuseInstanceIds(int id) {
    if (this.getInstantZone(id).getSharedReuseGroup() < 1) {
      return null;
    } else {
      List<Integer> sharedInstanceIds = new ArrayList();
      Iterator var3 = this._zones.values().iterator();

      while(var3.hasNext()) {
        InstantZone iz = (InstantZone)var3.next();
        if (iz.getSharedReuseGroup() > 0 && this.getInstantZone(id).getSharedReuseGroup() > 0 && iz.getSharedReuseGroup() == this.getInstantZone(id).getSharedReuseGroup()) {
          sharedInstanceIds.add(iz.getId());
        }
      }

      return sharedInstanceIds;
    }
  }

  public List<Integer> getSharedReuseInstanceIdsByGroup(int groupId) {
    if (groupId < 1) {
      return null;
    } else {
      List<Integer> sharedInstanceIds = new ArrayList();
      Iterator var3 = this._zones.values().iterator();

      while(var3.hasNext()) {
        InstantZone iz = (InstantZone)var3.next();
        if (iz.getSharedReuseGroup() > 0 && iz.getSharedReuseGroup() == groupId) {
          sharedInstanceIds.add(iz.getId());
        }
      }

      return sharedInstanceIds;
    }
  }

  public int size() {
    return this._zones.size();
  }

  public void clear() {
    this._zones.clear();
  }
}
