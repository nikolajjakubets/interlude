//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.Calendar;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.Announcements;
import l2.gameserver.model.entity.events.GlobalEvent;

public class March8Event extends GlobalEvent {
  private Calendar _calendar = Calendar.getInstance();
  private static final long LENGTH = 604800000L;

  public March8Event(MultiValueSet<String> set) {
    super(set);
  }

  public void initEvent() {
  }

  public void startEvent() {
    super.startEvent();
    Announcements.getInstance().announceToAll("Test startEvent");
  }

  public void stopEvent() {
    super.stopEvent();
    Announcements.getInstance().announceToAll("Test stopEvent");
  }

  public void reCalcNextTime(boolean onInit) {
    this.clearActions();
    if (onInit) {
      this._calendar.set(2, 2);
      this._calendar.set(5, 8);
      this._calendar.set(11, 0);
      this._calendar.set(12, 0);
      this._calendar.set(13, 0);
      if (this._calendar.getTimeInMillis() + 604800000L < System.currentTimeMillis()) {
        this._calendar.add(1, 1);
      }
    } else {
      this._calendar.add(1, 1);
    }

    this.registerActions();
  }

  protected long startTimeMillis() {
    return this._calendar.getTimeInMillis();
  }
}
