//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.instances.player;

import l2.gameserver.dao.CharacterFriendDAO;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2Friend;
import l2.gameserver.network.l2.s2c.L2FriendStatus;
import l2.gameserver.network.l2.s2c.SystemMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
public class FriendList {
  private Map<Integer, Friend> _friendList = Collections.emptyMap();
  private final Player _owner;

  public FriendList(Player owner) {
    this._owner = owner;
  }

  public void restore() {
    this._friendList = CharacterFriendDAO.getInstance().select(this._owner);
  }

  public void removeFriend(String name) {
    if (!StringUtils.isEmpty(name)) {
      int objectId = this.removeFriend0(name);
      if (objectId > 0) {
        Player friendChar = World.getPlayer(objectId);
        this._owner.sendPacket(new IStaticPacket[]{(new SystemMessage(133)).addString(name), new L2Friend(name, false, friendChar != null, objectId)});
        if (friendChar != null) {
          friendChar.sendPacket(new IStaticPacket[]{(new SystemMessage(481)).addString(this._owner.getName()), new L2Friend(this._owner, false)});
        }
      } else {
        this._owner.sendPacket((new SystemMessage(171)).addString(name));
      }

    }
  }

  public void notifyFriends(boolean login) {
    try {

      for (Friend friend : this._friendList.values()) {
        Player friendPlayer = GameObjectsStorage.getPlayer(friend.getObjectId());
        if (friendPlayer != null) {
          Friend thisFriend = (Friend) friendPlayer.getFriendList().getList().get(this._owner.getObjectId());
          if (thisFriend != null) {
            thisFriend.update(this._owner, login);
            if (login) {
              friendPlayer.sendPacket((new SystemMessage(503)).addString(this._owner.getName()));
            }

            friendPlayer.sendPacket(new L2FriendStatus(this._owner, login));
            friend.update(friendPlayer, login);
          }
        }
      }
    } catch (Exception e) {
      log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
      e.printStackTrace();
    }

  }

  public void addFriend(Player friendPlayer) {
    this._friendList.put(friendPlayer.getObjectId(), new Friend(friendPlayer));
    CharacterFriendDAO.getInstance().insert(this._owner, friendPlayer);
  }

  private int removeFriend0(String name) {
    if (name == null) {
      return 0;
    } else {
      Integer objectId = 0;
      Iterator var3 = this._friendList.entrySet().iterator();

      while(var3.hasNext()) {
        Entry<Integer, Friend> entry = (Entry)var3.next();
        if (name.equalsIgnoreCase(((Friend)entry.getValue()).getName())) {
          objectId = (Integer)entry.getKey();
          break;
        }
      }

      if (objectId > 0) {
        this._friendList.remove(objectId);
        CharacterFriendDAO.getInstance().delete(this._owner, objectId);
        return objectId;
      } else {
        return 0;
      }
    }
  }

  public Map<Integer, Friend> getList() {
    return this._friendList;
  }

  public String toString() {
    return "FriendList[owner=" + this._owner.getName() + "]";
  }
}
