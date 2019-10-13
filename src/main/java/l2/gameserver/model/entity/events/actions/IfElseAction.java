//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class IfElseAction implements EventAction {
  private String _name;
  private boolean _reverse;
  private List<EventAction> _ifList = Collections.emptyList();
  private List<EventAction> _elseList = Collections.emptyList();

  public IfElseAction(String name, boolean reverse) {
    this._name = name;
    this._reverse = reverse;
  }

  public void call(GlobalEvent event) {
    List var10000;
    label26: {
      label25: {
        if (this._reverse) {
          if (!event.ifVar(this._name)) {
            break label25;
          }
        } else if (event.ifVar(this._name)) {
          break label25;
        }

        var10000 = this._elseList;
        break label26;
      }

      var10000 = this._ifList;
    }

    List<EventAction> list = var10000;
    Iterator var3 = list.iterator();

    while(var3.hasNext()) {
      EventAction action = (EventAction)var3.next();
      action.call(event);
    }

  }

  public void setIfList(List<EventAction> ifList) {
    this._ifList = ifList;
  }

  public void setElseList(List<EventAction> elseList) {
    this._elseList = elseList;
  }
}
