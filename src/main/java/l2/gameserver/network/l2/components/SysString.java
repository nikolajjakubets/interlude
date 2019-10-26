//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.components;

public enum SysString {
  PASSENGER_BOAT_INFO(801),
  PREVIOUS(1037),
  NEXT(1038);

  private static final SysString[] VALUES = values();
  private final int _id;

  private SysString(int i) {
    this._id = i;
  }

  public int getId() {
    return this._id;
  }

  public static SysString valueOf2(String id) {
    SysString[] var1 = VALUES;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      SysString m = var1[var3];
      if (m.name().equals(id)) {
        return m;
      }
    }

    return null;
  }

  public static SysString valueOf(int id) {
    SysString[] var1 = VALUES;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      SysString m = var1[var3];
      if (m.getId() == id) {
        return m;
      }
    }

    return null;
  }
}
