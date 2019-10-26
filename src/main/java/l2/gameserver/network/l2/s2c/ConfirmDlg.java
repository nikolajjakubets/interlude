//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.components.SystemMsg;

public class ConfirmDlg extends SysMsgContainer<ConfirmDlg> {
  private int _time;
  private int _requestId;

  public ConfirmDlg(SystemMsg msg, int time) {
    super(msg);
    this._time = time;
  }

  protected final void writeImpl() {
    this.writeC(237);
    this.writeElements();
    this.writeD(this._time);
    this.writeD(this._requestId);
  }

  public void setRequestId(int requestId) {
    this._requestId = requestId;
  }
}
