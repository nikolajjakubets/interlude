//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.commons.util.Rnd;
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
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.CropProcure;
import org.apache.commons.lang3.ArrayUtils;

public class RequestProcureCropList extends L2GameClientPacket {
  private int _count;
  private int[] _items;
  private int[] _crop;
  private int[] _manor;
  private long[] _itemQ;

  public RequestProcureCropList() {
  }

  protected void readImpl() {
    this._count = this.readD();
    if (this._count * 16 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._crop = new int[this._count];
      this._manor = new int[this._count];
      this._itemQ = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._crop[i] = this.readD();
        this._manor[i] = this.readD();
        this._itemQ[i] = (long)this.readD();
        if (this._crop[i] < 1 || this._manor[i] < 1 || this._itemQ[i] < 1L || ArrayUtils.indexOf(this._items, this._items[i]) < i) {
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
        if (activeChar.isGM() || manor != null && manor.isInActingRange(activeChar)) {
          int currentManorId = manor == null ? 0 : manor.getCastle().getId();
          long totalFee = 0L;
          int slots = 0;
          long weight = 0L;

          int i;
          int objId;
          int cropId;
          int manorId;
          long count;
          ItemInstance item;
          Castle castle;
          CropProcure crop;
          try {
            for(i = 0; i < this._count; ++i) {
              objId = this._items[i];
              cropId = this._crop[i];
              manorId = this._manor[i];
              count = this._itemQ[i];
              item = activeChar.getInventory().getItemByObjectId(objId);
              if (item == null || item.getCount() < count || item.getItemId() != cropId) {
                return;
              }

              castle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, manorId);
              if (castle == null) {
                return;
              }

              crop = castle.getCrop(cropId, 0);
              if (crop == null || crop.getId() == 0 || crop.getPrice() == 0L) {
                return;
              }

              if (count > crop.getAmount()) {
                return;
              }

              long price = SafeMath.mulAndCheck(count, crop.getPrice());
              long fee = 0L;
              if (currentManorId != 0 && manorId != currentManorId) {
                fee = price * 5L / 100L;
              }

              totalFee = SafeMath.addAndCheck(totalFee, fee);
              int rewardItemId = Manor.getInstance().getRewardItem(cropId, crop.getReward());
              ItemTemplate template = ItemHolder.getInstance().getTemplate(rewardItemId);
              if (template == null) {
                return;
              }

              weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, (long)template.getWeight()));
              if (!template.isStackable() || activeChar.getInventory().getItemByItemId(cropId) == null) {
                ++slots;
              }
            }
          } catch (ArithmeticException var35) {
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

            if (activeChar.getInventory().getAdena() < totalFee) {
              activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
              return;
            }

            for(i = 0; i < this._count; ++i) {
              objId = this._items[i];
              cropId = this._crop[i];
              manorId = this._manor[i];
              count = this._itemQ[i];
              item = activeChar.getInventory().getItemByObjectId(objId);
              if (item != null && item.getCount() >= count && item.getItemId() == cropId) {
                castle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, manorId);
                if (castle != null) {
                  crop = castle.getCrop(cropId, 0);
                  if (crop != null && crop.getId() != 0 && crop.getPrice() != 0L && count <= crop.getAmount()) {
                    int rewardItemId = Manor.getInstance().getRewardItem(cropId, crop.getReward());
                    long sellPrice = count * crop.getPrice();
                    long rewardPrice = (long)ItemHolder.getInstance().getTemplate(rewardItemId).getReferencePrice();
                    if (rewardPrice != 0L) {
                      double reward = (double)sellPrice / (double)rewardPrice;
                      long rewardItemCount = (long)reward + (long)(Rnd.nextDouble() <= reward % 1.0D ? 1 : 0);
                      if (rewardItemCount < 1L) {
                        SystemMessage sm = new SystemMessage(1491);
                        sm.addItemName(cropId);
                        sm.addNumber(count);
                        activeChar.sendPacket(sm);
                      } else {
                        long fee = 0L;
                        if (currentManorId != 0 && manorId != currentManorId) {
                          fee = sellPrice * 5L / 100L;
                        }

                        if (activeChar.getInventory().destroyItemByObjectId(objId, count)) {
                          if (!activeChar.reduceAdena(fee, false)) {
                            SystemMessage sm = new SystemMessage(1491);
                            sm.addItemName(cropId);
                            sm.addNumber(count);
                            activeChar.sendPacket(new IStaticPacket[]{sm, Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA});
                          } else {
                            crop.setAmount(crop.getAmount() - count);
                            castle.updateCrop(crop.getId(), crop.getAmount(), 0);
                            castle.addToTreasuryNoTax(fee, false, false);
                            if (activeChar.getInventory().addItem(rewardItemId, rewardItemCount) != null) {
                              activeChar.sendPacket(new IStaticPacket[]{(new SystemMessage(1490)).addItemName(cropId).addNumber(count), SystemMessage2.removeItems(cropId, count), SystemMessage2.obtainItems(rewardItemId, rewardItemCount, 0)});
                              if (fee > 0L) {
                                activeChar.sendPacket((new SystemMessage(1607)).addNumber(fee));
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          } finally {
            activeChar.getInventory().writeUnlock();
          }

          activeChar.sendChanges();
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
