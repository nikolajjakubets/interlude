//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestAutoSoulShot extends L2GameClientPacket {
  private static final Logger LOG = LoggerFactory.getLogger(RequestAutoSoulShot.class);
  private int _itemId;
  private boolean _type;

  public RequestAutoSoulShot() {
  }

  protected void readImpl() {
    this._itemId = this.readD();
    this._type = this.readD() == 1;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getPrivateStoreType() == 0 && !activeChar.isDead()) {
        ItemInstance item = activeChar.getInventory().getItemByItemId(this._itemId);
        if (item != null) {
          if (item.getTemplate().isShotItem()) {
            if (!item.getTemplate().testCondition(activeChar, item, false)) {
              String msg = "Player " + activeChar.getName() + " trying illegal item use, ban this player!";
              Log.add(msg, "illegal-actions");
              LOG.warn(msg);
            } else if (this._type) {
              activeChar.addAutoSoulShot(this._itemId);
              activeChar.sendPacket(new ExAutoSoulShot(this._itemId, true));
              activeChar.sendPacket((new SystemMessage(1433)).addString(item.getName()));
              IItemHandler handler = item.getTemplate().getHandler();
              handler.useItem(activeChar, item, false);
            } else {
              activeChar.removeAutoSoulShot(this._itemId);
              activeChar.sendPacket(new ExAutoSoulShot(this._itemId, false));
              activeChar.sendPacket((new SystemMessage(1434)).addString(item.getName()));
            }
          }
        }
      }
    }
  }
}
