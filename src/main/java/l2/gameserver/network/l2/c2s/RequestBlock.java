//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBlock extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(RequestBlock.class);
  private static final int BLOCK = 0;
  private static final int UNBLOCK = 1;
  private static final int BLOCKLIST = 2;
  private static final int ALLBLOCK = 3;
  private static final int ALLUNBLOCK = 4;
  private Integer _type;
  private String targetName = null;

  public RequestBlock() {
  }

  protected void readImpl() {
    this._type = this.readD();
    if (this._type == 0 || this._type == 1) {
      this.targetName = this.readS(16);
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      switch(this._type) {
        case 0:
          activeChar.addToBlockList(this.targetName);
          break;
        case 1:
          activeChar.removeFromBlockList(this.targetName);
          break;
        case 2:
          Collection<String> blockList = activeChar.getBlockList();
          if (blockList != null) {
            activeChar.sendPacket(Msg._IGNORE_LIST_);
            Iterator var3 = blockList.iterator();

            while(var3.hasNext()) {
              String name = (String)var3.next();
              activeChar.sendMessage(name);
            }

            activeChar.sendPacket(Msg.__EQUALS__);
          }
          break;
        case 3:
          activeChar.setBlockAll(true);
          activeChar.sendPacket(Msg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
          activeChar.sendEtcStatusUpdate();
          break;
        case 4:
          activeChar.setBlockAll(false);
          activeChar.sendPacket(Msg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
          activeChar.sendEtcStatusUpdate();
          break;
        default:
          _log.info("Unknown 0x0a block type: " + this._type);
      }

    }
  }
}
