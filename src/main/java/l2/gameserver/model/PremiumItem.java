//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

public class PremiumItem {
  private int _itemId;
  private long _count;
  private String _sender;

  public PremiumItem(int itemid, long count, String sender) {
    this._itemId = itemid;
    this._count = count;
    this._sender = sender;
  }

  public void updateCount(long newcount) {
    this._count = newcount;
  }

  public int getItemId() {
    return this._itemId;
  }

  public long getCount() {
    return this._count;
  }

  public String getSender() {
    return this._sender;
  }
}
