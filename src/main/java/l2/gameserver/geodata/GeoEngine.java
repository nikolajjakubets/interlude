//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.geodata;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.geometry.Shape;
import l2.commons.util.NaturalOrderComparator;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoOptimizer.BlockLink;
import l2.gameserver.geodata.GeoOptimizer.CheckSumLoader;
import l2.gameserver.geodata.GeoOptimizer.GeoBlocksMatchFinder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.World;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoEngine {
  private static final Logger _log = LoggerFactory.getLogger(GeoEngine.class);
  public static final byte EAST = 1;
  public static final byte WEST = 2;
  public static final byte SOUTH = 4;
  public static final byte NORTH = 8;
  public static final byte NSWE_ALL = 15;
  public static final byte NSWE_NONE = 0;
  public static final byte BLOCKTYPE_FLAT = 0;
  public static final byte BLOCKTYPE_COMPLEX = 1;
  public static final byte BLOCKTYPE_MULTILEVEL = 2;
  public static final int BLOCKS_IN_MAP = 65536;
  public static int MAX_LAYERS = 1;
  private static final ByteBuffer[][] rawgeo;
  private static final byte[][][][][] geodata;

  public GeoEngine() {
  }

  public static short getType(int x, int y, int geoIndex) {
    return NgetType(x - World.MAP_MIN_X >> 4, y - World.MAP_MIN_Y >> 4, geoIndex);
  }

  public static int getHeight(Location loc, int geoIndex) {
    return getHeight(loc.x, loc.y, loc.z, geoIndex);
  }

  public static int getHeight(int x, int y, int z, int geoIndex) {
    return NgetHeight(x - World.MAP_MIN_X >> 4, y - World.MAP_MIN_Y >> 4, z, geoIndex);
  }

  public static boolean canMoveToCoord(int x, int y, int z, int tx, int ty, int tz, int geoIndex) {
    return canMove(x, y, z, tx, ty, tz, false, geoIndex) == 0;
  }

  public static byte getNSWE(int x, int y, int z, int geoIndex) {
    return NgetNSWE(x - World.MAP_MIN_X >> 4, y - World.MAP_MIN_Y >> 4, z, geoIndex);
  }

  public static Location moveCheck(int x, int y, int z, int tx, int ty, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, false, false, false, geoIndex);
  }

  public static Location moveCheck(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, false, false, returnPrev, geoIndex);
  }

  public static Location moveCheckWithCollision(int x, int y, int z, int tx, int ty, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, true, false, false, geoIndex);
  }

  public static Location moveCheckWithCollision(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, true, false, returnPrev, geoIndex);
  }

  public static Location moveCheckBackward(int x, int y, int z, int tx, int ty, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, false, true, false, geoIndex);
  }

  public static Location moveCheckBackward(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, false, true, returnPrev, geoIndex);
  }

  public static Location moveCheckBackwardWithCollision(int x, int y, int z, int tx, int ty, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, true, true, false, geoIndex);
  }

  public static Location moveCheckBackwardWithCollision(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex) {
    return MoveCheck(x, y, z, tx, ty, true, true, returnPrev, geoIndex);
  }

  public static Location moveInWaterCheck(int x, int y, int z, int tx, int ty, int tz, int waterZ, int geoIndex) {
    return MoveInWaterCheck(x - World.MAP_MIN_X >> 4, y - World.MAP_MIN_Y >> 4, z, tx - World.MAP_MIN_X >> 4, ty - World.MAP_MIN_Y >> 4, tz, waterZ, geoIndex);
  }

  public static Location moveCheckForAI(Location loc1, Location loc2, int geoIndex) {
    return MoveCheckForAI(loc1.x - World.MAP_MIN_X >> 4, loc1.y - World.MAP_MIN_Y >> 4, loc1.z, loc2.x - World.MAP_MIN_X >> 4, loc2.y - World.MAP_MIN_Y >> 4, geoIndex);
  }

  public static Location moveCheckInAir(int x, int y, int z, int tx, int ty, int tz, double collision, int geoIndex) {
    int gx = x - World.MAP_MIN_X >> 4;
    int gy = y - World.MAP_MIN_Y >> 4;
    int tgx = tx - World.MAP_MIN_X >> 4;
    int tgy = ty - World.MAP_MIN_Y >> 4;
    int nz = NgetHeight(tgx, tgy, tz, geoIndex);
    if (tz <= nz + 32) {
      tz = nz + 32;
    }

    Location result = canSee(gx, gy, z, tgx, tgy, tz, true, geoIndex);
    return result.equals(gx, gy, z) ? null : result.geo2world();
  }

  public static boolean canSeeTarget(GameObject actor, GameObject target, boolean air) {
    if (target == null) {
      return false;
    } else {
      return !(target instanceof GeoCollision) && !actor.equals(target) ? canSeeCoord(actor, target.getX(), target.getY(), target.getZ() + (int)target.getColHeight() + 64, air) : true;
    }
  }

  public static boolean canSeeCoord(GameObject actor, int tx, int ty, int tz, boolean air) {
    return actor != null && canSeeCoord(actor.getX(), actor.getY(), actor.getZ() + (int)actor.getColHeight() + 64, tx, ty, tz, air, actor.getGeoIndex());
  }

  public static boolean canSeeCoord(int x, int y, int z, int tx, int ty, int tz, boolean air, int geoIndex) {
    int mx = x - World.MAP_MIN_X >> 4;
    int my = y - World.MAP_MIN_Y >> 4;
    int tmx = tx - World.MAP_MIN_X >> 4;
    int tmy = ty - World.MAP_MIN_Y >> 4;
    return canSee(mx, my, z, tmx, tmy, tz, air, geoIndex).equals(tmx, tmy, tz) && canSee(tmx, tmy, tz, mx, my, z, air, geoIndex).equals(mx, my, z);
  }

  public static boolean canMoveWithCollision(int x, int y, int z, int tx, int ty, int tz, int geoIndex) {
    return canMove(x, y, z, tx, ty, tz, true, geoIndex) == 0;
  }

  public static boolean checkNSWE(byte NSWE, int x, int y, int tx, int ty) {
    if (NSWE == 15) {
      return true;
    } else if (NSWE == 0) {
      return false;
    } else {
      if (tx > x) {
        if ((NSWE & 1) == 0) {
          return false;
        }
      } else if (tx < x && (NSWE & 2) == 0) {
        return false;
      }

      if (ty > y) {
        if ((NSWE & 4) == 0) {
          return false;
        }
      } else if (ty < y && (NSWE & 8) == 0) {
        return false;
      }

      return true;
    }
  }

  public static String geoXYZ2Str(int _x, int _y, int _z) {
    return "(" + String.valueOf((_x << 4) + World.MAP_MIN_X + 8) + " " + ((_y << 4) + World.MAP_MIN_Y + 8) + " " + _z + ")";
  }

  public static String NSWE2Str(byte nswe) {
    String result = "";
    if ((nswe & 8) == 8) {
      result = result + "N";
    }

    if ((nswe & 4) == 4) {
      result = result + "S";
    }

    if ((nswe & 2) == 2) {
      result = result + "W";
    }

    if ((nswe & 1) == 1) {
      result = result + "E";
    }

    return result.isEmpty() ? "X" : result;
  }

  private static boolean NLOS_WATER(int x, int y, int z, int next_x, int next_y, int next_z, int geoIndex) {
    short[] layers1 = new short[MAX_LAYERS + 1];
    short[] layers2 = new short[MAX_LAYERS + 1];
    NGetLayers(x, y, layers1, geoIndex);
    NGetLayers(next_x, next_y, layers2, geoIndex);
    if (layers1[0] != 0 && layers2[0] != 0) {
      short z2 = -32768;

      short h;
      for(int i = 1; i <= layers2[0]; ++i) {
        h = (short)((short)(layers2[i] & '\ufff0') >> 1);
        if (Math.abs(next_z - z2) > Math.abs(next_z - h)) {
          z2 = h;
        }
      }

      if (next_z + 32 >= z2) {
        return true;
      } else {
        short z3 = -32768;

        for(int i = 1; i <= layers2[0]; ++i) {
          h = (short)((short)(layers2[i] & '\ufff0') >> 1);
          if (h < z2 + Config.MIN_LAYER_HEIGHT && Math.abs(next_z - z3) > Math.abs(next_z - h)) {
            z3 = h;
          }
        }

        if (z3 == -32768) {
          return false;
        } else {
          short z1 = -32768;
          byte NSWE1 = 15;

          for(int i = 1; i <= layers1[0]; ++i) {
            h = (short)((short)(layers1[i] & '\ufff0') >> 1);
            if (h < z + Config.MIN_LAYER_HEIGHT && Math.abs(z - z1) > Math.abs(z - h)) {
              z1 = h;
              NSWE1 = (byte)(layers1[i] & 15);
            }
          }

          return checkNSWE(NSWE1, x, y, next_x, next_y);
        }
      }
    } else {
      return true;
    }
  }

  private static int FindNearestLowerLayer(short[] layers, int z) {
    short nearest_layer_h = -32768;
    int nearest_layer = -2147483648;

    for(int i = 1; i <= layers[0]; ++i) {
      short h = (short)((short)(layers[i] & '\ufff0') >> 1);
      if (h < z && nearest_layer_h < h) {
        nearest_layer_h = h;
        nearest_layer = layers[i];
      }
    }

    return nearest_layer;
  }

  private static short CheckNoOneLayerInRangeAndFindNearestLowerLayer(short[] layers, int z0, int z1) {
    int z_min;
    int z_max;
    if (z0 > z1) {
      z_min = z1;
      z_max = z0;
    } else {
      z_min = z0;
      z_max = z1;
    }

    short nearest_layer = -32768;
    short nearest_layer_h = -32768;

    for(int i = 1; i <= layers[0]; ++i) {
      short h = (short)((short)(layers[i] & '\ufff0') >> 1);
      if (z_min <= h && h <= z_max) {
        return -32768;
      }

      if (h < z0 && nearest_layer_h < h) {
        nearest_layer_h = h;
        nearest_layer = layers[i];
      }
    }

    return nearest_layer;
  }

  public static boolean canSeeWallCheck(short layer, short nearest_lower_neighbor, byte directionNSWE, int curr_z, boolean air) {
    short nearest_lower_neighborh = (short)((short)(nearest_lower_neighbor & '\ufff0') >> 1);
    if (air) {
      return nearest_lower_neighborh < curr_z;
    } else {
      short layerh = (short)((short)(layer & '\ufff0') >> 1);
      int zdiff = nearest_lower_neighborh - layerh;
      return (layer & 15 & directionNSWE) != 0 || zdiff > -Config.MAX_Z_DIFF && zdiff != 0;
    }
  }

  public static Location canSee(int _x, int _y, int _z, int _tx, int _ty, int _tz, boolean air, int geoIndex) {
    int diff_x = _tx - _x;
    int diff_y = _ty - _y;
    int diff_z = _tz - _z;
    int dx = Math.abs(diff_x);
    int dy = Math.abs(diff_y);
    float steps = (float)Math.max(dx, dy);
    int curr_x = _x;
    int curr_y = _y;
    int curr_z = _z;
    short[] curr_layers = new short[MAX_LAYERS + 1];
    NGetLayers(_x, _y, curr_layers, geoIndex);
    Location result = new Location(_x, _y, _z, -1);
    if (steps == 0.0F) {
      if (CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, _z, _z + diff_z) != -32768) {
        result.set(_tx, _ty, _tz, 1);
      }

      return result;
    } else {
      float step_x = (float)diff_x / steps;
      float step_y = (float)diff_y / steps;
      float step_z = (float)diff_z / steps;
      float half_step_z = step_z / 2.0F;
      float next_x = (float)_x;
      float next_y = (float)_y;
      float next_z = (float)_z;
      short[] tmp_layers = new short[MAX_LAYERS + 1];

      for(int i = 0; (float)i < steps; ++i) {
        if (curr_layers[0] == 0) {
          result.set(_tx, _ty, _tz, 0);
          return result;
        }

        next_x += step_x;
        next_y += step_y;
        next_z += step_z;
        int i_next_x = (int)(next_x + 0.5F);
        int i_next_y = (int)(next_y + 0.5F);
        int i_next_z = (int)(next_z + 0.5F);
        int middle_z = (int)((float)curr_z + half_step_z);
        short src_nearest_lower_layer;
        if ((src_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, middle_z)) == -32768) {
          return result.setH(-10);
        }

        NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
        if (curr_layers[0] == 0) {
          result.set(_tx, _ty, _tz, 0);
          return result;
        }

        short dst_nearest_lower_layer;
        if ((dst_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, i_next_z, middle_z)) == -32768) {
          return result.setH(-11);
        }

        if (curr_x == i_next_x) {
          if (!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, (byte)(i_next_y > curr_y ? 4 : 8), curr_z, air)) {
            return result.setH(-20);
          }
        } else if (curr_y == i_next_y) {
          if (!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, (byte)(i_next_x > curr_x ? 1 : 2), curr_z, air)) {
            return result.setH(-21);
          }
        } else {
          NGetLayers(curr_x, i_next_y, tmp_layers, geoIndex);
          if (tmp_layers[0] == 0) {
            result.set(_tx, _ty, _tz, 0);
            return result;
          }

          short tmp_nearest_lower_layer;
          if ((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == -32768) {
            return result.setH(-30);
          }

          if (!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, (byte)(i_next_y > curr_y ? 4 : 8), curr_z, air) || !canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, (byte)(i_next_x > curr_x ? 1 : 2), curr_z, air)) {
            NGetLayers(i_next_x, curr_y, tmp_layers, geoIndex);
            if (tmp_layers[0] == 0) {
              result.set(_tx, _ty, _tz, 0);
              return result;
            }

            if ((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == -32768) {
              return result.setH(-31);
            }

            if (!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, (byte)(i_next_x > curr_x ? 1 : 2), curr_z, air)) {
              return result.setH(-32);
            }

            if (!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, (byte)(i_next_x > curr_x ? 1 : 2), curr_z, air)) {
              return result.setH(-33);
            }
          }
        }

        result.set(curr_x, curr_y, curr_z);
        curr_x = i_next_x;
        curr_y = i_next_y;
        curr_z = i_next_z;
      }

      result.set(_tx, _ty, _tz, 255);
      return result;
    }
  }

  private static Location MoveInWaterCheck(int x, int y, int z, int tx, int ty, int tz, int waterZ, int geoIndex) {
    int dx = tx - x;
    int dy = ty - y;
    int dz = tz - z;
    int inc_x = sign(dx);
    int inc_y = sign(dy);
    dx = Math.abs(dx);
    dy = Math.abs(dy);
    if (dx + dy == 0) {
      return (new Location(x, y, z)).geo2world();
    } else {
      float inc_z_for_x = dx == 0 ? 0.0F : (float)(dz / dx);
      float inc_z_for_y = dy == 0 ? 0.0F : (float)(dz / dy);
      float next_x = (float)x;
      float next_y = (float)y;
      float next_z = (float)z;
      int prev_x;
      int prev_y;
      int prev_z;
      int delta_A;
      int d;
      int delta_B;
      int i;
      if (dx >= dy) {
        delta_A = 2 * dy;
        d = delta_A - dx;
        delta_B = delta_A - 2 * dx;

        for(i = 0; i < dx; ++i) {
          prev_x = x;
          prev_y = y;
          prev_z = z;
          x = (int)next_x;
          y = (int)next_y;
          z = (int)next_z;
          if (d > 0) {
            d += delta_B;
            next_x += (float)inc_x;
            next_z += inc_z_for_x;
            next_y += (float)inc_y;
            next_z += inc_z_for_y;
          } else {
            d += delta_A;
            next_x += (float)inc_x;
            next_z += inc_z_for_x;
          }

          if (next_z >= (float)waterZ || !NLOS_WATER(x, y, z, (int)next_x, (int)next_y, (int)next_z, geoIndex)) {
            return (new Location(prev_x, prev_y, prev_z)).geo2world();
          }
        }
      } else {
        delta_A = 2 * dx;
        d = delta_A - dy;
        delta_B = delta_A - 2 * dy;

        for(i = 0; i < dy; ++i) {
          prev_x = x;
          prev_y = y;
          prev_z = z;
          x = (int)next_x;
          y = (int)next_y;
          z = (int)next_z;
          if (d > 0) {
            d += delta_B;
            next_x += (float)inc_x;
            next_z += inc_z_for_x;
            next_y += (float)inc_y;
            next_z += inc_z_for_y;
          } else {
            d += delta_A;
            next_y += (float)inc_y;
            next_z += inc_z_for_y;
          }

          if (next_z >= (float)waterZ || !NLOS_WATER(x, y, z, (int)next_x, (int)next_y, (int)next_z, geoIndex)) {
            return (new Location(prev_x, prev_y, prev_z)).geo2world();
          }
        }
      }

      return (new Location((int)next_x, (int)next_y, (int)next_z)).geo2world();
    }
  }

  private static int canMove(int __x, int __y, int _z, int __tx, int __ty, int _tz, boolean withCollision, int geoIndex) {
    int _x = __x - World.MAP_MIN_X >> 4;
    int _y = __y - World.MAP_MIN_Y >> 4;
    int _tx = __tx - World.MAP_MIN_X >> 4;
    int _ty = __ty - World.MAP_MIN_Y >> 4;
    int diff_x = _tx - _x;
    int diff_y = _ty - _y;
    int diff_z = _tz - _z;
    int dx = Math.abs(diff_x);
    int dy = Math.abs(diff_y);
    int dz = Math.abs(diff_z);
    float steps = (float)Math.max(dx, dy);
    if (steps == 0.0F) {
      return -5;
    } else {
      int curr_x = _x;
      int curr_y = _y;
      int curr_z = _z;
      short[] curr_layers = new short[MAX_LAYERS + 1];
      NGetLayers(_x, _y, curr_layers, geoIndex);
      if (curr_layers[0] == 0) {
        return 0;
      } else {
        float step_x = (float)diff_x / steps;
        float step_y = (float)diff_y / steps;
        float next_x = (float)_x;
        float next_y = (float)_y;
        short[] next_layers = new short[MAX_LAYERS + 1];
        short[] temp_layers = new short[MAX_LAYERS + 1];

        for(int i = 0; (float)i < steps; ++i) {
          next_x += step_x;
          next_y += step_y;
          int i_next_x = (int)(next_x + 0.5F);
          int i_next_y = (int)(next_y + 0.5F);
          NGetLayers(i_next_x, i_next_y, next_layers, geoIndex);
          if ((curr_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, i_next_x, i_next_y, next_layers, temp_layers, withCollision, geoIndex)) == -2147483648) {
            return 1;
          }

          short[] curr_next_switcher = curr_layers;
          curr_layers = next_layers;
          next_layers = curr_next_switcher;
          curr_x = i_next_x;
          curr_y = i_next_y;
        }

        diff_z = curr_z - _tz;
        dz = Math.abs(diff_z);
        if (Config.ALLOW_FALL_FROM_WALLS) {
          return diff_z < Config.MAX_Z_DIFF ? 0 : diff_z * 10000;
        } else {
          return dz > Config.MAX_Z_DIFF ? dz * 1000 : 0;
        }
      }
    }
  }

  private static Location MoveCheck(int __x, int __y, int _z, int __tx, int __ty, boolean withCollision, boolean backwardMove, boolean returnPrev, int geoIndex) {
    int _x = __x - World.MAP_MIN_X >> 4;
    int _y = __y - World.MAP_MIN_Y >> 4;
    int _tx = __tx - World.MAP_MIN_X >> 4;
    int _ty = __ty - World.MAP_MIN_Y >> 4;
    int diff_x = _tx - _x;
    int diff_y = _ty - _y;
    int dx = Math.abs(diff_x);
    int dy = Math.abs(diff_y);
    float steps = (float)Math.max(dx, dy);
    if (steps == 0.0F) {
      return new Location(__x, __y, _z);
    } else {
      float step_x = (float)diff_x / steps;
      float step_y = (float)diff_y / steps;
      int curr_x = _x;
      int curr_y = _y;
      int curr_z = _z;
      float next_x = (float)_x;
      float next_y = (float)_y;
      short[] next_layers = new short[MAX_LAYERS + 1];
      short[] temp_layers = new short[MAX_LAYERS + 1];
      short[] curr_layers = new short[MAX_LAYERS + 1];
      NGetLayers(_x, _y, curr_layers, geoIndex);
      int prev_x = _x;
      int prev_y = _y;
      int prev_z = _z;

      for(int i = 0; (float)i < steps; ++i) {
        next_x += step_x;
        next_y += step_y;
        int i_next_x = (int)(next_x + 0.5F);
        int i_next_y = (int)(next_y + 0.5F);
        NGetLayers(i_next_x, i_next_y, next_layers, geoIndex);
        int i_next_z;
        if ((i_next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, i_next_x, i_next_y, next_layers, temp_layers, withCollision, geoIndex)) == -2147483648 || backwardMove && NcanMoveNext(i_next_x, i_next_y, i_next_z, next_layers, curr_x, curr_y, curr_layers, temp_layers, withCollision, geoIndex) == -2147483648) {
          break;
        }

        short[] curr_next_switcher = curr_layers;
        curr_layers = next_layers;
        next_layers = curr_next_switcher;
        if (returnPrev) {
          prev_x = curr_x;
          prev_y = curr_y;
          prev_z = curr_z;
        }

        curr_x = i_next_x;
        curr_y = i_next_y;
        curr_z = i_next_z;
      }

      if (returnPrev) {
        curr_x = prev_x;
        curr_y = prev_y;
        curr_z = prev_z;
      }

      return (new Location(curr_x, curr_y, curr_z)).geo2world();
    }
  }

  public static List<Location> MoveList(int __x, int __y, int _z, int __tx, int __ty, int geoIndex, boolean onlyFullPath) {
    int _x = __x - World.MAP_MIN_X >> 4;
    int _y = __y - World.MAP_MIN_Y >> 4;
    int _tx = __tx - World.MAP_MIN_X >> 4;
    int _ty = __ty - World.MAP_MIN_Y >> 4;
    int diff_x = _tx - _x;
    int diff_y = _ty - _y;
    int incx = sign(diff_x);
    int incy = sign(diff_y);
    if (diff_x < 0) {
      diff_x = -diff_x;
    }

    if (diff_y < 0) {
      diff_y = -diff_y;
    }

    byte pdx;
    byte pdy;
    int es;
    int el;
    if (diff_x > diff_y) {
      pdx = incx;
      pdy = 0;
      es = diff_y;
      el = diff_x;
    } else {
      pdx = 0;
      pdy = incy;
      es = diff_x;
      el = diff_y;
    }

    int err = el / 2;
    int curr_x = _x;
    int curr_y = _y;
    int curr_z = _z;
    int next_x = _x;
    int next_y = _y;
    short[] next_layers = new short[MAX_LAYERS + 1];
    short[] temp_layers = new short[MAX_LAYERS + 1];
    short[] curr_layers = new short[MAX_LAYERS + 1];
    NGetLayers(_x, _y, curr_layers, geoIndex);
    if (curr_layers[0] == 0) {
      return null;
    } else {
      List<Location> result = new ArrayList(Math.min(1024, el + 1));
      result.add(new Location(_x, _y, _z));

      for(int i = 0; i < el; ++i) {
        err -= es;
        if (err < 0) {
          err += el;
          next_x += incx;
          next_y += incy;
        } else {
          next_x += pdx;
          next_y += pdy;
        }

        NGetLayers(next_x, next_y, next_layers, geoIndex);
        int next_z;
        if ((next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, false, geoIndex)) == -2147483648) {
          if (onlyFullPath) {
            return null;
          }
          break;
        }

        short[] t = curr_layers;
        curr_layers = next_layers;
        next_layers = t;
        curr_x = next_x;
        curr_y = next_y;
        curr_z = next_z;
        result.add(new Location(next_x, next_y, next_z));
      }

      return result;
    }
  }

  private static Location MoveCheckForAI(int x, int y, int z, int tx, int ty, int geoIndex) {
    int dx = tx - x;
    int dy = ty - y;
    int inc_x = sign(dx);
    int inc_y = sign(dy);
    dx = Math.abs(dx);
    dy = Math.abs(dy);
    if (dx + dy < 2 || dx == 2 && dy == 0 || dx == 0 && dy == 2) {
      return (new Location(x, y, z)).geo2world();
    } else {
      int next_x = x;
      int next_y = y;
      int next_z = z;
      int prev_x;
      int prev_y;
      int prev_z;
      int delta_A;
      int d;
      int delta_B;
      int i;
      if (dx >= dy) {
        delta_A = 2 * dy;
        d = delta_A - dx;
        delta_B = delta_A - 2 * dx;

        for(i = 0; i < dx; ++i) {
          prev_x = x;
          prev_y = y;
          prev_z = z;
          x = next_x;
          y = next_y;
          z = next_z;
          if (d > 0) {
            d += delta_B;
            next_x += inc_x;
            next_y += inc_y;
          } else {
            d += delta_A;
            next_x += inc_x;
          }

          next_z = NcanMoveNextForAI(x, next_y, next_z, next_x, next_y, geoIndex);
          if (next_z == 0) {
            return (new Location(prev_x, prev_y, prev_z)).geo2world();
          }
        }
      } else {
        delta_A = 2 * dx;
        d = delta_A - dy;
        delta_B = delta_A - 2 * dy;

        for(i = 0; i < dy; ++i) {
          prev_x = x;
          prev_y = y;
          prev_z = z;
          x = next_x;
          y = next_y;
          z = next_z;
          if (d > 0) {
            d += delta_B;
            next_x += inc_x;
            next_y += inc_y;
          } else {
            d += delta_A;
            next_y += inc_y;
          }

          next_z = NcanMoveNextForAI(next_x, y, next_z, next_x, next_y, geoIndex);
          if (next_z == 0) {
            return (new Location(prev_x, prev_y, prev_z)).geo2world();
          }
        }
      }

      return (new Location(next_x, next_y, next_z)).geo2world();
    }
  }

  private static boolean NcanMoveNextExCheck(int x, int y, int h, int nextx, int nexty, int hexth, short[] temp_layers, int geoIndex) {
    NGetLayers(x, y, temp_layers, geoIndex);
    if (temp_layers[0] == 0) {
      return true;
    } else {
      int temp_layer;
      if ((temp_layer = FindNearestLowerLayer(temp_layers, h + Config.MIN_LAYER_HEIGHT)) == -2147483648) {
        return false;
      } else {
        short temp_layer_h = (short)((short)(temp_layer & '\ufff0') >> 1);
        return Math.abs(temp_layer_h - hexth) < Config.MAX_Z_DIFF && Math.abs(temp_layer_h - h) < Config.MAX_Z_DIFF ? checkNSWE((byte)(temp_layer & 15), x, y, nextx, nexty) : false;
      }
    }
  }

  public static int NcanMoveNext(int x, int y, int z, short[] layers, int next_x, int next_y, short[] next_layers, short[] temp_layers, boolean withCollision, int geoIndex) {
    if (layers[0] != 0 && next_layers[0] != 0) {
      int layer;
      if ((layer = FindNearestLowerLayer(layers, z + Config.MIN_LAYER_HEIGHT)) == -2147483648) {
        return -2147483648;
      } else {
        byte layer_nswe = (byte)(layer & 15);
        if (!checkNSWE(layer_nswe, x, y, next_x, next_y)) {
          return -2147483648;
        } else {
          short layer_h = (short)((short)(layer & '\ufff0') >> 1);
          int next_layer;
          if ((next_layer = FindNearestLowerLayer(next_layers, layer_h + Config.MIN_LAYER_HEIGHT)) == -2147483648) {
            return -2147483648;
          } else {
            short next_layer_h = (short)((short)(next_layer & '\ufff0') >> 1);
            if (x != next_x && y != next_y) {
              if (!NcanMoveNextExCheck(x, next_y, layer_h, next_x, next_y, next_layer_h, temp_layers, geoIndex)) {
                return -2147483648;
              } else {
                return !NcanMoveNextExCheck(next_x, y, layer_h, next_x, next_y, next_layer_h, temp_layers, geoIndex) ? -2147483648 : next_layer_h;
              }
            } else {
              if (withCollision) {
                if (x == next_x) {
                  NgetHeightAndNSWE(x - 1, y, layer_h, temp_layers, geoIndex);
                  if (Math.abs(temp_layers[0] - layer_h) <= 15 && checkNSWE(layer_nswe, x - 1, y, x, y) && checkNSWE((byte)temp_layers[1], x - 1, y, x - 1, next_y)) {
                    NgetHeightAndNSWE(x + 1, y, layer_h, temp_layers, geoIndex);
                    if (Math.abs(temp_layers[0] - layer_h) <= 15 && checkNSWE(layer_nswe, x + 1, y, x, y) && checkNSWE((byte)temp_layers[1], x + 1, y, x + 1, next_y)) {
                      return next_layer_h;
                    }

                    return -2147483648;
                  }

                  return -2147483648;
                }

                NgetHeightAndNSWE(x, y - 1, layer_h, temp_layers, geoIndex);
                if (Math.abs(temp_layers[0] - layer_h) >= Config.MAX_Z_DIFF || !checkNSWE(layer_nswe, x, y - 1, x, y) || !checkNSWE((byte)temp_layers[1], x, y - 1, next_x, y - 1)) {
                  return -2147483648;
                }

                NgetHeightAndNSWE(x, y + 1, layer_h, temp_layers, geoIndex);
                if (Math.abs(temp_layers[0] - layer_h) >= Config.MAX_Z_DIFF || !checkNSWE(layer_nswe, x, y + 1, x, y) || !checkNSWE((byte)temp_layers[1], x, y + 1, next_x, y + 1)) {
                  return -2147483648;
                }
              }

              return next_layer_h;
            }
          }
        }
      }
    } else {
      return z;
    }
  }

  public static int NcanMoveNextForAI(int x, int y, int z, int next_x, int next_y, int geoIndex) {
    short[] layers1 = new short[MAX_LAYERS + 1];
    short[] layers2 = new short[MAX_LAYERS + 1];
    NGetLayers(x, y, layers1, geoIndex);
    NGetLayers(next_x, next_y, layers2, geoIndex);
    if (layers1[0] != 0 && layers2[0] != 0) {
      short z1 = -32768;
      byte NSWE1 = 15;

      short h;
      for(int i = 1; i <= layers1[0]; ++i) {
        h = (short)((short)(layers1[i] & '\ufff0') >> 1);
        if (Math.abs(z - z1) > Math.abs(z - h)) {
          z1 = h;
          NSWE1 = (byte)(layers1[i] & 15);
        }
      }

      if (z1 == -32768) {
        return 0;
      } else {
        short z2 = -32768;
        byte NSWE2 = 15;

        for(int i = 1; i <= layers2[0]; ++i) {
          h = (short)((short)(layers2[i] & '\ufff0') >> 1);
          if (Math.abs(z - z2) > Math.abs(z - h)) {
            z2 = h;
            NSWE2 = (byte)(layers2[i] & 15);
          }
        }

        if (z2 == -32768) {
          return 0;
        } else if (z1 > z2 && z1 - z2 > Config.MAX_Z_DIFF) {
          return 0;
        } else if (checkNSWE(NSWE1, x, y, next_x, next_y) && checkNSWE(NSWE2, next_x, next_y, x, y)) {
          return z2 == 0 ? 1 : z2;
        } else {
          return 0;
        }
      }
    } else {
      return z == 0 ? 1 : z;
    }
  }

  public static void NGetLayers(int geoX, int geoY, short[] result, int geoIndex) {
    result[0] = 0;
    byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
    if (block != null) {
      int index = 0;
      byte type = block[index];
      int index = index + 1;
      int cellX;
      int cellY;
      short height;
      switch(type) {
        case 0:
          height = makeShort(block[index + 1], block[index]);
          height = (short)(height & '\ufff0');
          ++result[0];
          result[1] = (short)((short)(height << 1) | 15);
          return;
        case 1:
          cellX = getCell(geoX);
          cellY = getCell(geoY);
          index += (cellX << 3) + cellY << 1;
          height = makeShort(block[index + 1], block[index]);
          ++result[0];
          result[1] = height;
          return;
        case 2:
          cellX = getCell(geoX);
          cellY = getCell(geoY);

          byte layer_count;
          for(int offset = (cellX << 3) + cellY; offset > 0; --offset) {
            layer_count = block[index];
            index += (layer_count << 1) + 1;
          }

          layer_count = block[index];
          ++index;
          if (layer_count > 0 && layer_count <= MAX_LAYERS) {
            for(result[0] = (short)layer_count; layer_count > 0; index += 2) {
              result[layer_count] = makeShort(block[index + 1], block[index]);
              --layer_count;
            }

            return;
          } else {
            return;
          }
        default:
          _log.error("GeoEngine: Unknown block type");
      }
    }
  }

  private static short NgetType(int geoX, int geoY, int geoIndex) {
    byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
    return block == null ? 0 : (short)block[0];
  }

  public static int NgetHeight(int geoX, int geoY, int z, int geoIndex) {
    byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
    if (block == null) {
      return z;
    } else {
      int index = 0;
      byte type = block[index];
      int index = index + 1;
      int cellX;
      int cellY;
      short height;
      switch(type) {
        case 0:
          height = makeShort(block[index + 1], block[index]);
          return (short)(height & '\ufff0');
        case 1:
          cellX = getCell(geoX);
          cellY = getCell(geoY);
          index += (cellX << 3) + cellY << 1;
          height = makeShort(block[index + 1], block[index]);
          return (short)((short)(height & '\ufff0') >> 1);
        case 2:
          cellX = getCell(geoX);
          cellY = getCell(geoY);

          byte layers;
          for(int offset = (cellX << 3) + cellY; offset > 0; --offset) {
            layers = block[index];
            index += (layers << 1) + 1;
          }

          layers = block[index];
          ++index;
          if (layers > 0 && layers <= MAX_LAYERS) {
            int z_nearest_lower_limit = z + Config.MIN_LAYER_HEIGHT;
            int z_nearest_lower = -2147483648;

            int z_nearest;
            for(z_nearest = -2147483648; layers > 0; index += 2) {
              height = (short)((short)(makeShort(block[index + 1], block[index]) & '\ufff0') >> 1);
              if (height < z_nearest_lower_limit) {
                z_nearest_lower = Math.max(z_nearest_lower, height);
              } else if (Math.abs(z - height) < Math.abs(z - z_nearest)) {
                z_nearest = height;
              }

              --layers;
            }

            return z_nearest_lower != -2147483648 ? z_nearest_lower : z_nearest;
          } else {
            return (short)z;
          }
        default:
          _log.error("GeoEngine: Unknown blockType");
          return z;
      }
    }
  }

  public static byte NgetNSWE(int geoX, int geoY, int z, int geoIndex) {
    byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
    if (block == null) {
      return 15;
    } else {
      int index = 0;
      byte type = block[index];
      int index = index + 1;
      int cellX;
      int cellY;
      short height;
      switch(type) {
        case 0:
          return 15;
        case 1:
          cellX = getCell(geoX);
          cellY = getCell(geoY);
          index += (cellX << 3) + cellY << 1;
          height = makeShort(block[index + 1], block[index]);
          return (byte)(height & 15);
        case 2:
          cellX = getCell(geoX);
          cellY = getCell(geoY);

          byte layers;
          for(int offset = (cellX << 3) + cellY; offset > 0; --offset) {
            layers = block[index];
            index += (layers << 1) + 1;
          }

          layers = block[index];
          ++index;
          if (layers > 0 && layers <= MAX_LAYERS) {
            short tempz1 = -32768;
            short tempz2 = -32768;
            int index_nswe1 = 0;
            int index_nswe2 = 0;

            for(int z_nearest_lower_limit = z + Config.MIN_LAYER_HEIGHT; layers > 0; index += 2) {
              height = (short)((short)(makeShort(block[index + 1], block[index]) & '\ufff0') >> 1);
              if (height < z_nearest_lower_limit) {
                if (height > tempz1) {
                  tempz1 = height;
                  index_nswe1 = index;
                }
              } else if (Math.abs(z - height) < Math.abs(z - tempz2)) {
                tempz2 = height;
                index_nswe2 = index;
              }

              --layers;
            }

            if (index_nswe1 > 0) {
              return (byte)(makeShort(block[index_nswe1 + 1], block[index_nswe1]) & 15);
            } else if (index_nswe2 > 0) {
              return (byte)(makeShort(block[index_nswe2 + 1], block[index_nswe2]) & 15);
            } else {
              return 15;
            }
          } else {
            return 15;
          }
        default:
          _log.error("GeoEngine: Unknown block type.");
          return 15;
      }
    }
  }

  public static void NgetHeightAndNSWE(int geoX, int geoY, short z, short[] result, int geoIndex) {
    byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
    if (block == null) {
      result[0] = z;
      result[1] = 15;
    } else {
      int index = 0;
      short NSWE = 15;
      byte type = block[index];
      int index = index + 1;
      int cellX;
      int cellY;
      short height;
      switch(type) {
        case 0:
          height = makeShort(block[index + 1], block[index]);
          result[0] = (short)(height & '\ufff0');
          result[1] = 15;
          return;
        case 1:
          cellX = getCell(geoX);
          cellY = getCell(geoY);
          index += (cellX << 3) + cellY << 1;
          height = makeShort(block[index + 1], block[index]);
          result[0] = (short)((short)(height & '\ufff0') >> 1);
          result[1] = (short)(height & 15);
          return;
        case 2:
          cellX = getCell(geoX);
          cellY = getCell(geoY);

          byte layers;
          for(int offset = (cellX << 3) + cellY; offset > 0; --offset) {
            layers = block[index];
            index += (layers << 1) + 1;
          }

          layers = block[index];
          ++index;
          if (layers > 0 && layers <= MAX_LAYERS) {
            short tempz1 = -32768;
            short tempz2 = -32768;
            int index_nswe1 = 0;
            int index_nswe2 = 0;

            for(int z_nearest_lower_limit = z + Config.MIN_LAYER_HEIGHT; layers > 0; index += 2) {
              height = (short)((short)(makeShort(block[index + 1], block[index]) & '\ufff0') >> 1);
              if (height < z_nearest_lower_limit) {
                if (height > tempz1) {
                  tempz1 = height;
                  index_nswe1 = index;
                }
              } else if (Math.abs(z - height) < Math.abs(z - tempz2)) {
                tempz2 = height;
                index_nswe2 = index;
              }

              --layers;
            }

            if (index_nswe1 > 0) {
              NSWE = makeShort(block[index_nswe1 + 1], block[index_nswe1]);
              NSWE = (short)(NSWE & 15);
            } else if (index_nswe2 > 0) {
              NSWE = makeShort(block[index_nswe2 + 1], block[index_nswe2]);
              NSWE = (short)(NSWE & 15);
            }

            result[0] = tempz1 > -32768 ? tempz1 : tempz2;
            result[1] = NSWE;
            return;
          } else {
            result[0] = z;
            result[1] = 15;
            return;
          }
        default:
          _log.error("GeoEngine: Unknown block type.");
          result[0] = z;
          result[1] = 15;
      }
    }
  }

  protected static short makeShort(byte b1, byte b0) {
    return (short)(b1 << 8 | b0 & 255);
  }

  protected static int getBlock(int geoPos) {
    return (geoPos >> 3) % 256;
  }

  protected static int getCell(int geoPos) {
    return geoPos % 8;
  }

  protected static int getBlockIndex(int blockX, int blockY) {
    return (blockX << 8) + blockY;
  }

  private static byte sign(int x) {
    return (byte)(x >= 0 ? 1 : -1);
  }

  private static byte[] getGeoBlockFromGeoCoords(int geoX, int geoY, int geoIndex) {
    if (!Config.ALLOW_GEODATA) {
      return null;
    } else {
      int ix = geoX >> 11;
      int iy = geoY >> 11;
      if (ix >= 0 && ix < World.WORLD_SIZE_X && iy >= 0 && iy < World.WORLD_SIZE_Y) {
        byte[][][] region = geodata[ix][iy];
        int blockX = getBlock(geoX);
        int blockY = getBlock(geoY);
        int regIndex = 0;
        if ((geoIndex & 251658240) == 251658240) {
          int x = (geoIndex & 16711680) >> 16;
          int y = (geoIndex & '\uff00') >> 8;
          if (ix == x && iy == y) {
            regIndex = geoIndex & 255;
          }
        }

        return region[regIndex][getBlockIndex(blockX, blockY)];
      } else {
        return null;
      }
    }
  }

  public static void load() {
    if (Config.ALLOW_GEODATA) {
      _log.info("GeoEngine: Loading Geodata...");
      File f = new File(Config.DATAPACK_ROOT, "geodata");
      if (f.exists() && f.isDirectory()) {
        int counter = 0;
        Pattern p = Pattern.compile(Config.GEOFILES_PATTERN);
        List<File> geoFiles = new ArrayList();
        File[] var4 = f.listFiles();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
          File q = var4[var6];
          if (!q.isDirectory()) {
            String fn = q.getName();
            Matcher m = p.matcher(fn);
            if (m.matches()) {
              geoFiles.add(q);
            }
          }
        }

        Collections.sort(geoFiles, NaturalOrderComparator.FILE_NAME_COMPARATOR);

        for(Iterator var10 = geoFiles.iterator(); var10.hasNext(); ++counter) {
          File q = (File)var10.next();
          String fn = q.getName();
          fn = fn.substring(0, 5);
          String[] xy = fn.split("_");
          byte rx = Byte.parseByte(xy[0]);
          byte ry = Byte.parseByte(xy[1]);
          LoadGeodataFile(q, rx, ry);
          LoadGeodata(rx, ry, 0);
        }

        _log.info("GeoEngine: Loaded " + counter + " map(s), max layers: " + MAX_LAYERS);
        if (Config.COMPACT_GEO) {
          compact();
        }

      } else {
        _log.info("GeoEngine: Files missing, loading aborted.");
      }
    }
  }

  public static boolean LoadGeodataFile(File geoFile, byte rx, byte ry) {
    int ix = rx - Config.GEO_X_FIRST;
    int iy = ry - Config.GEO_Y_FIRST;
    String geoName = geoFile.getName();
    if (ix >= 0 && iy >= 0 && ix <= (World.MAP_MAX_X >> 15) + Math.abs(World.MAP_MIN_X >> 15) && iy <= (World.MAP_MAX_Y >> 15) + Math.abs(World.MAP_MIN_Y >> 15)) {
      _log.info("GeoEngine: Loading: " + geoName);

      try {
        FileChannel inChannel = (new RandomAccessFile(geoFile, "r")).getChannel();
        ByteBuffer buf = null;
        int size = 0;
        int checkSum = 0;
        if (geoName.endsWith(".l2j")) {
          size = (int)inChannel.size();
          buf = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
          inChannel.read(buf);
        } else if (geoName.endsWith(".l2g")) {
          size = Math.max(4, (int)inChannel.size() - 4);
          buf = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
          buf.limit(4);
          inChannel.read(buf);
          buf.flip();
          checkSum = -2126429781 ^ buf.getInt();
          buf.clear();
          inChannel.read(buf);
          buf.rewind();

          for(byte key = (byte)(checkSum >> 24 & 255 ^ checkSum >> 16 & 255 ^ checkSum >> 8 & 255 ^ checkSum >> 0 & 255); buf.hasRemaining(); checkSum -= key = buf.get(buf.position() - 1)) {
            buf.put(buf.position(), (byte)(buf.get() ^ key));
          }

          buf.rewind();
          if (checkSum != 0) {
            throw new RuntimeException("Test failed!");
          }
        }

        rawgeo[ix][iy] = buf;
        if (size >= 196608 && checkSum == 0) {
          return true;
        } else {
          throw new RuntimeException("Invalid geodata : " + geoName + "!");
        }
      } catch (IOException var11) {
        _log.error("", var11);
        return false;
      }
    } else {
      _log.info("GeoEngine: File " + geoName + " was not loaded!!! ");
      return false;
    }
  }

  public static void LoadGeodata(int rx, int ry, int regIndex) {
    int ix = rx - Config.GEO_X_FIRST;
    int iy = ry - Config.GEO_Y_FIRST;
    ByteBuffer geo = rawgeo[ix][iy];
    int index = 0;
    int block = false;
    int floor = 0;
    byte[][] blocks;
    synchronized(geodata) {
      if ((blocks = geodata[ix][iy][regIndex]) == null) {
        geodata[ix][iy][regIndex] = blocks = new byte[65536][];
      }
    }

    for(int block = 0; block < 65536; ++block) {
      byte type = geo.get(index);
      ++index;
      byte[] geoBlock;
      switch(type) {
        case 0:
          geoBlock = new byte[]{type, geo.get(index), geo.get(index + 1)};
          index += 2;
          blocks[block] = geoBlock;
          break;
        case 1:
          geoBlock = new byte[129];
          geoBlock[0] = type;
          geo.position(index);
          geo.get(geoBlock, 1, 128);
          index += 128;
          blocks[block] = geoBlock;
          break;
        case 2:
          int orgIndex = index;

          int diff;
          for(diff = 0; diff < 64; ++diff) {
            byte layers = geo.get(index);
            MAX_LAYERS = Math.max(MAX_LAYERS, layers);
            index += (layers << 1) + 1;
            if (layers > floor) {
              floor = layers;
            }
          }

          diff = index - orgIndex;
          geoBlock = new byte[diff + 1];
          geoBlock[0] = type;
          geo.position(orgIndex);
          geo.get(geoBlock, 1, diff);
          blocks[block] = geoBlock;
          break;
        default:
          throw new RuntimeException("Invalid geodata: " + rx + "_" + ry + "!");
      }
    }

  }

  public static int NextGeoIndex(int rx, int ry, int refId) {
    if (!Config.ALLOW_GEODATA) {
      return 0;
    } else {
      int ix = rx - Config.GEO_X_FIRST;
      int iy = ry - Config.GEO_Y_FIRST;
      int regIndex = -1;
      synchronized(geodata) {
        byte[][][] region = geodata[ix][iy];

        for(int i = 0; i < region.length; ++i) {
          if (region[i] == null) {
            regIndex = i;
            break;
          }
        }

        if (regIndex == -1) {
          byte[][][] resizedRegion = new byte[(regIndex = region.length) + 1][][];

          for(int i = 0; i < region.length; ++i) {
            resizedRegion[i] = region[i];
          }

          geodata[ix][iy] = resizedRegion;
        }

        LoadGeodata(rx, ry, regIndex);
      }

      return 251658240 | ix << 16 | iy << 8 | regIndex;
    }
  }

  public static void FreeGeoIndex(int geoIndex) {
    if (Config.ALLOW_GEODATA) {
      if ((geoIndex & 251658240) == 251658240) {
        int ix = (geoIndex & 16711680) >> 16;
        int iy = (geoIndex & '\uff00') >> 8;
        int regIndex = geoIndex & 255;
        synchronized(geodata) {
          geodata[ix][iy][regIndex] = (byte[][])null;
        }
      }
    }
  }

  public static void removeGeoCollision(GeoCollision collision, int geoIndex) {
    Shape shape = collision.getShape();
    byte[][] around = collision.getGeoAround();
    if (around == null) {
      throw new RuntimeException("Attempt to remove unitialized collision: " + collision);
    } else {
      int minX = shape.getXmin() - World.MAP_MIN_X - 16 >> 4;
      int minY = shape.getYmin() - World.MAP_MIN_Y - 16 >> 4;
      int minZ = shape.getZmin();
      int maxZ = shape.getZmax();

      for(int gX = 0; gX < around.length; ++gX) {
        for(int gY = 0; gY < around[gX].length; ++gY) {
          int geoX = minX + gX;
          int geoY = minY + gY;
          byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
          if (block != null) {
            int cellX = getCell(geoX);
            int cellY = getCell(geoY);
            int index = 0;
            byte blockType = block[index];
            int index = index + 1;
            short height;
            byte old_nswe;
            switch(blockType) {
              case 1:
                index += (cellX << 3) + cellY << 1;
                height = makeShort(block[index + 1], block[index]);
                old_nswe = (byte)(height & 15);
                height = (short)(height & '\ufff0');
                height = (short)(height >> 1);
                if (height >= minZ && height <= maxZ) {
                  height = (short)(height << 1);
                  height = (short)(height & '\ufff0');
                  height = (short)(height | old_nswe);
                  if (collision.isConcrete()) {
                    height = (short)(height | around[gX][gY]);
                  } else {
                    height = (short)(height & ~around[gX][gY]);
                  }

                  block[index + 1] = (byte)(height >> 8);
                  block[index] = (byte)(height & 255);
                }
                break;
              case 2:
                int neededIndex = -1;

                byte layers;
                for(int offset = (cellX << 3) + cellY; offset > 0; --offset) {
                  layers = block[index];
                  index += (layers << 1) + 1;
                }

                layers = block[index];
                ++index;
                if (layers > 0 && layers <= MAX_LAYERS) {
                  short temph = -32768;

                  for(old_nswe = 15; layers > 0; index += 2) {
                    height = makeShort(block[index + 1], block[index]);
                    byte tmp_nswe = (byte)(height & 15);
                    height = (short)(height & '\ufff0');
                    height = (short)(height >> 1);
                    int z_diff_last = Math.abs(minZ - temph);
                    int z_diff_curr = Math.abs(maxZ - height);
                    if (z_diff_last > z_diff_curr) {
                      old_nswe = tmp_nswe;
                      temph = height;
                      neededIndex = index;
                    }

                    --layers;
                  }

                  if (temph != -32768 && temph >= minZ && temph <= maxZ) {
                    temph = (short)(temph << 1);
                    temph = (short)(temph & '\ufff0');
                    temph = (short)(temph | old_nswe);
                    if (collision.isConcrete()) {
                      temph = (short)(temph | around[gX][gY]);
                    } else {
                      temph = (short)(temph & ~around[gX][gY]);
                    }

                    block[neededIndex + 1] = (byte)(temph >> 8);
                    block[neededIndex] = (byte)(temph & 255);
                  }
                }
            }
          }
        }
      }

    }
  }

  public static void applyGeoCollision(GeoCollision collision, int geoIndex) {
    Shape shape = collision.getShape();
    if (shape.getXmax() == shape.getYmax() && shape.getXmax() == 0) {
      throw new RuntimeException("Attempt to add incorrect collision: " + collision);
    } else {
      boolean isFirstTime = false;
      int minX = shape.getXmin() - World.MAP_MIN_X - 16 >> 4;
      int maxX = shape.getXmax() - World.MAP_MIN_X + 16 >> 4;
      int minY = shape.getYmin() - World.MAP_MIN_Y - 16 >> 4;
      int maxY = shape.getYmax() - World.MAP_MIN_Y + 16 >> 4;
      int minZ = shape.getZmin();
      int maxZ = shape.getZmax();
      byte[][] around = collision.getGeoAround();
      int gX;
      int gY;
      int ax;
      int ay;
      if (around == null) {
        isFirstTime = true;
        byte[][] cells = new byte[maxX - minX + 1][maxY - minY + 1];

        int gX;
        int gY;
        for(gX = minX; gX <= maxX; ++gX) {
          label187:
          for(gY = minY; gY <= maxY; ++gY) {
            gX = (gX << 4) + World.MAP_MIN_X;
            gY = (gY << 4) + World.MAP_MIN_Y;

            for(ax = gX; ax < gX + 16; ++ax) {
              for(ay = gY; ay < gY + 16; ++ay) {
                if (shape.isInside(ax, ay)) {
                  cells[gX - minX][gY - minY] = 1;
                  continue label187;
                }
              }
            }
          }
        }

        around = new byte[maxX - minX + 1][maxY - minY + 1];

        for(gX = 0; gX < cells.length; ++gX) {
          for(gY = 0; gY < cells[gX].length; ++gY) {
            if (cells[gX][gY] == 1) {
              around[gX][gY] = 15;
              byte _nswe;
              if (gY > 0 && cells[gX][gY - 1] == 0) {
                _nswe = around[gX][gY - 1];
                _nswe = (byte)(_nswe | 4);
                around[gX][gY - 1] = _nswe;
              }

              if (gY + 1 < cells[gX].length && cells[gX][gY + 1] == 0) {
                _nswe = around[gX][gY + 1];
                _nswe = (byte)(_nswe | 8);
                around[gX][gY + 1] = _nswe;
              }

              if (gX > 0 && cells[gX - 1][gY] == 0) {
                _nswe = around[gX - 1][gY];
                _nswe = (byte)(_nswe | 1);
                around[gX - 1][gY] = _nswe;
              }

              if (gX + 1 < cells.length && cells[gX + 1][gY] == 0) {
                _nswe = around[gX + 1][gY];
                _nswe = (byte)(_nswe | 2);
                around[gX + 1][gY] = _nswe;
              }
            }
          }
        }

        collision.setGeoAround(around);
      }

      for(gX = 0; gX < around.length; ++gX) {
        for(gY = 0; gY < around[gX].length; ++gY) {
          ax = minX + gX;
          ay = minY + gY;
          byte[] block = getGeoBlockFromGeoCoords(ax, ay, geoIndex);
          if (block != null) {
            int cellX = getCell(ax);
            int cellY = getCell(ay);
            int index = 0;
            byte blockType = block[index];
            int index = index + 1;
            short height;
            byte old_nswe;
            byte close_nswe;
            switch(blockType) {
              case 1:
                index += (cellX << 3) + cellY << 1;
                height = makeShort(block[index + 1], block[index]);
                old_nswe = (byte)(height & 15);
                height = (short)(height & '\ufff0');
                height = (short)(height >> 1);
                if (height >= minZ && height <= maxZ) {
                  close_nswe = around[gX][gY];
                  if (isFirstTime) {
                    if (collision.isConcrete()) {
                      close_nswe &= old_nswe;
                    } else {
                      close_nswe = (byte)(close_nswe & ~old_nswe);
                    }

                    around[gX][gY] = close_nswe;
                  }

                  height = (short)(height << 1);
                  height = (short)(height & '\ufff0');
                  height = (short)(height | old_nswe);
                  if (collision.isConcrete()) {
                    height = (short)(height & ~close_nswe);
                  } else {
                    height = (short)(height | close_nswe);
                  }

                  block[index + 1] = (byte)(height >> 8);
                  block[index] = (byte)(height & 255);
                }
                break;
              case 2:
                int neededIndex = -1;

                byte layers;
                for(int offset = (cellX << 3) + cellY; offset > 0; --offset) {
                  layers = block[index];
                  index += (layers << 1) + 1;
                }

                layers = block[index];
                ++index;
                if (layers > 0 && layers <= MAX_LAYERS) {
                  short temph = -32768;

                  for(old_nswe = 15; layers > 0; index += 2) {
                    height = makeShort(block[index + 1], block[index]);
                    byte tmp_nswe = (byte)(height & 15);
                    height = (short)(height & '\ufff0');
                    height = (short)(height >> 1);
                    int z_diff_last = Math.abs(minZ - temph);
                    int z_diff_curr = Math.abs(maxZ - height);
                    if (z_diff_last > z_diff_curr) {
                      old_nswe = tmp_nswe;
                      temph = height;
                      neededIndex = index;
                    }

                    --layers;
                  }

                  if (temph != -32768 && temph >= minZ && temph <= maxZ) {
                    close_nswe = around[gX][gY];
                    if (isFirstTime) {
                      if (collision.isConcrete()) {
                        close_nswe &= old_nswe;
                      } else {
                        close_nswe = (byte)(close_nswe & ~old_nswe);
                      }

                      around[gX][gY] = close_nswe;
                    }

                    temph = (short)(temph << 1);
                    temph = (short)(temph & '\ufff0');
                    temph = (short)(temph | old_nswe);
                    if (collision.isConcrete()) {
                      temph = (short)(temph & ~close_nswe);
                    } else {
                      temph = (short)(temph | close_nswe);
                    }

                    block[neededIndex + 1] = (byte)(temph >> 8);
                    block[neededIndex] = (byte)(temph & 255);
                  }
                }
            }
          }
        }
      }

    }
  }

  public static void compact() {
    long total = 0L;
    long optimized = 0L;

    for(int mapX = 0; mapX < World.WORLD_SIZE_X; ++mapX) {
      for(int mapY = 0; mapY < World.WORLD_SIZE_Y; ++mapY) {
        if (geodata[mapX][mapY] != null) {
          total += 65536L;
          BlockLink[] links = GeoOptimizer.loadBlockMatches("geodata/matches/" + (mapX + Config.GEO_X_FIRST) + "_" + (mapY + Config.GEO_Y_FIRST) + ".matches");
          if (links != null) {
            for(int i = 0; i < links.length; ++i) {
              byte[][][] link_region = geodata[links[i].linkMapX][links[i].linkMapY];
              if (link_region != null) {
                link_region[links[i].linkBlockIndex][0] = geodata[mapX][mapY][links[i].blockIndex][0];
                ++optimized;
              }
            }
          }
        }
      }
    }

    _log.info(String.format("GeoEngine: - Compacted %d of %d blocks...", optimized, total));
  }

  public static boolean equalsData(byte[] a1, byte[] a2) {
    if (a1.length != a2.length) {
      return false;
    } else {
      for(int i = 0; i < a1.length; ++i) {
        if (a1[i] != a2[i]) {
          return false;
        }
      }

      return true;
    }
  }

  public static boolean compareGeoBlocks(int mapX1, int mapY1, int blockIndex1, int mapX2, int mapY2, int blockIndex2) {
    return equalsData(geodata[mapX1][mapY1][blockIndex1][0], geodata[mapX2][mapY2][blockIndex2][0]);
  }

  private static void initChecksums() {
    _log.info("GeoEngine: - Generating Checksums...");
    (new File(Config.DATAPACK_ROOT, "geodata/checksum")).mkdirs();
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    GeoOptimizer.checkSums = new int[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][];

    for(int mapX = 0; mapX < World.WORLD_SIZE_X; ++mapX) {
      for(int mapY = 0; mapY < World.WORLD_SIZE_Y; ++mapY) {
        if (geodata[mapX][mapY] != null) {
          executor.execute(new CheckSumLoader(mapX, mapY, geodata[mapX][mapY]));
        }
      }
    }

    try {
      executor.awaitTermination(9223372036854775807L, TimeUnit.SECONDS);
    } catch (InterruptedException var3) {
      _log.error("", var3);
    }

  }

  private static void initBlockMatches(int maxScanRegions) {
    _log.info("GeoEngine: Generating Block Matches...");
    (new File(Config.DATAPACK_ROOT, "geodata/matches")).mkdirs();
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    for(int mapX = 0; mapX < World.WORLD_SIZE_X; ++mapX) {
      for(int mapY = 0; mapY < World.WORLD_SIZE_Y; ++mapY) {
        if (geodata[mapX][mapY] != null && GeoOptimizer.checkSums != null && GeoOptimizer.checkSums[mapX][mapY] != null) {
          executor.execute(new GeoBlocksMatchFinder(mapX, mapY, maxScanRegions));
        }
      }
    }

    try {
      executor.awaitTermination(9223372036854775807L, TimeUnit.SECONDS);
    } catch (InterruptedException var4) {
      _log.error("", var4);
    }

  }

  public static void deleteChecksumFiles() {
    for(int mapX = 0; mapX < World.WORLD_SIZE_X; ++mapX) {
      for(int mapY = 0; mapY < World.WORLD_SIZE_Y; ++mapY) {
        if (geodata[mapX][mapY] != null) {
          (new File(Config.DATAPACK_ROOT, "geodata/checksum/" + (mapX + Config.GEO_X_FIRST) + "_" + (mapY + Config.GEO_Y_FIRST) + ".crc")).delete();
        }
      }
    }

  }

  public static void genBlockMatches(int maxScanRegions) {
    initChecksums();
    initBlockMatches(maxScanRegions);
  }

  public static void unload() {
    for(int mapX = 0; mapX < World.WORLD_SIZE_X; ++mapX) {
      for(int mapY = 0; mapY < World.WORLD_SIZE_Y; ++mapY) {
        geodata[mapX][mapY] = (byte[][][])null;
      }
    }

  }

  static {
    rawgeo = new ByteBuffer[World.WORLD_SIZE_X][World.WORLD_SIZE_Y];
    geodata = new byte[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][1][][];
  }
}
