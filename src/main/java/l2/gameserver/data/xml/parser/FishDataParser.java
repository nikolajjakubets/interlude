//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import l2.commons.collections.MultiValueSet;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.FishDataHolder;
import l2.gameserver.templates.item.support.FishGroup;
import l2.gameserver.templates.item.support.FishTemplate;
import l2.gameserver.templates.item.support.LureTemplate;
import l2.gameserver.templates.item.support.LureType;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class FishDataParser extends AbstractFileParser<FishDataHolder> {
  private static final FishDataParser _instance = new FishDataParser();

  public static FishDataParser getInstance() {
    return _instance;
  }

  private FishDataParser() {
    super(FishDataHolder.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/fishdata.xml");
  }

  public String getDTDFileName() {
    return "fishdata.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator();

    while(true) {
      while(iterator.hasNext()) {
        Element e = (Element)iterator.next();
        Iterator forLureIterator;
        MultiValueSet map;
        Attribute attribute;
        if ("fish".equals(e.getName())) {
          map = new MultiValueSet();
          forLureIterator = e.attributeIterator();

          while(forLureIterator.hasNext()) {
            attribute = (Attribute)forLureIterator.next();
            map.put(attribute.getName(), attribute.getValue());
          }

          ((FishDataHolder)this.getHolder()).addFish(new FishTemplate(map));
        } else if ("lure".equals(e.getName())) {
          map = new MultiValueSet();
          forLureIterator = e.attributeIterator();

          while(forLureIterator.hasNext()) {
            attribute = (Attribute)forLureIterator.next();
            map.put(attribute.getName(), attribute.getValue());
          }

          Map<FishGroup, Integer> chances = new HashMap();
          Iterator elementIterator = e.elementIterator();

          while(elementIterator.hasNext()) {
            Element chanceElement = (Element)elementIterator.next();
            chances.put(FishGroup.valueOf(chanceElement.attributeValue("type")), Integer.parseInt(chanceElement.attributeValue("value")));
          }

          map.put("chances", chances);
          ((FishDataHolder)this.getHolder()).addLure(new LureTemplate(map));
        } else if ("distribution".equals(e.getName())) {
          int id = Integer.parseInt(e.attributeValue("id"));
          forLureIterator = e.elementIterator();

          while(forLureIterator.hasNext()) {
            Element forLureElement = (Element)forLureIterator.next();
            LureType lureType = LureType.valueOf(forLureElement.attributeValue("type"));
            Map<FishGroup, Integer> chances = new HashMap();
            Iterator chanceIterator = forLureElement.elementIterator();

            while(chanceIterator.hasNext()) {
              Element chanceElement = (Element)chanceIterator.next();
              chances.put(FishGroup.valueOf(chanceElement.attributeValue("type")), Integer.parseInt(chanceElement.attributeValue("value")));
            }

            ((FishDataHolder)this.getHolder()).addDistribution(id, lureType, chances);
          }
        }
      }

      return;
    }
  }
}
