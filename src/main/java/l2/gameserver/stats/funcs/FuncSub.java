//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class FuncSub extends Func {
  public FuncSub(Stats stat, int order, Object owner, double value) {
    super(stat, order, owner, value);
  }

  public void calc(Env env) {
    switch(this.stat) {
      case MAX_CP:
      case MAX_HP:
      case MAX_MP:
        env.value = Math.max(env.value - this.value, 1.0D);
        break;
      default:
        env.value -= this.value;
    }

  }
}
