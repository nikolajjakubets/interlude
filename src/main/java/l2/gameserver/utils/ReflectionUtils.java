//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.templates.InstantZone;

public class ReflectionUtils {
  public ReflectionUtils() {
  }

  public static DoorInstance getDoor(int id) {
    return ReflectionManager.DEFAULT.getDoor(id);
  }

  public static List<DoorInstance> getDoors(int... ids) {
    List<DoorInstance> doors = new ArrayList(ids.length);

    for(int idIdx = 0; idIdx < ids.length; ++idIdx) {
      DoorInstance door = getDoor(ids[idIdx]);
      if (door != null) {
        doors.add(door);
      }
    }

    return doors;
  }

  public static Zone getZone(String name) {
    return ReflectionManager.DEFAULT.getZone(name);
  }

  public static List<Zone> getZonesByType(ZoneType zoneType) {
    Collection<Zone> zones = ReflectionManager.DEFAULT.getZones();
    if (zones.isEmpty()) {
      return Collections.emptyList();
    } else {
      List<Zone> zones2 = new ArrayList(5);
      Iterator var3 = zones.iterator();

      while(var3.hasNext()) {
        Zone z = (Zone)var3.next();
        if (z.getType() == zoneType) {
          zones2.add(z);
        }
      }

      return zones2;
    }
  }

  public static Reflection enterReflection(Player invoker, int instancedZoneId) {
    InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
    return enterReflection(invoker, new Reflection(), iz);
  }

  public static Reflection enterReflection(Player invoker, Reflection r, int instancedZoneId) {
    InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
    return enterReflection(invoker, r, iz);
  }

  public static Reflection enterReflection(Player invoker, Reflection r, InstantZone iz) {
    r.init(iz);
    if (r.getReturnLoc() == null) {
      r.setReturnLoc(invoker.getLoc());
    }

    switch(iz.getEntryType()) {
      case SOLO:
        if (iz.getRemovedItemId() > 0) {
          ItemFunctions.removeItem(invoker, iz.getRemovedItemId(), (long)iz.getRemovedItemCount(), true);
        }

        if (iz.getGiveItemId() > 0) {
          ItemFunctions.addItem(invoker, iz.getGiveItemId(), (long)iz.getGiveItemCount(), true);
        }

        if (iz.isDispelBuffs()) {
          invoker.dispelBuffs();
        }

        if (iz.getSetReuseUponEntry() && iz.getResetReuse().next(System.currentTimeMillis()) > System.currentTimeMillis()) {
          invoker.setInstanceReuse(iz.getId(), System.currentTimeMillis());
        }

        invoker.setVar("backCoords", invoker.getLoc().toXYZString(), -1L);
        invoker.teleToLocation(iz.getTeleportCoord(), r);
        break;
      case PARTY:
        Party party = invoker.getParty();
        party.setReflection(r);
        r.setParty(party);
        Iterator var8 = party.getPartyMembers().iterator();

        while(var8.hasNext()) {
          Player member = (Player)var8.next();
          if (iz.getRemovedItemId() > 0) {
            ItemFunctions.removeItem(member, iz.getRemovedItemId(), (long)iz.getRemovedItemCount(), true);
          }

          if (iz.getGiveItemId() > 0) {
            ItemFunctions.addItem(member, iz.getGiveItemId(), (long)iz.getGiveItemCount(), true);
          }

          if (iz.isDispelBuffs()) {
            member.dispelBuffs();
          }

          if (iz.getSetReuseUponEntry()) {
            member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
          }

          member.setVar("backCoords", invoker.getLoc().toXYZString(), -1L);
          member.teleToLocation(iz.getTeleportCoord(), r);
        }

        return r;
      case COMMAND_CHANNEL:
        Party commparty = invoker.getParty();
        CommandChannel cc = commparty.getCommandChannel();
        cc.setReflection(r);
        r.setCommandChannel(cc);
        Iterator var6 = cc.iterator();

        while(var6.hasNext()) {
          Player member = (Player)var6.next();
          if (iz.getRemovedItemId() > 0) {
            ItemFunctions.removeItem(member, iz.getRemovedItemId(), (long)iz.getRemovedItemCount(), true);
          }

          if (iz.getGiveItemId() > 0) {
            ItemFunctions.addItem(member, iz.getGiveItemId(), (long)iz.getGiveItemCount(), true);
          }

          if (iz.isDispelBuffs()) {
            member.dispelBuffs();
          }

          if (iz.getSetReuseUponEntry()) {
            member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
          }

          member.setVar("backCoords", invoker.getLoc().toXYZString(), -1L);
          member.teleToLocation(iz.getTeleportCoord(), r);
        }
    }

    return r;
  }
}
