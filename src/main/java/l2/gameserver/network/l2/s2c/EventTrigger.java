//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class EventTrigger extends L2GameServerPacket {
  private int _trapId;
  private boolean _active;

  public EventTrigger(int trapId, boolean active) {
    this._trapId = trapId;
    this._active = active;
  }

  protected final void writeImpl() {
    this.writeC(207);
    this.writeD(this._trapId);
    this.writeC(this._active ? 1 : 0);
  }
}
