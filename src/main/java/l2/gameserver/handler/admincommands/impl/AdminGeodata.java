//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminGeodata implements IAdminCommandHandler {
  public AdminGeodata() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminGeodata.Commands command = (AdminGeodata.Commands)comm;
    if (!activeChar.getPlayerAccess().CanReload) {
      return false;
    } else {
      switch(command) {
        case admin_geo_z:
          if (wordList.length > 1) {
            activeChar.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(activeChar.getX(), activeChar.getY(), Integer.parseInt(wordList[1]), activeChar.getReflectionId()) + " Loc_Z = " + activeChar.getZ());
          } else {
            activeChar.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(activeChar.getLoc(), activeChar.getReflectionId()) + " Loc_Z = " + activeChar.getZ());
          }
          break;
        case admin_geo_type:
          int type = GeoEngine.getType(activeChar.getX(), activeChar.getY(), activeChar.getReflectionId());
          activeChar.sendMessage("GeoEngine: Geo_Type = " + type);
          break;
        case admin_geo_nswe:
          String result = "";
          byte nswe = GeoEngine.getNSWE(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getReflectionId());
          if ((nswe & 8) == 0) {
            result = result + " N";
          }

          if ((nswe & 4) == 0) {
            result = result + " S";
          }

          if ((nswe & 2) == 0) {
            result = result + " W";
          }

          if ((nswe & 1) == 0) {
            result = result + " E";
          }

          activeChar.sendMessage("GeoEngine: Geo_NSWE -> " + nswe + "->" + result);
          break;
        case admin_geo_los:
          if (activeChar.getTarget() != null) {
            if (GeoEngine.canSeeTarget(activeChar, activeChar.getTarget(), false)) {
              activeChar.sendMessage("GeoEngine: Can See Target");
            } else {
              activeChar.sendMessage("GeoEngine: Can't See Target");
            }
          } else {
            activeChar.sendMessage("None Target!");
          }
          break;
        case admin_geo_move:
          if (activeChar.getTarget() != null) {
            if (GeoEngine.canMoveToCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getTarget().getX(), activeChar.getTarget().getY(), activeChar.getTarget().getZ(), activeChar.getGeoIndex())) {
              activeChar.sendMessage("GeoEngine: Can move to target.");
            } else {
              activeChar.sendMessage("GeoEngine: Can't move to target.");
            }
          } else {
            activeChar.sendMessage("None target!");
          }
          break;
        case admin_geo_trace:
          if (wordList.length < 2) {
            activeChar.sendMessage("Usage: //geo_trace on|off");
            return false;
          }

          if (wordList[1].equalsIgnoreCase("on")) {
            activeChar.setVar("trace", "1", -1L);
          } else if (wordList[1].equalsIgnoreCase("off")) {
            activeChar.unsetVar("trace");
          } else {
            activeChar.sendMessage("Usage: //geo_trace on|off");
          }
          break;
        case admin_geo_map:
          int x = (activeChar.getX() - World.MAP_MIN_X >> 15) + Config.GEO_X_FIRST;
          int y = (activeChar.getY() - World.MAP_MIN_Y >> 15) + Config.GEO_Y_FIRST;
          activeChar.sendMessage("GeoMap: " + x + "_" + y);
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminGeodata.Commands.values();
  }

  private static enum Commands {
    admin_geo_z,
    admin_geo_type,
    admin_geo_nswe,
    admin_geo_los,
    admin_geo_move,
    admin_geo_trace,
    admin_geo_map;

    private Commands() {
    }
  }
}
