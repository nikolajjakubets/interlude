//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.s2c.DropItem;
import l2.gameserver.templates.mapregion.DomainArea;

public class AdminZone implements IAdminCommandHandler {
  public AdminZone() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminZone.Commands command = (AdminZone.Commands)comm;
    if (activeChar != null && activeChar.getPlayerAccess().CanTeleport) {
      switch(command) {
        case admin_zone_check:
          activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
          activeChar.sendMessage("Zone list:");
          List<Zone> zones = new ArrayList<>();
          World.getZones(zones, activeChar.getLoc(), activeChar.getReflection());
          Iterator var13 = zones.iterator();

          while(var13.hasNext()) {
            Zone zone = (Zone)var13.next();
            activeChar.sendMessage(zone.getType().toString() + ", name: " + zone.getName() + ", state: " + (zone.isActive() ? "active" : "not active") + ", inside: " + zone.checkIfInZone(activeChar) + "/" + zone.checkIfInZone(activeChar.getX(), activeChar.getY(), activeChar.getZ()));
          }

          return true;
        case admin_region:
          activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
          activeChar.sendMessage("Objects list:");
          Iterator var10 = activeChar.getCurrentRegion().iterator();

          while(var10.hasNext()) {
            GameObject o = (GameObject)var10.next();
            if (o != null) {
              activeChar.sendMessage(o.toString());
            }
          }

          return true;
        case admin_vis_count:
          activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
          activeChar.sendMessage("Players count: " + World.getAroundPlayers(activeChar).size());
          break;
        case admin_pos:
          String pos = activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ() + ", " + activeChar.getHeading() + " Geo [" + (activeChar.getX() - World.MAP_MIN_X >> 4) + ", " + (activeChar.getY() - World.MAP_MIN_Y >> 4) + "] Ref " + activeChar.getReflectionId();
          activeChar.sendMessage("Pos: " + pos);
          activeChar.sendPacket(new DropItem(0, 16777215 & Rnd.nextInt(), 57, activeChar.getLoc().clone().setZ(activeChar.getZ() + 64), false, 1));
          break;
        case admin_domain:
          DomainArea domain = (DomainArea)MapRegionManager.getInstance().getRegionData(DomainArea.class, activeChar);
          Castle castle = domain != null ? (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, domain.getId()) : null;
          if (castle != null) {
            activeChar.sendMessage("Domain: " + castle.getName());
          } else {
            activeChar.sendMessage("Domain: Unknown");
          }
      }

      return true;
    } else {
      return false;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminZone.Commands.values();
  }

  private static enum Commands {
    admin_zone_check,
    admin_region,
    admin_pos,
    admin_vis_count,
    admin_domain;

    private Commands() {
    }
  }
}
