//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.funcs;

import java.util.HashMap;
import java.util.Map;
import l2.gameserver.stats.Stats;

public enum EFunction {
  Set {
    public Func create(Stats stat, int order, Object owner, double value) {
      return new FuncSet(stat, order, owner, value);
    }
  },
  Add {
    public Func create(Stats stat, int order, Object owner, double value) {
      return new FuncAdd(stat, order, owner, value);
    }
  },
  Sub {
    public Func create(Stats stat, int order, Object owner, double value) {
      return new FuncSub(stat, order, owner, value);
    }
  },
  Mul {
    public Func create(Stats stat, int order, Object owner, double value) {
      return new FuncMul(stat, order, owner, value);
    }
  },
  Div {
    public Func create(Stats stat, int order, Object owner, double value) {
      return new FuncDiv(stat, order, owner, value);
    }
  },
  Enchant {
    public Func create(Stats stat, int order, Object owner, double value) {
      return new FuncEnchant(stat, order, owner, value);
    }
  };

  public static final EFunction[] VALUES = values();
  public static final Map<String, EFunction> VALUES_BY_LOWER_NAME = new HashMap<>();

  private EFunction() {
  }

  public abstract Func create(Stats var1, int var2, Object var3, double var4);

  static {
    EFunction[] var0 = VALUES;
    int var1 = var0.length;

    for(int var2 = 0; var2 < var1; ++var2) {
      EFunction eFunc = var0[var2];
      VALUES_BY_LOWER_NAME.put(eFunc.name().toLowerCase(), eFunc);
    }

  }
}
