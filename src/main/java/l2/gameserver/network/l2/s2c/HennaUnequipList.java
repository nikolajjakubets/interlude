//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;

public class HennaUnequipList extends L2GameServerPacket {
  private int _emptySlots;
  private long _adena;
  private List<Henna> availHenna = new ArrayList(3);

  public HennaUnequipList(Player player) {
    this._adena = player.getAdena();
    this._emptySlots = player.getHennaEmptySlots();

    for(int i = 1; i <= 3; ++i) {
      if (player.getHenna(i) != null) {
        this.availHenna.add(player.getHenna(i));
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(229);
    this.writeD((int)this._adena);
    this.writeD(this._emptySlots);
    this.writeD(this.availHenna.size());
    Iterator var1 = this.availHenna.iterator();

    while(var1.hasNext()) {
      Henna henna = (Henna)var1.next();
      this.writeD(henna.getSymbolId());
      this.writeD(henna.getDyeId());
      this.writeD((int)henna.getDrawCount());
      this.writeD((int)henna.getPrice());
      this.writeD(1);
    }

  }
}
