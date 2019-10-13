//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class DimensionalRift extends Reflection {
  protected static final long seconds_5 = 5000L;
  protected static final int MILLISECONDS_IN_MINUTE = 60000;
  protected int _roomType;
  protected List<Integer> _completedRooms = new ArrayList();
  protected int jumps_current = 0;
  private Future<?> teleporterTask;
  private Future<?> spawnTask;
  private Future<?> killRiftTask;
  protected int _choosenRoom = -1;
  protected boolean _hasJumped = false;
  protected boolean isBossRoom = false;

  public DimensionalRift(Party party, int type, int room) {
    this.onCreate();
    this.startCollapseTimer(7200000L);
    this.setName("Dimensional Rift");
    if (this instanceof DelusionChamber) {
      InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(type + 120);
      this.setInstancedZone(iz);
      this.setName(iz.getName());
    }

    this._roomType = type;
    this.setParty(party);
    if (!(this instanceof DelusionChamber)) {
      party.setDimensionalRift(this);
    }

    party.setReflection(this);
    this._choosenRoom = room;
    this.checkBossRoom(this._choosenRoom);
    Location coords = this.getRoomCoord(this._choosenRoom);
    this.setReturnLoc(party.getPartyLeader().getLoc());
    this.setTeleportLoc(coords);
    Iterator var5 = party.getPartyMembers().iterator();

    while(var5.hasNext()) {
      Player p = (Player)var5.next();
      p.setVar("backCoords", this.getReturnLoc().toXYZString(), -1L);
      DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(coords, 50, 100, this.getGeoIndex()), this);
      p.setReflection(this);
    }

    this.createSpawnTimer(this._choosenRoom);
    this.createTeleporterTimer();
  }

  public int getType() {
    return this._roomType;
  }

  public int getCurrentRoom() {
    return this._choosenRoom;
  }

  protected void createTeleporterTimer() {
    if (this.teleporterTask != null) {
      this.teleporterTask.cancel(false);
      this.teleporterTask = null;
    }

    this.teleporterTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        if (DimensionalRift.this.jumps_current < DimensionalRift.this.getMaxJumps() && DimensionalRift.this.getPlayersInside(true) > 0) {
          ++DimensionalRift.this.jumps_current;
          DimensionalRift.this.teleportToNextRoom();
          DimensionalRift.this.createTeleporterTimer();
        } else {
          DimensionalRift.this.createNewKillRiftTimer();
        }

      }
    }, this.calcTimeToNextJump());
  }

  public void createSpawnTimer(int room) {
    if (this.spawnTask != null) {
      this.spawnTask.cancel(false);
      this.spawnTask = null;
    }

    final DimensionalRiftRoom riftRoom = DimensionalRiftManager.getInstance().getRoom(this._roomType, room);
    this.spawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        Iterator var1 = riftRoom.getSpawns().iterator();

        while(var1.hasNext()) {
          SimpleSpawner s = (SimpleSpawner)var1.next();
          SimpleSpawner sp = s.clone();
          sp.setReflection(DimensionalRift.this);
          DimensionalRift.this.addSpawn(sp);
          if (!DimensionalRift.this.isBossRoom) {
            sp.startRespawn();
          }

          for(int i = 0; i < sp.getAmount(); ++i) {
            sp.doSpawn(true);
          }
        }

        DimensionalRift.this.addSpawnWithoutRespawn(DimensionalRift.this.getManagerId(), riftRoom.getTeleportCoords(), 0);
      }
    }, (long)Config.RIFT_SPAWN_DELAY);
  }

  public synchronized void createNewKillRiftTimer() {
    if (this.killRiftTask != null) {
      this.killRiftTask.cancel(false);
      this.killRiftTask = null;
    }

    this.killRiftTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        if (!DimensionalRift.this.isCollapseStarted()) {
          Iterator var1 = DimensionalRift.this.getParty().getPartyMembers().iterator();

          while(var1.hasNext()) {
            Player p = (Player)var1.next();
            if (p != null && p.getReflection() == DimensionalRift.this) {
              DimensionalRiftManager.getInstance().teleportToWaitingRoom(p);
            }
          }

          DimensionalRift.this.collapse();
        }
      }
    }, 100L);
  }

  public void partyMemberInvited() {
    this.createNewKillRiftTimer();
  }

  public void partyMemberExited(Player player) {
    if (this.getParty().getMemberCount() < Config.RIFT_MIN_PARTY_SIZE || this.getParty().getMemberCount() == 1 || this.getPlayersInside(true) == 0) {
      this.createNewKillRiftTimer();
    }

  }

  public void manualTeleport(Player player, NpcInstance npc) {
    if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift) {
      if (!player.getParty().isLeader(player)) {
        DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
      } else if (!this.isBossRoom) {
        if (this._hasJumped) {
          DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/AlreadyTeleported.htm", npc);
        } else {
          this._hasJumped = true;
          this.teleportToNextRoom();
        }
      } else {
        this.manualExitRift(player, npc);
      }
    }
  }

  public void manualExitRift(Player player, NpcInstance npc) {
    if (player.isInParty() && player.getParty().isInDimensionalRift()) {
      if (!player.getParty().isLeader(player)) {
        DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
      } else {
        this.createNewKillRiftTimer();
      }
    }
  }

  protected void teleportToNextRoom() {
    this._completedRooms.add(this._choosenRoom);
    Iterator var1 = this.getSpawns().iterator();

    while(var1.hasNext()) {
      Spawner s = (Spawner)var1.next();
      s.deleteAll();
    }

    int size = DimensionalRiftManager.getInstance().getRooms(this._roomType).size();
    if (this.getType() >= 5 && this.jumps_current == this.getMaxJumps()) {
      this._choosenRoom = 9;
    } else {
      List<Integer> notCompletedRooms = new ArrayList();

      for(int i = 1; i <= size; ++i) {
        if (!this._completedRooms.contains(i)) {
          notCompletedRooms.add(i);
        }
      }

      this._choosenRoom = (Integer)notCompletedRooms.get(Rnd.get(notCompletedRooms.size()));
    }

    this.checkBossRoom(this._choosenRoom);
    this.setTeleportLoc(this.getRoomCoord(this._choosenRoom));
    Iterator var6 = this.getParty().getPartyMembers().iterator();

    while(var6.hasNext()) {
      Player p = (Player)var6.next();
      if (p.getReflection() == this) {
        DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(this.getRoomCoord(this._choosenRoom), 50, 100, this.getGeoIndex()), this);
      }
    }

    this.createSpawnTimer(this._choosenRoom);
  }

  public void collapse() {
    if (!this.isCollapseStarted()) {
      Future<?> task = this.teleporterTask;
      if (task != null) {
        this.teleporterTask = null;
        task.cancel(false);
      }

      task = this.spawnTask;
      if (task != null) {
        this.spawnTask = null;
        task.cancel(false);
      }

      task = this.killRiftTask;
      if (task != null) {
        this.killRiftTask = null;
        task.cancel(false);
      }

      this._completedRooms = null;
      Party party = this.getParty();
      if (party != null) {
        party.setDimensionalRift((DimensionalRift)null);
      }

      super.collapse();
    }
  }

  protected long calcTimeToNextJump() {
    return this.isBossRoom ? 3600000L : (long)(Config.RIFT_AUTO_JUMPS_TIME * '\uea60' + Rnd.get(Config.RIFT_AUTO_JUMPS_TIME_RAND));
  }

  public void memberDead(Player player) {
    if (this.getPlayersInside(true) == 0) {
      this.createNewKillRiftTimer();
    }

  }

  public void usedTeleport(Player player) {
    if (this.getPlayersInside(false) < Config.RIFT_MIN_PARTY_SIZE) {
      this.createNewKillRiftTimer();
    }

  }

  public void checkBossRoom(int room) {
    this.isBossRoom = DimensionalRiftManager.getInstance().getRoom(this._roomType, room).isBossRoom();
  }

  public Location getRoomCoord(int room) {
    return DimensionalRiftManager.getInstance().getRoom(this._roomType, room).getTeleportCoords();
  }

  public int getMaxJumps() {
    return Math.max(Math.min(Config.RIFT_MAX_JUMPS, 8), 1);
  }

  public boolean canChampions() {
    return true;
  }

  public String getName() {
    return "Dimensional Rift";
  }

  protected int getManagerId() {
    return 31865;
  }

  protected int getPlayersInside(boolean alive) {
    if (this._playerCount == 0) {
      return 0;
    } else {
      int sum = 0;
      Iterator var3 = this.getPlayers().iterator();

      while(true) {
        Player p;
        do {
          if (!var3.hasNext()) {
            return sum;
          }

          p = (Player)var3.next();
        } while(alive && p.isDead());

        ++sum;
      }
    }
  }

  public void removeObject(GameObject o) {
    if (o.isPlayer() && this._playerCount <= 1) {
      this.createNewKillRiftTimer();
    }

    super.removeObject(o);
  }
}
