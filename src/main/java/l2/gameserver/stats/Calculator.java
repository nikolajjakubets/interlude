//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Creature;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncOwner;

public final class Calculator {
  private Func[] _functions;
  private double _base;
  private double _last;
  public final Stats _stat;
  public final Creature _character;

  public Calculator(Stats stat, Creature character) {
    this._stat = stat;
    this._character = character;
    this._functions = Func.EMPTY_FUNC_ARRAY;
  }

  public int size() {
    return this._functions.length;
  }

  public void addFunc(Func f) {
    this._functions = (Func[])ArrayUtils.add(this._functions, f);
    ArrayUtils.eqSort(this._functions);
  }

  public void removeFunc(Func f) {
    this._functions = (Func[])ArrayUtils.remove(this._functions, f);
    if (this._functions.length == 0) {
      this._functions = Func.EMPTY_FUNC_ARRAY;
    } else {
      ArrayUtils.eqSort(this._functions);
    }

  }

  public void removeOwner(Object owner) {
    Func[] tmp = this._functions;
    Func[] var3 = tmp;
    int var4 = tmp.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      Func element = var3[var5];
      if (element.owner == owner) {
        this.removeFunc(element);
      }
    }

  }

  public void calc(Env env) {
    Func[] funcs = this._functions;
    this._base = env.value;
    boolean overrideLimits = false;
    Func[] var4 = funcs;
    int var5 = funcs.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      Func func = var4[var6];
      if (func != null) {
        if (func.owner instanceof FuncOwner) {
          if (!((FuncOwner)func.owner).isFuncEnabled()) {
            continue;
          }

          if (((FuncOwner)func.owner).overrideLimits()) {
            overrideLimits = true;
          }
        }

        if (func.getCondition() == null || func.getCondition().test(env)) {
          func.calc(env);
        }
      }
    }

    if (!overrideLimits) {
      env.value = this._stat.validate(env.value);
    }

    if (env.value != this._last) {
      double last = this._last;
      this._last = env.value;
    }

  }

  public Func[] getFunctions() {
    return this._functions;
  }

  public double getBase() {
    return this._base;
  }

  public double getLast() {
    return this._last;
  }
}
