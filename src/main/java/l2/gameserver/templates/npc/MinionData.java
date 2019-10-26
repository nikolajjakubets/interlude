//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.npc;

public class MinionData {
  private final int _minionId;
  private final int _minionAmount;

  public MinionData(int minionId, int minionAmount) {
    this._minionId = minionId;
    this._minionAmount = minionAmount;
  }

  public int getMinionId() {
    return this._minionId;
  }

  public int getAmount() {
    return this._minionAmount;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o.getClass() != this.getClass()) {
      return false;
    } else {
      return ((MinionData)o).getMinionId() == this.getMinionId();
    }
  }
}
