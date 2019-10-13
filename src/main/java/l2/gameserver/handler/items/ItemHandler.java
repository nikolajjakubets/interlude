//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.items;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;

public class ItemHandler extends AbstractHolder {
  private static final ItemHandler _instance = new ItemHandler();

  public static ItemHandler getInstance() {
    return _instance;
  }

  private ItemHandler() {
  }

  public void registerItemHandler(IItemHandler handler) {
    int[] ids = handler.getItemIds();
    int[] var3 = ids;
    int var4 = ids.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int itemId = var3[var5];
      ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
      if (template == null) {
        this.warn("Item not found: " + itemId + " handler: " + handler.getClass().getSimpleName());
      } else if (template.getHandler() != IItemHandler.NULL) {
        this.warn("Duplicate handler for item: " + itemId + "(" + template.getHandler().getClass().getSimpleName() + "," + handler.getClass().getSimpleName() + ")");
      } else {
        template.setHandler(handler);
      }
    }

  }

  public void unregisterItemHandler(IItemHandler handler) {
    int[] ids = handler.getItemIds();
    int[] var3 = ids;
    int var4 = ids.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int itemId = var3[var5];
      ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
      if (template == null) {
        this.warn("Item not found: " + itemId + " handler: " + handler.getClass().getSimpleName());
      } else if (template.getHandler() != handler) {
        this.warn("Attempt to unregister item handler");
      } else {
        template.setHandler(handler);
      }
    }

  }

  public int size() {
    return 0;
  }

  public void clear() {
  }
}
