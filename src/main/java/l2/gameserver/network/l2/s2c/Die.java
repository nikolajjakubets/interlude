//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.pledge.Clan;

public class Die extends L2GameServerPacket {
  private int _objectId;
  private boolean _fake;
  private boolean _sweepable;
  private Map<RestartType, Boolean> _types;

  public Die(Creature cha) {
    this._types = new HashMap(RestartType.VALUES.length);
    this._objectId = cha.getObjectId();
    this._fake = !cha.isDead();
    if (cha.isMonster()) {
      this._sweepable = ((MonsterInstance)cha).isSweepActive();
    } else if (cha.isPlayer()) {
      Player player = (Player)cha;
      if (!player.isOlyCompetitionStarted() && !player.isResurectProhibited()) {
        this.put(RestartType.FIXED, player.getPlayerAccess().ResurectFixed || player.getInventory().getCountOf(9218) > 0L && !player.isOnSiegeField());
        this.put(RestartType.TO_VILLAGE, true);
        Clan clan = null;
        if (this.get(RestartType.TO_VILLAGE)) {
          clan = player.getClan();
        }

        if (clan != null) {
          this.put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
          this.put(RestartType.TO_CASTLE, clan.getCastle() > 0);
        }

        Iterator var4 = cha.getEvents().iterator();

        while(var4.hasNext()) {
          GlobalEvent e = (GlobalEvent)var4.next();
          e.checkRestartLocs(player, this._types);
        }
      }
    }

  }

  protected final void writeImpl() {
    if (!this._fake) {
      this.writeC(6);
      this.writeD(this._objectId);
      this.writeD(this.get(RestartType.TO_VILLAGE));
      this.writeD(this.get(RestartType.TO_CLANHALL));
      this.writeD(this.get(RestartType.TO_CASTLE));
      this.writeD(this.get(RestartType.TO_FLAG));
      this.writeD(this._sweepable ? 1 : 0);
      this.writeD(this.get(RestartType.FIXED));
    }
  }

  private void put(RestartType t, boolean b) {
    this._types.put(t, b);
  }

  private boolean get(RestartType t) {
    Boolean b = (Boolean)this._types.get(t);
    return b != null && b;
  }
}
