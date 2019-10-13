//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.commons.geometry.Polygon;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.mapregion.DomainArea;
import org.dom4j.Element;

public class DomainParser extends AbstractFileParser<MapRegionManager> {
  private static final DomainParser _instance = new DomainParser();

  public static DomainParser getInstance() {
    return _instance;
  }

  protected DomainParser() {
    super(MapRegionManager.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/mapregion/domains.xml");
  }

  public String getDTDFileName() {
    return "domains.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator();

    while(true) {
      Element listElement;
      do {
        if (!iterator.hasNext()) {
          return;
        }

        listElement = (Element)iterator.next();
      } while(!"domain".equals(listElement.getName()));

      int id = Integer.parseInt(listElement.attributeValue("id"));
      Territory territory = null;
      Iterator i = listElement.elementIterator();

      while(i.hasNext()) {
        Element n = (Element)i.next();
        if ("polygon".equalsIgnoreCase(n.getName())) {
          Polygon shape = ZoneParser.parsePolygon(n);
          if (!shape.validate()) {
            this.error("DomainParser: invalid territory data : " + shape + "!");
          }

          if (territory == null) {
            territory = new Territory();
          }

          territory.add(shape);
        }
      }

      if (territory == null) {
        throw new RuntimeException("DomainParser: empty territory!");
      }

      ((MapRegionManager)this.getHolder()).addRegionData(new DomainArea(id, territory));
    }
  }
}
