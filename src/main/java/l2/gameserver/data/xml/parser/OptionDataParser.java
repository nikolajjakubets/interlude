//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.OptionDataTemplate;
import org.dom4j.Element;

public final class OptionDataParser extends StatParser<OptionDataHolder> {
  private static final OptionDataParser _instance = new OptionDataParser();

  public static OptionDataParser getInstance() {
    return _instance;
  }

  protected OptionDataParser() {
    super(OptionDataHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/optiondata");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "optiondata.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator itemIterator = rootElement.elementIterator();

    while(itemIterator.hasNext()) {
      Element optionDataElement = (Element)itemIterator.next();
      OptionDataTemplate template = new OptionDataTemplate(Integer.parseInt(optionDataElement.attributeValue("id")));
      Iterator subIterator = optionDataElement.elementIterator();

      while(subIterator.hasNext()) {
        Element subElement = (Element)subIterator.next();
        String subName = subElement.getName();
        if (subName.equalsIgnoreCase("for")) {
          this.parseFor(subElement, template);
        } else {
          int id;
          int level;
          if (subName.equalsIgnoreCase("trigger")) {
            id = this.parseNumber(subElement.attributeValue("id")).intValue();
            level = this.parseNumber(subElement.attributeValue("level")).intValue();
            TriggerType t = TriggerType.valueOf(subElement.attributeValue("type"));
            double chance = this.parseNumber(subElement.attributeValue("chance")).doubleValue();
            TriggerInfo trigger = new TriggerInfo(id, level, t, chance);
            template.addTrigger(trigger);
          } else if (subName.equalsIgnoreCase("skill")) {
            id = Integer.parseInt(subElement.attributeValue("id"));
            level = Integer.parseInt(subElement.attributeValue("level"));
            Skill skill = SkillTable.getInstance().getInfo(id, level);
            if (skill != null) {
              template.addSkill(skill);
            } else {
              this.info("Skill not found(" + id + "," + level + ") for option data:" + template.getId() + "; file:" + this.getCurrentFileName());
            }
          }
        }
      }

      ((OptionDataHolder)this.getHolder()).addTemplate(template);
    }

  }

  protected Object getTableValue(String name) {
    return null;
  }
}
