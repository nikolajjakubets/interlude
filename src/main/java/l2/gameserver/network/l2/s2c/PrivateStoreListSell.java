//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.TradeItem;

public class PrivateStoreListSell extends L2GameServerPacket {
  private int _sellerId;
  private long _adena;
  private final boolean _package;
  private List<TradeItem> _sellList;

  public PrivateStoreListSell(Player buyer, Player seller) {
    this._sellerId = seller.getObjectId();
    this._adena = buyer.getAdena();
    this._package = seller.getPrivateStoreType() == 8;
    this._sellList = seller.getSellList();
  }

  protected final void writeImpl() {
    this.writeC(155);
    this.writeD(this._sellerId);
    this.writeD(this._package ? 1 : 0);
    this.writeD((int)this._adena);
    this.writeD(this._sellList.size());
    Iterator var1 = this._sellList.iterator();

    while(var1.hasNext()) {
      TradeItem si = (TradeItem)var1.next();
      this.writeD(si.getItem().getType2ForPackets());
      this.writeD(si.getObjectId());
      this.writeD(si.getItemId());
      this.writeD((int)si.getCount());
      this.writeH(0);
      this.writeH(si.getEnchantLevel());
      this.writeH(0);
      this.writeD(si.getItem().getBodyPart());
      this.writeD((int)si.getOwnersPrice());
      this.writeD((int)si.getStorePrice());
    }

  }
}
