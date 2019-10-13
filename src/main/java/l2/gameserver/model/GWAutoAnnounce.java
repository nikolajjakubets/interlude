//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;

public class GWAutoAnnounce {
  private final int _id;
  private ArrayList<String> _msg;
  private int _repeat;
  private long _nextSend;
  private boolean _isOnScreen;

  public GWAutoAnnounce(int id) {
    this._id = id;
  }

  public int getId() {
    return this._id;
  }

  public void setScreenAnnounce(boolean arg) {
    this._isOnScreen = arg;
  }

  public boolean isScreenAnnounce() {
    return this._isOnScreen;
  }

  public void setAnnounce(int delay, int repeat, ArrayList<String> msg) {
    this._nextSend = System.currentTimeMillis() + (long)(delay * 1000);
    this._repeat = repeat;
    this._msg = msg;
  }

  public void updateRepeat() {
    this._nextSend = System.currentTimeMillis() + (long)(this._repeat * 1000);
  }

  public boolean canAnnounce() {
    return System.currentTimeMillis() > this._nextSend;
  }

  public ArrayList<String> getMessage() {
    return this._msg;
  }
}
