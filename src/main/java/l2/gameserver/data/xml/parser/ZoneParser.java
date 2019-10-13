//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Circle;
import l2.commons.geometry.Polygon;
import l2.commons.geometry.Rectangle;
import l2.commons.geometry.Shape;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.model.Territory;
import l2.gameserver.model.World;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public class ZoneParser extends AbstractDirParser<ZoneHolder> {
  private static final ZoneParser _instance = new ZoneParser();

  public static ZoneParser getInstance() {
    return _instance;
  }

  protected ZoneParser() {
    super(ZoneHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/zone/");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "zone.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator();

    while(true) {
      StatsSet zoneDat;
      Element zoneElement;
      do {
        if (!iterator.hasNext()) {
          return;
        }

        zoneDat = new StatsSet();
        zoneElement = (Element)iterator.next();
      } while(!"zone".equals(zoneElement.getName()));

      zoneDat.set("name", zoneElement.attribute("name").getValue());
      zoneDat.set("type", zoneElement.attribute("type").getValue());
      Territory territory = null;
      Iterator i = zoneElement.elementIterator();

      while(true) {
        while(i.hasNext()) {
          Element n = (Element)i.next();
          if ("set".equals(n.getName())) {
            zoneDat.set(n.attributeValue("name"), n.attributeValue("val"));
          } else {
            ArrayList PKrestartPoints;
            Iterator ii;
            Element d;
            Location loc;
            if ("restart_point".equals(n.getName())) {
              PKrestartPoints = new ArrayList();
              ii = n.elementIterator();

              while(ii.hasNext()) {
                d = (Element)ii.next();
                if ("coords".equalsIgnoreCase(d.getName())) {
                  loc = Location.parseLoc(d.attribute("loc").getValue());
                  PKrestartPoints.add(loc);
                }
              }

              zoneDat.set("restart_points", PKrestartPoints);
            } else if (!"PKrestart_point".equals(n.getName())) {
              boolean isShape;
              if (!(isShape = "rectangle".equalsIgnoreCase(n.getName())) && !"banned_rectangle".equalsIgnoreCase(n.getName())) {
                if (!(isShape = "circle".equalsIgnoreCase(n.getName())) && !"banned_cicrcle".equalsIgnoreCase(n.getName())) {
                  if ((isShape = "polygon".equalsIgnoreCase(n.getName())) || "banned_polygon".equalsIgnoreCase(n.getName())) {
                    Polygon shape = parsePolygon(n);
                    if (!shape.validate()) {
                      this.error("ZoneParser: invalid territory data : " + shape + ", zone: " + zoneDat.getString("name") + "!");
                    }

                    if (territory == null) {
                      territory = new Territory();
                      zoneDat.set("territory", territory);
                    }

                    if (isShape) {
                      territory.add(shape);
                    } else {
                      territory.addBanned(shape);
                    }
                  }
                } else {
                  Shape shape = parseCircle(n);
                  if (territory == null) {
                    territory = new Territory();
                    zoneDat.set("territory", territory);
                  }

                  if (isShape) {
                    territory.add(shape);
                  } else {
                    territory.addBanned(shape);
                  }
                }
              } else {
                Shape shape = parseRectangle(n);
                if (territory == null) {
                  territory = new Territory();
                  zoneDat.set("territory", territory);
                }

                if (isShape) {
                  territory.add(shape);
                } else {
                  territory.addBanned(shape);
                }
              }
            } else {
              PKrestartPoints = new ArrayList();
              ii = n.elementIterator();

              while(ii.hasNext()) {
                d = (Element)ii.next();
                if ("coords".equalsIgnoreCase(d.getName())) {
                  loc = Location.parseLoc(d.attribute("loc").getValue());
                  PKrestartPoints.add(loc);
                }
              }

              zoneDat.set("PKrestart_points", PKrestartPoints);
            }
          }
        }

        if (territory == null || territory.getTerritories().isEmpty()) {
          this.error("Empty territory for zone: " + zoneDat.get("name"));
        }

        ZoneTemplate template = new ZoneTemplate(zoneDat);
        ((ZoneHolder)this.getHolder()).addTemplate(template);
        break;
      }
    }
  }

  public static Rectangle parseRectangle(Element n) throws Exception {
    int zmin = World.MAP_MIN_Z;
    int zmax = World.MAP_MAX_Z;
    Iterator<Element> i = n.elementIterator();
    Element d = (Element)i.next();
    String[] coord = d.attributeValue("loc").split("[\\s,;]+");
    int x1 = Integer.parseInt(coord[0]);
    int y1 = Integer.parseInt(coord[1]);
    if (coord.length > 2) {
      zmin = Integer.parseInt(coord[2]);
      zmax = Integer.parseInt(coord[3]);
    }

    d = (Element)i.next();
    coord = d.attributeValue("loc").split("[\\s,;]+");
    int x2 = Integer.parseInt(coord[0]);
    int y2 = Integer.parseInt(coord[1]);
    if (coord.length > 2) {
      zmin = Integer.parseInt(coord[2]);
      zmax = Integer.parseInt(coord[3]);
    }

    Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
    rectangle.setZmin(zmin);
    rectangle.setZmax(zmax);
    return rectangle;
  }

  public static Polygon parsePolygon(Element shape) throws Exception {
    Polygon poly = new Polygon();
    Iterator i = shape.elementIterator();

    while(i.hasNext()) {
      Element d = (Element)i.next();
      if ("coords".equals(d.getName())) {
        String[] coord = d.attributeValue("loc").split("[\\s,;]+");
        if (coord.length < 4) {
          poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
        } else {
          poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(Integer.parseInt(coord[2])).setZmax(Integer.parseInt(coord[3]));
        }
      }
    }

    return poly;
  }

  public static Circle parseCircle(Element shape) throws Exception {
    String[] coord = shape.attribute("loc").getValue().split("[\\s,;]+");
    Circle circle;
    if (coord.length < 5) {
      circle = (new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2]))).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
    } else {
      circle = (new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2]))).setZmin(Integer.parseInt(coord[3])).setZmax(Integer.parseInt(coord[4]));
    }

    return circle;
  }
}
