//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ArmorSetsHolder;
import l2.gameserver.model.ArmorSet;
import org.dom4j.Element;

public final class ArmorSetsParser extends AbstractFileParser<ArmorSetsHolder> {
  private static final ArmorSetsParser _instance = new ArmorSetsParser();

  public static ArmorSetsParser getInstance() {
    return _instance;
  }

  private ArmorSetsParser() {
    super(ArmorSetsHolder.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/armor_sets.xml");
  }

  public String getDTDFileName() {
    return "armor_sets.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    String[] chest;
    String[] legs;
    String[] head;
    String[] gloves;
    String[] feet;
    String[] skills;
    String[] shield;
    String[] shield_skills;
    String[] enchant6skills;
    int id;
    for(Iterator iterator = rootElement.elementIterator("set"); iterator.hasNext(); ((ArmorSetsHolder)this.getHolder()).addArmorSet(new ArmorSet(id, chest, legs, head, gloves, feet, skills, shield, shield_skills, enchant6skills))) {
      chest = null;
      legs = null;
      head = null;
      gloves = null;
      feet = null;
      skills = null;
      shield = null;
      shield_skills = null;
      enchant6skills = null;
      Element element = (Element)iterator.next();
      id = Integer.parseInt(element.attributeValue("id"));
      if (element.attributeValue("chest") != null) {
        chest = element.attributeValue("chest").split(";");
      }

      if (element.attributeValue("legs") != null) {
        legs = element.attributeValue("legs").split(";");
      }

      if (element.attributeValue("head") != null) {
        head = element.attributeValue("head").split(";");
      }

      if (element.attributeValue("gloves") != null) {
        gloves = element.attributeValue("gloves").split(";");
      }

      if (element.attributeValue("feet") != null) {
        feet = element.attributeValue("feet").split(";");
      }

      if (element.attributeValue("skills") != null) {
        skills = element.attributeValue("skills").split(";");
      }

      if (element.attributeValue("shield") != null) {
        shield = element.attributeValue("shield").split(";");
      }

      if (element.attributeValue("shield_skills") != null) {
        shield_skills = element.attributeValue("shield_skills").split(";");
      }

      if (element.attributeValue("enchant6skills") != null) {
        enchant6skills = element.attributeValue("enchant6skills").split(";");
      }
    }

  }
}
