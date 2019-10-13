//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import gnu.trove.TIntIntHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.CubicHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.CubicTemplate;
import l2.gameserver.templates.CubicTemplate.ActionType;
import l2.gameserver.templates.CubicTemplate.SkillInfo;
import org.dom4j.Element;

public final class CubicParser extends AbstractFileParser<CubicHolder> {
  private static CubicParser _instance = new CubicParser();

  public static CubicParser getInstance() {
    return _instance;
  }

  protected CubicParser() {
    super(CubicHolder.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/cubics.xml");
  }

  public String getDTDFileName() {
    return "cubics.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator();

    while(iterator.hasNext()) {
      Element cubicElement = (Element)iterator.next();
      int id = Integer.parseInt(cubicElement.attributeValue("id"));
      int level = Integer.parseInt(cubicElement.attributeValue("level"));
      int delay = Integer.parseInt(cubicElement.attributeValue("delay"));
      CubicTemplate template = new CubicTemplate(id, level, delay);
      ((CubicHolder)this.getHolder()).addCubicTemplate(template);
      Iterator skillsIterator = cubicElement.elementIterator();

      while(skillsIterator.hasNext()) {
        Element skillsElement = (Element)skillsIterator.next();
        int chance = Integer.parseInt(skillsElement.attributeValue("chance"));
        List<SkillInfo> skills = new ArrayList(1);
        Iterator skillIterator = skillsElement.elementIterator();

        while(skillIterator.hasNext()) {
          Element skillElement = (Element)skillIterator.next();
          int id2 = Integer.parseInt(skillElement.attributeValue("id"));
          int level2 = Integer.parseInt(skillElement.attributeValue("level"));
          String val = skillElement.attributeValue("chance");
          int chance2 = val == null ? 0 : Integer.parseInt(val);
          boolean canAttackDoor = Boolean.parseBoolean(skillElement.attributeValue("can_attack_door"));
          val = skillElement.attributeValue("min_hp");
          int minHp = val == null ? 0 : Integer.parseInt(val);
          val = skillElement.attributeValue("min_hp_per");
          int minHpPer = val == null ? 0 : Integer.parseInt(val);
          ActionType type = ActionType.valueOf(skillElement.attributeValue("action_type"));
          TIntIntHashMap set = new TIntIntHashMap();
          Iterator chanceIterator = skillElement.elementIterator();

          while(chanceIterator.hasNext()) {
            Element chanceElement = (Element)chanceIterator.next();
            int min = Integer.parseInt(chanceElement.attributeValue("min"));
            int max = Integer.parseInt(chanceElement.attributeValue("max"));
            int value = Integer.parseInt(chanceElement.attributeValue("value"));

            for(int i = min; i <= max; ++i) {
              set.put(i, value);
            }
          }

          if (chance2 == 0 && set.isEmpty()) {
            this.warn("Wrong skill chance. Cubic: " + id + "/" + level);
          }

          Skill skill = SkillTable.getInstance().getInfo(id2, level2);
          if (skill != null) {
            skill.setCubicSkill(true);
            skills.add(new SkillInfo(skill, chance2, type, canAttackDoor, minHp, minHpPer, set));
          }
        }

        template.putSkills(chance, skills);
      }
    }

  }
}
