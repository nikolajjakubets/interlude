//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;

public final class FuncTemplate {
  public static final FuncTemplate[] EMPTY_ARRAY = new FuncTemplate[0];
  public Condition _applyCond;
  public EFunction _func;
  public Stats _stat;
  public int _order;
  public double _value;

  public FuncTemplate(Condition applyCond, String func, Stats stat, int order, double value) {
    this._applyCond = applyCond;
    this._stat = stat;
    this._order = order;
    this._value = value;
    this._func = (EFunction)EFunction.VALUES_BY_LOWER_NAME.get(func.toLowerCase());
    if (this._func == null) {
      throw new RuntimeException("Unknown function " + func);
    }
  }

  public FuncTemplate(Condition applyCond, EFunction func, Stats stat, int order, double value) {
    this._applyCond = applyCond;
    this._stat = stat;
    this._order = order;
    this._value = value;
    this._func = func;
  }

  public Func getFunc(Object owner) {
    Func f = this._func.create(this._stat, this._order, owner, this._value);
    if (this._applyCond != null) {
      f.setCondition(this._applyCond);
    }

    return f;
  }
}
