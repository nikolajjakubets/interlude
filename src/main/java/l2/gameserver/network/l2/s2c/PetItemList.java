//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;

public class PetItemList extends L2GameServerPacket {
  private ItemInstance[] items;

  public PetItemList(PetInstance cha) {
    this.items = cha.getInventory().getItems();
  }

  protected final void writeImpl() {
    this.writeC(178);
    this.writeH(this.items.length);
    ItemInstance[] var1 = this.items;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance item = var1[var3];
      this.writeH(item.getTemplate().getType1());
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD((int)item.getCount());
      this.writeH(item.getTemplate().getType2ForPackets());
      this.writeH(item.getBlessed());
      this.writeH(item.isEquipped() ? 1 : 0);
      this.writeD(item.getTemplate().getBodyPart());
      this.writeH(item.getEnchantLevel());
      this.writeH(item.getDamaged());
    }

  }
}
