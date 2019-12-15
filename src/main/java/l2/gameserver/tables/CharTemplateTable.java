//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import l2.commons.data.xml.helpers.SimpleDTDEntityResolver;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.StatsSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class CharTemplateTable {
  private static CharTemplateTable _instance;
  private Map<Integer, PlayerTemplate> _templates = new HashMap<>();
  private Map<ClassId, List<ShortCut>> _shortCuts;

  public static CharTemplateTable getInstance() {
    if (_instance == null) {
      _instance = new CharTemplateTable();
    }

    return _instance;
  }

  private CharTemplateTable() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM class_list, char_templates WHERE class_list.id = char_templates.classId ORDER BY class_list.id");
      rset = statement.executeQuery();

      while (rset.next()) {
        StatsSet set = new StatsSet();
        ClassId classId = ClassId.VALUES[rset.getInt("class_list.id")];
        set.set("classId", rset.getInt("class_list.id"));
        set.set("className", rset.getString("char_templates.className"));
        set.set("raceId", rset.getInt("char_templates.RaceId"));
        set.set("baseSTR", rset.getInt("char_templates.STR"));
        set.set("baseCON", rset.getInt("char_templates.CON"));
        set.set("baseDEX", rset.getInt("char_templates.DEX"));
        set.set("baseINT", rset.getInt("char_templates._INT"));
        set.set("baseWIT", rset.getInt("char_templates.WIT"));
        set.set("baseMEN", rset.getInt("char_templates.MEN"));
        set.set("baseHpMax", 0);
        set.set("baseMpMax", 0);
        set.set("baseCpMax", 0);
        set.set("baseHpReg", 0.01D);
        set.set("baseCpReg", 0.01D);
        set.set("baseMpReg", 0.01D);
        set.set("basePAtk", rset.getInt("char_templates.p_atk"));
        set.set("basePDef", rset.getInt("char_templates.p_def"));
        set.set("baseMAtk", rset.getInt("char_templates.m_atk"));
        set.set("baseMDef", 41);
        set.set("basePAtkSpd", rset.getInt("char_templates.p_spd"));
        set.set("baseMAtkSpd", classId.isMage() ? Config.BASE_MAGE_CAST_SPEED : Config.BASE_WARRIOR_CAST_SPEED);
        set.set("baseCritRate", rset.getInt("char_templates.critical"));
        set.set("baseWalkSpd", rset.getInt("char_templates.walk_spd"));
        set.set("baseRunSpd", rset.getInt("char_templates.run_spd"));
        set.set("baseShldDef", 0);
        set.set("baseShldRate", 0);
        if (set.getInteger("raceId") == 3) {
          set.set("baseAtkRange", 25);
        } else {
          set.set("baseAtkRange", 20);
        }

        set.set("baseExp", Experience.getExpForLevel(rset.getInt("char_templates.level")));
        set.set("spawnX", rset.getInt("char_templates.x"));
        set.set("spawnY", rset.getInt("char_templates.y"));
        set.set("spawnZ", rset.getInt("char_templates.z"));
        set.set("isMale", true);
        set.set("collision_radius", rset.getDouble("char_templates.m_col_r"));
        set.set("collision_height", rset.getDouble("char_templates.m_col_h"));
        PlayerTemplate ct = new PlayerTemplate(set);

        int x;
        for (x = 1; x < 15; ++x) {
          if (rset.getInt("char_templates.items" + x) != 0) {
            ct.addItem(rset.getInt("char_templates.items" + x));
          }
        }

        this._templates.put(ct.classId.getId(), ct);
        set.set("isMale", false);
        set.set("collision_radius", rset.getDouble("char_templates.f_col_r"));
        set.set("collision_height", rset.getDouble("char_templates.f_col_h"));
        ct = new PlayerTemplate(set);

        for (x = 1; x < 15; ++x) {
          int itemId = rset.getInt("char_templates.items" + x);
          if (itemId != 0) {
            ct.addItem(itemId);
          }
        }

        this._templates.put(ct.classId.getId() | 256, ct);
      }
    } catch (Exception e) {
      log.error("CharTemplateTable: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    log.info("CharTemplateTable: Loaded " + this._templates.size() + " Character Templates.");
    this._shortCuts = parseShortCuts(new File(Config.DATAPACK_ROOT, "data/character_shortcuts.xml"));
  }

  public PlayerTemplate getTemplate(ClassId classId, boolean female) {
    return this.getTemplate(classId.getId(), female);
  }

  public PlayerTemplate getTemplate(int classId, boolean female) {
    int key = classId;
    if (female) {
      key = classId | 256;
    }

    return this._templates.get(key);
  }

  public List<ShortCut> getShortCuts(ClassId classId) {
    List<ShortCut> result = this._shortCuts.get(classId);
    return result == null ? Collections.emptyList() : result;
  }

  public List<ShortCut> getShortCuts(Player player) {
    return this.getShortCuts(player.getClassId());
  }

  private static Map<ClassId, List<ShortCut>> parseShortCuts(final File file) {
    Map<ClassId, List<ShortCut>> result = new HashMap<>();
    if (!file.exists()) {
      log.warn("File " + file.getAbsolutePath() + " not exists");
      return Collections.emptyMap();
    } else {
      SAXReader reader = new SAXReader();
      reader.setValidation(true);
      reader.setErrorHandler(new ErrorHandler() {
        public void warning(SAXParseException exception) throws SAXException {
          CharTemplateTable.log.warn("File: " + file.getName() + ":" + exception.getLineNumber() + " warning: " + exception.getMessage());
        }

        public void error(SAXParseException exception) throws SAXException {
          CharTemplateTable.log.error("File: " + file.getName() + ":" + exception.getLineNumber() + " error: " + exception.getMessage());
        }

        public void fatalError(SAXParseException exception) throws SAXException {
          CharTemplateTable.log.error("File: " + file.getName() + ":" + exception.getLineNumber() + " fatal: " + exception.getMessage());
        }
      });
      reader.setEntityResolver(new SimpleDTDEntityResolver(new File(file.getParentFile(), FilenameUtils.removeExtension(file.getName()) + ".dtd")));

      try (FileInputStream fis = new FileInputStream(file)) {
        Document document = reader.read(fis);
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.elementIterator();

        while (true) {
          while (true) {
            Element listElement;
            do {
              if (!iterator.hasNext()) {
                return result;
              }

              listElement = (Element) iterator.next();
            } while (!"shortcut".equals(listElement.getName()));

            ClassId classId = null;
            String classIdStr = listElement.attributeValue("classId");
            if (classIdStr != null) {
              classId = ClassId.valueOf(classIdStr);
            }

            int slot = Integer.parseInt(listElement.attributeValue("slot", "0"));
            int page = Integer.parseInt(listElement.attributeValue("page", "0"));
            String shortCutType = listElement.attributeValue("type");
            byte type;
            if (!"ITEM".equalsIgnoreCase(shortCutType) && !"TYPE_ITEM".equalsIgnoreCase(shortCutType)) {
              if (!"SKILL".equalsIgnoreCase(shortCutType) && !"TYPE_SKILL".equalsIgnoreCase(shortCutType)) {
                if (!"ACTION".equalsIgnoreCase(shortCutType) && !"TYPE_ACTION".equalsIgnoreCase(shortCutType)) {
                  throw new RuntimeException("Unknown short cut type");
                }

                type = 3;
              } else {
                type = 2;
              }
            } else {
              type = 1;
            }

            int id = Integer.parseInt(listElement.attributeValue("id", "0"));
            int level = Integer.parseInt(listElement.attributeValue("level", "-1"));
            int characterType = Integer.parseInt(listElement.attributeValue("characterType", "1"));
            ShortCut shortCut = new ShortCut(slot, page, type, id, level, characterType);
            if (classId == null) {
              ClassId[] var34 = ClassId.VALUES;
              int var19 = var34.length;

              for (ClassId cId : var34) {
                List<ShortCut> shortCuts = result.computeIfAbsent(cId, k -> new ArrayList<>());

                shortCuts.add(shortCut);
              }
            } else {
              List<ShortCut> shortCuts = result.computeIfAbsent(classId, k -> new ArrayList<>());

              shortCuts.add(shortCut);
            }
          }
        }
      } catch (Exception var31) {
        log.warn("Exception: " + var31, var31);
      }

      return result;
    }
  }
}
