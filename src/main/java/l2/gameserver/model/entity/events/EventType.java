//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events;

public enum EventType {
  MAIN_EVENT,
  SIEGE_EVENT,
  PVP_EVENT,
  BOAT_EVENT,
  FUN_EVENT;

  private int _step = this.ordinal() * 1000;

  private EventType() {
  }

  public int step() {
    return this._step;
  }
}
