//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.conditions.ConditionClanPlayerMinPledgeRank;
import l2.gameserver.stats.conditions.ConditionLogicAnd;
import l2.gameserver.stats.conditions.ConditionLogicNot;
import l2.gameserver.stats.conditions.ConditionLogicOr;
import l2.gameserver.stats.conditions.ConditionPlayerClassId;
import l2.gameserver.stats.conditions.ConditionPlayerClassIsMage;
import l2.gameserver.stats.conditions.ConditionPlayerGender;
import l2.gameserver.stats.conditions.ConditionPlayerInTeam;
import l2.gameserver.stats.conditions.ConditionPlayerInstanceZone;
import l2.gameserver.stats.conditions.ConditionPlayerIsHero;
import l2.gameserver.stats.conditions.ConditionPlayerMaxLevel;
import l2.gameserver.stats.conditions.ConditionPlayerMinLevel;
import l2.gameserver.stats.conditions.ConditionPlayerMinMaxDamage;
import l2.gameserver.stats.conditions.ConditionPlayerOlympiad;
import l2.gameserver.stats.conditions.ConditionPlayerRace;
import l2.gameserver.stats.conditions.ConditionPlayerResidence;
import l2.gameserver.stats.conditions.ConditionSlotItemId;
import l2.gameserver.stats.conditions.ConditionTargetPlayable;
import l2.gameserver.stats.conditions.ConditionUsingItemType;
import l2.gameserver.stats.conditions.ConditionUsingSkill;
import l2.gameserver.stats.conditions.ConditionZoneType;
import l2.gameserver.stats.funcs.EFunction;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.dom4j.Attribute;
import org.dom4j.Element;

public abstract class StatParser<H extends AbstractHolder> extends AbstractDirParser<H> {
  protected StatParser(H holder) {
    super(holder);
  }

  protected Condition parseFirstCond(Element sub) {
    List e = sub.elements();
    if (e.isEmpty()) {
      return null;
    } else {
      Element element = (Element)e.get(0);
      return this.parseCond(element);
    }
  }

  protected Condition parseCond(Element element) {
    String name = element.getName();
    if (name.equalsIgnoreCase("and")) {
      return this.parseLogicAnd(element);
    } else if (name.equalsIgnoreCase("or")) {
      return this.parseLogicOr(element);
    } else if (name.equalsIgnoreCase("not")) {
      return this.parseLogicNot(element);
    } else if (name.equalsIgnoreCase("target")) {
      return this.parseTargetCondition(element);
    } else if (name.equalsIgnoreCase("player")) {
      return this.parsePlayerCondition(element);
    } else if (name.equalsIgnoreCase("using")) {
      return this.parseUsingCondition(element);
    } else {
      return name.equalsIgnoreCase("zone") ? this.parseZoneCondition(element) : null;
    }
  }

  protected Condition parseLogicAnd(Element n) {
    ConditionLogicAnd cond = new ConditionLogicAnd();
    Iterator iterator = n.elementIterator();

    while(iterator.hasNext()) {
      Element condElement = (Element)iterator.next();
      cond.add(this.parseCond(condElement));
    }

    if (cond._conditions == null || cond._conditions.length == 0) {
      this.error("Empty <and> condition in " + this.getCurrentFileName());
    }

    return cond;
  }

  protected Condition parseLogicOr(Element n) {
    ConditionLogicOr cond = new ConditionLogicOr();
    Iterator iterator = n.elementIterator();

    while(iterator.hasNext()) {
      Element condElement = (Element)iterator.next();
      cond.add(this.parseCond(condElement));
    }

    if (cond._conditions == null || cond._conditions.length == 0) {
      this.error("Empty <or> condition in " + this.getCurrentFileName());
    }

    return cond;
  }

  protected Condition parseLogicNot(Element n) {
    Iterator var2 = n.elements().iterator();
    if (var2.hasNext()) {
      Object element = var2.next();
      return new ConditionLogicNot(this.parseCond((Element)element));
    } else {
      this.error("Empty <not> condition in " + this.getCurrentFileName());
      return null;
    }
  }

  protected Condition parseTargetCondition(Element element) {
    Condition cond = null;
    Iterator iterator = element.attributeIterator();

    while(iterator.hasNext()) {
      Attribute attribute = (Attribute)iterator.next();
      String name = attribute.getName();
      String value = attribute.getValue();
      if (name.equalsIgnoreCase("pvp")) {
        cond = this.joinAnd(cond, new ConditionTargetPlayable(Boolean.valueOf(value)));
      }
    }

    return cond;
  }

  protected Condition parseZoneCondition(Element element) {
    Condition cond = null;
    Iterator iterator = element.attributeIterator();

    while(iterator.hasNext()) {
      Attribute attribute = (Attribute)iterator.next();
      String name = attribute.getName();
      String value = attribute.getValue();
      if (name.equalsIgnoreCase("type")) {
        cond = this.joinAnd(cond, new ConditionZoneType(value));
      }
    }

    return cond;
  }

  protected Condition parsePlayerCondition(Element element) {
    Condition cond = null;
    Iterator iterator = element.attributeIterator();

    while(iterator.hasNext()) {
      Attribute attribute = (Attribute)iterator.next();
      String name = attribute.getName();
      String value = attribute.getValue();
      String[] st;
      if (name.equalsIgnoreCase("residence")) {
        st = value.split(";");
        cond = this.joinAnd(cond, new ConditionPlayerResidence(Integer.parseInt(st[1]), ResidenceType.valueOf(st[0])));
      } else if (name.equalsIgnoreCase("classId")) {
        cond = this.joinAnd(cond, new ConditionPlayerClassId(value.split(",")));
      } else if (name.equalsIgnoreCase("olympiad")) {
        cond = this.joinAnd(cond, new ConditionPlayerOlympiad(Boolean.valueOf(value)));
      } else if (name.equalsIgnoreCase("min_pledge_rank")) {
        cond = this.joinAnd(cond, new ConditionClanPlayerMinPledgeRank(value));
      } else if (name.equalsIgnoreCase("is_hero")) {
        cond = this.joinAnd(cond, new ConditionPlayerIsHero(Boolean.parseBoolean(value)));
      } else if (name.equalsIgnoreCase("on_pvp_event")) {
        cond = this.joinAnd(cond, new ConditionPlayerInTeam(Boolean.parseBoolean(value)));
      } else if (name.equalsIgnoreCase("class_is_mage")) {
        cond = this.joinAnd(cond, new ConditionPlayerClassIsMage(Boolean.parseBoolean(value)));
      } else if (name.equalsIgnoreCase("instance_zone")) {
        cond = this.joinAnd(cond, new ConditionPlayerInstanceZone(Integer.parseInt(value)));
      } else if (name.equalsIgnoreCase("minLevel")) {
        cond = this.joinAnd(cond, new ConditionPlayerMinLevel(Integer.parseInt(value)));
      } else if (name.equalsIgnoreCase("maxLevel")) {
        cond = this.joinAnd(cond, new ConditionPlayerMaxLevel(Integer.parseInt(value)));
      } else if (name.equalsIgnoreCase("race")) {
        cond = this.joinAnd(cond, new ConditionPlayerRace(value));
      } else if (name.equalsIgnoreCase("gender")) {
        cond = this.joinAnd(cond, new ConditionPlayerGender(value));
      } else if (name.equalsIgnoreCase("damage")) {
        st = value.split(";");
        cond = this.joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(st[0]), Double.parseDouble(st[1])));
      }
    }

    return cond;
  }

  protected Condition parseUsingCondition(Element element) {
    Condition cond = null;
    Iterator iterator = element.attributeIterator();

    while(true) {
      while(iterator.hasNext()) {
        Attribute attribute = (Attribute)iterator.next();
        String name = attribute.getName();
        String value = attribute.getValue();
        if (name.equalsIgnoreCase("slotitem")) {
          StringTokenizer st = new StringTokenizer(value, ";");
          int id = Integer.parseInt(st.nextToken().trim());
          int slot = Integer.parseInt(st.nextToken().trim());
          int enchant = 0;
          if (st.hasMoreTokens()) {
            enchant = Integer.parseInt(st.nextToken().trim());
          }

          cond = this.joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
        } else if (!name.equalsIgnoreCase("kind") && !name.equalsIgnoreCase("weapon")) {
          if (name.equalsIgnoreCase("skill")) {
            cond = this.joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(value)));
          }
        } else {
          long mask = 0L;
          StringTokenizer st = new StringTokenizer(value, ",");

          while(true) {
            label59:
            while(st.hasMoreTokens()) {
              String item = st.nextToken().trim();
              WeaponType[] var11 = WeaponType.VALUES;
              int var12 = var11.length;

              int var13;
              for(var13 = 0; var13 < var12; ++var13) {
                WeaponType wt = var11[var13];
                if (wt.toString().equalsIgnoreCase(item)) {
                  mask |= wt.mask();
                  continue label59;
                }
              }

              ArmorType[] var18 = ArmorType.VALUES;
              var12 = var18.length;

              for(var13 = 0; var13 < var12; ++var13) {
                ArmorType at = var18[var13];
                if (at.toString().equalsIgnoreCase(item)) {
                  mask |= at.mask();
                  continue label59;
                }
              }

              this.error("Invalid item kind: \"" + item + "\" in " + this.getCurrentFileName());
            }

            if (mask != 0L) {
              cond = this.joinAnd(cond, new ConditionUsingItemType(mask));
            }
            break;
          }
        }
      }

      return cond;
    }
  }

  protected Condition joinAnd(Condition cond, Condition c) {
    if (cond == null) {
      return c;
    } else if (cond instanceof ConditionLogicAnd) {
      ((ConditionLogicAnd)cond).add(c);
      return cond;
    } else {
      ConditionLogicAnd and = new ConditionLogicAnd();
      and.add(cond);
      and.add(c);
      return and;
    }
  }

  protected void parseFor(Element forElement, StatTemplate template) {
    Iterator iterator = forElement.elementIterator();

    while(iterator.hasNext()) {
      Element element = (Element)iterator.next();
      String elementName = element.getName();
      EFunction func = (EFunction)EFunction.VALUES_BY_LOWER_NAME.get(elementName.toLowerCase());
      if (null == func) {
        throw new RuntimeException("Unknown function specified '" + elementName + "'");
      }

      this.attachFunc(element, template, func);
    }

  }

  protected void parseTriggers(Element f, StatTemplate triggerable) {
    Iterator iterator = f.elementIterator();

    while(iterator.hasNext()) {
      Element element = (Element)iterator.next();
      int id = this.parseNumber(element.attributeValue("id")).intValue();
      int level = this.parseNumber(element.attributeValue("level")).intValue();
      TriggerType t = TriggerType.valueOf(element.attributeValue("type"));
      double chance = this.parseNumber(element.attributeValue("chance")).doubleValue();
      TriggerInfo trigger = new TriggerInfo(id, level, t, chance);
      triggerable.addTrigger(trigger);
      Iterator subIterator = element.elementIterator();

      while(subIterator.hasNext()) {
        Element subElement = (Element)subIterator.next();
        Condition condition = this.parseFirstCond(subElement);
        if (condition != null) {
          trigger.addCondition(condition);
        }
      }
    }

  }

  protected void attachFunc(Element n, StatTemplate template, String name) {
    Stats stat = Stats.valueOfXml(n.attributeValue("stat"));
    String order = n.attributeValue("order");
    int ord = this.parseNumber(order).intValue();
    Condition applyCond = this.parseFirstCond(n);
    double val = 0.0D;
    if (n.attributeValue("value") != null) {
      val = this.parseNumber(n.attributeValue("value")).doubleValue();
    }

    template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
  }

  protected void attachFunc(Element n, StatTemplate template, EFunction func) {
    Stats stat = Stats.valueOfXml(n.attributeValue("stat"));
    String order = n.attributeValue("order");
    int ord = this.parseNumber(order).intValue();
    Condition applyCond = this.parseFirstCond(n);
    double val = 0.0D;
    if (n.attributeValue("value") != null) {
      val = this.parseNumber(n.attributeValue("value")).doubleValue();
    }

    template.attachFunc(new FuncTemplate(applyCond, func, stat, ord, val));
  }

  protected Number parseNumber(String value) {
    if (value.charAt(0) == '#') {
      value = this.getTableValue(value).toString();
    }

    try {
      if (value.indexOf(46) == -1) {
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

  protected abstract Object getTableValue(String var1);
}
