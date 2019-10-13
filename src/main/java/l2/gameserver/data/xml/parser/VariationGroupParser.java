//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.VariationGroupHolder;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.dom4j.Element;

public class VariationGroupParser extends AbstractFileParser<VariationGroupHolder> {
  private static final VariationGroupParser _instance = new VariationGroupParser();
  private HashMap<Integer, VariationGroupData> _byMineralId = new HashMap();

  public static VariationGroupParser getInstance() {
    return _instance;
  }

  private VariationGroupParser() {
    super(VariationGroupHolder.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/variation_group.xml");
  }

  public String getDTDFileName() {
    return "variation_group.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator it = rootElement.elementIterator();

    while(it.hasNext()) {
      Element vge = (Element)it.next();
      String groupName = vge.attributeValue("name");
      List<Integer> itemsList = new ArrayList();
      List<VariationGroupData> variationGroupDataList = new ArrayList();
      Iterator it2 = vge.elementIterator();

      while(true) {
        while(it2.hasNext()) {
          Element vge2 = (Element)it2.next();
          String vge2Name = vge2.getName();
          if ("items".equalsIgnoreCase(vge2Name)) {
            StringTokenizer stItems = new StringTokenizer(vge2.getStringValue());

            while(stItems.hasMoreTokens()) {
              itemsList.add(Integer.parseInt(stItems.nextToken()));
            }
          } else if ("fee".equalsIgnoreCase(vge2Name)) {
            long cancelPrice = Long.parseLong(vge2.attributeValue("cancelPrice"));
            Iterator it3 = vge2.elementIterator();

            while(it3.hasNext()) {
              Element vge3 = (Element)it3.next();
              if ("mineral".equalsIgnoreCase(vge3.getName())) {
                int mineralItemId = Integer.parseInt(vge3.attributeValue("itemId"));
                int gemstoneItemId = Integer.parseInt(vge3.attributeValue("gemstoneItemId"));
                long gemstoneItemCnt = Long.parseLong(vge3.attributeValue("gemstoneItemCnt"));
                variationGroupDataList.add(new VariationGroupData(mineralItemId, gemstoneItemId, gemstoneItemCnt, cancelPrice));
              }
            }
          }
        }

        if (variationGroupDataList.isEmpty()) {
          throw new RuntimeException("Undefined fee for group " + groupName);
        }

        int[] itemIds = new int[itemsList.size()];

        for(int i = 0; i < itemsList.size(); ++i) {
          itemIds[i] = (Integer)itemsList.get(i);
        }

        Arrays.sort(itemIds);
        Iterator var20 = variationGroupDataList.iterator();

        while(var20.hasNext()) {
          VariationGroupData variationGroupData = (VariationGroupData)var20.next();
          ((VariationGroupHolder)this.getHolder()).addSorted(itemIds, variationGroupData);
        }
        break;
      }
    }

  }
}
