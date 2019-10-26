//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class TutorialEnableClientEvent extends L2GameServerPacket {
  private int _event = 0;

  public TutorialEnableClientEvent(int event) {
    this._event = event;
  }

  protected final void writeImpl() {
    this.writeC(162);
    this.writeD(this._event);
  }
}
