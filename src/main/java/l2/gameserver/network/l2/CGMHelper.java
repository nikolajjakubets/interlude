//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2;

import l2.gameserver.Config;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public abstract class CGMHelper {
  private static final CGMHelper INSTANCE = init();

  public CGMHelper() {
  }

  private static CGMHelper init() {
    CGMHelper inst = null;

    try {
      String instClassName = Config.ALT_CG_MODULE;
      CGMHelper.CGMType[] var2 = CGMHelper.CGMType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        CGMHelper.CGMType cgmType = var2[var4];
        if (cgmType.name().equals(Config.ALT_CG_MODULE)) {
          instClassName = cgmType.getImplClassName();
        }
      }

      if (instClassName != null) {
        inst = (CGMHelper)Class.forName(instClassName).newInstance();
      }
    } catch (Exception var6) {
      var6.printStackTrace();
    }

    return inst;
  }

  public static CGMHelper getInstance() {
    return INSTANCE;
  }

  public static boolean isActive() {
    return getInstance() != null;
  }

  public abstract L2GameClientPacket handle(GameClient var1, int var2);

  public abstract GameCrypt createCrypt();

  public abstract byte[] getRandomKey();

  public abstract void addHWIDBan(String var1, String var2, String var3, String var4);

  public static enum CGMType {
    NONE((String)null),
    LAMEGUARD("l2.gameserver.network.l2.cgm.LameGuardHelperImpl"),
    SMARTGUARD("l2.gameserver.network.l2.cgm.SmartGuardHelperImpl"),
    STRIXGUARD("l2.gameserver.network.l2.cgm.StrixGuardHelperImpl");

    private final String _implClassName;

    private CGMType(String implClassName) {
      this._implClassName = implClassName;
    }

    public String getImplClassName() {
      return this._implClassName;
    }

    public boolean isActive() {
      return this._implClassName != null;
    }
  }
}
