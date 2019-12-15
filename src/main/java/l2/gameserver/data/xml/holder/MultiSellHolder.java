//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.MultiSellEntry;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.MultiSellList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class MultiSellHolder {
  private static MultiSellHolder _instance = new MultiSellHolder();
  private static final String NODE_PRODUCTION = "production";
  private static final String NODE_INGRIDIENT = "ingredient";
  private TIntObjectHashMap<MultiSellHolder.MultiSellListContainer> entries = new TIntObjectHashMap();

  public static MultiSellHolder getInstance() {
    return _instance;
  }

  public MultiSellHolder.MultiSellListContainer getList(int id) {
    return this.entries.get(id);
  }

  public MultiSellHolder() {
    this.parseData();
  }

  public void reload() {
    this.parseData();
  }

  private void parseData() {
    this.entries.clear();
    this.parse();
  }

  private void hashFiles(String dirname, List<File> hash) {
    File dir = new File(Config.DATAPACK_ROOT, "data/" + dirname);
    if (!dir.exists()) {
      log.info("Dir " + dir.getAbsolutePath() + " not exists");
    } else {
      File[] files = dir.listFiles();
      File[] var5 = files;
      int var6 = files.length;

      for (int var7 = 0; var7 < var6; ++var7) {
        File f = var5[var7];
        if (f.getName().endsWith(".xml")) {
          hash.add(f);
        } else if (f.isDirectory() && !f.getName().equals(".svn")) {
          this.hashFiles(dirname + "/" + f.getName(), hash);
        }
      }

    }
  }

  public void addMultiSellListContainer(int id, MultiSellHolder.MultiSellListContainer list) {
    if (this.entries.containsKey(id)) {
      log.warn("MultiSell redefined: " + id);
    }

    list.setListId(id);
    this.entries.put(id, list);
  }

  public MultiSellHolder.MultiSellListContainer remove(String s) {
    return this.remove(new File(s));
  }

  public MultiSellHolder.MultiSellListContainer remove(File f) {
    return this.remove(Integer.parseInt(f.getName().replaceAll(".xml", "")));
  }

  public MultiSellHolder.MultiSellListContainer remove(int id) {
    return this.entries.remove(id);
  }

  public void parseFile(File f) {
    boolean var2 = false;

    int id;
    try {
      id = Integer.parseInt(f.getName().replaceAll(".xml", ""));
    } catch (Exception var7) {
      log.error("Error loading file " + f, var7);
      return;
    }

    Document doc = null;

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setIgnoringComments(true);
      doc = factory.newDocumentBuilder().parse(f);
    } catch (Exception var6) {
      log.error("Error loading file " + f, var6);
      return;
    }

    try {
      this.addMultiSellListContainer(id, this.parseDocument(doc, id));
    } catch (Exception var5) {
      log.error("Error in file " + f, var5);
    }

  }

  private void parse() {
    List<File> files = new ArrayList<>();
    this.hashFiles("multisell", files);
    Iterator var2 = files.iterator();

    while (var2.hasNext()) {
      File f = (File) var2.next();
      this.parseFile(f);
    }

  }

  protected MultiSellHolder.MultiSellListContainer parseDocument(Document doc, int id) {
    MultiSellHolder.MultiSellListContainer list = new MultiSellHolder.MultiSellListContainer();
    int entId = 1;

    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("item".equalsIgnoreCase(d.getNodeName())) {
            MultiSellEntry e = this.parseEntry(d, id);
            if (e != null) {
              e.setEntryId(entId++);
              list.addEntry(e);
            }
          } else if ("config".equalsIgnoreCase(d.getNodeName())) {
            list.setShowAll(XMLUtil.getAttributeBooleanValue(d, "showall", true));
            list.setNoTax(XMLUtil.getAttributeBooleanValue(d, "notax", false));
            list.setKeepEnchant(XMLUtil.getAttributeBooleanValue(d, "keepenchanted", false));
            list.setNoKey(XMLUtil.getAttributeBooleanValue(d, "nokey", false));
          }
        }
      }
    }

    return list;
  }

  protected MultiSellEntry parseEntry(Node n, int multiSellId) {
    MultiSellEntry entry = new MultiSellEntry();

    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
      int id;
      long count;
      MultiSellIngredient mi;
      if ("ingredient".equalsIgnoreCase(d.getNodeName())) {
        id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
        count = Long.parseLong(d.getAttributes().getNamedItem("count").getNodeValue());
        mi = new MultiSellIngredient(id, count);
        if (d.getAttributes().getNamedItem("enchant") != null) {
          mi.setItemEnchant(Integer.parseInt(d.getAttributes().getNamedItem("enchant").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("mantainIngredient") != null) {
          mi.setMantainIngredient(Boolean.parseBoolean(d.getAttributes().getNamedItem("mantainIngredient").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("fireAttr") != null) {
          mi.getItemAttributes().setFire(Integer.parseInt(d.getAttributes().getNamedItem("fireAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("waterAttr") != null) {
          mi.getItemAttributes().setWater(Integer.parseInt(d.getAttributes().getNamedItem("waterAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("earthAttr") != null) {
          mi.getItemAttributes().setEarth(Integer.parseInt(d.getAttributes().getNamedItem("earthAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("windAttr") != null) {
          mi.getItemAttributes().setWind(Integer.parseInt(d.getAttributes().getNamedItem("windAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("holyAttr") != null) {
          mi.getItemAttributes().setHoly(Integer.parseInt(d.getAttributes().getNamedItem("holyAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("unholyAttr") != null) {
          mi.getItemAttributes().setUnholy(Integer.parseInt(d.getAttributes().getNamedItem("unholyAttr").getNodeValue()));
        }

        entry.addIngredient(mi);
      } else if ("production".equalsIgnoreCase(d.getNodeName())) {
        id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
        count = Long.parseLong(d.getAttributes().getNamedItem("count").getNodeValue());
        mi = new MultiSellIngredient(id, count);
        if (d.getAttributes().getNamedItem("enchant") != null) {
          mi.setItemEnchant(Integer.parseInt(d.getAttributes().getNamedItem("enchant").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("fireAttr") != null) {
          mi.getItemAttributes().setFire(Integer.parseInt(d.getAttributes().getNamedItem("fireAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("waterAttr") != null) {
          mi.getItemAttributes().setWater(Integer.parseInt(d.getAttributes().getNamedItem("waterAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("earthAttr") != null) {
          mi.getItemAttributes().setEarth(Integer.parseInt(d.getAttributes().getNamedItem("earthAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("windAttr") != null) {
          mi.getItemAttributes().setWind(Integer.parseInt(d.getAttributes().getNamedItem("windAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("holyAttr") != null) {
          mi.getItemAttributes().setHoly(Integer.parseInt(d.getAttributes().getNamedItem("holyAttr").getNodeValue()));
        }

        if (d.getAttributes().getNamedItem("unholyAttr") != null) {
          mi.getItemAttributes().setUnholy(Integer.parseInt(d.getAttributes().getNamedItem("unholyAttr").getNodeValue()));
        }

        if (!Config.ALT_ALLOW_SHADOW_WEAPONS && id > 0) {
          ItemTemplate item = ItemHolder.getInstance().getTemplate(id);
          if (item != null && item.isShadowItem() && item.isWeapon() && !Config.ALT_ALLOW_SHADOW_WEAPONS) {
            return null;
          }
        }

        entry.addProduct(mi);
      }
    }

    if (!entry.getIngredients().isEmpty() && !entry.getProduction().isEmpty()) {
      if (entry.getIngredients().size() == 1 && entry.getProduction().size() == 1 && entry.getIngredients().get(0).getItemId() == 57) {
        ItemTemplate item = ItemHolder.getInstance().getTemplate(entry.getProduction().get(0).getItemId());
        if (item == null) {
          log.warn("MultiSell [" + multiSellId + "] Production [" + entry.getProduction().get(0).getItemId() + "] not found!");
          return null;
        }

        long refPrice = entry.getProduction().get(0).getItemCount() * (long) item.getReferencePrice();
        if (refPrice > entry.getIngredients().get(0).getItemCount()) {
          log.warn("MultiSell [" + multiSellId + "] Production '" + item.getName() + "' [" + entry.getProduction().get(0).getItemId() + "] price is lower than referenced | " + refPrice + " > " + entry.getIngredients().get(0).getItemCount());
        }
      }

      return entry;
    } else {
      log.warn("MultiSell [" + multiSellId + "] is empty!");
      return null;
    }
  }

  private static long[] parseItemIdAndCount(String s) {
    if (s != null && !s.isEmpty()) {
      String[] a = s.split(":");

      try {
        long id = Integer.parseInt(a[0]);
        long count = a.length > 1 ? Long.parseLong(a[1]) : 1L;
        return new long[]{id, count};
      } catch (Exception e) {
        log.error("parseItemIdAndCount: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
        return null;
      }
    } else {
      return null;
    }
  }

  public static MultiSellEntry parseEntryFromStr(String s) {
    if (s != null && !s.isEmpty()) {
      String[] a = s.split("->");
      if (a.length != 2) {
        return null;
      } else {
        long[] ingredient;
        long[] production;
        if ((ingredient = parseItemIdAndCount(a[0])) != null && (production = parseItemIdAndCount(a[1])) != null) {
          MultiSellEntry entry = new MultiSellEntry();
          entry.addIngredient(new MultiSellIngredient((int) ingredient[0], ingredient[1]));
          entry.addProduct(new MultiSellIngredient((int) production[0], production[1]));
          return entry;
        } else {
          return null;
        }
      }
    } else {
      return null;
    }
  }

  public void SeparateAndSend(int listId, Player player, double taxRate) {
    int[] var5 = Config.ALT_DISABLED_MULTISELL;
    int var6 = var5.length;

    for (int var7 = 0; var7 < var6; ++var7) {
      int i = var5[var7];
      if (i == listId) {
        player.sendMessage(new CustomMessage("common.MultisellForbidden", player));
        return;
      }
    }

    MultiSellHolder.MultiSellListContainer list = this.getList(listId);
    if (list == null) {
      player.sendMessage(new CustomMessage("common.NoMultisell", player));
    } else {
      this.SeparateAndSend(list, player, taxRate);
    }
  }

  public void SeparateAndSend(MultiSellHolder.MultiSellListContainer list, Player player, double taxRate) {
    list = this.generateMultiSell(list, player, taxRate);
    MultiSellHolder.MultiSellListContainer temp = new MultiSellHolder.MultiSellListContainer();
    int page = 1;
    temp.setListId(list.getListId());
    player.setMultisell(list);

    MultiSellEntry e;
    for (Iterator var7 = list.getEntries().iterator(); var7.hasNext(); temp.addEntry(e)) {
      e = (MultiSellEntry) var7.next();
      if (temp.getEntries().size() == Config.MULTISELL_SIZE) {
        player.sendPacket(new MultiSellList(temp, page, 0));
        ++page;
        temp = new MultiSellHolder.MultiSellListContainer();
        temp.setListId(list.getListId());
      }
    }

    if (player.isGM()) {
      Functions.sendDebugMessage(player, "MultiSell: " + temp.getListId() + ".xml");
    }

    player.sendPacket(new MultiSellList(temp, page, 1));
  }

  private MultiSellHolder.MultiSellListContainer generateMultiSell(MultiSellHolder.MultiSellListContainer container, Player player, double taxRate) {
    MultiSellHolder.MultiSellListContainer list = new MultiSellHolder.MultiSellListContainer();
    list.setListId(container.getListId());
    boolean enchant = container.isKeepEnchant();
    boolean notax = container.isNoTax();
    boolean showall = container.isShowAll();
    boolean nokey = container.isNoKey();
    list.setShowAll(showall);
    list.setKeepEnchant(enchant);
    list.setNoTax(notax);
    list.setNoKey(nokey);
    ItemInstance[] items = player.getInventory().getItems();
    Iterator var11 = container.getEntries().iterator();

    while (true) {
      label172:
      while (var11.hasNext()) {
        MultiSellEntry origEntry = (MultiSellEntry) var11.next();
        MultiSellEntry ent = origEntry.clone();
        Object ingridients;
        if (!notax && taxRate > 0.0D) {
          double tax = 0.0D;
          long adena = 0L;
          ingridients = new ArrayList(ent.getIngredients().size() + 1);
          Iterator var19 = ent.getIngredients().iterator();

          while (var19.hasNext()) {
            MultiSellIngredient i = (MultiSellIngredient) var19.next();
            if (i.getItemId() == 57) {
              adena += i.getItemCount();
              tax += (double) i.getItemCount() * taxRate;
            } else {
              ((List) ingridients).add(i);
              if (i.getItemId() == -200) {
                tax += (double) (i.getItemCount() / 120L * 1000L) * taxRate * 100.0D;
              }

              if (i.getItemId() >= 1) {
                ItemTemplate item = ItemHolder.getInstance().getTemplate(i.getItemId());
                if (item.isStackable()) {
                  tax += (double) ((long) item.getReferencePrice() * i.getItemCount()) * taxRate;
                }
              }
            }
          }

          adena = Math.round((double) adena + tax);
          if (adena > 0L) {
            ((List) ingridients).add(new MultiSellIngredient(57, adena));
          }

          ent.setTax(Math.round(tax));
          ent.getIngredients().clear();
          ent.getIngredients().addAll((Collection) ingridients);
        } else {
          ingridients = ent.getIngredients();
        }

        if (showall) {
          list.entries.add(ent);
        } else {
          List<Integer> itms = new ArrayList<>();
          Iterator var16 = ((List) ingridients).iterator();

          while (true) {
            while (true) {
              ItemTemplate template;
              MultiSellIngredient ingredient;
              do {
                do {
                  if (!var16.hasNext()) {
                    continue label172;
                  }

                  ingredient = (MultiSellIngredient) var16.next();
                  template = ingredient.getItemId() <= 0 ? null : ItemHolder.getInstance().getTemplate(ingredient.getItemId());
                } while (ingredient.getItemId() > 0 && !nokey && !template.isEquipment());
              } while (ingredient.getItemId() == 12374);

              if (ingredient.getItemId() == -200) {
                if (!itms.contains(ingredient.getItemId()) && player.getClan() != null && (long) player.getClan().getReputationScore() >= ingredient.getItemCount()) {
                  itms.add(ingredient.getItemId());
                }
              } else if (ingredient.getItemId() == -100) {
                if (!itms.contains(ingredient.getItemId()) && (long) player.getPcBangPoints() >= ingredient.getItemCount()) {
                  itms.add(ingredient.getItemId());
                }
              } else {
                ItemInstance[] var28 = items;
                int var29 = items.length;

                for (int var30 = 0; var30 < var29; ++var30) {
                  ItemInstance item = var28[var30];
                  if (item.getItemId() == ingredient.getItemId() && item.canBeExchanged(player) && !itms.contains(enchant ? (long) ingredient.getItemId() + (long) ingredient.getItemEnchant() * 100000L : (long) ingredient.getItemId()) && item.getEnchantLevel() >= ingredient.getItemEnchant()) {
                    if (item.isStackable() && item.getCount() < ingredient.getItemCount()) {
                      break;
                    }

                    itms.add(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000 : ingredient.getItemId());
                    MultiSellEntry possibleEntry = new MultiSellEntry(enchant ? ent.getEntryId() + item.getEnchantLevel() * 100000 : ent.getEntryId());

                    Iterator var24;
                    MultiSellIngredient ig;
                    for (var24 = ent.getProduction().iterator(); var24.hasNext(); possibleEntry.addProduct(ig)) {
                      ig = (MultiSellIngredient) var24.next();
                      if (enchant && template.canBeEnchanted(true)) {
                        ig.setItemEnchant(item.getEnchantLevel());
                        ig.setItemAttributes(item.getAttributes().clone());
                      }
                    }

                    for (var24 = ((List) ingridients).iterator(); var24.hasNext(); possibleEntry.addIngredient(ig)) {
                      ig = (MultiSellIngredient) var24.next();
                      if (enchant && ig.getItemId() > 0 && ItemHolder.getInstance().getTemplate(ig.getItemId()).canBeEnchanted(true)) {
                        ig.setItemEnchant(item.getEnchantLevel());
                        ig.setItemAttributes(item.getAttributes().clone());
                      }
                    }

                    list.entries.add(possibleEntry);
                    break;
                  }
                }
              }
            }
          }
        }
      }

      return list;
    }
  }

  public static class MultiSellListContainer {
    private int _listId;
    private boolean _showall = true;
    private boolean keep_enchanted = false;
    private boolean is_dutyfree = false;
    private boolean nokey = false;
    private List<MultiSellEntry> entries = new ArrayList<>();

    public MultiSellListContainer() {
    }

    public void setListId(int listId) {
      this._listId = listId;
    }

    public int getListId() {
      return this._listId;
    }

    public void setShowAll(boolean bool) {
      this._showall = bool;
    }

    public boolean isShowAll() {
      return this._showall;
    }

    public void setNoTax(boolean bool) {
      this.is_dutyfree = bool;
    }

    public boolean isNoTax() {
      return this.is_dutyfree;
    }

    public void setNoKey(boolean bool) {
      this.nokey = bool;
    }

    public boolean isNoKey() {
      return this.nokey;
    }

    public void setKeepEnchant(boolean bool) {
      this.keep_enchanted = bool;
    }

    public boolean isKeepEnchant() {
      return this.keep_enchanted;
    }

    public void addEntry(MultiSellEntry e) {
      this.entries.add(e);
    }

    public List<MultiSellEntry> getEntries() {
      return this.entries;
    }

    public boolean isEmpty() {
      return this.entries.isEmpty();
    }
  }
}
