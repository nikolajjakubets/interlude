//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Location;

public class PartyMemberPosition extends L2GameServerPacket {
  private final Map<Integer, Location> positions = new HashMap<>();

  public PartyMemberPosition() {
  }

  public PartyMemberPosition add(Player actor) {
    this.positions.put(actor.getObjectId(), actor.getLoc());
    return this;
  }

  public int size() {
    return this.positions.size();
  }

  protected final void writeImpl() {
    this.writeC(167);
    this.writeD(this.positions.size());
    Iterator var1 = this.positions.entrySet().iterator();

    while(var1.hasNext()) {
      Entry<Integer, Location> e = (Entry)var1.next();
      this.writeD((Integer)e.getKey());
      this.writeD(((Location)e.getValue()).x);
      this.writeD(((Location)e.getValue()).y);
      this.writeD(((Location)e.getValue()).z);
    }

  }
}
