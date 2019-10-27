//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Player;

public class RewardGroup implements Cloneable {
  private double _chance;
  private boolean _isAdena = false;
  private boolean _isSealStone = false;
  private boolean _notRate = false;
  private List<RewardData> _items = new ArrayList<>();
  private double _chanceSum;

  public RewardGroup(double chance) {
    this.setChance(chance);
  }

  public boolean notRate() {
    return this._notRate;
  }

  public void setNotRate(boolean notRate) {
    this._notRate = notRate;
  }

  public double getChance() {
    return this._chance;
  }

  public void setChance(double chance) {
    this._chance = chance;
  }

  public boolean isAdena() {
    return this._isAdena;
  }

  public boolean isSealStone() {
    return this._isSealStone;
  }

  public void setIsAdena(boolean isAdena) {
    this._isAdena = isAdena;
  }

  public void addData(RewardData item) {
    if (item.getItem().isAdena()) {
      this._isAdena = true;
    } else if (item.getItem().isSealStone()) {
      this._isSealStone = true;
    }

    this._chanceSum += item.getChance();
    item.setChanceInGroup(this._chanceSum);
    this._items.add(item);
  }

  public List<RewardData> getItems() {
    return this._items;
  }

  public RewardGroup clone() {
    RewardGroup ret = new RewardGroup(this._chance);
    Iterator var2 = this._items.iterator();

    while(var2.hasNext()) {
      RewardData i = (RewardData)var2.next();
      ret.addData(i.clone());
    }

    return ret;
  }

  public List<RewardItem> roll(RewardType type, Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
    switch(type) {
      case NOT_RATED_GROUPED:
      case NOT_RATED_NOT_GROUPED:
        return this.rollItems(mod, 1.0D, 1.0D);
      case SWEEP:
        return this.rollSpoil(Config.RATE_DROP_SPOIL, player.getRateSpoil(), mod);
      case RATED_GROUPED:
        if (this._isAdena) {
          return this.rollAdena(mod, player.getRateAdena());
        } else if (this._isSealStone) {
          return this.rollSealStones(mod, player.getRateItems());
        } else if (isRaid) {
          return this.rollItems(mod, Config.RATE_DROP_RAIDBOSS * (double)player.getBonus().getDropRaidItems(), 1.0D);
        } else {
          if (isSiegeGuard) {
            return this.rollItems(mod, Config.RATE_DROP_SIEGE_GUARD, 1.0D);
          }

          return this.rollItems(mod, Config.RATE_DROP_ITEMS, player.getRateItems());
        }
      default:
        return Collections.emptyList();
    }
  }

  private List<RewardItem> rollSealStones(double mod, double playerRate) {
    List<RewardItem> ret = this.rollItems(mod, Config.RATE_DROP_SEAL_STONES, playerRate);

    RewardItem rewardItem;
    for(Iterator var6 = ret.iterator(); var6.hasNext(); rewardItem.isSealStone = true) {
      rewardItem = (RewardItem)var6.next();
    }

    return ret;
  }

  public List<RewardItem> rollItems(double mod, double baseRate, double playerRate) {
    if (mod <= 0.0D) {
      return Collections.emptyList();
    } else {
      double rate;
      if (this._notRate) {
        rate = Math.min(mod, 1.0D);
      } else {
        rate = baseRate * playerRate * mod;
      }

      double mult = Math.ceil(rate);
      boolean firstPass = true;
      List<RewardItem> ret = new ArrayList(this._items.size() * 3 / 2);

      for(long n = 0L; (double)n < mult; ++n) {
        double gmult = rate - (double)n;
        if ((double)Rnd.get(1, 1000000) <= this._chance * Math.min(gmult, 1.0D)) {
          if (!Config.ALT_MULTI_DROP) {
            this.rollFinal(this._items, ret, Math.max(gmult, 1.0D), firstPass);
            break;
          }

          this.rollFinal(this._items, ret, 1.0D, firstPass);
        }

        firstPass = false;
      }

      return ret;
    }
  }

  private List<RewardItem> rollSpoil(double baseRate, double playerRate, double mod) {
    if (mod <= 0.0D) {
      return Collections.emptyList();
    } else {
      double rate;
      if (this._notRate) {
        rate = Math.min(mod, 1.0D);
      } else {
        rate = baseRate * playerRate * mod;
      }

      double mult = Math.ceil(rate);
      boolean firstPass = true;
      List<RewardItem> ret = new ArrayList(this._items.size() * 3 / 2);

      for(long n = 0L; (double)n < mult; ++n) {
        if ((double)Rnd.get(1, 1000000) <= this._chance * Math.min(rate - (double)n, 1.0D)) {
          this.rollFinal(this._items, ret, 1.0D, firstPass);
        }

        firstPass = false;
      }

      return ret;
    }
  }

  private List<RewardItem> rollAdena(double mod, double playerRate) {
    return this.rollAdena(mod, Config.RATE_DROP_ADENA, playerRate);
  }

  private List<RewardItem> rollAdena(double mod, double baseRate, double playerRate) {
    double chance = this._chance;
    if (mod > 10.0D) {
      mod *= this._chance / 1000000.0D;
      chance = 1000000.0D;
    }

    if (mod <= 0.0D) {
      return Collections.emptyList();
    } else if ((double)Rnd.get(1, 1000000) > chance) {
      return Collections.emptyList();
    } else {
      double rate = baseRate * playerRate * mod;
      List<RewardItem> ret = new ArrayList(this._items.size());
      this.rollFinal(this._items, ret, rate, true);

      RewardItem i;
      for(Iterator var12 = ret.iterator(); var12.hasNext(); i.isAdena = true) {
        i = (RewardItem)var12.next();
      }

      return ret;
    }
  }

  private void rollFinal(List<RewardData> items, List<RewardItem> ret, double mult, boolean firstPass) {
    int chance = Rnd.get(0, (int)Math.max(this._chanceSum, 1000000.0D));
    Iterator var11 = items.iterator();

    while(var11.hasNext()) {
      RewardData i = (RewardData)var11.next();
      if ((firstPass || !i.onePassOnly()) && (double)chance < i.getChanceInGroup() && (double)chance > i.getChanceInGroup() - i.getChance()) {
        double imult = i.notRate() ? 1.0D : mult;
        long count = (long)Math.floor((double)i.getMinDrop() * imult);
        long max = (long)Math.ceil((double)i.getMaxDrop() * imult);
        if (count != max) {
          count = Rnd.get(count, max);
        }

        RewardItem t = null;
        Iterator var16 = ret.iterator();

        while(var16.hasNext()) {
          RewardItem r = (RewardItem)var16.next();
          if (i.getItemId() == r.itemId) {
            t = r;
            break;
          }
        }

        if (t == null) {
          ret.add(t = new RewardItem(i.getItemId()));
          t.count = count;
        } else if (!i.notRate()) {
          t.count = SafeMath.addAndLimit(t.count, count);
        }
        break;
      }
    }

  }
}
