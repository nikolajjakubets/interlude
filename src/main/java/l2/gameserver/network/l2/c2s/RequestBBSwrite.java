//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestBBSwrite extends L2GameClientPacket {
  private String _url;
  private String _arg1;
  private String _arg2;
  private String _arg3;
  private String _arg4;
  private String _arg5;

  public RequestBBSwrite() {
  }

  public void readImpl() {
    this._url = this.readS();
    this._arg1 = this.readS();
    this._arg2 = this.readS();
    this._arg3 = this.readS();
    this._arg4 = this.readS();
    this._arg5 = this.readS();
  }

  public void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(this._url);
      if (handler != null) {
        if (!Config.COMMUNITYBOARD_ENABLED) {
          activeChar.sendPacket(new SystemMessage(938));
        } else {
          handler.onWriteCommand(activeChar, this._url, this._arg1, this._arg2, this._arg3, this._arg4, this._arg5);
        }
      }

    }
  }
}
