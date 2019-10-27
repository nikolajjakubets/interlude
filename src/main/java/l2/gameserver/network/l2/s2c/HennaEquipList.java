//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;

public class HennaEquipList extends L2GameServerPacket {
  private int _emptySlots;
  private long _adena;
  private List<Henna> _hennas = new ArrayList<>();

  public HennaEquipList(Player player) {
    this._adena = player.getAdena();
    this._emptySlots = player.getHennaEmptySlots();
    List<Henna> list = HennaHolder.getInstance().generateList(player);
    Iterator var3 = list.iterator();

    while(var3.hasNext()) {
      Henna element = (Henna)var3.next();
      if (player.getInventory().getItemByItemId(element.getDyeId()) != null) {
        this._hennas.add(element);
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(226);
    this.writeD((int)this._adena);
    this.writeD(this._emptySlots);
    if (this._hennas.size() != 0) {
      this.writeD(this._hennas.size());
      Iterator var1 = this._hennas.iterator();

      while(var1.hasNext()) {
        Henna henna = (Henna)var1.next();
        this.writeD(henna.getSymbolId());
        this.writeD(henna.getDyeId());
        this.writeD((int)henna.getDrawCount());
        this.writeD((int)henna.getPrice());
        this.writeD(1);
      }
    } else {
      this.writeD(1);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
    }

  }
}
