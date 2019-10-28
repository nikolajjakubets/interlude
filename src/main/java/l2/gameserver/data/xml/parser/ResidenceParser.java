//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.entity.residence.ResidenceFunction;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.utils.Location;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ResidenceParser extends AbstractDirParser<ResidenceHolder> {
  private static ResidenceParser _instance = new ResidenceParser();

  public static ResidenceParser getInstance() {
    return _instance;
  }

  private ResidenceParser() {
    super(ResidenceHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/residences/");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "residence.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    String impl = rootElement.attributeValue("impl");
    Class<?> clazz = null;
    StatsSet set = new StatsSet();
    Iterator iterator = rootElement.attributeIterator();

    while(iterator.hasNext()) {
      Attribute element = (Attribute)iterator.next();
      set.set(element.getName(), element.getValue());
    }

    iterator = null;

    Residence residence;
    try {
      clazz = Class.forName("l2.gameserver.model.entity.residence." + impl);
      Constructor constructor = clazz.getConstructor(StatsSet.class);
      residence = (Residence)constructor.newInstance(set);
      ((ResidenceHolder)this.getHolder()).addResidence(residence);
    } catch (Exception var25) {
      this.error("fail to init: " + this.getCurrentFileName(), var25);
      return;
    }

    iterator = rootElement.elementIterator();

    while(true) {
      int level;
      int lease;
      ResidenceFunction function;
      label189:
      while(true) {
        if (!iterator.hasNext()) {
          return;
        }

        Element element = (Element)iterator.next();
        String nodeName = element.getName();
        level = element.attributeValue("level") == null ? 0 : Integer.parseInt(element.attributeValue("level"));
        lease = (int)((double)(element.attributeValue("lease") == null ? 0 : Integer.parseInt(element.attributeValue("lease"))) * Config.RESIDENCE_LEASE_FUNC_MULTIPLIER);
        int npcId = element.attributeValue("npcId") == null ? 0 : Integer.parseInt(element.attributeValue("npcId"));
        int listId = element.attributeValue("listId") == null ? 0 : Integer.parseInt(element.attributeValue("listId"));
        function = null;
        if (nodeName.equalsIgnoreCase("teleport")) {
          function = this.checkAndGetFunction(residence, 1);
          List<TeleportLocation> targets = new ArrayList<>();
          Iterator it2 = element.elementIterator();

          while(it2.hasNext()) {
            Element teleportElement = (Element)it2.next();
            if ("target".equalsIgnoreCase(teleportElement.getName())) {
              String name = teleportElement.attributeValue("name");
              long price = Long.parseLong(teleportElement.attributeValue("price"));
              int itemId = teleportElement.attributeValue("item") == null ? 57 : Integer.parseInt(teleportElement.attributeValue("item"));
              TeleportLocation loc = new TeleportLocation(itemId, price, name, 0);
              loc.set(Location.parseLoc(teleportElement.attributeValue("loc")));
              targets.add(loc);
            }
          }

          function.addTeleports(level, (TeleportLocation[])targets.toArray(new TeleportLocation[targets.size()]));
          break;
        }

        if (nodeName.equalsIgnoreCase("support")) {
          if (level > 9 && !Config.ALT_CH_ALLOW_1H_BUFFS) {
            continue;
          }

          function = this.checkAndGetFunction(residence, 6);
          function.addBuffs(level);
          break;
        }

        if (nodeName.equalsIgnoreCase("item_create")) {
          function = this.checkAndGetFunction(residence, 2);
          function.addBuylist(level, new int[]{npcId, listId});
          break;
        }

        if (nodeName.equalsIgnoreCase("curtain")) {
          function = this.checkAndGetFunction(residence, 7);
          break;
        }

        if (nodeName.equalsIgnoreCase("platform")) {
          function = this.checkAndGetFunction(residence, 8);
          break;
        }

        if (nodeName.equalsIgnoreCase("restore_exp")) {
          function = this.checkAndGetFunction(residence, 5);
          break;
        }

        if (nodeName.equalsIgnoreCase("restore_hp")) {
          function = this.checkAndGetFunction(residence, 3);
          break;
        }

        if (nodeName.equalsIgnoreCase("restore_mp")) {
          function = this.checkAndGetFunction(residence, 4);
          break;
        }

        Iterator subElementIterator;
        Element subElement;
        int itemId;
        int npcId2;
        if (nodeName.equalsIgnoreCase("skills")) {
          subElementIterator = element.elementIterator();

          while(true) {
            if (!subElementIterator.hasNext()) {
              break label189;
            }

            subElement = (Element)subElementIterator.next();
            itemId = Integer.parseInt(subElement.attributeValue("id"));
            npcId2 = Integer.parseInt(subElement.attributeValue("level"));
            Skill skill = SkillTable.getInstance().getInfo(itemId, npcId2);
            if (skill != null) {
              residence.addSkill(skill);
            }
          }
        }

        Location loc;
        if (nodeName.equalsIgnoreCase("banish_points")) {
          subElementIterator = element.elementIterator();

          while(true) {
            if (!subElementIterator.hasNext()) {
              break label189;
            }

            loc = Location.parse((Element)subElementIterator.next());
            residence.addBanishPoint(loc);
          }
        }

        if (nodeName.equalsIgnoreCase("owner_restart_points")) {
          subElementIterator = element.elementIterator();

          while(true) {
            if (!subElementIterator.hasNext()) {
              break label189;
            }

            loc = Location.parse((Element)subElementIterator.next());
            residence.addOwnerRestartPoint(loc);
          }
        }

        if (nodeName.equalsIgnoreCase("other_restart_points")) {
          subElementIterator = element.elementIterator();

          while(true) {
            if (!subElementIterator.hasNext()) {
              break label189;
            }

            loc = Location.parse((Element)subElementIterator.next());
            residence.addOtherRestartPoint(loc);
          }
        }

        if (nodeName.equalsIgnoreCase("chaos_restart_points")) {
          subElementIterator = element.elementIterator();

          while(true) {
            if (!subElementIterator.hasNext()) {
              break label189;
            }

            loc = Location.parse((Element)subElementIterator.next());
            residence.addChaosRestartPoint(loc);
          }
        }

        if (!nodeName.equalsIgnoreCase("merchant_guards")) {
          break;
        }

        subElementIterator = element.elementIterator();

        while(true) {
          if (!subElementIterator.hasNext()) {
            break label189;
          }

          subElement = (Element)subElementIterator.next();
          itemId = Integer.parseInt(subElement.attributeValue("item_id"));
          npcId2 = Integer.parseInt(subElement.attributeValue("npc_id"));
          int maxGuard = Integer.parseInt(subElement.attributeValue("max"));
          IntSet intSet = new HashIntSet(3);
          String[] ssq = subElement.attributeValue("ssq").split(";");
          String[] var21 = ssq;
          int var22 = ssq.length;

          for(int var23 = 0; var23 < var22; ++var23) {
            String q = var21[var23];
            if (q.equalsIgnoreCase("cabal_null")) {
              intSet.add(0);
            } else if (q.equalsIgnoreCase("cabal_dusk")) {
              intSet.add(1);
            } else if (q.equalsIgnoreCase("cabal_dawn")) {
              intSet.add(2);
            } else {
              this.error("Unknown ssq type: " + q + "; file: " + this.getCurrentFileName());
            }
          }

          ((Castle)residence).addMerchantGuard(new MerchantGuard(itemId, npcId2, maxGuard, intSet));
        }
      }

      if (function != null) {
        function.addLease(level, lease);
      }
    }
  }

  private ResidenceFunction checkAndGetFunction(Residence residence, int type) {
    ResidenceFunction function = residence.getFunction(type);
    if (function == null) {
      function = new ResidenceFunction(residence.getId(), type);
      residence.addFunction(function);
    }

    return function;
  }
}
