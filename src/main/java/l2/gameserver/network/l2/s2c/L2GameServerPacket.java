//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.commons.net.nio.impl.SendablePacket;
import l2.gameserver.GameServer;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.item.ItemTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class L2GameServerPacket extends SendablePacket<GameClient> implements IStaticPacket {

  public L2GameServerPacket() {
  }

  public final boolean write() {
    try {
      this.writeImpl();
      return true;
    } catch (Exception var5) {
      log.error("Client: " + this.getClient() + " - Failed writing: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), var5);
      return false;
    } finally {
      ;
    }
  }

  protected abstract void writeImpl();

  protected void writeEx(int value) {
    this.writeC(254);
    this.writeH(value);
  }

  protected void writeD(boolean b) {
    this.writeD(b ? 1 : 0);
  }

  protected void writeDD(int[] values, boolean sendCount) {
    if (sendCount) {
      this.getByteBuffer().putInt(values.length);
    }

    for (int value : values) {
      this.getByteBuffer().putInt(value);
    }

  }

  protected void writeDD(int[] values) {
    this.writeDD(values, false);
  }

  protected void writeItemInfo(ItemInstance item) {
    this.writeItemInfo(item, item.getCount());
  }

  protected void writeItemInfo(ItemInstance item, long count) {
    this.writeH(item.getTemplate().getType1());
    this.writeD(item.getObjectId());
    this.writeD(item.getItemId());
    this.writeD((int) count);
    this.writeH(item.getTemplate().getType2ForPackets());
    this.writeH(item.getBlessed());
    this.writeH(item.isEquipped() ? 1 : 0);
    this.writeD(item.getTemplate().getBodyPart());
    this.writeH(item.getEnchantLevel());
    this.writeH(item.getDamaged());
    this.writeH(item.getVariationStat1());
    this.writeH(item.getVariationStat2());
    this.writeD(item.getDuration());
  }

  protected void writeItemInfo(ItemInfo item) {
    this.writeItemInfo(item, item.getCount());
  }

  protected void writeItemInfo(ItemInfo item, long count) {
    this.writeH(item.getItem().getType1());
    this.writeD(item.getObjectId());
    this.writeD(item.getItemId());
    this.writeD((int) count);
    this.writeH(item.getItem().getType2ForPackets());
    this.writeH(item.getCustomType1());
    this.writeH(item.isEquipped() ? 1 : 0);
    this.writeD(item.getItem().getBodyPart());
    this.writeH(item.getEnchantLevel());
    this.writeH(item.getCustomType2());
    this.writeH(item.getVariationStat1());
    this.writeH(item.getVariationStat2());
    this.writeD(item.getShadowLifeTime());
  }

  protected void writeItemElements(MultiSellIngredient item) {
    if (item.getItemId() <= 0) {
      this.writeItemElements();
    } else {
      ItemTemplate i = ItemHolder.getInstance().getTemplate(item.getItemId());
      if (item.getItemAttributes().getValue() > 0) {
        if (i.isWeapon()) {
          Element e = item.getItemAttributes().getElement();
          this.writeH(e.getId());
          this.writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
          this.writeH(0);
          this.writeH(0);
          this.writeH(0);
          this.writeH(0);
          this.writeH(0);
          this.writeH(0);
        } else if (i.isArmor()) {
          this.writeH(-1);
          this.writeH(0);

          for (Element e : Element.VALUES) {
            this.writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
          }
        } else {
          this.writeItemElements();
        }
      } else {
        this.writeItemElements();
      }

    }
  }

  protected void writeItemElements() {
    this.writeH(-1);
    this.writeH(0);
    this.writeH(0);
    this.writeH(0);
    this.writeH(0);
    this.writeH(0);
    this.writeH(0);
    this.writeH(0);
  }

  public String getType() {
    return "[S] " + this.getClass().getSimpleName();
  }

  public L2GameServerPacket packet(Player player) {
    return this;
  }
}
