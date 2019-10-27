//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.gameserver.Config;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BuyListHolder {
  private static final Logger _log = LoggerFactory.getLogger(BuyListHolder.class);
  private static BuyListHolder _instance;
  private Map<Integer, BuyListHolder.NpcTradeList> _lists = new HashMap<>();

  public static BuyListHolder getInstance() {
    if (_instance == null) {
      _instance = new BuyListHolder();
    }

    return _instance;
  }

  public static void reload() {
    _instance = new BuyListHolder();
  }

  private BuyListHolder() {
    try {
      File filelists = new File(Config.DATAPACK_ROOT, "data/merchant_filelists.xml");
      DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
      factory1.setValidating(false);
      factory1.setIgnoringComments(true);
      Document doc1 = factory1.newDocumentBuilder().parse(filelists);
      int counterFiles = 0;
      int counterItems = 0;

      for(Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling()) {
        if ("list".equalsIgnoreCase(n1.getNodeName())) {
          for(Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling()) {
            if ("file".equalsIgnoreCase(d1.getNodeName())) {
              String filename = d1.getAttributes().getNamedItem("name").getNodeValue();
              File file = new File(Config.DATAPACK_ROOT, "data/" + filename);
              DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
              factory2.setValidating(false);
              factory2.setIgnoringComments(true);
              Document doc2 = factory2.newDocumentBuilder().parse(file);
              ++counterFiles;

              for(Node n2 = doc2.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if ("list".equalsIgnoreCase(n2.getNodeName())) {
                  for(Node d2 = n2.getFirstChild(); d2 != null; d2 = d2.getNextSibling()) {
                    if ("tradelist".equalsIgnoreCase(d2.getNodeName())) {
                      String[] npcs = d2.getAttributes().getNamedItem("npc").getNodeValue().split(";");
                      String[] shopIds = d2.getAttributes().getNamedItem("shop").getNodeValue().split(";");
                      String[] markups = new String[0];
                      boolean haveMarkups = false;
                      if (d2.getAttributes().getNamedItem("markup") != null) {
                        markups = d2.getAttributes().getNamedItem("markup").getNodeValue().split(";");
                        haveMarkups = true;
                      }

                      int size = npcs.length;
                      int n;
                      if (!haveMarkups) {
                        markups = new String[size];

                        for(n = 0; n < size; ++n) {
                          markups[n] = "0";
                        }
                      }

                      if (shopIds.length == size && markups.length == size) {
                        for(n = 0; n < size; ++n) {
                          int npc_id = Integer.parseInt(npcs[n]);
                          int shop_id = Integer.parseInt(shopIds[n]);
                          double markup = npc_id > 0 ? 1.0D + Double.parseDouble(markups[n]) / 100.0D : 0.0D;
                          BuyListHolder.NpcTradeList tl = new BuyListHolder.NpcTradeList(shop_id);
                          tl.setNpcId(npc_id);

                          for(Node i = d2.getFirstChild(); i != null; i = i.getNextSibling()) {
                            if ("item".equalsIgnoreCase(i.getNodeName())) {
                              int itemId = Integer.parseInt(i.getAttributes().getNamedItem("id").getNodeValue());
                              ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
                              if (template == null) {
                                _log.warn("Template not found for itemId: " + itemId + " for shop " + shop_id);
                              } else if (this.checkItem(template)) {
                                ++counterItems;
                                long price = i.getAttributes().getNamedItem("price") != null ? Long.parseLong(i.getAttributes().getNamedItem("price").getNodeValue()) : Math.round((double)template.getReferencePrice() * markup);
                                TradeItem item = new TradeItem();
                                item.setItemId(itemId);
                                int itemCount = i.getAttributes().getNamedItem("count") != null ? Integer.parseInt(i.getAttributes().getNamedItem("count").getNodeValue()) : 0;
                                int itemRechargeTime = i.getAttributes().getNamedItem("time") != null ? Integer.parseInt(i.getAttributes().getNamedItem("time").getNodeValue()) : 0;
                                item.setOwnersPrice(price);
                                item.setCount((long)itemCount);
                                item.setCurrentValue((long)itemCount);
                                item.setLastRechargeTime((int)(System.currentTimeMillis() / 60000L));
                                item.setRechargeTime(itemRechargeTime);
                                tl.addItem(item);
                              }
                            }
                          }

                          this._lists.put(shop_id, tl);
                        }
                      } else {
                        _log.warn("Do not correspond to the size of arrays");
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      _log.info("TradeController: Loaded " + counterFiles + " file(s).");
      _log.info("TradeController: Loaded " + counterItems + " Items.");
      _log.info("TradeController: Loaded " + this._lists.size() + " Buylists.");
    } catch (Exception var33) {
      _log.warn("TradeController: Buylists could not be initialized.");
      _log.error("", var33);
    }

  }

  public boolean checkItem(ItemTemplate template) {
    if (template.isEquipment() && !template.isForPet() && Config.ALT_SHOP_PRICE_LIMITS.length > 0) {
      for(int i = 0; i < Config.ALT_SHOP_PRICE_LIMITS.length; i += 2) {
        if (template.getBodyPart() == Config.ALT_SHOP_PRICE_LIMITS[i]) {
          if (template.getReferencePrice() > Config.ALT_SHOP_PRICE_LIMITS[i + 1]) {
            return false;
          }
          break;
        }
      }
    }

    if (Config.ALT_SHOP_UNALLOWED_ITEMS.length > 0) {
      int[] var6 = Config.ALT_SHOP_UNALLOWED_ITEMS;
      int var3 = var6.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        int i = var6[var4];
        if (template.getItemId() == i) {
          return false;
        }
      }
    }

    return true;
  }

  public BuyListHolder.NpcTradeList getBuyList(int listId) {
    return (BuyListHolder.NpcTradeList)this._lists.get(listId);
  }

  public void addToBuyList(int listId, BuyListHolder.NpcTradeList list) {
    this._lists.put(listId, list);
  }

  public static class NpcTradeList {
    private List<TradeItem> tradeList = new ArrayList<>();
    private int _id;
    private int _npcId;

    public NpcTradeList(int id) {
      this._id = id;
    }

    public int getListId() {
      return this._id;
    }

    public void setNpcId(int id) {
      this._npcId = id;
    }

    public int getNpcId() {
      return this._npcId;
    }

    public void addItem(TradeItem ti) {
      this.tradeList.add(ti);
    }

    public synchronized List<TradeItem> getItems() {
      List<TradeItem> result = new ArrayList<>();
      long currentTime = System.currentTimeMillis() / 60000L;
      Iterator var4 = this.tradeList.iterator();

      while(true) {
        TradeItem ti;
        do {
          if (!var4.hasNext()) {
            return result;
          }

          ti = (TradeItem)var4.next();
          if (!ti.isCountLimited()) {
            break;
          }

          if (ti.getCurrentValue() < ti.getCount() && (long)(ti.getLastRechargeTime() + ti.getRechargeTime()) <= currentTime) {
            ti.setLastRechargeTime(ti.getLastRechargeTime() + ti.getRechargeTime());
            ti.setCurrentValue(ti.getCount());
          }
        } while(ti.getCurrentValue() == 0L);

        result.add(ti);
      }
    }

    public TradeItem getItemByItemId(int itemId) {
      Iterator var2 = this.tradeList.iterator();

      TradeItem ti;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        ti = (TradeItem)var2.next();
      } while(ti.getItemId() != itemId);

      return ti;
    }

    public synchronized void updateItems(List<TradeItem> buyList) {
      Iterator var2 = buyList.iterator();

      while(var2.hasNext()) {
        TradeItem ti = (TradeItem)var2.next();
        TradeItem ic = this.getItemByItemId(ti.getItemId());
        if (ic.isCountLimited()) {
          ic.setCurrentValue(Math.max(ic.getCurrentValue() - ti.getCount(), 0L));
        }
      }

    }
  }
}
