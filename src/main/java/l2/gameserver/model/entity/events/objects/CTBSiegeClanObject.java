//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.dao.SiegePlayerDAO;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;

public class CTBSiegeClanObject extends SiegeClanObject {
  private List<Integer> _players;
  private long _npcId;

  public CTBSiegeClanObject(String type, Clan clan, long param, long date) {
    super(type, clan, param, date);
    this._players = new ArrayList<>();
    this._npcId = param;
  }

  public CTBSiegeClanObject(String type, Clan clan, long param) {
    this(type, clan, param, System.currentTimeMillis());
  }

  public void select(Residence r) {
    this._players.addAll(SiegePlayerDAO.getInstance().select(r, this.getObjectId()));
  }

  public List<Integer> getPlayers() {
    return this._players;
  }

  public void setEvent(boolean start, SiegeEvent event) {
    Iterator var3 = this.getPlayers().iterator();

    while(var3.hasNext()) {
      int i = (Integer)var3.next();
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

  public boolean isParticle(Player player) {
    return this._players.contains(player.getObjectId());
  }

  public long getParam() {
    return this._npcId;
  }

  public void setParam(int npcId) {
    this._npcId = (long)npcId;
  }
}
