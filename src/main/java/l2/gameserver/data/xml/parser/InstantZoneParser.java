//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Polygon;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.DoorHolder;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.templates.InstantZone.DoorInfo;
import l2.gameserver.templates.InstantZone.SpawnInfo;
import l2.gameserver.templates.InstantZone.SpawnInfo2;
import l2.gameserver.templates.InstantZone.ZoneInfo;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import org.dom4j.Element;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class InstantZoneParser extends AbstractDirParser<InstantZoneHolder> {
  private static InstantZoneParser _instance = new InstantZoneParser();

  public static InstantZoneParser getInstance() {
    return _instance;
  }

  public InstantZoneParser() {
    super(InstantZoneHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/instances/");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "instances.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator();

    while(iterator.hasNext()) {
      Element element = (Element)iterator.next();
      SchedulingPattern resetReuse = new SchedulingPattern("30 6 * * *");
      int timelimit = -1;
      int timer = 60;
      int mapx = -1;
      int mapy = -1;
      boolean dispelBuffs = false;
      boolean onPartyDismiss = true;
      int sharedReuseGroup = 0;
      int collapseIfEmpty = false;
      int spawnType = false;
      SpawnInfo spawnDat = null;
      int removedItemId = 0;
      int removedItemCount = 0;
      int giveItemId = 0;
      int givedItemCount = 0;
      int requiredQuestId = 0;
      int maxChannels = true;
      boolean removedItemNecessity = false;
      boolean setReuseUponEntry = true;
      StatsSet params = new StatsSet();
      List<SpawnInfo> spawns = new ArrayList();
      IntObjectMap<DoorInfo> doors = Containers.emptyIntObjectMap();
      Map<String, ZoneInfo> zones = Collections.emptyMap();
      Map<String, SpawnInfo2> spawns2 = Collections.emptyMap();
      int instanceId = Integer.parseInt(element.attributeValue("id"));
      String name = element.attributeValue("name");
      String n = element.attributeValue("timelimit");
      if (n != null) {
        timelimit = Integer.parseInt(n);
      }

      n = element.attributeValue("collapseIfEmpty");
      int collapseIfEmpty = Integer.parseInt(n);
      n = element.attributeValue("maxChannels");
      int maxChannels = Integer.parseInt(n);
      n = element.attributeValue("dispelBuffs");
      dispelBuffs = n != null && Boolean.parseBoolean(n);
      int minLevel = 0;
      int maxLevel = 0;
      int minParty = 1;
      int maxParty = 9;
      List<Location> teleportLocs = Collections.emptyList();
      Location ret = null;
      Iterator subIterator = element.elementIterator();

      while(true) {
        label251:
        while(subIterator.hasNext()) {
          Element subElement = (Element)subIterator.next();
          if ("level".equalsIgnoreCase(subElement.getName())) {
            minLevel = Integer.parseInt(subElement.attributeValue("min"));
            maxLevel = Integer.parseInt(subElement.attributeValue("max"));
          } else if ("collapse".equalsIgnoreCase(subElement.getName())) {
            onPartyDismiss = Boolean.parseBoolean(subElement.attributeValue("on-party-dismiss"));
            timer = Integer.parseInt(subElement.attributeValue("timer"));
          } else if ("party".equalsIgnoreCase(subElement.getName())) {
            minParty = Integer.parseInt(subElement.attributeValue("min"));
            maxParty = Integer.parseInt(subElement.attributeValue("max"));
          } else if ("return".equalsIgnoreCase(subElement.getName())) {
            ret = Location.parseLoc(subElement.attributeValue("loc"));
          } else if ("teleport".equalsIgnoreCase(subElement.getName())) {
            if (((List)teleportLocs).isEmpty()) {
              teleportLocs = new ArrayList(1);
            }

            ((List)teleportLocs).add(Location.parseLoc(subElement.attributeValue("loc")));
          } else if ("remove".equalsIgnoreCase(subElement.getName())) {
            removedItemId = Integer.parseInt(subElement.attributeValue("itemId"));
            removedItemCount = Integer.parseInt(subElement.attributeValue("count"));
            removedItemNecessity = Boolean.parseBoolean(subElement.attributeValue("necessary"));
          } else if ("give".equalsIgnoreCase(subElement.getName())) {
            giveItemId = Integer.parseInt(subElement.attributeValue("itemId"));
            givedItemCount = Integer.parseInt(subElement.attributeValue("count"));
          } else if ("quest".equalsIgnoreCase(subElement.getName())) {
            requiredQuestId = Integer.parseInt(subElement.attributeValue("id"));
          } else if ("reuse".equalsIgnoreCase(subElement.getName())) {
            resetReuse = new SchedulingPattern(subElement.attributeValue("resetReuse"));
            sharedReuseGroup = Integer.parseInt(subElement.attributeValue("sharedReuseGroup"));
            setReuseUponEntry = Boolean.parseBoolean(subElement.attributeValue("setUponEntry"));
          } else if ("geodata".equalsIgnoreCase(subElement.getName())) {
            String[] rxy = subElement.attributeValue("map").split("_");
            mapx = Integer.parseInt(rxy[0]);
            mapy = Integer.parseInt(rxy[1]);
          } else {
            Iterator var43;
            Element e;
            boolean active;
            boolean spawned;
            if ("doors".equalsIgnoreCase(subElement.getName())) {
              var43 = subElement.elements().iterator();

              while(var43.hasNext()) {
                e = (Element)var43.next();
                if (((IntObjectMap)doors).isEmpty()) {
                  doors = new HashIntObjectMap();
                }

                active = e.attributeValue("opened") != null && Boolean.parseBoolean(e.attributeValue("opened"));
                spawned = e.attributeValue("invul") == null || Boolean.parseBoolean(e.attributeValue("invul"));
                DoorTemplate template = DoorHolder.getInstance().getTemplate(Integer.parseInt(e.attributeValue("id")));
                ((IntObjectMap)doors).put(template.getNpcId(), new DoorInfo(template, active, spawned));
              }
            } else if ("zones".equalsIgnoreCase(subElement.getName())) {
              var43 = subElement.elements().iterator();

              while(var43.hasNext()) {
                e = (Element)var43.next();
                if (((Map)zones).isEmpty()) {
                  zones = new HashMap();
                }

                active = e.attributeValue("active") != null && Boolean.parseBoolean(e.attributeValue("active"));
                ZoneTemplate template = ZoneHolder.getInstance().getTemplate(e.attributeValue("name"));
                if (template == null) {
                  this.error("Zone: " + e.attributeValue("name") + " not found; file: " + this.getCurrentFileName());
                } else {
                  ((Map)zones).put(template.getName(), new ZoneInfo(template, active));
                }
              }
            } else if ("add_parameters".equalsIgnoreCase(subElement.getName())) {
              var43 = subElement.elements().iterator();

              while(var43.hasNext()) {
                e = (Element)var43.next();
                if ("param".equalsIgnoreCase(e.getName())) {
                  params.set(e.attributeValue("name"), e.attributeValue("value"));
                }
              }
            } else if ("spawns".equalsIgnoreCase(subElement.getName())) {
              var43 = subElement.elements().iterator();

              while(true) {
                while(true) {
                  if (!var43.hasNext()) {
                    continue label251;
                  }

                  e = (Element)var43.next();
                  if ("group".equalsIgnoreCase(e.getName())) {
                    String group = e.attributeValue("name");
                    spawned = e.attributeValue("spawned") != null && Boolean.parseBoolean(e.attributeValue("spawned"));
                    List<SpawnTemplate> templates = SpawnHolder.getInstance().getSpawn(group);
                    if (templates == null) {
                      this.info("not find spawn group: " + group + " in file: " + this.getCurrentFileName());
                    } else {
                      if (((Map)spawns2).isEmpty()) {
                        spawns2 = new Hashtable();
                      }

                      ((Map)spawns2).put(group, new SpawnInfo2(templates, spawned));
                    }
                  } else if ("spawn".equalsIgnoreCase(e.getName())) {
                    String[] mobs = e.attributeValue("mobId").split(" ");
                    String respawnNode = e.attributeValue("respawn");
                    int respawn = respawnNode != null ? Integer.parseInt(respawnNode) : 0;
                    String respawnRndNode = e.attributeValue("respawnRnd");
                    int respawnRnd = respawnRndNode != null ? Integer.parseInt(respawnRndNode) : 0;
                    String countNode = e.attributeValue("count");
                    int count = countNode != null ? Integer.parseInt(countNode) : 1;
                    List<Location> coords = new ArrayList();
                    int spawnType = 0;
                    String spawnTypeNode = e.attributeValue("type");
                    if (spawnTypeNode != null && !spawnTypeNode.equalsIgnoreCase("point")) {
                      if (spawnTypeNode.equalsIgnoreCase("rnd")) {
                        spawnType = 1;
                      } else if (spawnTypeNode.equalsIgnoreCase("loc")) {
                        spawnType = 2;
                      } else {
                        this.error("Spawn type  '" + spawnTypeNode + "' is unknown!");
                      }
                    } else {
                      spawnType = 0;
                    }

                    Iterator var51 = e.elements().iterator();

                    while(var51.hasNext()) {
                      Element e2 = (Element)var51.next();
                      if ("coords".equalsIgnoreCase(e2.getName())) {
                        coords.add(Location.parseLoc(e2.attributeValue("loc")));
                      }
                    }

                    Territory territory = null;
                    if (spawnType == 2) {
                      Polygon poly = new Polygon();
                      Iterator var53 = coords.iterator();

                      while(var53.hasNext()) {
                        Location loc = (Location)var53.next();
                        poly.add(loc.x, loc.y).setZmin(loc.z).setZmax(loc.z);
                      }

                      if (!poly.validate()) {
                        this.error("invalid spawn territory for instance id : " + instanceId + " - " + poly + "!");
                      }

                      territory = (new Territory()).add(poly);
                    }

                    String[] var69 = mobs;
                    int var70 = mobs.length;

                    for(int var71 = 0; var71 < var70; ++var71) {
                      String mob = var69[var71];
                      int mobId = Integer.parseInt(mob);
                      spawnDat = new SpawnInfo(spawnType, mobId, count, respawn, respawnRnd, coords, territory);
                      spawns.add(spawnDat);
                    }
                  }
                }
              }
            }
          }
        }

        InstantZone instancedZone = new InstantZone(instanceId, name, resetReuse, sharedReuseGroup, timelimit, dispelBuffs, minLevel, maxLevel, minParty, maxParty, timer, onPartyDismiss, (List)teleportLocs, ret, mapx, mapy, (IntObjectMap)doors, (Map)zones, (Map)spawns2, spawns, collapseIfEmpty, maxChannels, removedItemId, removedItemCount, removedItemNecessity, giveItemId, givedItemCount, requiredQuestId, setReuseUponEntry, params);
        ((InstantZoneHolder)this.getHolder()).addInstantZone(instancedZone);
        break;
      }
    }

  }
}
