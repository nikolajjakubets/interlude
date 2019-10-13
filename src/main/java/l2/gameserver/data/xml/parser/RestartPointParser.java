//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractFileParser;
import l2.commons.geometry.Polygon;
import l2.commons.geometry.Rectangle;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.Territory;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Race;
import l2.gameserver.templates.mapregion.RestartArea;
import l2.gameserver.templates.mapregion.RestartPoint;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class RestartPointParser extends AbstractFileParser<MapRegionManager> {
  private static final RestartPointParser _instance = new RestartPointParser();

  public static RestartPointParser getInstance() {
    return _instance;
  }

  private RestartPointParser() {
    super(MapRegionManager.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/mapregion/restart_points.xml");
  }

  public String getDTDFileName() {
    return "restart_points.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    List<Pair<Territory, Map<Race, String>>> restartArea = new ArrayList();
    Map<String, RestartPoint> restartPoint = new HashMap();
    Iterator iterator = rootElement.elementIterator();

    while(true) {
      while(iterator.hasNext()) {
        Element listElement = (Element)iterator.next();
        if ("restart_area".equals(listElement.getName())) {
          Territory territory = null;
          Map<Race, String> restarts = new HashMap();
          Iterator i = listElement.elementIterator();

          while(i.hasNext()) {
            Element n = (Element)i.next();
            if ("region".equalsIgnoreCase(n.getName())) {
              Attribute map = n.attribute("map");
              String s = map.getValue();
              String[] val = s.split("_");
              int rx = Integer.parseInt(val[0]);
              int ry = Integer.parseInt(val[1]);
              int x1 = World.MAP_MIN_X + (rx - Config.GEO_X_FIRST << 15);
              int y1 = World.MAP_MIN_Y + (ry - Config.GEO_Y_FIRST << 15);
              int x2 = x1 + '耀' - 1;
              int y2 = y1 + '耀' - 1;
              Rectangle shape = new Rectangle(x1, y1, x2, y2);
              shape.setZmin(World.MAP_MIN_Z);
              shape.setZmax(World.MAP_MAX_Z);
              if (territory == null) {
                territory = new Territory();
              }

              territory.add(shape);
            } else if ("polygon".equalsIgnoreCase(n.getName())) {
              Polygon shape = ZoneParser.parsePolygon(n);
              if (!shape.validate()) {
                this.error("RestartPointParser: invalid territory data : " + shape + "!");
              }

              if (territory == null) {
                territory = new Territory();
              }

              territory.add(shape);
            } else if ("restart".equalsIgnoreCase(n.getName())) {
              Race race = Race.valueOf(n.attributeValue("race"));
              String locName = n.attributeValue("loc");
              restarts.put(race, locName);
            }
          }

          if (territory == null) {
            throw new RuntimeException("RestartPointParser: empty territory!");
          }

          if (restarts.isEmpty()) {
            throw new RuntimeException("RestartPointParser: restarts not defined!");
          }

          restartArea.add(new ImmutablePair(territory, restarts));
        } else if ("restart_loc".equals(listElement.getName())) {
          String name = listElement.attributeValue("name");
          int bbs = Integer.parseInt(listElement.attributeValue("bbs", "0"));
          int msgId = Integer.parseInt(listElement.attributeValue("msg_id", "0"));
          List<Location> restartPoints = new ArrayList();
          List<Location> PKrestartPoints = new ArrayList();
          Iterator i = listElement.elementIterator();

          while(true) {
            while(i.hasNext()) {
              Element n = (Element)i.next();
              Iterator ii;
              Element d;
              Location loc;
              if ("restart_point".equals(n.getName())) {
                ii = n.elementIterator();

                while(ii.hasNext()) {
                  d = (Element)ii.next();
                  if ("coords".equalsIgnoreCase(d.getName())) {
                    loc = Location.parseLoc(d.attribute("loc").getValue());
                    restartPoints.add(loc);
                  }
                }
              } else if ("PKrestart_point".equals(n.getName())) {
                ii = n.elementIterator();

                while(ii.hasNext()) {
                  d = (Element)ii.next();
                  if ("coords".equalsIgnoreCase(d.getName())) {
                    loc = Location.parseLoc(d.attribute("loc").getValue());
                    PKrestartPoints.add(loc);
                  }
                }
              }
            }

            if (restartPoints.isEmpty()) {
              throw new RuntimeException("RestartPointParser: restart_points not defined for restart_loc : " + name + "!");
            }

            if (PKrestartPoints.isEmpty()) {
              PKrestartPoints = restartPoints;
            }

            RestartPoint rp = new RestartPoint(name, bbs, msgId, restartPoints, PKrestartPoints);
            restartPoint.put(name, rp);
            break;
          }
        }
      }

      iterator = restartArea.iterator();

      while(iterator.hasNext()) {
        Pair<Territory, Map<Race, String>> ra = (Pair)iterator.next();
        Map<Race, RestartPoint> restarts = new HashMap();
        Iterator var24 = ((Map)ra.getValue()).entrySet().iterator();

        while(var24.hasNext()) {
          Entry<Race, String> e = (Entry)var24.next();
          RestartPoint rp = (RestartPoint)restartPoint.get(e.getValue());
          if (rp == null) {
            throw new RuntimeException("RestartPointParser: restart_loc not found : " + (String)e.getValue() + "!");
          }

          restarts.put(e.getKey(), rp);

          try {
            ((MapRegionManager)this.getHolder()).addRegionData(new RestartArea((Territory)ra.getKey(), restarts));
          } catch (Exception var20) {
            System.out.println("Cant add restart area \"" + (String)e.getValue() + "\"");
            var20.printStackTrace();
          }
        }
      }

      return;
    }
  }
}
