//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.geometry.Rectangle;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Territory;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.TeleportToLocation;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DimensionalRiftManager {
  private static final Logger _log = LoggerFactory.getLogger(DimensionalRiftManager.class);
  private static DimensionalRiftManager _instance;
  private Map<Integer, Map<Integer, DimensionalRiftManager.DimensionalRiftRoom>> _rooms = new ConcurrentHashMap();
  private static final int DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;

  public static DimensionalRiftManager getInstance() {
    if (_instance == null) {
      _instance = new DimensionalRiftManager();
    }

    return _instance;
  }

  public DimensionalRiftManager() {
    this.load();
  }

  public DimensionalRiftManager.DimensionalRiftRoom getRoom(int type, int room) {
    return (DimensionalRiftManager.DimensionalRiftRoom)((Map)this._rooms.get(type)).get(room);
  }

  public Map<Integer, DimensionalRiftManager.DimensionalRiftRoom> getRooms(int type) {
    return (Map)this._rooms.get(type);
  }

  public void load() {
    int countGood = 0;
    int countBad = 0;

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setIgnoringComments(true);
      File file = new File(Config.DATAPACK_ROOT, "data/dimensional_rift.xml");
      if (!file.exists()) {
        throw new IOException();
      }

      Document doc = factory.newDocumentBuilder().parse(file);
      Location tele = new Location();
//      int xMin = false;
//      int xMax = false;
//      int yMin = false;
//      int yMax = false;
//      int zMin = false;
//      int zMax = false;

      for(Node rift = doc.getFirstChild(); rift != null; rift = rift.getNextSibling()) {
        if ("rift".equalsIgnoreCase(rift.getNodeName())) {
          for(Node area = rift.getFirstChild(); area != null; area = area.getNextSibling()) {
            if ("area".equalsIgnoreCase(area.getNodeName())) {
              NamedNodeMap attrs = area.getAttributes();
              int type = Integer.parseInt(attrs.getNamedItem("type").getNodeValue());

              for(Node room = area.getFirstChild(); room != null; room = room.getNextSibling()) {
                if ("room".equalsIgnoreCase(room.getNodeName())) {
                  attrs = room.getAttributes();
                  int roomId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                  Node boss = attrs.getNamedItem("isBossRoom");
                  boolean isBossRoom = boss != null ? Boolean.parseBoolean(boss.getNodeValue()) : false;
                  Territory territory = null;

                  Node spawn;
                  for(spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling()) {
                    if ("teleport".equalsIgnoreCase(spawn.getNodeName())) {
                      attrs = spawn.getAttributes();
                      tele = Location.parseLoc(attrs.getNamedItem("loc").getNodeValue());
                    } else if ("zone".equalsIgnoreCase(spawn.getNodeName())) {
                      attrs = spawn.getAttributes();
                      int xMin = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
                      int xMax = Integer.parseInt(attrs.getNamedItem("xMax").getNodeValue());
                      int yMin = Integer.parseInt(attrs.getNamedItem("yMin").getNodeValue());
                      int yMax = Integer.parseInt(attrs.getNamedItem("yMax").getNodeValue());
                      int zMin = Integer.parseInt(attrs.getNamedItem("zMin").getNodeValue());
                      int zMax = Integer.parseInt(attrs.getNamedItem("zMax").getNodeValue());
                      territory = (new Territory()).add((new Rectangle(xMin, yMin, xMax, yMax)).setZmin(zMin).setZmax(zMax));
                    }
                  }

                  if (territory == null) {
                    _log.error("DimensionalRiftManager: invalid spawn data for room id " + roomId + "!");
                  }

                  if (!this._rooms.containsKey(type)) {
                    this._rooms.put(type, new ConcurrentHashMap());
                  }

                  ((Map)this._rooms.get(type)).put(roomId, new DimensionalRiftManager.DimensionalRiftRoom(territory, tele, isBossRoom));

                  for(spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling()) {
                    if ("spawn".equalsIgnoreCase(spawn.getNodeName())) {
                      attrs = spawn.getAttributes();
                      int mobId = Integer.parseInt(attrs.getNamedItem("mobId").getNodeValue());
                      int delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
                      int count = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());
                      NpcTemplate template = NpcHolder.getInstance().getTemplate(mobId);
                      if (template == null) {
                        _log.warn("Template " + mobId + " not found!");
                      }

                      if (!this._rooms.containsKey(type)) {
                        _log.warn("Type " + type + " not found!");
                      } else if (!((Map)this._rooms.get(type)).containsKey(roomId)) {
                        _log.warn("Room " + roomId + " in Type " + type + " not found!");
                      }

                      if (template != null && this._rooms.containsKey(type) && ((Map)this._rooms.get(type)).containsKey(roomId)) {
                        SimpleSpawner spawnDat = new SimpleSpawner(template);
                        spawnDat.setTerritory(territory);
                        spawnDat.setHeading(-1);
                        spawnDat.setRespawnDelay(delay);
                        spawnDat.setAmount(count);
                        ((DimensionalRiftManager.DimensionalRiftRoom)((Map)this._rooms.get(type)).get(roomId)).getSpawns().add(spawnDat);
                        ++countGood;
                      } else {
                        ++countBad;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception var28) {
      _log.error("DimensionalRiftManager: Error on loading dimensional rift spawns!", var28);
    }

    int typeSize = this._rooms.keySet().size();
    int roomSize = 0;

    int b;
    for(Iterator var31 = this._rooms.keySet().iterator(); var31.hasNext(); roomSize += ((Map)this._rooms.get(b)).keySet().size()) {
      b = (Integer)var31.next();
    }

    _log.info("DimensionalRiftManager: Loaded " + typeSize + " room types with " + roomSize + " rooms.");
    _log.info("DimensionalRiftManager: Loaded " + countGood + " dimensional rift spawns, " + countBad + " errors.");
  }

  public void reload() {
    Iterator var1 = this._rooms.keySet().iterator();

    while(var1.hasNext()) {
      int b = (Integer)var1.next();
      ((Map)this._rooms.get(b)).clear();
    }

    this._rooms.clear();
    this.load();
  }

  public boolean checkIfInRiftZone(Location loc, boolean ignorePeaceZone) {
    if (ignorePeaceZone) {
      return ((DimensionalRiftManager.DimensionalRiftRoom)((Map)this._rooms.get(0)).get(1)).checkIfInZone(loc);
    } else {
      return ((DimensionalRiftManager.DimensionalRiftRoom)((Map)this._rooms.get(0)).get(1)).checkIfInZone(loc) && !((DimensionalRiftManager.DimensionalRiftRoom)((Map)this._rooms.get(0)).get(0)).checkIfInZone(loc);
    }
  }

  public boolean checkIfInPeaceZone(Location loc) {
    return ((DimensionalRiftManager.DimensionalRiftRoom)((Map)this._rooms.get(0)).get(0)).checkIfInZone(loc);
  }

  public void teleportToWaitingRoom(Player player) {
    teleToLocation(player, Location.findPointToStay(this.getRoom(0, 0).getTeleportCoords(), 0, 250, ReflectionManager.DEFAULT.getGeoIndex()), (Reflection)null);
  }

  public void start(Player player, int type, NpcInstance npc) {
    if (!player.isInParty()) {
      this.showHtmlFile(player, "rift/NoParty.htm", npc);
    } else {
      if (!player.isGM()) {
        if (!player.getParty().isLeader(player)) {
          this.showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
          return;
        }

        if (player.getParty().isInDimensionalRift()) {
          this.showHtmlFile(player, "rift/Cheater.htm", npc);
          if (!player.isGM()) {
            _log.warn("Player " + player.getName() + "(" + player.getObjectId() + ") was cheating in dimension rift area!");
          }

          return;
        }

        if (player.getParty().getMemberCount() < Config.RIFT_MIN_PARTY_SIZE) {
          this.showHtmlFile(player, "rift/SmallParty.htm", npc);
          return;
        }

        Iterator var4 = player.getParty().getPartyMembers().iterator();

        while(var4.hasNext()) {
          Player p = (Player)var4.next();
          if (!this.checkIfInPeaceZone(p.getLoc())) {
            this.showHtmlFile(player, "rift/NotInWaitingRoom.htm", npc);
            return;
          }
        }

        Iterator var8 = player.getParty().getPartyMembers().iterator();

        label59:
        while(true) {
          Player p;
          if (!var8.hasNext()) {
            var8 = player.getParty().getPartyMembers().iterator();

            do {
              if (!var8.hasNext()) {
                break label59;
              }

              p = (Player)var8.next();
            } while(p.getInventory().destroyItemByItemId(7079, this.getNeededItems(type)));

            this.showHtmlFile(player, "rift/NoFragments.htm", npc);
            return;
          }

          p = (Player)var8.next();
          ItemInstance i = p.getInventory().getItemByItemId(7079);
          if (i == null || i.getCount() < this.getNeededItems(type)) {
            this.showHtmlFile(player, "rift/NoFragments.htm", npc);
            return;
          }
        }
      }

      new DimensionalRift(player.getParty(), type, Rnd.get(1, ((Map)this._rooms.get(type)).size() - 1));
    }
  }

  private long getNeededItems(int type) {
    switch(type) {
      case 1:
        return (long)Config.RIFT_ENTER_COST_RECRUIT;
      case 2:
        return (long)Config.RIFT_ENTER_COST_SOLDIER;
      case 3:
        return (long)Config.RIFT_ENTER_COST_OFFICER;
      case 4:
        return (long)Config.RIFT_ENTER_COST_CAPTAIN;
      case 5:
        return (long)Config.RIFT_ENTER_COST_COMMANDER;
      case 6:
        return (long)Config.RIFT_ENTER_COST_HERO;
      default:
        return 9223372036854775807L;
    }
  }

  public void showHtmlFile(Player player, String file, NpcInstance npc) {
    NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
    html.setFile(file);
    html.replace("%t_name%", npc.getName());
    player.sendPacket(html);
  }

  public static void teleToLocation(Player player, Location loc, Reflection ref) {
    if (!player.isTeleporting() && !player.isDeleted()) {
      player.setIsTeleporting(true);
      player.setTarget((GameObject)null);
      player.stopMove();
      if (player.isInBoat()) {
        player.setBoat((Boat)null);
      }

      player.breakFakeDeath();
      player.decayMe();
      player.setLoc(loc);
      if (ref == null) {
        player.setReflection(ReflectionManager.DEFAULT);
      }

      player.setLastClientPosition((Location)null);
      player.setLastServerPosition((Location)null);
      player.sendPacket(new TeleportToLocation(player, loc));
    }
  }

  public class DimensionalRiftRoom {
    private final Territory _territory;
    private final Location _teleportCoords;
    private final boolean _isBossRoom;
    private final List<SimpleSpawner> _roomSpawns;

    public DimensionalRiftRoom(Territory territory, Location tele, boolean isBossRoom) {
      this._territory = territory;
      this._teleportCoords = tele;
      this._isBossRoom = isBossRoom;
      this._roomSpawns = new ArrayList();
    }

    public Location getTeleportCoords() {
      return this._teleportCoords;
    }

    public boolean checkIfInZone(Location loc) {
      return this.checkIfInZone(loc.x, loc.y, loc.z);
    }

    public boolean checkIfInZone(int x, int y, int z) {
      return this._territory.isInside(x, y, z);
    }

    public boolean isBossRoom() {
      return this._isBossRoom;
    }

    public List<SimpleSpawner> getSpawns() {
      return this._roomSpawns;
    }
  }
}
