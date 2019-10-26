//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.mail.Mail;

public class ExChangePostState extends L2GameServerPacket {
  private boolean _receivedBoard;
  private Mail[] _mails;
  private int _changeId;

  public ExChangePostState(boolean receivedBoard, int type, Mail... n) {
    this._receivedBoard = receivedBoard;
    this._mails = n;
    this._changeId = type;
  }

  protected void writeImpl() {
    this.writeEx(179);
    this.writeD(this._receivedBoard ? 1 : 0);
    this.writeD(this._mails.length);
    Mail[] var1 = this._mails;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Mail mail = var1[var3];
      this.writeD(mail.getMessageId());
      this.writeD(this._changeId);
    }

  }
}
