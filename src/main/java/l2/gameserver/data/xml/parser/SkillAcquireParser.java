//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import gnu.trove.TIntObjectHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.ClassType2;
import org.dom4j.Element;

public final class SkillAcquireParser extends AbstractDirParser<SkillAcquireHolder> {
  private static final SkillAcquireParser _instance = new SkillAcquireParser();

  public static SkillAcquireParser getInstance() {
    return _instance;
  }

  protected SkillAcquireParser() {
    super(SkillAcquireHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/skill_tree/");
  }

  public boolean isIgnored(File b) {
    return false;
  }

  public String getDTDFileName() {
    return "tree.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator("pledge_skill_tree");

    while(iterator.hasNext()) {
      ((SkillAcquireHolder)this.getHolder()).addAllPledgeLearns(this.parseSkillLearn((Element)iterator.next()));
    }

    iterator = rootElement.elementIterator("fishing_skill_tree");

    while(iterator.hasNext()) {
      Element nxt = (Element)iterator.next();
      Iterator classIterator = nxt.elementIterator("race");

      while(classIterator.hasNext()) {
        Element classElement = (Element)classIterator.next();
        int race = Integer.parseInt(classElement.attributeValue("id"));
        List<SkillLearn> learns = this.parseSkillLearn(classElement);
        ((SkillAcquireHolder)this.getHolder()).addAllFishingLearns(race, learns);
      }
    }

    iterator = rootElement.elementIterator("normal_skill_tree");

    while(iterator.hasNext()) {
      TIntObjectHashMap<List<SkillLearn>> map = new TIntObjectHashMap();
      Element nxt = (Element)iterator.next();
      Iterator classIterator = nxt.elementIterator("class");

      while(classIterator.hasNext()) {
        Element classElement = (Element)classIterator.next();
        int classId = Integer.parseInt(classElement.attributeValue("id"));
        List<SkillLearn> learns = this.parseSkillLearn(classElement);
        map.put(classId, learns);
      }

      ((SkillAcquireHolder)this.getHolder()).addAllNormalSkillLearns(map);
    }

  }

  private List<SkillLearn> parseSkillLearn(Element tree) {
    List<SkillLearn> skillLearns = new ArrayList();
    Iterator iterator = tree.elementIterator("skill");

    while(iterator.hasNext()) {
      Element element = (Element)iterator.next();
      int id = Integer.parseInt(element.attributeValue("id"));
      int level = Integer.parseInt(element.attributeValue("level"));
      int cost = element.attributeValue("cost") == null ? 0 : Integer.parseInt(element.attributeValue("cost"));
      int min_level = Integer.parseInt(element.attributeValue("min_level"));
      int item_id = element.attributeValue("item_id") == null ? 0 : Integer.parseInt(element.attributeValue("item_id"));
      long item_count = element.attributeValue("item_count") == null ? 1L : Long.parseLong(element.attributeValue("item_count"));
      boolean clicked = element.attributeValue("clicked") != null && Boolean.parseBoolean(element.attributeValue("clicked"));
      boolean autoLearn = Boolean.parseBoolean(element.attributeValue("auto_learn", "true"));
      ClassType2 classtype2 = ClassType2.valueOf(element.attributeValue("classtype2", "None"));
      skillLearns.add(new SkillLearn(id, level, min_level, cost * Config.SKILL_COST_RATE, item_id, item_count, clicked, classtype2, autoLearn));
    }

    return skillLearns;
  }
}
