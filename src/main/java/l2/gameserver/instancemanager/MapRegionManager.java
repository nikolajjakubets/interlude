//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.World;
import l2.gameserver.templates.mapregion.RegionData;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

public class MapRegionManager extends AbstractHolder {
  private static final MapRegionManager _instance = new MapRegionManager();
  private RegionData[][][] map;

  public static MapRegionManager getInstance() {
    return _instance;
  }

  private MapRegionManager() {
    this.map = new RegionData[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][0];
  }

  private int regionX(int x) {
    return x - World.MAP_MIN_X >> 15;
  }

  private int regionY(int y) {
    return y - World.MAP_MIN_Y >> 15;
  }

  public void addRegionData(RegionData rd) {
    for(int x = this.regionX(rd.getTerritory().getXmin()); x <= this.regionX(rd.getTerritory().getXmax()); ++x) {
      for(int y = this.regionY(rd.getTerritory().getYmin()); y <= this.regionY(rd.getTerritory().getYmax()); ++y) {
        this.map[x][y] = ArrayUtils.add(this.map[x][y], rd);
      }
    }

  }

  public <T extends RegionData> T getRegionData(Class<T> clazz, GameObject o) {
    return this.getRegionData(clazz, o.getX(), o.getY(), o.getZ());
  }

  public <T extends RegionData> T getRegionData(Class<T> clazz, Location loc) {
    return this.getRegionData(clazz, loc.getX(), loc.getY(), loc.getZ());
  }

  public <T extends RegionData> T getRegionData(Class<T> clazz, int x, int y, int z) {
    RegionData[] var5 = this.map[this.regionX(x)][this.regionY(y)];

    for (RegionData regionDatas : var5) {
      if (regionDatas.getClass() == clazz && regionDatas.getTerritory().isInside(x, y, z)) {
        return (T) regionDatas;
      }
    }

    return null;
  }

  public int size() {
    return World.WORLD_SIZE_X * World.WORLD_SIZE_Y;
  }

  public void clear() {
  }
}
