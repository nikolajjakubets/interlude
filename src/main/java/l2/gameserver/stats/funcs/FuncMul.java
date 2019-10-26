//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class FuncMul extends Func {
  public FuncMul(Stats stat, int order, Object owner, double value) {
    super(stat, order, owner, value);
  }

  public void calc(Env env) {
    env.value *= this.value;
  }
}
