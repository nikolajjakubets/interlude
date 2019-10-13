//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.CapsuleItemHolder;
import l2.gameserver.data.xml.holder.CapsuleItemHolder.CapsuledItem;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapsuleItemParser extends AbstractFileParser<CapsuleItemHolder> {
  private static final Logger LOG = LoggerFactory.getLogger(CapsuleItemParser.class);
  private static final CapsuleItemParser INSTANCE = new CapsuleItemParser();

  public static CapsuleItemParser getInstance() {
    return INSTANCE;
  }

  protected CapsuleItemParser() {
    super(CapsuleItemHolder.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/capsule_items.xml");
  }

  public String getDTDFileName() {
    return "capsule_items.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator capsuleItemEntryIt = rootElement.elementIterator();

    while(true) {
      while(true) {
        Element capsuleItemEntryElement;
        do {
          if (!capsuleItemEntryIt.hasNext()) {
            return;
          }

          capsuleItemEntryElement = (Element)capsuleItemEntryIt.next();
        } while(!"capsule".equalsIgnoreCase(capsuleItemEntryElement.getName()));

        int capsuleItemId = Integer.parseInt(capsuleItemEntryElement.attributeValue("itemId"));
        int requiredItemId = Integer.parseInt(capsuleItemEntryElement.attributeValue("requiredItemId", "0"));
        long requiredItemAmount = Long.parseLong(capsuleItemEntryElement.attributeValue("requiredItemAmount", "0"));
        List<CapsuledItem> capsuledItems = new LinkedList();
        Iterator itemIt = capsuleItemEntryElement.elementIterator("item");

        while(itemIt.hasNext()) {
          Element itemEleemnt = (Element)itemIt.next();
          int itemId = Integer.parseInt(itemEleemnt.attributeValue("id"));
          long minCnt = Long.parseLong(itemEleemnt.attributeValue("min"));
          long maxCnt = Long.parseLong(itemEleemnt.attributeValue("max"));
          double chance = Double.parseDouble(itemEleemnt.attributeValue("chance", "100."));
          int minEnchant = Integer.parseInt(itemEleemnt.attributeValue("enchant_min", "0"));
          int maxEnchant = Integer.parseInt(itemEleemnt.attributeValue("enchant_max", "0"));
          if (minCnt > maxCnt) {
            LOG.error("Capsuled item " + itemId + " min > max in capsule " + capsuleItemId);
          } else {
            CapsuledItem capsuledItem = new CapsuledItem(itemId, minCnt, maxCnt, chance, minEnchant, maxEnchant);
            capsuledItems.add(capsuledItem);
          }
        }

        if (capsuledItems.isEmpty()) {
          LOG.warn("Capsule item " + itemIt + " is empty.");
        }

        if (requiredItemId > 0 && requiredItemAmount > 0L) {
          ((CapsuleItemHolder)this.getHolder()).add(capsuleItemId, Pair.of(requiredItemId, requiredItemAmount), capsuledItems);
        } else {
          ((CapsuleItemHolder)this.getHolder()).add(capsuleItemId, capsuledItems);
        }
      }
    }
  }
}
