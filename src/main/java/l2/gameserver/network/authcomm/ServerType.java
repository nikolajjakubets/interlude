//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

public enum ServerType {
  NORMAL,
  RELAX,
  TEST,
  NO_LABEL,
  RESTRICTED,
  EVENT,
  FREE;

  private int _mask = 1 << this.ordinal();

  private ServerType() {
  }

  public int getMask() {
    return this._mask;
  }
}
