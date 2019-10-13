//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public interface IRefineryHandler {
  void onInitRefinery(Player var1);

  void onPutTargetItem(Player var1, ItemInstance var2);

  void onPutMineralItem(Player var1, ItemInstance var2, ItemInstance var3);

  void onPutGemstoneItem(Player var1, ItemInstance var2, ItemInstance var3, ItemInstance var4, long var5);

  void onRequestRefine(Player var1, ItemInstance var2, ItemInstance var3, ItemInstance var4, long var5);

  void onInitRefineryCancel(Player var1);

  void onPutTargetCancelItem(Player var1, ItemInstance var2);

  void onRequestCancelRefine(Player var1, ItemInstance var2);
}
