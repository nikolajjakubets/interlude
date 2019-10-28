//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.OptionDataTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.*;
import l2.gameserver.templates.item.ItemTemplate.ItemClass;
import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;

public final class ItemParser extends StatParser<ItemHolder> {
  private static final ItemParser _instance = new ItemParser();

  public static ItemParser getInstance() {
    return _instance;
  }

  protected ItemParser() {
    super(ItemHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/items/");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "item.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator itemIterator = rootElement.elementIterator();

    while(true) {
      Element itemElement;
      StatsSet set;
      Object template;
      label118:
      while(true) {
        if (!itemIterator.hasNext()) {
          return;
        }

        itemElement = (Element)itemIterator.next();
        set = new StatsSet();
        set.set("item_id", itemElement.attributeValue("id"));
        set.set("name", itemElement.attributeValue("name"));
        set.set("add_name", itemElement.attributeValue("add_name", ""));
        int slot = 0;
        Iterator subIterator = itemElement.elementIterator();

        while(true) {
          while(subIterator.hasNext()) {
            Element subElement = (Element)subIterator.next();
            String subName = subElement.getName();
            if (subName.equalsIgnoreCase("set")) {
              set.set(subElement.attributeValue("name"), subElement.attributeValue("value"));
            } else if (subName.equalsIgnoreCase("equip")) {
              Iterator slotIterator = subElement.elementIterator();

              while(slotIterator.hasNext()) {
                Element slotElement = (Element)slotIterator.next();
                Bodypart bodypart = Bodypart.valueOf(slotElement.attributeValue("id"));
                if (bodypart.getReal() != null) {
                  slot = bodypart.mask();
                } else {
                  slot |= bodypart.mask();
                }
              }
            }
          }

          set.set("bodypart", slot);
          subIterator = null;

          try {
            if (itemElement.getName().equalsIgnoreCase("weapon")) {
              if (!set.containsKey("class")) {
                if ((slot & 256) > 0) {
                  set.set("class", ItemClass.ARMOR);
                } else {
                  set.set("class", ItemClass.WEAPON);
                }
              }

              template = new WeaponTemplate(set);
            } else if (itemElement.getName().equalsIgnoreCase("armor")) {
              if (!set.containsKey("class")) {
                if ((slot & 180032) > 0) {
                  set.set("class", ItemClass.ARMOR);
                } else if ((slot & 62) > 0) {
                  set.set("class", ItemClass.JEWELRY);
                } else {
                  set.set("class", ItemClass.ACCESSORY);
                }
              }

              template = new ArmorTemplate(set);
            } else {
              template = new EtcItemTemplate(set);
            }
            break label118;
          } catch (Exception var18) {
            this.warn("Fail create item: " + set.get("item_id"), var18);
            break;
          }
        }
      }

      Iterator subIterator = itemElement.elementIterator();

      while(true) {
        label162:
        while(subIterator.hasNext()) {
          Element subElement = (Element)subIterator.next();
          String subName = subElement.getName();
          if (subName.equalsIgnoreCase("for")) {
            this.parseFor(subElement, (StatTemplate)template);
          } else if (subName.equalsIgnoreCase("triggers")) {
            this.parseTriggers(subElement, (StatTemplate)template);
          } else {
            int val;
            int i;
            Iterator nextIterator;
            Element nextElement;
            if (subName.equalsIgnoreCase("skills")) {
              nextIterator = subElement.elementIterator();

              while(nextIterator.hasNext()) {
                nextElement = (Element)nextIterator.next();
                val = Integer.parseInt(nextElement.attributeValue("id"));
                i = Integer.parseInt(nextElement.attributeValue("level"));
                Skill skill = SkillTable.getInstance().getInfo(val, i);
                if (skill != null) {
                  ((ItemTemplate)template).attachSkill(skill);
                } else {
                  this.info("Skill not found(" + val + "," + i + ") for item:" + set.getObject("item_id") + "; file:" + this.getCurrentFileName());
                }
              }
            } else {
              int msgId;
              if (subName.equalsIgnoreCase("enchant4_skill")) {
                int id = Integer.parseInt(subElement.attributeValue("id"));
                msgId = Integer.parseInt(subElement.attributeValue("level"));
                Skill skill = SkillTable.getInstance().getInfo(id, msgId);
                if (skill != null) {
                  ((ItemTemplate)template).setEnchant4Skill(skill);
                }
              } else if (subName.equalsIgnoreCase("cond")) {
                Condition condition = this.parseFirstCond(subElement);
                if (condition != null) {
                  msgId = this.parseNumber(subElement.attributeValue("msgId")).intValue();
                  condition.setSystemMsg(msgId);
                  ((ItemTemplate)template).addCondition(condition);
                }
              } else if (subName.equalsIgnoreCase("attributes")) {
                int[] attributes = new int[6];
                nextIterator = subElement.elementIterator();

                while(nextIterator.hasNext()) {
                  nextElement = (Element) nextIterator.next();
                  if (nextElement.getName().equalsIgnoreCase("attribute")) {
                    l2.gameserver.model.base.Element element = l2.gameserver.model.base.Element.getElementByName(nextElement.attributeValue("element"));
                    attributes[element.getId()] = Integer.parseInt(nextElement.attributeValue("value"));
                  }
                }

                ((ItemTemplate)template).setBaseAtributeElements(attributes);
              } else if (subName.equalsIgnoreCase("enchant_options")) {
                nextIterator = subElement.elementIterator();

                while(true) {
                  do {
                    if (!nextIterator.hasNext()) {
                      continue label162;
                    }

                    nextElement = (Element)nextIterator.next();
                  } while(!nextElement.getName().equalsIgnoreCase("level"));

                  val = Integer.parseInt(nextElement.attributeValue("val"));
                  i = 0;
                  int[] options = new int[3];
                  Iterator var15 = nextElement.elements().iterator();

                  while(var15.hasNext()) {
                    Element optionElement = (Element)var15.next();
                    OptionDataTemplate optionData = OptionDataHolder.getInstance().getTemplate(Integer.parseInt(optionElement.attributeValue("id")));
                    if (optionData == null) {
                      this.error("Not found option_data for id: " + optionElement.attributeValue("id") + "; item_id: " + set.get("item_id"));
                    } else {
                      options[i++] = optionData.getId();
                    }
                  }

                  ((ItemTemplate)template).addEnchantOptions(val, options);
                }
              }
            }
          }
        }

        ((ItemHolder)this.getHolder()).addItem((ItemTemplate)template);
        break;
      }
    }
  }

  protected Object getTableValue(String name) {
    return null;
  }
}
