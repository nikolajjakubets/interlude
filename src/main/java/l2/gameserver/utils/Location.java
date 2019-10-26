//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import java.io.Serializable;
import l2.commons.geometry.Point2D;
import l2.commons.geometry.Point3D;
import l2.commons.util.Rnd;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.World;
import l2.gameserver.templates.spawn.SpawnRange;
import org.dom4j.Element;

public class Location extends Point3D implements SpawnRange, Serializable {
  public int h;

  public Location() {
  }

  public Location(int x, int y, int z, int heading) {
    super(x, y, z);
    this.h = heading;
  }

  public Location(int x, int y, int z) {
    this(x, y, z, 0);
  }

  public Location(GameObject obj) {
    this(obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
  }

  public int getHeading() {
    return this.h;
  }

  public int getH() {
    return this.h;
  }

  public Location changeZ(int zDiff) {
    this.z += zDiff;
    return this;
  }

  public Location correctGeoZ() {
    this.z = GeoEngine.getHeight(this.x, this.y, this.z, 0);
    return this;
  }

  public Location correctGeoZ(int refIndex) {
    this.z = GeoEngine.getHeight(this.x, this.y, this.z, refIndex);
    return this;
  }

  public Location setX(int x) {
    this.x = x;
    return this;
  }

  public Location setY(int y) {
    this.y = y;
    return this;
  }

  public Location setZ(int z) {
    this.z = z;
    return this;
  }

  public Location setH(int h) {
    this.h = h;
    return this;
  }

  public Location set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  public Location set(int x, int y, int z, int h) {
    this.set(x, y, z);
    this.h = h;
    return this;
  }

  public Location set(Location loc) {
    this.x = loc.x;
    this.y = loc.y;
    this.z = loc.z;
    this.h = loc.h;
    return this;
  }

  public Location world2geo() {
    this.x = this.x - World.MAP_MIN_X >> 4;
    this.y = this.y - World.MAP_MIN_Y >> 4;
    return this;
  }

  public Location geo2world() {
    this.x = (this.x << 4) + World.MAP_MIN_X + 8;
    this.y = (this.y << 4) + World.MAP_MIN_Y + 8;
    return this;
  }

  public double distance(Location loc) {
    return this.distance(loc.x, loc.y);
  }

  public double distance(int x, int y) {
    long dx = (long)(this.x - x);
    long dy = (long)(this.y - y);
    return Math.sqrt((double)(dx * dx + dy * dy));
  }

  public double distance3D(Location loc) {
    return this.distance3D(loc.x, loc.y, loc.z);
  }

  public double distance3D(int x, int y, int z) {
    long dx = (long)(this.x - x);
    long dy = (long)(this.y - y);
    long dz = (long)(this.z - z);
    return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
  }

  public Location clone() {
    return new Location(this.x, this.y, this.z, this.h);
  }

  public final String toString() {
    return this.x + "," + this.y + "," + this.z + "," + this.h;
  }

  public boolean isNull() {
    return this.x == 0 || this.y == 0 || this.z == 0;
  }

  public final String toXYZString() {
    return this.x + " " + this.y + " " + this.z;
  }

  public final String toXYZHString() {
    return this.x + " " + this.y + " " + this.z + " " + this.h;
  }

  public static Location parseLoc(String s) throws IllegalArgumentException {
    String[] xyzh = s.split("[\\s,;]+");
    if (xyzh.length < 3) {
      throw new IllegalArgumentException("Can't parse location from string: " + s);
    } else {
      int x = Integer.parseInt(xyzh[0]);
      int y = Integer.parseInt(xyzh[1]);
      int z = Integer.parseInt(xyzh[2]);
      int h = xyzh.length < 4 ? 0 : Integer.parseInt(xyzh[3]);
      return new Location(x, y, z, h);
    }
  }

  public static Location parse(Element element) {
    int x = Integer.parseInt(element.attributeValue("x"));
    int y = Integer.parseInt(element.attributeValue("y"));
    int z = Integer.parseInt(element.attributeValue("z"));
    int h = element.attributeValue("h") == null ? 0 : Integer.parseInt(element.attributeValue("h"));
    return new Location(x, y, z, h);
  }

  public Location indent(Location to, int indent, boolean includeZ) {
    if (indent <= 0) {
      return this;
    } else {
      long dx = (long)(this.getX() - to.getX());
      long dy = (long)(this.getY() - to.getY());
      long dz = (long)(this.getZ() - to.getZ());
      double distance = includeZ ? Math.sqrt((double)(dx * dx + dy * dy + dz * dz)) : Math.sqrt((double)(dx * dx + dy * dy));
      if (distance <= (double)indent) {
        this.set(to.getX(), to.getY(), to.getZ());
        return this;
      } else {
        if (distance >= 1.0D) {
          double cut = (double)indent / distance;
          this.setX(this.getX() - (int)((double)dx * cut + 0.5D));
          this.setY(this.getY() - (int)((double)dy * cut + 0.5D));
          this.setZ(this.getZ() - (int)((double)dz * cut + 0.5D));
        }

        return this;
      }
    }
  }

  public boolean equalsGeo(Object o) {
    if (this == o) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o instanceof Point2D) {
      Point2D otherPoint2D = (Point2D)o;
      if (otherPoint2D.x - World.MAP_MIN_X >> 4 != this.x - World.MAP_MIN_X >> 4) {
        return false;
      } else if (otherPoint2D.y - World.MAP_MIN_Y >> 4 != this.y - World.MAP_MIN_Y >> 4) {
        return false;
      } else if (o instanceof Point3D) {
        return ((Point3D)o).z == this.z;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  public Location indent(Location to, int indent) {
    return this.indent(to, indent, true);
  }

  public static Location findFrontPosition(GameObject obj, GameObject obj2, int radiusmin, int radiusmax) {
    if (radiusmax != 0 && radiusmax >= radiusmin) {
      double collision = obj.getColRadius() + obj2.getColRadius();
      int minangle = 0;
      int maxangle = 360;
      if (!obj.equals(obj2)) {
        double angle = PositionUtils.calculateAngleFrom(obj, obj2);
        minangle = (int)angle - 45;
        maxangle = (int)angle + 45;
      }

      Location pos = new Location();

      for(int i = 0; i < 100; ++i) {
        int randomRadius = Rnd.get(radiusmin, radiusmax);
        int randomAngle = Rnd.get(minangle, maxangle);
        pos.x = obj.getX() + (int)((collision + (double)randomRadius) * Math.cos(Math.toRadians((double)randomAngle)));
        pos.y = obj.getY() + (int)((collision + (double)randomRadius) * Math.sin(Math.toRadians((double)randomAngle)));
        pos.z = obj.getZ();
        int tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, obj.getGeoIndex());
        if (Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, obj.getGeoIndex()) == 15) {
          pos.z = tempz;
          if (!obj.equals(obj2)) {
            pos.h = PositionUtils.getHeadingTo(pos, obj2.getLoc());
          } else {
            pos.h = obj.getHeading();
          }

          return pos;
        }
      }

      return new Location(obj);
    } else {
      return new Location(obj);
    }
  }

  public static Location findAroundPosition(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex) {
    for(int i = 0; i < 100; ++i) {
      Location pos = coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
      int tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
      if (GeoEngine.canMoveToCoord(x, y, z, pos.x, pos.y, tempz, geoIndex) && GeoEngine.canMoveToCoord(pos.x, pos.y, tempz, x, y, z, geoIndex)) {
        pos.z = tempz;
        return pos;
      }
    }

    return new Location(x, y, z);
  }

  public static Location findAroundPosition(Location loc, int radius, int geoIndex) {
    return findAroundPosition(loc.x, loc.y, loc.z, 0, radius, geoIndex);
  }

  public static Location findAroundPosition(Location loc, int radiusmin, int radiusmax, int geoIndex) {
    return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
  }

  public static Location findAroundPosition(GameObject obj, Location loc, int radiusmin, int radiusmax) {
    return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
  }

  public static Location findAroundPosition(GameObject obj, int radiusmin, int radiusmax) {
    return findAroundPosition(obj, obj.getLoc(), radiusmin, radiusmax);
  }

  public static Location findAroundPosition(GameObject obj, int radius) {
    return findAroundPosition((GameObject)obj, 0, radius);
  }

  public static Location findPointToStay(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex) {
    for(int i = 0; i < 100; ++i) {
      Location pos = coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
      int tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
      if (Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, geoIndex) == 15) {
        pos.z = tempz;
        return pos;
      }
    }

    return new Location(x, y, z);
  }

  public static Location findPointToStay(Location loc, int radius, int geoIndex) {
    return findPointToStay(loc.x, loc.y, loc.z, 0, radius, geoIndex);
  }

  public static Location findPointToStay(Location loc, int radiusmin, int radiusmax, int geoIndex) {
    return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
  }

  public static Location findPointToStay(GameObject obj, Location loc, int radiusmin, int radiusmax) {
    return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
  }

  public static Location findPointToStay(GameObject obj, int radiusmin, int radiusmax) {
    return findPointToStay(obj, obj.getLoc(), radiusmin, radiusmax);
  }

  public static Location findPointToStay(GameObject obj, int radius) {
    return findPointToStay((GameObject)obj, 0, radius);
  }

  public static Location coordsRandomize(Location loc, int radiusmin, int radiusmax) {
    return coordsRandomize(loc.x, loc.y, loc.z, loc.h, radiusmin, radiusmax);
  }

  public static Location coordsRandomize(int x, int y, int z, int heading, int radiusmin, int radiusmax) {
    if (radiusmax != 0 && radiusmax >= radiusmin) {
      int radius = Rnd.get(radiusmin, radiusmax);
      double angle = Rnd.nextDouble() * 2.0D * 3.141592653589793D;
      return new Location((int)((double)x + (double)radius * Math.cos(angle)), (int)((double)y + (double)radius * Math.sin(angle)), z, heading);
    } else {
      return new Location(x, y, z, heading);
    }
  }

  public static Location findNearest(Creature creature, Location[] locs) {
    Location defloc = null;
    Location[] var3 = locs;
    int var4 = locs.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      Location loc = var3[var5];
      if (defloc == null) {
        defloc = loc;
      } else if (creature.getDistance(loc) < creature.getDistance(defloc)) {
        defloc = loc;
      }
    }

    return defloc;
  }

  public static int getRandomHeading() {
    return Rnd.get(65535);
  }

  public Location getRandomLoc(int ref) {
    return this;
  }
}
