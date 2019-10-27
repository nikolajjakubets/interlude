//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PlayerMessageStack {
  private static PlayerMessageStack _instance;
  private final Map<Integer, List<L2GameServerPacket>> _stack = new HashMap<>();

  public static PlayerMessageStack getInstance() {
    if (_instance == null) {
      _instance = new PlayerMessageStack();
    }

    return _instance;
  }

  public PlayerMessageStack() {
  }

  public void mailto(int char_obj_id, L2GameServerPacket message) {
    Player cha = GameObjectsStorage.getPlayer(char_obj_id);
    if (cha != null) {
      cha.sendPacket(message);
    } else {
      synchronized(this._stack) {
        Object messages;
        if (this._stack.containsKey(char_obj_id)) {
          messages = (List)this._stack.remove(char_obj_id);
        } else {
          messages = new ArrayList<>();
        }

        ((List)messages).add(message);
        this._stack.put(char_obj_id, messages);
      }
    }
  }

  public void CheckMessages(Player cha) {
    List<L2GameServerPacket> messages = null;
    synchronized(this._stack) {
      if (!this._stack.containsKey(cha.getObjectId())) {
        return;
      }

      messages = (List)this._stack.remove(cha.getObjectId());
    }

    if (messages != null && messages.size() != 0) {
      Iterator var3 = messages.iterator();

      while(var3.hasNext()) {
        L2GameServerPacket message = (L2GameServerPacket)var3.next();
        cha.sendPacket(message);
      }

    }
  }
}
