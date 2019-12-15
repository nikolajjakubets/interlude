//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills;

import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.templates.SkillEnchant;
import l2.gameserver.templates.StatsSet;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
/** @deprecated */
@Deprecated
public final class DocumentSkill extends DocumentBase {
  private static final String SKILL_ENCHANT_NODE_NAME = "enchant";
  private static final Comparator<Integer> INTEGER_KEY_ASC_COMPARATOR = new Comparator<Integer>() {
    public int compare(Integer o1, Integer o2) {
      return o1 - o2;
    }
  };
  protected Map<String, Map<Integer, Object>> tables = new LinkedHashMap();
  private DocumentSkill.SkillLoad currentSkill = null;
  private Set<String> usedTables = new HashSet<>();
  private List<Skill> skillsInFile = new LinkedList();

  DocumentSkill(File file) {
    super(file);
  }

  protected void resetTable() {
    if (!this.usedTables.isEmpty()) {

      for (String table : this.tables.keySet()) {
        if (!this.usedTables.contains(table)) {
          log.warn("Unused table " + table + " for skill " + this.currentSkill.id);
        }
      }
    }

    this.usedTables.clear();
    this.tables.clear();
  }

  private void setCurrentSkill(DocumentSkill.SkillLoad skill) {
    this.currentSkill = skill;
  }

  protected List<Skill> getSkills() {
    return this.skillsInFile;
  }

  protected Object getTableValue(String name) {
    Map<Integer, Object> values = (Map) this.tables.get(name);
    if (values == null) {
      log.error("No table " + name + " for skill " + this.currentSkill.id);
      return 0;
    } else if (!values.containsKey(this.currentSkill.currentLevel)) {
      log.error("No value in table " + name + " for skill " + this.currentSkill.id + " at level " + this.currentSkill.currentLevel);
      return 0;
    } else {
      this.usedTables.add(name);
      return values.get(this.currentSkill.currentLevel);
    }
  }

  protected Object getTableValue(String name, int level) {
    Map<Integer, Object> values = (Map) this.tables.get(name);
    if (values == null) {
      log.error("No table " + name + " for skill " + this.currentSkill.id);
      return 0;
    } else if (!values.containsKey(level)) {
      log.error("No value in table " + name + " for skill " + this.currentSkill.id + " at level " + level);
      return 0;
    } else {
      this.usedTables.add(name);
      return values.get(level);
    }
  }

  protected void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("skill".equalsIgnoreCase(d.getNodeName())) {
            this.parseSkill(d);
            this.skillsInFile.addAll(this.currentSkill.skills);
            this.resetTable();
          }
        }
      } else if ("skill".equalsIgnoreCase(n.getNodeName())) {
        this.parseSkill(n);
        this.skillsInFile.addAll(this.currentSkill.skills);
      }
    }

  }

  private void loadTable(Node tableNode, int skillLevelOffset, int levels) {
    NamedNodeMap tableNodeAttrs = tableNode.getAttributes();
    String tableName = tableNodeAttrs.getNamedItem("name").getNodeValue();
    Object[] tableContent = this.fillTableToSize(this.parseTable(tableNode), levels);
    Map<Integer, Object> globalTableLevels = (Map) this.tables.get(tableName);
    if (globalTableLevels == null) {
      globalTableLevels = new TreeMap(INTEGER_KEY_ASC_COMPARATOR);
      this.tables.put(tableName, globalTableLevels);
    }

    for (int tblContIdx = 0; tblContIdx < tableContent.length; ++tblContIdx) {
      int skillLvl = skillLevelOffset + tblContIdx;
      if (((Map) globalTableLevels).containsKey(skillLvl)) {
        log.error("Duplicate skill level " + skillLvl + " in table " + tableName + " in skill " + this.currentSkill.id);
        return;
      }

      ((Map) globalTableLevels).put(skillLvl, tableContent[tblContIdx]);
    }

  }

  protected void parseSkill(Node n) {
    NamedNodeMap attrs = n.getAttributes();
    int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
    String skillName = attrs.getNamedItem("name").getNodeValue();
    int skillBaseLevels = Integer.parseInt(attrs.getNamedItem("levels").getNodeValue());
    this.setCurrentSkill(new DocumentSkill.SkillLoad(skillId, skillName));
    ArrayList skillLevelsList = new ArrayList<>();

    try {
      for (int skillLvl = 1; skillLvl <= skillBaseLevels; ++skillLvl) {
        skillLevelsList.add(skillLvl);
      }

      Node skillRootNode = n.cloneNode(true);
      int skillChildNodeIdx = 0;

      Node skillNode;
      String skillNodeName;
      for (int skillChildNodesLen = skillRootNode.getChildNodes().getLength(); skillChildNodeIdx < skillChildNodesLen; ++skillChildNodeIdx) {
        skillNode = skillRootNode.getChildNodes().item(skillChildNodeIdx);
        skillNodeName = skillNode.getNodeName();
        if (skillNodeName.startsWith("enchant")) {
          int skillCurrEnchantRoute;
          try {
            skillCurrEnchantRoute = Integer.parseInt(skillNodeName.substring("enchant".length()));
          } catch (NumberFormatException var19) {
            log.error("Wrong enchant " + skillNodeName + " in skill " + skillId);
            break;
          }

          int skillEnchRouteFirstSkillLevel = EnchantSkillHolder.getInstance().getFirstSkillLevelOf(skillId, skillCurrEnchantRoute);
          Node skillEnchLevelsNode = skillNode.getAttributes().getNamedItem("levels");
          int skillEnchantLevels;
          if (skillEnchLevelsNode != null) {
            skillEnchantLevels = Integer.parseInt(skillEnchLevelsNode.getNodeValue());
          } else {
            log.warn("Skill " + skillId + " have no enchant levels in route " + skillCurrEnchantRoute + ".");
            skillEnchantLevels = EnchantSkillHolder.getInstance().getMaxEnchantLevelOf(skillId);
          }

          int skillRouteMaxEnchantLevel = EnchantSkillHolder.getInstance().getMaxEnchantLevelOf(skillId);
          if (skillEnchantLevels != skillRouteMaxEnchantLevel) {
            log.warn("Unknown enchant levels " + skillEnchantLevels + " for skill " + skillId + ". Actual " + skillRouteMaxEnchantLevel);
          }

          for (int skillEnchantLevel = 1; skillEnchantLevel <= skillEnchantLevels; ++skillEnchantLevel) {
            SkillEnchant skillEnchant = EnchantSkillHolder.getInstance().getSkillEnchant(skillId, skillCurrEnchantRoute, skillEnchantLevel);
            if (skillEnchant == null) {
              log.error("No enchant level " + skillEnchantLevel + " in route " + skillCurrEnchantRoute + " for skill " + skillId);
              break;
            }

            skillLevelsList.add(skillEnchant.getSkillLevel());
          }

          for (Node skillEnchNode = skillNode.getFirstChild(); skillEnchNode != null; skillEnchNode = skillEnchNode.getNextSibling()) {
            if ("table".equalsIgnoreCase(skillEnchNode.getNodeName())) {
              this.loadTable(skillEnchNode, skillEnchRouteFirstSkillLevel, skillEnchantLevels);
            } else if (skillEnchNode.getNodeType() == 1) {
              log.error("Unknown element of enchant \"" + skillEnchNode.getNodeName() + "\" in skill " + skillId);
            }
          }
        }
      }

      for (Node skillTableNode = n.getFirstChild(); skillTableNode != null; skillTableNode = skillTableNode.getNextSibling()) {
        if ("table".equalsIgnoreCase(skillTableNode.getNodeName())) {
          this.loadTable(skillTableNode, 1, skillBaseLevels);
        }
      }

      Iterator var23 = this.tables.entrySet().iterator();

      while (var23.hasNext()) {
        Entry<String, Map<Integer, Object>> tableEntry = (Entry) var23.next();
        Map<Integer, Object> table = (Map) tableEntry.getValue();
        Object baseEnchantValue = table.get(skillBaseLevels);
        Iterator var33 = skillLevelsList.iterator();

        while (var33.hasNext()) {
          Integer skillLevel = (Integer) var33.next();
          if (skillLevel > skillBaseLevels && !table.containsKey(skillLevel)) {
            table.put(skillLevel, baseEnchantValue);
          }
        }
      }

      var23 = skillLevelsList.iterator();

      Integer skillLevel;
      while (var23.hasNext()) {
        skillLevel = (Integer) var23.next();
        StatsSet currLevelStatSet = new StatsSet();
        currLevelStatSet.set("skill_id", this.currentSkill.id);
        currLevelStatSet.set("level", skillLevel);
        currLevelStatSet.set("name", this.currentSkill.name);
        currLevelStatSet.set("base_level", skillBaseLevels);
        this.currentSkill.sets.put(skillLevel, currLevelStatSet);
      }

      var23 = skillLevelsList.iterator();

      while (var23.hasNext()) {
        skillLevel = (Integer) var23.next();

        for (Node skillSetNode = n.getFirstChild(); skillSetNode != null; skillSetNode = skillSetNode.getNextSibling()) {
          if ("set".equalsIgnoreCase(skillSetNode.getNodeName())) {
            StatsSet skillCurrLevelSet = (StatsSet) this.currentSkill.sets.get(skillLevel);
            this.currentSkill.currentLevel = skillLevel;
            this.parseBeanSet(skillSetNode, skillCurrLevelSet, skillLevel);
          }
        }
      }

      var23 = this.currentSkill.sets.values().iterator();

      Skill currSkill;
      while (var23.hasNext()) {
        StatsSet currStatsSet = (StatsSet) var23.next();
        currSkill = ((SkillType) currStatsSet.getEnum("skillType", SkillType.class)).makeSkill(currStatsSet);
        this.currentSkill.currentSkills.put(currSkill.getLevel(), currSkill);
      }

      var23 = skillLevelsList.iterator();

      while (var23.hasNext()) {
        skillLevel = (Integer) var23.next();
        this.currentSkill.currentLevel = skillLevel;
        currSkill = (Skill) this.currentSkill.currentSkills.get(skillLevel);
        if (currSkill == null) {
          log.error("Undefined skill id " + skillId + " level " + skillLevel);
          return;
        }

        currSkill.setDisplayLevel(skillLevel);

        for (skillNode = n.getFirstChild(); skillNode != null; skillNode = skillNode.getNextSibling()) {
          skillNodeName = skillNode.getNodeName();
          if ("cond".equalsIgnoreCase(skillNodeName)) {
            Condition condition = this.parseCondition(skillNode.getFirstChild());
            if (condition != null) {
              Node sysMsgIdAttr = skillNode.getAttributes().getNamedItem("msgId");
              if (sysMsgIdAttr != null) {
                int sysMsgId = this.parseNumber(sysMsgIdAttr.getNodeValue()).intValue();
                condition.setSystemMsg(sysMsgId);
              }

              currSkill.attach(condition);
            }
          } else if ("for".equalsIgnoreCase(skillNodeName)) {
            this.parseTemplate(skillNode, currSkill);
          } else if ("triggers".equalsIgnoreCase(skillNodeName)) {
            this.parseTrigger(skillNode, currSkill);
          }
        }
      }

      this.currentSkill.skills.addAll(this.currentSkill.currentSkills.values());
    } catch (Exception var20) {
      log.error("Error loading skill " + skillId, var20);
    }

  }

  protected Object[] parseTable(Node n) {
    NamedNodeMap attrs = n.getAttributes();
    String name = attrs.getNamedItem("name").getNodeValue();
    if (name.charAt(0) != '#') {
      throw new IllegalArgumentException("Table name must start with #");
    } else {
      StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
      ArrayList array = new ArrayList<>();

      while (data.hasMoreTokens()) {
        array.add(data.nextToken());
      }

      Object[] res = array.toArray(new Object[array.size()]);
      return res;
    }
  }

  private Object[] fillTableToSize(Object[] table, int size) {
    if (table.length < size) {
      Object[] ret = new Object[size];
      System.arraycopy(table, 0, ret, 0, table.length);
      table = ret;
    }

    for (int j = 1; j < size; ++j) {
      if (table[j] == null) {
        table[j] = table[j - 1];
      }
    }

    return table;
  }

  public static class SkillLoad {
    public final int id;
    public final String name;
    public final Map<Integer, StatsSet> sets;
    public final List<Skill> skills;
    public final Map<Integer, Skill> currentSkills;
    public int currentLevel;

    public SkillLoad(int id_, String name_) {
      this.id = id_;
      this.name = name_;
      this.sets = new TreeMap<>(DocumentSkill.INTEGER_KEY_ASC_COMPARATOR);
      this.skills = new ArrayList<>();
      this.currentSkills = new TreeMap<>(DocumentSkill.INTEGER_KEY_ASC_COMPARATOR);
    }
  }
}
