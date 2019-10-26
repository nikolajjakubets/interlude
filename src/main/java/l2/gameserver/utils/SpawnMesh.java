//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import l2.commons.geometry.Polygon;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.World;
import l2.gameserver.templates.spawn.SpawnRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnMesh extends Polygon implements SpawnRange {
  private static final Logger LOG = LoggerFactory.getLogger(SpawnMesh.class);

  public SpawnMesh() {
  }

  public Location getRandomLoc(int geoIndex) {
    Location loc = new Location(0, 0, 0);
    int width = this.getXmax() - this.getXmin();
    int height = this.getYmax() - this.getYmin();
    int dropZ = this.getZmin() + (this.getZmax() - this.getZmin()) / 2;
    int maxAttempts = Math.max(2048, (height >> 4) * (width >> 4));
    int var8 = 0;

    label53:
    do {
      loc.setX(Rnd.get(this.getXmin(), this.getXmax()));
      loc.setY(Rnd.get(this.getYmin(), this.getYmax()));
      loc.setZ(dropZ);
      if (this.isInside(loc.getX(), loc.getY())) {
        int tempz = GeoEngine.getHeight(loc, geoIndex);
        if (this.getZmin() != this.getZmax()) {
          if (tempz < this.getZmin() || tempz > this.getZmax()) {
            continue;
          }
        } else if (tempz < this.getZmin() - Config.MAX_Z_DIFF || tempz > this.getZmin() + Config.MAX_Z_DIFF) {
          continue;
        }

        loc.setZ(tempz);
        int geoX = loc.getX() - World.MAP_MIN_X >> 4;
        int geoY = loc.getY() - World.MAP_MIN_Y >> 4;

        for(int gx = geoX - 1; gx <= geoX + 1; ++gx) {
          for(int gy = geoY - 1; gy <= geoY + 1; ++gy) {
            if (GeoEngine.NgetNSWE(gx, gy, tempz, geoIndex) != 15) {
              continue label53;
            }
          }
        }

        return loc;
      }
    } while(var8++ < maxAttempts);

    if (Config.ALT_DEBUG_ENABLED) {
      LOG.warn("Cant find suitable point in " + this.toString() + " z[" + this.getZmin() + " " + this.getZmax() + "] last: " + loc);
    }

    return loc;
  }
}
