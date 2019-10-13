//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.instances.player;

import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.gameserver.model.Player;

public class Friend {
  private final int _objectId;
  private String _name;
  private int _classId;
  private int _level;
  private HardReference<Player> _playerRef = HardReferences.emptyRef();

  public Friend(int objectId, String name, int classId, int level) {
    this._objectId = objectId;
    this._name = name;
    this._classId = classId;
    this._level = level;
  }

  public Friend(Player player) {
    this._objectId = player.getObjectId();
    this.update(player, true);
  }

  public void update(Player player, boolean set) {
    this._level = player.getLevel();
    this._name = player.getName();
    this._classId = player.getActiveClassId();
    this._playerRef = set ? player.getRef() : HardReferences.emptyRef();
  }

  public String getName() {
    Player player = this.getPlayer();
    return player == null ? this._name : player.getName();
  }

  public int getObjectId() {
    return this._objectId;
  }

  public int getClassId() {
    Player player = this.getPlayer();
    return player == null ? this._classId : player.getActiveClassId();
  }

  public int getLevel() {
    Player player = this.getPlayer();
    return player == null ? this._level : player.getLevel();
  }

  public boolean isOnline() {
    Player player = (Player)this._playerRef.get();
    return player != null && !player.isInOfflineMode();
  }

  public Player getPlayer() {
    Player player = (Player)this._playerRef.get();
    return player != null && !player.isInOfflineMode() ? player : null;
  }
}
