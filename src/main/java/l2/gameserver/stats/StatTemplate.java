//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.stats.triggers.TriggerInfo;

public class StatTemplate {
  protected FuncTemplate[] _funcTemplates;
  protected List<TriggerInfo> _triggerList;

  public StatTemplate() {
    this._funcTemplates = FuncTemplate.EMPTY_ARRAY;
    this._triggerList = Collections.emptyList();
  }

  public List<TriggerInfo> getTriggerList() {
    return this._triggerList;
  }

  public void addTrigger(TriggerInfo f) {
    if (this._triggerList.isEmpty()) {
      this._triggerList = new ArrayList(4);
    }

    this._triggerList.add(f);
  }

  public void attachFunc(FuncTemplate f) {
    this._funcTemplates = (FuncTemplate[])ArrayUtils.add(this._funcTemplates, f);
  }

  public FuncTemplate[] getAttachedFuncs() {
    return this._funcTemplates;
  }

  public Func[] getStatFuncs(Object owner) {
    if (this._funcTemplates.length == 0) {
      return Func.EMPTY_FUNC_ARRAY;
    } else {
      Func[] funcs = new Func[this._funcTemplates.length];

      for(int i = 0; i < funcs.length; ++i) {
        funcs[i] = this._funcTemplates[i].getFunc(owner);
      }

      return funcs;
    }
  }
}
