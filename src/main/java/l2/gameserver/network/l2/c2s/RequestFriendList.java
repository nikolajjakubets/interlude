//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.actor.instances.player.Friend;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestFriendList extends L2GameClientPacket {
  public RequestFriendList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.sendPacket(Msg._FRIENDS_LIST_);
      Map<Integer, Friend> _list = activeChar.getFriendList().getList();
      Iterator var3 = _list.entrySet().iterator();

      while(var3.hasNext()) {
        Entry<Integer, Friend> entry = (Entry)var3.next();
        Player friend = World.getPlayer((Integer)entry.getKey());
        if (friend != null) {
          activeChar.sendPacket((new SystemMessage(488)).addName(friend));
        } else {
          activeChar.sendPacket((new SystemMessage(489)).addString(((Friend)entry.getValue()).getName()));
        }
      }

      activeChar.sendPacket(Msg.__EQUALS__);
    }
  }
}
