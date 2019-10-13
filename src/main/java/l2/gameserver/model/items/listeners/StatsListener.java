//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.funcs.Func;

public final class StatsListener implements OnEquipListener {
  private static final StatsListener _instance = new StatsListener();

  public StatsListener() {
  }

  public static StatsListener getInstance() {
    return _instance;
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    actor.removeStatsOwner(item);
    actor.updateStats();
  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    Func[] funcs = item.getStatFuncs();
    actor.addStatFuncs(funcs);
    actor.updateStats();
  }
}
