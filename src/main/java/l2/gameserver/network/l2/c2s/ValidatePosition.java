//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.BoatHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExServerPrimitive;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ValidatePosition extends L2GameClientPacket {
  private final Location _loc = new Location();
  private int _boatObjectId;
  private Location _lastClientPosition;
  private Location _lastServerPosition;

  public ValidatePosition() {
  }

  protected void readImpl() {
    this._loc.x = this.readD();
    this._loc.y = this.readD();
    this._loc.z = this.readD();
    this._loc.h = this.readD();
    this._boatObjectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (!activeChar.isTeleporting() && !activeChar.isInObserverMode() && !activeChar.isOlyObserver()) {
        this._lastClientPosition = activeChar.getLastClientPosition();
        this._lastServerPosition = activeChar.getLastServerPosition();
        if (this._lastClientPosition == null) {
          this._lastClientPosition = activeChar.getLoc();
        }

        if (this._lastServerPosition == null) {
          this._lastServerPosition = activeChar.getLoc();
        }

        if (activeChar.getX() == 0 && activeChar.getY() == 0 && activeChar.getZ() == 0) {
          this.correctPosition(activeChar);
        } else {
          if (activeChar.isInFlyingTransform()) {
            if (this._loc.x > -166168) {
              activeChar.setTransformation(0);
              return;
            }

            if (this._loc.z <= 0 || this._loc.z >= 6000) {
              activeChar.teleToLocation(activeChar.getLoc().setZ(Math.min(5950, Math.max(50, this._loc.z))));
              return;
            }
          }

          double diff = activeChar.getDistance(this._loc.x, this._loc.y);
          int dz = Math.abs(this._loc.z - activeChar.getZ());
          int h = this._lastServerPosition.z - activeChar.getZ();
          if (this._boatObjectId > 0) {
            Boat boat = BoatHolder.getInstance().getBoat(this._boatObjectId);
            if (boat != null && activeChar.getBoat() == boat) {
              activeChar.setHeading(this._loc.h);
              boat.validateLocationPacket(activeChar);
            }

            activeChar.setLastClientPosition(this._loc.setH(activeChar.getHeading()));
            activeChar.setLastServerPosition(activeChar.getLoc());
          } else {
            if (activeChar.isFalling()) {
              diff = 0.0D;
              dz = 0;
              h = 0;
            }

            int maxDiff = 256 + ((GameClient)this.getClient()).getPing() * activeChar.getMoveSpeed() / 1000;
            int maxZDiff = activeChar.maxZDiff() + 64;
            int dbgMove = activeChar.getVarInt("debugMove", 0);
            if (dbgMove > 0) {
              ExServerPrimitive sp = new ExServerPrimitive("", activeChar.getLoc().clone().setZ(activeChar.getZ() + 64));
              sp.addLine("Diff: " + diff + " max: " + maxDiff, 16777215, true, activeChar.getX(), activeChar.getY(), activeChar.getZ() + 80, this._loc.getX(), this._loc.getY(), this._loc.getZ() + 64);
              activeChar.broadcastPacket(new L2GameServerPacket[]{sp});
            }

            boolean canFall = !activeChar.isInWater() && !activeChar.isFlying();
            if (canFall && h >= 256) {
              activeChar.falling(h);
            } else if (canFall && dz >= maxZDiff) {
              if (activeChar.getIncorrectValidateCount() >= 6) {
                activeChar.teleToClosestTown();
              } else if (activeChar.getIncorrectValidateCount() > 3) {
                activeChar.teleToLocation(Location.findPointToStay(this._lastServerPosition, activeChar.getIncorrectValidateCount() * 32, activeChar.getGeoIndex()));
                activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
              } else {
                activeChar.teleToLocation(this._lastServerPosition);
                activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
              }
            } else if (canFall && dz >= maxZDiff / 2) {
              activeChar.validateLocation(0);
            } else if (this._loc.z >= -30000 && this._loc.z <= 30000) {
              if (diff > 1024.0D) {
                if (activeChar.getIncorrectValidateCount() >= 6) {
                  activeChar.teleToClosestTown();
                } else if (activeChar.getIncorrectValidateCount() > 3) {
                  activeChar.teleToLocation(Location.findPointToStay(this._lastServerPosition, activeChar.getIncorrectValidateCount() * 32, activeChar.getGeoIndex()));
                  activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
                } else {
                  activeChar.teleToLocation(activeChar.getLoc());
                  activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
                }
              } else if (diff > (double)maxDiff) {
                activeChar.validateLocation(1);
              } else {
                activeChar.setIncorrectValidateCount(0);
              }
            } else if (activeChar.getIncorrectValidateCount() >= 3) {
              activeChar.teleToClosestTown();
            } else {
              this.correctPosition(activeChar);
              activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
            }

            activeChar.setLastClientPosition(this._loc.setH(activeChar.getHeading()));
            activeChar.setLastServerPosition(activeChar.getLoc());
          }
        }
      }
    }
  }

  private void correctPosition(Player activeChar) {
    if (activeChar.isGM()) {
      activeChar.sendMessage("Server loc: " + activeChar.getLoc());
      activeChar.sendMessage("Correcting position...");
    }

    if (this._lastServerPosition.x != 0 && this._lastServerPosition.y != 0 && this._lastServerPosition.z != 0) {
      if (GeoEngine.getNSWE(this._lastServerPosition.x, this._lastServerPosition.y, this._lastServerPosition.z, activeChar.getGeoIndex()) == 15) {
        activeChar.teleToLocation(this._lastServerPosition);
      } else {
        activeChar.teleToClosestTown();
      }
    } else if (this._lastClientPosition.x != 0 && this._lastClientPosition.y != 0 && this._lastClientPosition.z != 0) {
      if (GeoEngine.getNSWE(this._lastClientPosition.x, this._lastClientPosition.y, this._lastClientPosition.z, activeChar.getGeoIndex()) == 15) {
        activeChar.teleToLocation(this._lastClientPosition);
      } else {
        activeChar.teleToClosestTown();
      }
    } else {
      activeChar.teleToClosestTown();
    }

  }
}
