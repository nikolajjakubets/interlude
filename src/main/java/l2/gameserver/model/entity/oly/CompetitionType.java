//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

public enum CompetitionType {
  TEAM_CLASS_FREE(0),
  CLASS_FREE(1),
  CLASS_INDIVIDUAL(2);

  private int _type_idx;

  private CompetitionType(int type_idx) {
    this._type_idx = type_idx;
  }

  public int getTypeIdx() {
    return this._type_idx;
  }

  public static CompetitionType getTypeOf(int idx) {
    CompetitionType[] var1 = values();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      CompetitionType type = var1[var3];
      if (type.getTypeIdx() == idx) {
        return type;
      }
    }

    return null;
  }
}
