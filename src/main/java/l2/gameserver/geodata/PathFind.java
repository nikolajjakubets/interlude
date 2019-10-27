//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.geodata;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.geodata.PathFindBuffers.GeoNode;
import l2.gameserver.geodata.PathFindBuffers.PathFindBuffer;
import l2.gameserver.utils.Location;

public class PathFind {
  private int geoIndex = 0;
  private PathFindBuffer buff;
  private List<Location> path;
  private final short[] hNSWE = new short[2];
  private final Location startPoint;
  private final Location endPoint;
  private GeoNode startNode;
  private GeoNode endNode;
  private GeoNode currentNode;

  public PathFind(int x, int y, int z, int destX, int destY, int destZ, boolean isPlayable, int geoIndex) {
    this.geoIndex = geoIndex;
    this.startPoint = Config.PATHFIND_BOOST == 0 ? new Location(x, y, z) : GeoEngine.moveCheckWithCollision(x, y, z, destX, destY, true, geoIndex);
    this.endPoint = Config.PATHFIND_BOOST == 2 && Math.abs(destZ - z) <= 200 ? GeoEngine.moveCheckBackwardWithCollision(destX, destY, destZ, this.startPoint.x, this.startPoint.y, true, geoIndex) : new Location(destX, destY, destZ);
    this.startPoint.world2geo();
    this.endPoint.world2geo();
    this.startPoint.z = GeoEngine.NgetHeight(this.startPoint.x, this.startPoint.y, this.startPoint.z, geoIndex);
    this.endPoint.z = GeoEngine.NgetHeight(this.endPoint.x, this.endPoint.y, this.endPoint.z, geoIndex);
    int xdiff = Math.abs(this.endPoint.x - this.startPoint.x);
    int ydiff = Math.abs(this.endPoint.y - this.startPoint.y);
    if (xdiff == 0 && ydiff == 0) {
      if (Math.abs(this.endPoint.z - this.startPoint.z) < 32) {
        this.path = new ArrayList<>();
        this.path.add(0, this.startPoint);
      }

    } else {
      int mapSize = 2 * Math.max(xdiff, ydiff);
      if ((this.buff = PathFindBuffers.alloc(mapSize)) != null) {
        this.buff.offsetX = this.startPoint.x - this.buff.mapSize / 2;
        this.buff.offsetY = this.startPoint.y - this.buff.mapSize / 2;
        ++this.buff.totalUses;
        if (isPlayable) {
          ++this.buff.playableUses;
        }

        this.findPath();
        this.buff.free();
        PathFindBuffers.recycle(this.buff);
      }

    }
  }

  private List<Location> findPath() {
    this.startNode = this.buff.nodes[this.startPoint.x - this.buff.offsetX][this.startPoint.y - this.buff.offsetY].set(this.startPoint.x, this.startPoint.y, (short)this.startPoint.z);
    GeoEngine.NgetHeightAndNSWE(this.startPoint.x, this.startPoint.y, (short)this.startPoint.z, this.hNSWE, this.geoIndex);
    this.startNode.z = this.hNSWE[0];
    this.startNode.nswe = this.hNSWE[1];
    this.startNode.costFromStart = 0.0F;
    this.startNode.state = 1;
    this.startNode.parent = null;
    this.endNode = this.buff.nodes[this.endPoint.x - this.buff.offsetX][this.endPoint.y - this.buff.offsetY].set(this.endPoint.x, this.endPoint.y, (short)this.endPoint.z);
    this.startNode.costToEnd = this.pathCostEstimate(this.startNode);
    this.startNode.totalCost = this.startNode.costFromStart + this.startNode.costToEnd;
    this.buff.open.add(this.startNode);
    long nanos = System.nanoTime();
    long searhTime = 0L;

    int itr;
    for(itr = 0; (searhTime = System.nanoTime() - nanos) < Config.PATHFIND_MAX_TIME && (this.currentNode = (GeoNode)this.buff.open.poll()) != null; this.currentNode.state = -1) {
      ++itr;
      if (this.currentNode.x == this.endPoint.x && this.currentNode.y == this.endPoint.y && Math.abs(this.currentNode.z - this.endPoint.z) < 64) {
        this.path = this.tracePath(this.currentNode);
        break;
      }

      this.handleNode(this.currentNode);
    }

    PathFindBuffer var10000 = this.buff;
    var10000.totalTime += searhTime;
    var10000 = this.buff;
    var10000.totalItr += (long)itr;
    if (this.path != null) {
      ++this.buff.successUses;
    } else if (searhTime > Config.PATHFIND_MAX_TIME) {
      ++this.buff.overtimeUses;
    }

    return this.path;
  }

  private List<Location> tracePath(GeoNode f) {
    ArrayList locations = new ArrayList<>();

    do {
      locations.add(0, f.getLoc());
      f = f.parent;
    } while(f.parent != null);

    return locations;
  }

  private void handleNode(GeoNode node) {
    int clX = node.x;
    int clY = node.y;
    short clZ = node.z;
    this.getHeightAndNSWE(clX, clY, clZ);
    short NSWE = this.hNSWE[1];
    if (Config.PATHFIND_DIAGONAL) {
      if ((NSWE & 4) == 4 && (NSWE & 1) == 1) {
        this.getHeightAndNSWE(clX + 1, clY, clZ);
        if ((this.hNSWE[1] & 4) == 4) {
          this.getHeightAndNSWE(clX, clY + 1, clZ);
          if ((this.hNSWE[1] & 1) == 1) {
            this.handleNeighbour(clX + 1, clY + 1, node, true);
          }
        }
      }

      if ((NSWE & 4) == 4 && (NSWE & 2) == 2) {
        this.getHeightAndNSWE(clX - 1, clY, clZ);
        if ((this.hNSWE[1] & 4) == 4) {
          this.getHeightAndNSWE(clX, clY + 1, clZ);
          if ((this.hNSWE[1] & 2) == 2) {
            this.handleNeighbour(clX - 1, clY + 1, node, true);
          }
        }
      }

      if ((NSWE & 8) == 8 && (NSWE & 1) == 1) {
        this.getHeightAndNSWE(clX + 1, clY, clZ);
        if ((this.hNSWE[1] & 8) == 8) {
          this.getHeightAndNSWE(clX, clY - 1, clZ);
          if ((this.hNSWE[1] & 1) == 1) {
            this.handleNeighbour(clX + 1, clY - 1, node, true);
          }
        }
      }

      if ((NSWE & 8) == 8 && (NSWE & 2) == 2) {
        this.getHeightAndNSWE(clX - 1, clY, clZ);
        if ((this.hNSWE[1] & 8) == 8) {
          this.getHeightAndNSWE(clX, clY - 1, clZ);
          if ((this.hNSWE[1] & 2) == 2) {
            this.handleNeighbour(clX - 1, clY - 1, node, true);
          }
        }
      }
    }

    if ((NSWE & 1) == 1) {
      this.handleNeighbour(clX + 1, clY, node, false);
    }

    if ((NSWE & 2) == 2) {
      this.handleNeighbour(clX - 1, clY, node, false);
    }

    if ((NSWE & 4) == 4) {
      this.handleNeighbour(clX, clY + 1, node, false);
    }

    if ((NSWE & 8) == 8) {
      this.handleNeighbour(clX, clY - 1, node, false);
    }

  }

  private float pathCostEstimate(GeoNode n) {
    int diffx = this.endNode.x - n.x;
    int diffy = this.endNode.y - n.y;
    int diffz = this.endNode.z - n.z;
    return (float)Math.sqrt((double)(diffx * diffx + diffy * diffy + diffz * diffz / 256));
  }

  private float traverseCost(GeoNode from, GeoNode n, boolean d) {
    if (n.nswe == 15 && Math.abs(n.z - from.z) <= 16) {
      this.getHeightAndNSWE(n.x + 1, n.y, n.z);
      if (this.hNSWE[1] == 15 && Math.abs(n.z - this.hNSWE[0]) <= 16) {
        this.getHeightAndNSWE(n.x - 1, n.y, n.z);
        if (this.hNSWE[1] == 15 && Math.abs(n.z - this.hNSWE[0]) <= 16) {
          this.getHeightAndNSWE(n.x, n.y + 1, n.z);
          if (this.hNSWE[1] == 15 && Math.abs(n.z - this.hNSWE[0]) <= 16) {
            this.getHeightAndNSWE(n.x, n.y - 1, n.z);
            if (this.hNSWE[1] == 15 && Math.abs(n.z - this.hNSWE[0]) <= 16) {
              return d ? 1.414F : 1.0F;
            } else {
              return 2.0F;
            }
          } else {
            return 2.0F;
          }
        } else {
          return 2.0F;
        }
      } else {
        return 2.0F;
      }
    } else {
      return 3.0F;
    }
  }

  private void handleNeighbour(int x, int y, GeoNode from, boolean d) {
    int nX = x - this.buff.offsetX;
    int nY = y - this.buff.offsetY;
    if (nX < this.buff.mapSize && nX >= 0 && nY < this.buff.mapSize && nY >= 0) {
      GeoNode n = this.buff.nodes[nX][nY];
      if (!n.isSet()) {
        n = n.set(x, y, from.z);
        GeoEngine.NgetHeightAndNSWE(x, y, from.z, this.hNSWE, this.geoIndex);
        n.z = this.hNSWE[0];
        n.nswe = this.hNSWE[1];
      }

      int height = Math.abs(n.z - from.z);
      if (height <= Config.PATHFIND_MAX_Z_DIFF && n.nswe != 0) {
        float newCost = from.costFromStart + this.traverseCost(from, n, d);
        if (n.state != 1 && n.state != -1 || n.costFromStart > newCost) {
          if (n.state == 0) {
            n.costToEnd = this.pathCostEstimate(n);
          }

          n.parent = from;
          n.costFromStart = newCost;
          n.totalCost = n.costFromStart + n.costToEnd;
          if (n.state != 1) {
            n.state = 1;
            this.buff.open.add(n);
          }
        }
      }
    }
  }

  private void getHeightAndNSWE(int x, int y, short z) {
    int nX = x - this.buff.offsetX;
    int nY = y - this.buff.offsetY;
    if (nX < this.buff.mapSize && nX >= 0 && nY < this.buff.mapSize && nY >= 0) {
      GeoNode n = this.buff.nodes[nX][nY];
      if (!n.isSet()) {
        n = n.set(x, y, z);
        GeoEngine.NgetHeightAndNSWE(x, y, z, this.hNSWE, this.geoIndex);
        n.z = this.hNSWE[0];
        n.nswe = this.hNSWE[1];
      } else {
        this.hNSWE[0] = n.z;
        this.hNSWE[1] = n.nswe;
      }

    } else {
      this.hNSWE[1] = 0;
    }
  }

  public List<Location> getPath() {
    return this.path;
  }
}
