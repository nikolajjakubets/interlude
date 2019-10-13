//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.reward;

public class RewardItem {
  public final int itemId;
  public long count;
  public boolean isAdena;
  public boolean isSealStone;

  public RewardItem(int itemId) {
    this.itemId = itemId;
    this.count = 1L;
  }
}
