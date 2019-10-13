//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.pledge.Clan;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class CMGSiegeClanObject extends SiegeClanObject {
  private IntSet _players = new HashIntSet();
  private long _param;

  public CMGSiegeClanObject(String type, Clan clan, long param, long date) {
    super(type, clan, param, date);
    this._param = param;
  }

  public CMGSiegeClanObject(String type, Clan clan, long param) {
    super(type, clan, param);
    this._param = param;
  }

  public void addPlayer(int objectId) {
    this._players.add(objectId);
  }

  public long getParam() {
    return this._param;
  }

  public boolean isParticle(Player player) {
    return this._players.contains(player.getObjectId());
  }

  public void setEvent(boolean start, SiegeEvent event) {
    int[] var3 = this._players.toArray();
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int i = var3[var5];
      Player player = GameObjectsStorage.getPlayer(i);
      if (player != null) {
        if (start) {
          player.addEvent(event);
        } else {
          player.removeEvent(event);
        }

        player.broadcastCharInfo();
      }
    }

  }

  public void setParam(long param) {
    this._param = param;
  }

  public IntSet getPlayers() {
    return this._players;
  }
}
