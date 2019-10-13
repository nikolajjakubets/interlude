//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.ManorManagerInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.SeedProduction;

public class RequestBuySeed extends L2GameClientPacket {
  private int _count;
  private int _manorId;
  private int[] _items;
  private long[] _itemQ;

  public RequestBuySeed() {
  }

  protected void readImpl() {
    this._manorId = this.readD();
    this._count = this.readD();
    if (this._count * 8 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
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
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
        activeChar.sendActionFailed();
      } else {
        GameObject target = activeChar.getTarget();
        ManorManagerInstance manor = target != null && target instanceof ManorManagerInstance ? (ManorManagerInstance)target : null;
        if (activeChar.isGM() || manor != null && manor.isInActingRange(activeChar)) {
          Castle castle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
          if (castle != null) {
            long totalPrice = 0L;
            int slots = 0;
            long weight = 0L;

            int i;
            int seedId;
            long count;
            try {
              for(i = 0; i < this._count; ++i) {
                seedId = this._items[i];
                count = this._itemQ[i];
                long price = 0L;
                long residual = 0L;
                SeedProduction seed = castle.getSeed(seedId, 0);
                price = seed.getPrice();
                residual = seed.getCanProduce();
                if (price < 1L) {
                  return;
                }

                if (residual < count) {
                  return;
                }

                totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(count, price));
                ItemTemplate item = ItemHolder.getInstance().getTemplate(seedId);
                if (item == null) {
                  return;
                }

                weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, (long)item.getWeight()));
                if (!item.isStackable() || activeChar.getInventory().getItemByItemId(seedId) == null) {
                  ++slots;
                }
              }
            } catch (ArithmeticException var24) {
              this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              return;
            }

            activeChar.getInventory().writeLock();

            label302: {
              try {
                if (activeChar.getInventory().validateWeight(weight)) {
                  if (!activeChar.getInventory().validateCapacity((long)slots)) {
                    this.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                    return;
                  }

                  if (!activeChar.reduceAdena(totalPrice, true)) {
                    this.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    return;
                  }

                  castle.addToTreasuryNoTax(totalPrice, false, true);
                  i = 0;

                  while(true) {
                    if (i >= this._count) {
                      break label302;
                    }

                    seedId = this._items[i];
                    count = this._itemQ[i];
                    SeedProduction seed = castle.getSeed(seedId, 0);
                    seed.setCanProduce(seed.getCanProduce() - count);
                    castle.updateSeed(seed.getId(), seed.getCanProduce(), 0);
                    activeChar.getInventory().addItem(seedId, count);
                    activeChar.sendPacket(SystemMessage2.obtainItems(seedId, count, 0));
                    ++i;
                  }
                }

                this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
              } finally {
                activeChar.getInventory().writeUnlock();
              }

              return;
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
