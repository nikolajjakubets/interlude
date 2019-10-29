//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import l2.commons.collections.MultiValueSet;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Point2D;
import l2.commons.geometry.Polygon;
import l2.commons.time.cron.AddPattern;
import l2.commons.time.cron.NextTime;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.time.cron.SchedulingPattern.InvalidPatternException;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.templates.spawn.SpawnNpcInfo;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.SpawnMesh;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

@Slf4j
public final class SpawnParser extends AbstractDirParser<SpawnHolder> {
  private static final Logger LOG = LoggerFactory.getLogger(SpawnParser.class);
  private static final SpawnParser _instance = new SpawnParser();

  public static SpawnParser getInstance() {
    return _instance;
  }

  protected SpawnParser() {
    super(SpawnHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/spawn/");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "spawn.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator spawnListIterator = rootElement.elementIterator();

    label105:
    while(true) {
      Element spawnListElement;
      do {
        if (!spawnListIterator.hasNext()) {
          return;
        }

        spawnListElement = (Element)spawnListIterator.next();
      } while(!"spawn".equalsIgnoreCase(spawnListElement.getName()));

      String makerName = spawnListElement.attributeValue("name");
      String eventName = spawnListElement.attributeValue("event_name");
      SpawnMesh spawnMesh = null;
      Iterator spawnIterator = spawnListElement.elementIterator();

      while(true) {
        label101:
        while(true) {
          if (!spawnIterator.hasNext()) {
            continue label105;
          }

          Element spawnElement = (Element)spawnIterator.next();
          if ("mesh".equalsIgnoreCase(spawnElement.getName())) {
            spawnMesh = this.parseSpawnMesh(spawnElement);
          } else if ("npc".equalsIgnoreCase(spawnElement.getName())) {
            int npcTemplateId = Integer.parseInt(spawnElement.attributeValue("id", "0"));
            int count = Integer.parseInt(spawnElement.attributeValue("count"));
            long respawn = Long.parseLong(spawnElement.attributeValue("respawn", "60"));
            long respawnRand = Long.parseLong(spawnElement.attributeValue("respawn_rand", "0"));
            if (respawnRand > respawn) {
              throw new RuntimeException("Invalid respawn respawn_rand > respawn of " + spawnListElement.asXML());
            }

            String respawnCronPattern = spawnElement.attributeValue("respawn_cron");
            NextTime respawnCron = null;
            if (respawnCronPattern != null) {
              try {
                respawnCron = new SchedulingPattern(respawnCronPattern);
              } catch (InvalidPatternException e) {
                log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
                try {
                  respawnCron = new AddPattern(respawnCronPattern);
                } catch (Exception e1) {
                  log.error("Exception: eMessage={}, eClass={}, eCause={}", e1.getMessage(), this.getClass().getSimpleName(), e1.getCause());
                  throw new RuntimeException("Invalid respawn data of " + spawnListElement.asXML(), e1);
                }

              }
            }

            PeriodOfDay pod = PeriodOfDay.valueOf(spawnElement.attributeValue("period_of_day", PeriodOfDay.ALL.name()));
            Location spawnPos = null;
            if (spawnElement.attributeValue("pos") != null) {
              spawnPos = Location.parseLoc(spawnElement.attributeValue("pos"));
            } else if (spawnMesh == null) {
              throw new RuntimeException("Neither mesh nor pos defined " + spawnListElement.asXML());
            }

            MultiValueSet<String> aiParams = StatsSet.EMPTY;
            Iterator npcIterator = spawnElement.elementIterator();

            while(true) {
              Element npcElement;
              do {
                if (!npcIterator.hasNext()) {
                  try {
                    SpawnTemplate spawnTemplate = new SpawnTemplate(makerName, eventName, pod, count, respawn, respawnRand, respawnCron);
                    SpawnNpcInfo sni = new SpawnNpcInfo(npcTemplateId, count, aiParams);
                    spawnTemplate.addNpc(sni);
                    spawnTemplate.addSpawnRange(spawnPos != null ? spawnPos : spawnMesh);
                    this.getHolder().addSpawn(eventName != null ? eventName : PeriodOfDay.ALL.name(), spawnTemplate);
                  } catch (Exception e) {
                    log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());

                  }
                  continue label101;
                }

                npcElement = (Element)npcIterator.next();
              } while(!"ai_params".equalsIgnoreCase(npcElement.getName()));

              Iterator npcAiParamsIterator = npcElement.elementIterator();

              while(npcAiParamsIterator.hasNext()) {
                Element npcAiParamsElement = (Element)npcAiParamsIterator.next();
                if ("set".equalsIgnoreCase(npcAiParamsElement.getName())) {
                  if (aiParams == StatsSet.EMPTY) {
                    aiParams = new MultiValueSet();
                  }

                  aiParams.set(npcAiParamsElement.attributeValue("name"), npcAiParamsElement.attributeValue("val"));
                }
              }
            }
          }
        }
      }
    }
  }

  private Territory parseTerritory(String name, Element e) {
    Territory t = new Territory();
    t.add(this.parsePolygon0(name, e));
    Iterator iterator = e.elementIterator("banned_territory");

    while(iterator.hasNext()) {
      t.addBanned(this.parsePolygon0(name, (Element)iterator.next()));
    }

    return t;
  }

  private Polygon parsePolygon0(String name, Element e) {
    Polygon temp = new Polygon();
    Iterator addIterator = e.elementIterator("add");

    while(addIterator.hasNext()) {
      Element addElement = (Element)addIterator.next();
      int x = Integer.parseInt(addElement.attributeValue("x"));
      int y = Integer.parseInt(addElement.attributeValue("y"));
      int zmin = Integer.parseInt(addElement.attributeValue("zmin"));
      int zmax = Integer.parseInt(addElement.attributeValue("zmax"));
      temp.add(x, y).setZmin(zmin).setZmax(zmax);
    }

    if (!temp.validate()) {
      this.error("Invalid polygon: " + name + "{" + temp + "}. File: " + this.getCurrentFileName());
    }

    return temp;
  }

  private SpawnMesh parseSpawnMesh(Element e) {
    short meshZMin = 32767;
    short meshZMax = -32768;
    Iterator<Element> vertexesIt = e.elementIterator("vertex");
    LinkedList vertexes = new LinkedList();

    while(vertexesIt.hasNext()) {
      Element vertexElement = vertexesIt.next();
      int vertexX = Integer.parseInt(vertexElement.attributeValue("x"));
      int vertexY = Integer.parseInt(vertexElement.attributeValue("y"));
      meshZMin = (short)Math.min(meshZMin, Short.parseShort(vertexElement.attributeValue("minz")));
      meshZMax = (short)Math.max(meshZMax, Short.parseShort(vertexElement.attributeValue("maxz")));
      vertexes.add(new Point2D(vertexX, vertexY));
    }

    SpawnMesh spawnMesh = new SpawnMesh();
    Iterator var10 = vertexes.iterator();

    while(var10.hasNext()) {
      Point2D vertex = (Point2D)var10.next();
      spawnMesh.add(vertex.getX(), vertex.getY());
    }

    assert meshZMax >= meshZMin;

    spawnMesh.setZmax(meshZMax);
    spawnMesh.setZmin(meshZMin);
    if (spawnMesh.validate() && spawnMesh.getZmin() <= spawnMesh.getZmax()) {
      return spawnMesh;
    } else {
      throw new RuntimeException("Invalid spawn mesh " + spawnMesh + " defined for the node " + e.asXML());
    }
  }
}
