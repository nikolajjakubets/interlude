//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Collections;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.ManorManagerInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.CropProcure;

public class RequestProcureCrop extends L2GameClientPacket {
  private int _manorId;
  private int _count;
  private int[] _items;
  private long[] _itemQ;
  private List<CropProcure> _procureList = Collections.emptyList();

  public RequestProcureCrop() {
  }

  protected void readImpl() {
    this._manorId = this.readD();
    this._count = this.readD();
    if (this._count * 16 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this.readD();
        this._items[i] = this.readD();
        this._itemQ[i] = (long)this.readD();
        if (this._itemQ[i] < 1L) {
          this._count = 0;
          return;
        }
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._count != 0) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (activeChar.isInTrade()) {
        activeChar.sendActionFailed();
      } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
        activeChar.sendActionFailed();
      } else {
        GameObject target = activeChar.getTarget();
        ManorManagerInstance manor = target != null && target instanceof ManorManagerInstance ? (ManorManagerInstance)target : null;
        if (activeChar.isGM() || manor != null && activeChar.isInActingRange(manor)) {
          Castle castle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
          if (castle != null) {
            int slots = 0;
            long weight = 0L;

            int i;
            int itemId;
            long count;
            try {
              for(i = 0; i < this._count; ++i) {
                itemId = this._items[i];
                count = this._itemQ[i];
                CropProcure crop = castle.getCrop(itemId, 0);
                if (crop == null) {
                  return;
                }

                int rewradItemId = Manor.getInstance().getRewardItem(itemId, castle.getCrop(itemId, 0).getReward());
                long rewradItemCount = Manor.getInstance().getRewardAmountPerCrop(castle.getId(), itemId, castle.getCropRewardType(itemId));
                SafeMath.mulAndCheck(count, rewradItemCount);
                ItemTemplate template = ItemHolder.getInstance().getTemplate(rewradItemId);
                if (template == null) {
                  return;
                }

                weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, (long)template.getWeight()));
                if (!template.isStackable() || activeChar.getInventory().getItemByItemId(itemId) == null) {
                  ++slots;
                }
              }
            } catch (ArithmeticException var23) {
              this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              return;
            }

            activeChar.getInventory().writeLock();

            try {
              if (!activeChar.getInventory().validateWeight(weight)) {
                this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
              }

              if (!activeChar.getInventory().validateCapacity((long)slots)) {
                this.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                return;
              }

              this._procureList = castle.getCropProcure(0);

              for(i = 0; i < this._count; ++i) {
                itemId = this._items[i];
                count = this._itemQ[i];
                int rewradItemId = Manor.getInstance().getRewardItem(itemId, castle.getCrop(itemId, 0).getReward());
                long rewradItemCount = Manor.getInstance().getRewardAmountPerCrop(castle.getId(), itemId, castle.getCropRewardType(itemId));
                rewradItemCount = SafeMath.mulAndCheck(count, rewradItemCount);
                if (activeChar.getInventory().destroyItemByItemId(itemId, count)) {
                  ItemInstance item = activeChar.getInventory().addItem(rewradItemId, rewradItemCount);
                  if (item != null) {
                    activeChar.sendPacket(SystemMessage2.obtainItems(rewradItemId, rewradItemCount, 0));
                  }
                }
              }
            } catch (ArithmeticException var21) {
              this._count = 0;
            } finally {
              activeChar.getInventory().writeUnlock();
            }

            activeChar.sendChanges();
          }
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
