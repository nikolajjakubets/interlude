//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;

public class RewardList extends ArrayList<RewardGroup> {
  public static final int MAX_CHANCE = 1000000;
  private final RewardType _type;
  private final boolean _autoLoot;

  public RewardList(RewardType rewardType, boolean a) {
    super(5);
    this._type = rewardType;
    this._autoLoot = a;
  }

  public List<RewardItem> roll(Player player) {
    return this.roll(player, 1.0D, false, false);
  }

  public List<RewardItem> roll(Player player, double mod) {
    return this.roll(player, mod, false, false);
  }

  public List<RewardItem> roll(Player player, double mod, boolean isRaid) {
    return this.roll(player, mod, isRaid, false);
  }

  public List<RewardItem> roll(Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
    List<RewardItem> temp = new ArrayList(this.size());
    Iterator var7 = this.iterator();

    while(true) {
      List tdl;
      do {
        if (!var7.hasNext()) {
          return temp;
        }

        RewardGroup g = (RewardGroup)var7.next();
        tdl = g.roll(this._type, player, mod, isRaid, isSiegeGuard);
      } while(tdl.isEmpty());

      Iterator var10 = tdl.iterator();

      while(var10.hasNext()) {
        RewardItem itd = (RewardItem)var10.next();
        temp.add(itd);
      }
    }
  }

  public boolean validate() {
    Iterator var1 = this.iterator();

    while(var1.hasNext()) {
      RewardGroup g = (RewardGroup)var1.next();
      int chanceSum = 0;

      RewardData d;
      for(Iterator var4 = g.getItems().iterator(); var4.hasNext(); chanceSum = (int)((double)chanceSum + d.getChance())) {
        d = (RewardData)var4.next();
      }

      if (chanceSum <= 1000000) {
        return true;
      }

      double mod = (double)(1000000 / chanceSum);
      Iterator var6 = g.getItems().iterator();

      while(var6.hasNext()) {
        RewardData d = (RewardData)var6.next();
        double chance = d.getChance() * mod;
        d.setChance(chance);
        g.setChance(1000000.0D);
      }
    }

    return false;
  }

  public boolean isAutoLoot() {
    return this._autoLoot;
  }

  public RewardType getType() {
    return this._type;
  }
}
