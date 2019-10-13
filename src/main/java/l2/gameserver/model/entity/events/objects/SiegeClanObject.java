//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SiegeClanObject implements Serializable {
  private String _type;
  private Clan _clan;
  private NpcInstance _flag;
  private final long _date;

  public SiegeClanObject(String type, Clan clan, long param) {
    this(type, clan, 0L, System.currentTimeMillis());
  }

  public SiegeClanObject(String type, Clan clan, long param, long date) {
    this._type = type;
    this._clan = clan;
    this._date = date;
  }

  public int getObjectId() {
    return this._clan.getClanId();
  }

  public Clan getClan() {
    return this._clan;
  }

  public NpcInstance getFlag() {
    return this._flag;
  }

  public void deleteFlag() {
    if (this._flag != null) {
      this._flag.deleteMe();
      this._flag = null;
    }

  }

  public void setFlag(NpcInstance npc) {
    this._flag = npc;
  }

  public void setType(String type) {
    this._type = type;
  }

  public String getType() {
    return this._type;
  }

  public void broadcast(IStaticPacket... packet) {
    this.getClan().broadcastToOnlineMembers(packet);
  }

  public void broadcast(L2GameServerPacket... packet) {
    this.getClan().broadcastToOnlineMembers(packet);
  }

  public void setEvent(boolean start, SiegeEvent event) {
    Iterator var3;
    Player player;
    if (start) {
      var3 = this._clan.getOnlineMembers(0).iterator();

      while(var3.hasNext()) {
        player = (Player)var3.next();
        player.addEvent(event);
        player.broadcastCharInfo();
      }
    } else {
      var3 = this._clan.getOnlineMembers(0).iterator();

      while(var3.hasNext()) {
        player = (Player)var3.next();
        player.removeEvent(event);
        player.broadcastCharInfo();
      }
    }

  }

  public boolean isParticle(Player player) {
    return true;
  }

  public long getParam() {
    return 0L;
  }

  public long getDate() {
    return this._date;
  }

  public static class SiegeClanComparatorImpl implements Comparator<SiegeClanObject> {
    private static final SiegeClanObject.SiegeClanComparatorImpl _instance = new SiegeClanObject.SiegeClanComparatorImpl();

    public SiegeClanComparatorImpl() {
    }

    public static SiegeClanObject.SiegeClanComparatorImpl getInstance() {
      return _instance;
    }

    public int compare(SiegeClanObject o1, SiegeClanObject o2) {
      return o2.getParam() < o1.getParam() ? -1 : (o2.getParam() == o1.getParam() ? 0 : 1);
    }
  }
}
