//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.SendablePacket;

public class IGPwdCng extends SendablePacket {
  private int _requestor_oid;
  private String _account;
  private String _old_pass;
  private String _new_pass;

  public IGPwdCng(Player requestor, String old_pass, String new_pass) {
    this._requestor_oid = requestor.getObjectId();
    this._account = requestor.getAccountName();
    this._old_pass = old_pass;
    this._new_pass = new_pass;
  }

  protected void writeImpl() {
    this.writeC(160);
    this.writeD(this._requestor_oid);
    this.writeS(this._account);
    this.writeS(this._old_pass);
    this.writeS(this._new_pass);
  }
}
