//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills;

import l2.gameserver.model.Skill;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.*;
import l2.gameserver.stats.conditions.ConditionGameTime.CheckGameTime;
import l2.gameserver.stats.conditions.ConditionPlayerRiding.CheckPlayerRiding;
import l2.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import l2.gameserver.stats.funcs.EFunction;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.utils.PositionUtils.TargetDirection;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.StringTokenizer;

@Slf4j
/** @deprecated */
@Deprecated
abstract class DocumentBase {
  private File file;

  DocumentBase(File file) {
    this.file = file;
  }

  Document parse() {
    Document doc;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setIgnoringComments(true);
      doc = factory.newDocumentBuilder().parse(this.file);
    } catch (Exception var4) {
      log.error("Error loading file " + this.file, var4);
      return null;
    }

    try {
      this.parseDocument(doc);
      return doc;
    } catch (Exception var3) {
      log.error("Error in file " + this.file, var3);
      return null;
    }
  }

  protected abstract void parseDocument(Document var1);

  protected abstract Object getTableValue(String var1);

  protected abstract Object getTableValue(String var1, int var2);

  protected void parseTemplate(Node n, StatTemplate template) {
    n = n.getFirstChild();
    if (n != null) {
      for (; n != null; n = n.getNextSibling()) {
        if (n.getNodeType() != 3) {
          String nodeName = n.getNodeName();
          if (EFunction.VALUES_BY_LOWER_NAME.containsKey(nodeName.toLowerCase())) {
            this.attachFunc(n, template, EFunction.VALUES_BY_LOWER_NAME.get(nodeName.toLowerCase()));
          } else if ("effect".equalsIgnoreCase(nodeName)) {
            if (template instanceof EffectTemplate) {
              throw new RuntimeException("Nested effects");
            }

            this.attachEffect(n, template);
          } else {
            if (!(template instanceof EffectTemplate)) {
              throw new RuntimeException("Unknown template " + nodeName);
            }

            if ("def".equalsIgnoreCase(nodeName)) {
              EffectTemplate effectTemplate = (EffectTemplate) template;
              StatsSet effectTemplateParamsSet = effectTemplate.getParam();
              Skill skill = (Skill) effectTemplateParamsSet.getObject("object");
              this.parseBeanSet(n, effectTemplateParamsSet, skill.getLevel());
            } else {
              Condition cond = this.parseCondition(n);
              if (cond != null) {
                ((EffectTemplate) template).attachCond(cond);
              }
            }
          }
        }
      }

    }
  }

  protected void parseTrigger(Node n, StatTemplate template) {
    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("trigger".equalsIgnoreCase(n.getNodeName())) {
        NamedNodeMap map = n.getAttributes();
        int id = this.parseNumber(map.getNamedItem("id").getNodeValue()).intValue();
        int level = this.parseNumber(map.getNamedItem("level").getNodeValue()).intValue();
        TriggerType t = TriggerType.valueOf(map.getNamedItem("type").getNodeValue());
        double chance = this.parseNumber(map.getNamedItem("chance").getNodeValue()).doubleValue();
        TriggerInfo trigger = new TriggerInfo(id, level, t, chance);
        template.addTrigger(trigger);

        for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
          Condition condition = this.parseCondition(n.getFirstChild());
          if (condition != null) {
            trigger.addCondition(condition);
          }
        }
      }
    }

  }

  protected void attachFunc(Node n, StatTemplate template, String name) {
    Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
    String order = n.getAttributes().getNamedItem("order").getNodeValue();
    int ord = this.parseNumber(order).intValue();
    Condition applyCond = this.parseCondition(n.getFirstChild());
    double val = 0.0D;
    if (n.getAttributes().getNamedItem("val") != null) {
      val = this.parseNumber(n.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
    }

    template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
  }

  protected void attachFunc(Node n, StatTemplate template, EFunction func) {
    Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
    String order = n.getAttributes().getNamedItem("order").getNodeValue();
    int ord = this.parseNumber(order).intValue();
    Condition applyCond = this.parseCondition(n.getFirstChild());
    double val = 0.0D;
    if (n.getAttributes().getNamedItem("val") != null) {
      val = this.parseNumber(n.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
    }

    template.attachFunc(new FuncTemplate(applyCond, func, stat, ord, val));
  }

  protected void attachEffect(Node n, Object template) {
    NamedNodeMap attrs = n.getAttributes();
    StatsSet set = new StatsSet();
    set.set("name", attrs.getNamedItem("name").getNodeValue());
    set.set("object", template);
    if (attrs.getNamedItem("count") != null) {
      set.set("count", this.parseNumber(attrs.getNamedItem("count").getNodeValue()).intValue());
    }

    if (attrs.getNamedItem("time") != null) {
      set.set("time", this.parseNumber(attrs.getNamedItem("time").getNodeValue()).intValue());
    }

    set.set("value", attrs.getNamedItem("val") != null ? this.parseNumber(attrs.getNamedItem("val").getNodeValue()).doubleValue() : 0.0D);
    set.set("abnormal", AbnormalEffect.NULL);
    set.set("abnormal2", AbnormalEffect.NULL);
    set.set("abnormal3", AbnormalEffect.NULL);
    if (attrs.getNamedItem("abnormal") != null) {
      AbnormalEffect ae = AbnormalEffect.getByName(attrs.getNamedItem("abnormal").getNodeValue());
      if (ae.isSpecial()) {
        set.set("abnormal2", ae);
      }

      if (ae.isEvent()) {
        set.set("abnormal3", ae);
      } else {
        set.set("abnormal", ae);
      }
    }

    if (attrs.getNamedItem("stackType") != null) {
      set.set("stackType", attrs.getNamedItem("stackType").getNodeValue());
    }

    if (attrs.getNamedItem("stackType2") != null) {
      set.set("stackType2", attrs.getNamedItem("stackType2").getNodeValue());
    }

    if (attrs.getNamedItem("stackOrder") != null) {
      set.set("stackOrder", this.parseNumber(attrs.getNamedItem("stackOrder").getNodeValue()).intValue());
    }

    if (attrs.getNamedItem("applyOnCaster") != null) {
      set.set("applyOnCaster", Boolean.valueOf(attrs.getNamedItem("applyOnCaster").getNodeValue()));
    }

    if (attrs.getNamedItem("applyOnSummon") != null) {
      set.set("applyOnSummon", Boolean.valueOf(attrs.getNamedItem("applyOnSummon").getNodeValue()));
    }

    if (attrs.getNamedItem("displayId") != null) {
      set.set("displayId", this.parseNumber(attrs.getNamedItem("displayId").getNodeValue()).intValue());
    }

    if (attrs.getNamedItem("displayLevel") != null) {
      set.set("displayLevel", this.parseNumber(attrs.getNamedItem("displayLevel").getNodeValue()).intValue());
    }

    if (attrs.getNamedItem("chance") != null) {
      set.set("chance", this.parseNumber(attrs.getNamedItem("chance").getNodeValue()).intValue());
    }

    if (attrs.getNamedItem("cancelOnAction") != null) {
      set.set("cancelOnAction", Boolean.valueOf(attrs.getNamedItem("cancelOnAction").getNodeValue()));
    }

    if (attrs.getNamedItem("isOffensive") != null) {
      set.set("isOffensive", Boolean.valueOf(attrs.getNamedItem("isOffensive").getNodeValue()));
    }

    if (attrs.getNamedItem("isReflectable") != null) {
      set.set("isReflectable", Boolean.valueOf(attrs.getNamedItem("isReflectable").getNodeValue()));
    }

    EffectTemplate lt = new EffectTemplate(set);
    this.parseTemplate(n, lt);

    for (Node n1 = n.getFirstChild(); n1 != null; n1 = n1.getNextSibling()) {
      if ("triggers".equalsIgnoreCase(n1.getNodeName())) {
        this.parseTrigger(n1, lt);
      }
    }

    if (template instanceof Skill) {
      ((Skill) template).attach(lt);
    }

  }

  protected Condition parseCondition(Node n) {
    while (n != null && n.getNodeType() != 1) {
      n = n.getNextSibling();
    }

    if (n == null) {
      return null;
    } else if ("and".equalsIgnoreCase(n.getNodeName())) {
      return this.parseLogicAnd(n);
    } else if ("or".equalsIgnoreCase(n.getNodeName())) {
      return this.parseLogicOr(n);
    } else if ("not".equalsIgnoreCase(n.getNodeName())) {
      return this.parseLogicNot(n);
    } else if ("player".equalsIgnoreCase(n.getNodeName())) {
      return this.parsePlayerCondition(n);
    } else if ("target".equalsIgnoreCase(n.getNodeName())) {
      return this.parseTargetCondition(n);
    } else if ("has".equalsIgnoreCase(n.getNodeName())) {
      return this.parseHasCondition(n);
    } else if ("using".equalsIgnoreCase(n.getNodeName())) {
      return this.parseUsingCondition(n);
    } else if ("game".equalsIgnoreCase(n.getNodeName())) {
      return this.parseGameCondition(n);
    } else if ("zone".equalsIgnoreCase(n.getNodeName())) {
      return this.parseZoneCondition(n);
    } else {
      return null;
    }
  }

  protected Condition parseLogicAnd(Node n) {
    ConditionLogicAnd cond = new ConditionLogicAnd();

    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == 1) {
        cond.add(this.parseCondition(n));
      }
    }

    if (cond._conditions == null || cond._conditions.length == 0) {
      log.error("Empty <and> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseLogicOr(Node n) {
    ConditionLogicOr cond = new ConditionLogicOr();

    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == 1) {
        cond.add(this.parseCondition(n));
      }
    }

    if (cond._conditions == null || cond._conditions.length == 0) {
      log.error("Empty <or> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseLogicNot(Node n) {
    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == 1) {
        return new ConditionLogicNot(this.parseCondition(n));
      }
    }

    log.error("Empty <not> condition in " + this.file);
    return null;
  }

  protected Condition parsePlayerCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      Node a = attrs.item(i);
      String nodeName = a.getNodeName();
      if ("race".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionPlayerRace(a.getNodeValue()));
      } else {
        int id;
        if ("minLevel".equalsIgnoreCase(nodeName)) {
          id = this.parseNumber(a.getNodeValue()).intValue();
          cond = this.joinAnd(cond, new ConditionPlayerMinLevel(id));
        } else if ("summon_siege_golem".equalsIgnoreCase(nodeName)) {
          cond = this.joinAnd(cond, new ConditionPlayerSummonSiegeGolem());
        } else if ("maxLevel".equalsIgnoreCase(nodeName)) {
          id = this.parseNumber(a.getNodeValue()).intValue();
          cond = this.joinAnd(cond, new ConditionPlayerMaxLevel(id));
        } else if ("maxPK".equalsIgnoreCase(nodeName)) {
          id = this.parseNumber(a.getNodeValue()).intValue();
          cond = this.joinAnd(cond, new ConditionPlayerMaxPK(id));
        } else {
          boolean val;
          if ("resting".equalsIgnoreCase(nodeName)) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
          } else if ("moving".equalsIgnoreCase(nodeName)) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
          } else if ("running".equalsIgnoreCase(nodeName)) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
          } else if ("standing".equalsIgnoreCase(nodeName)) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerState(CheckPlayerState.STANDING, val));
          } else if ("flying".equalsIgnoreCase(a.getNodeName())) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
          } else if ("flyingTransform".equalsIgnoreCase(a.getNodeName())) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING_TRANSFORM, val));
          } else if ("olympiad".equalsIgnoreCase(a.getNodeName())) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerOlympiad(val));
          } else if ("on_pvp_event".equalsIgnoreCase(a.getNodeName())) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerInTeam(val));
          } else if ("is_hero".equalsIgnoreCase(nodeName)) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerIsHero(val));
          } else if ("class_is_mage".equalsIgnoreCase(nodeName)) {
            val = Boolean.parseBoolean(a.getNodeValue());
            cond = this.joinAnd(cond, new ConditionPlayerClassIsMage(val));
          } else if ("min_pledge_rank".equalsIgnoreCase(nodeName)) {
            cond = this.joinAnd(cond, new ConditionClanPlayerMinPledgeRank(a.getNodeValue()));
          } else if ("percentHP".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerPercentHp(id));
          } else if ("percentMP".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerPercentMp(id));
          } else if ("percentCP".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerPercentCp(id));
          } else if ("chargesMin".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerChargesMin(id));
          } else if ("chargesMax".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerChargesMax(id));
          } else if ("agathion".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerAgathion(id));
          } else if ("cubic".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerCubic(id));
          } else if ("instance_zone".equalsIgnoreCase(nodeName)) {
            id = this.parseNumber(a.getNodeValue()).intValue();
            cond = this.joinAnd(cond, new ConditionPlayerInstanceZone(id));
          } else if ("riding".equalsIgnoreCase(nodeName)) {
            String riding = a.getNodeValue();
            if ("strider".equalsIgnoreCase(riding)) {
              cond = this.joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.STRIDER));
            } else if ("wyvern".equalsIgnoreCase(riding)) {
              cond = this.joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.WYVERN));
            } else if ("none".equalsIgnoreCase(riding)) {
              cond = this.joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.NONE));
            }
          } else if ("classId".equalsIgnoreCase(nodeName)) {
            cond = this.joinAnd(cond, new ConditionPlayerClassId(a.getNodeValue().split(",")));
          } else if ("gender".equalsIgnoreCase(nodeName)) {
            cond = this.joinAnd(cond, new ConditionPlayerGender(a.getNodeValue()));
          } else {
            int skillId;
            int skillMinSeed;
            StringTokenizer st;
            if ("hasBuffId".equalsIgnoreCase(nodeName)) {
              st = new StringTokenizer(a.getNodeValue(), ";");
              skillId = Integer.parseInt(st.nextToken().trim());
              skillMinSeed = -1;
              if (st.hasMoreTokens()) {
                skillMinSeed = Integer.parseInt(st.nextToken().trim());
              }

              cond = this.joinAnd(cond, new ConditionPlayerHasBuffId(skillId, skillMinSeed));
            } else if ("hasBuff".equalsIgnoreCase(nodeName)) {
              st = new StringTokenizer(a.getNodeValue(), ";");
              EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
              skillMinSeed = -1;
              if (st.hasMoreTokens()) {
                skillMinSeed = Integer.parseInt(st.nextToken().trim());
              }

              cond = this.joinAnd(cond, new ConditionPlayerHasBuff(et, skillMinSeed));
            } else if ("damage".equalsIgnoreCase(nodeName)) {
              String[] split = a.getNodeValue().split(";");
              cond = this.joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            } else if ("skillMinSeed".equalsIgnoreCase(nodeName)) {
              st = new StringTokenizer(a.getNodeValue(), ";");
              skillId = Integer.parseInt(st.nextToken().trim());
              skillMinSeed = Integer.parseInt(st.nextToken().trim());
              cond = this.joinAnd(cond, new ConditionPlayerSkillMinSeed(skillId, skillMinSeed));
            }
          }
        }
      }
    }

    if (cond == null) {
      log.error("Unrecognized <player> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseTargetCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      Node a = attrs.item(i);
      String nodeName = a.getNodeName();
      String nodeValue = a.getNodeValue();
      if ("aggro".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetAggro(Boolean.parseBoolean(nodeValue)));
      } else if ("pvp".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPlayable(Boolean.parseBoolean(nodeValue)));
      } else if ("player".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPlayer(Boolean.parseBoolean(nodeValue)));
      } else if ("exclude_caster".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPlayerNotMe(Boolean.parseBoolean(nodeValue)));
      } else if ("summon".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetSummon(Boolean.parseBoolean(nodeValue)));
      } else if ("mob".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetMob(Boolean.parseBoolean(nodeValue)));
      } else if ("targetInTheSameParty".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetInTheSameParty(Boolean.parseBoolean(nodeValue)));
      } else if ("mobId".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetMobId(Integer.parseInt(nodeValue)));
      } else if ("race".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetRace(nodeValue));
      } else if ("npc_class".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetNpcClass(nodeValue));
      } else if ("playerRace".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPlayerRace(nodeValue));
      } else if ("forbiddenClassIds".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetForbiddenClassId(nodeValue.split(";")));
      } else if ("playerSameClan".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetClan(nodeValue));
      } else if ("castledoor".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetCastleDoor(Boolean.parseBoolean(nodeValue)));
      } else if ("direction".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetDirection(TargetDirection.valueOf(nodeValue.toUpperCase())));
      } else if ("percentHP".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPercentHp(this.parseNumber(a.getNodeValue()).intValue()));
      } else if ("percentMP".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPercentMp(this.parseNumber(a.getNodeValue()).intValue()));
      } else if ("percentCP".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionTargetPercentCp(this.parseNumber(a.getNodeValue()).intValue()));
      } else {
        StringTokenizer st;
        int level;
        if ("hasBuffId".equalsIgnoreCase(nodeName)) {
          st = new StringTokenizer(nodeValue, ";");
          int id = Integer.parseInt(st.nextToken().trim());
          level = -1;
          if (st.hasMoreTokens()) {
            level = Integer.parseInt(st.nextToken().trim());
          }

          cond = this.joinAnd(cond, new ConditionTargetHasBuffId(id, level));
        } else if ("hasBuff".equalsIgnoreCase(nodeName)) {
          st = new StringTokenizer(nodeValue, ";");
          EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
          level = -1;
          if (st.hasMoreTokens()) {
            level = Integer.parseInt(st.nextToken().trim());
          }

          cond = this.joinAnd(cond, new ConditionTargetHasBuff(et, level));
        } else if ("hasForbiddenSkill".equalsIgnoreCase(nodeName)) {
          cond = this.joinAnd(cond, new ConditionTargetHasForbiddenSkill(this.parseNumber(a.getNodeValue()).intValue()));
        }
      }
    }

    if (cond == null) {
      log.error("Unrecognized <target> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseUsingCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      Node a = attrs.item(i);
      String nodeName = a.getNodeName();
      String nodeValue = a.getNodeValue();
      if (!"kind".equalsIgnoreCase(nodeName) && !"weapon".equalsIgnoreCase(nodeName)) {
        if ("armor".equalsIgnoreCase(nodeName)) {
          ArmorType armor = ArmorType.valueOf(nodeValue.toUpperCase());
          cond = this.joinAnd(cond, new ConditionUsingArmor(armor));
        } else if ("skill".equalsIgnoreCase(nodeName)) {
          cond = this.joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(nodeValue)));
        } else if ("blowskill".equalsIgnoreCase(nodeName)) {
          cond = this.joinAnd(cond, new ConditionUsingBlowSkill(Boolean.parseBoolean(nodeValue)));
        } else if ("slotitem".equalsIgnoreCase(nodeName)) {
          StringTokenizer st = new StringTokenizer(nodeValue, ";");
          int id = Integer.parseInt(st.nextToken().trim());
          int slot = Integer.parseInt(st.nextToken().trim());
          int enchant = 0;
          if (st.hasMoreTokens()) {
            enchant = Integer.parseInt(st.nextToken().trim());
          }

          cond = this.joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
        }
      } else {
        long mask = 0L;
        StringTokenizer st = new StringTokenizer(nodeValue, ",");

        while (true) {
          label68:
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            WeaponType[] var12 = WeaponType.VALUES;
            int var13 = var12.length;

            int var14;
            for (var14 = 0; var14 < var13; ++var14) {
              WeaponType wt = var12[var14];
              if (wt.toString().equalsIgnoreCase(item)) {
                mask |= wt.mask();
                continue label68;
              }
            }

            ArmorType[] var20 = ArmorType.VALUES;
            var13 = var20.length;

            for (var14 = 0; var14 < var13; ++var14) {
              ArmorType at = var20[var14];
              if (at.toString().equalsIgnoreCase(item)) {
                mask |= at.mask();
                continue label68;
              }
            }

            log.error("Invalid item kind: \"" + item + "\" in " + this.file);
          }

          if (mask != 0L) {
            cond = this.joinAnd(cond, new ConditionUsingItemType(mask));
          }
          break;
        }
      }
    }

    if (cond == null) {
      log.error("Unrecognized <using> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseHasCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      Node a = attrs.item(i);
      String nodeName = a.getNodeName();
      String nodeValue = a.getNodeValue();
      if ("skill".equalsIgnoreCase(nodeName)) {
        StringTokenizer st = new StringTokenizer(nodeValue, ";");
        Integer id = this.parseNumber(st.nextToken().trim()).intValue();
        int level = this.parseNumber(st.nextToken().trim()).shortValue();
        cond = this.joinAnd(cond, new ConditionHasSkill(id, level));
      } else if ("success".equalsIgnoreCase(nodeName)) {
        cond = this.joinAnd(cond, new ConditionFirstEffectSuccess(Boolean.valueOf(nodeValue)));
      }
    }

    if (cond == null) {
      log.error("Unrecognized <has> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseGameCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      Node a = attrs.item(i);
      if ("night".equalsIgnoreCase(a.getNodeName())) {
        boolean val = Boolean.parseBoolean(a.getNodeValue());
        cond = this.joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
      }
    }

    if (cond == null) {
      log.error("Unrecognized <game> condition in " + this.file);
    }

    return cond;
  }

  protected Condition parseZoneCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      Node a = attrs.item(i);
      if ("type".equalsIgnoreCase(a.getNodeName())) {
        cond = this.joinAnd(cond, new ConditionZoneType(a.getNodeValue()));
      } else if ("name".equalsIgnoreCase(a.getNodeName())) {
        cond = this.joinAnd(cond, new ConditionZoneName(a.getNodeValue()));
      }
    }

    if (cond == null) {
      log.error("Unrecognized <zone> condition in " + this.file);
    }

    return cond;
  }

  protected void parseBeanSet(Node n, StatsSet set, int level) {
    try {
      String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
      String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
      char ch = value.length() == 0 ? 32 : value.charAt(0);
      if (value.contains("#") && ch != '#') {
        String[] var7 = value.split("[;: ]+");
        int var8 = var7.length;

        for (String str : var7) {
          if (str.charAt(0) == '#') {
            value = value.replace(str, String.valueOf(this.getTableValue(str, level)));
          }
        }
      }

      if (ch == '#') {
        Object tableVal = this.getTableValue(value, level);
        Number parsedVal = this.parseNumber(tableVal.toString());
        set.set(name, parsedVal == null ? tableVal : String.valueOf(parsedVal));
      } else if ((Character.isDigit(ch) || ch == '-') && !value.contains(" ") && !value.contains(";")) {
        set.set(name, String.valueOf(this.parseNumber(value)));
      } else {
        set.set(name, value);
      }
    } catch (Exception var11) {
      System.out.println(n.getAttributes().getNamedItem("name") + " " + set.get("skill_id"));
      var11.printStackTrace();
    }

  }

  protected Number parseNumber(String value) {
    if (value.charAt(0) == '#') {
      value = this.getTableValue(value).toString();
    }

    try {
      if (value.equalsIgnoreCase("max")) {
        return 1.0D / 0.0;
      } else if (value.equalsIgnoreCase("min")) {
        return -1.0D / 0.0;
      } else if (value.indexOf(46) == -1) {
        int radix = 10;
        if (value.length() > 2 && value.substring(0, 2).equalsIgnoreCase("0x")) {
          value = value.substring(2);
          radix = 16;
        }

        return Integer.parseInt(value, radix);
      } else {
        return Double.valueOf(value);
      }
    } catch (NumberFormatException var3) {
      return null;
    }
  }

  protected Condition joinAnd(Condition cond, Condition c) {
    if (cond == null) {
      return c;
    } else if (cond instanceof ConditionLogicAnd) {
      ((ConditionLogicAnd) cond).add(c);
      return cond;
    } else {
      ConditionLogicAnd and = new ConditionLogicAnd();
      and.add(cond);
      and.add(c);
      return and;
    }
  }
}
