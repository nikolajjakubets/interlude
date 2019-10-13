//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import l2.commons.geometry.Point3D;
import l2.commons.geometry.Shape;
import l2.commons.util.Rnd;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;

public class Territory implements Shape, SpawnRange {
  protected final Point3D max = new Point3D();
  protected final Point3D min = new Point3D();
  private final List<Shape> include = new ArrayList(1);
  private final List<Shape> exclude = new ArrayList(1);

  public Territory() {
  }

  public Territory add(Shape shape) {
    if (this.include.isEmpty()) {
      this.max.x = shape.getXmax();
      this.max.y = shape.getYmax();
      this.max.z = shape.getZmax();
      this.min.x = shape.getXmin();
      this.min.y = shape.getYmin();
      this.min.z = shape.getZmin();
    } else {
      this.max.x = Math.max(this.max.x, shape.getXmax());
      this.max.y = Math.max(this.max.y, shape.getYmax());
      this.max.z = Math.max(this.max.z, shape.getZmax());
      this.min.x = Math.min(this.min.x, shape.getXmin());
      this.min.y = Math.min(this.min.y, shape.getYmin());
      this.min.z = Math.min(this.min.z, shape.getZmin());
    }

    this.include.add(shape);
    return this;
  }

  public Territory addBanned(Shape shape) {
    this.exclude.add(shape);
    return this;
  }

  public List<Shape> getTerritories() {
    return this.include;
  }

  public List<Shape> getBannedTerritories() {
    return this.exclude;
  }

  public boolean isInside(int x, int y) {
    for(int i = 0; i < this.include.size(); ++i) {
      Shape shape = (Shape)this.include.get(i);
      if (shape.isInside(x, y)) {
        return !this.isExcluded(x, y);
      }
    }

    return false;
  }

  public boolean isInside(int x, int y, int z) {
    if (x >= this.min.x && x <= this.max.x && y >= this.min.y && y <= this.max.y && z >= this.min.z && z <= this.max.z) {
      for(int i = 0; i < this.include.size(); ++i) {
        Shape shape = (Shape)this.include.get(i);
        if (shape.isInside(x, y, z)) {
          return !this.isExcluded(x, y, z);
        }
      }

      return false;
    } else {
      return false;
    }
  }

  public boolean isExcluded(int x, int y) {
    for(int i = 0; i < this.exclude.size(); ++i) {
      Shape shape = (Shape)this.exclude.get(i);
      if (shape.isInside(x, y)) {
        return true;
      }
    }

    return false;
  }

  public boolean isExcluded(int x, int y, int z) {
    for(int i = 0; i < this.exclude.size(); ++i) {
      Shape shape = (Shape)this.exclude.get(i);
      if (shape.isInside(x, y, z)) {
        return true;
      }
    }

    return false;
  }

  public int getXmax() {
    return this.max.x;
  }

  public int getXmin() {
    return this.min.x;
  }

  public int getYmax() {
    return this.max.y;
  }

  public int getYmin() {
    return this.min.y;
  }

  public int getZmax() {
    return this.max.z;
  }

  public int getZmin() {
    return this.min.z;
  }

  public static Location getRandomLoc(Territory territory) {
    return getRandomLoc(territory, 0);
  }

  public static Location getRandomLoc(Territory territory, int geoIndex) {
    Location pos = new Location();
    List<Shape> territories = territory.getTerritories();

    label51:
    for(int i = 0; i < 100; ++i) {
      Shape shape = (Shape)territories.get(Rnd.get(territories.size()));
      pos.x = Rnd.get(shape.getXmin(), shape.getXmax());
      pos.y = Rnd.get(shape.getYmin(), shape.getYmax());
      pos.z = shape.getZmin() + (shape.getZmax() - shape.getZmin()) / 2;
      if (territory.isInside(pos.x, pos.y)) {
        int tempz = GeoEngine.getHeight(pos, geoIndex);
        if (shape.getZmin() != shape.getZmax()) {
          if (tempz < shape.getZmin() || tempz > shape.getZmax()) {
            continue;
          }
        } else if (tempz < shape.getZmin() - 200 || tempz > shape.getZmin() + 200) {
          continue;
        }

        pos.z = tempz;
        int geoX = pos.x - World.MAP_MIN_X >> 4;
        int geoY = pos.y - World.MAP_MIN_Y >> 4;

        for(int x = geoX - 1; x <= geoX + 1; ++x) {
          for(int y = geoY - 1; y <= geoY + 1; ++y) {
            if (GeoEngine.NgetNSWE(x, y, tempz, geoIndex) != 15) {
              continue label51;
            }
          }
        }

        return pos;
      }
    }

    return pos;
  }

  public Location getRandomLoc(int geoIndex) {
    return getRandomLoc(this, geoIndex);
  }
}
