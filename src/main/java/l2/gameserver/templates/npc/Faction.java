//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.npc;

import gnu.trove.TIntArrayList;
import l2.commons.util.TroveUtils;

public class Faction {
  public static final String none = "none";
  public static final Faction NONE = new Faction("none");
  public final String factionId;
  public int factionRange;
  public TIntArrayList ignoreId;

  public Faction(String factionId) {
    this.ignoreId = TroveUtils.EMPTY_INT_ARRAY_LIST;
    this.factionId = factionId;
  }

  public String getName() {
    return this.factionId;
  }

  public void setRange(int factionRange) {
    this.factionRange = factionRange;
  }

  public int getRange() {
    return this.factionRange;
  }

  public void addIgnoreNpcId(int npcId) {
    if (this.ignoreId.isEmpty()) {
      this.ignoreId = new TIntArrayList();
    }

    this.ignoreId.add(npcId);
  }

  public boolean isIgnoreNpcId(int npcId) {
    return this.ignoreId.contains(npcId);
  }

  public boolean isNone() {
    return this.factionId.isEmpty() || this.factionId.equals("none");
  }

  public boolean equals(Faction faction) {
    return !this.isNone() && faction.getName().equalsIgnoreCase(this.factionId);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else {
      return o.getClass() != this.getClass() ? false : this.equals((Faction)o);
    }
  }

  public String toString() {
    return this.isNone() ? "none" : this.factionId;
  }
}
