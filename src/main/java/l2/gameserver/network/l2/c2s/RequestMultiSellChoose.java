//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.MultiSellEntry;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemAttributes;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestMultiSellChoose extends L2GameClientPacket {
  private int _listId;
  private int _entryId;
  private long _amount;

  public RequestMultiSellChoose() {
  }

  protected void readImpl() {
    this._listId = this.readD();
    this._entryId = this.readD();
    this._amount = (long)this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._amount >= 1L) {
      MultiSellListContainer list1 = activeChar.getMultisell();
      if (list1 == null) {
        activeChar.sendActionFailed();
        activeChar.setMultisell((MultiSellListContainer)null);
      } else if (list1.getListId() != this._listId) {
        Log.add("Player " + activeChar.getName() + " trying to change multisell list id, ban this player!", "illegal-actions");
        activeChar.sendActionFailed();
        activeChar.setMultisell((MultiSellListContainer)null);
      } else if (activeChar.isActionsDisabled()) {
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
        NpcInstance merchant = activeChar.getLastNpc();
        if (list1.getListId() >= 0 && !activeChar.isGM() && !NpcInstance.canBypassCheck(activeChar, merchant)) {
          activeChar.setMultisell((MultiSellListContainer)null);
        } else {
          MultiSellEntry entry = null;
          Iterator var5 = list1.getEntries().iterator();

          while(var5.hasNext()) {
            MultiSellEntry $entry = (MultiSellEntry)var5.next();
            if ($entry.getEntryId() == this._entryId) {
              entry = $entry;
              break;
            }
          }

          if (entry != null) {
            boolean keepenchant = list1.isKeepEnchant();
            boolean notax = list1.isNoTax();
            List<RequestMultiSellChoose.ItemData> items = new ArrayList<>();
            PcInventory inventory = activeChar.getInventory();
            long totalPrice = 0L;
            Castle castle = merchant != null ? merchant.getCastle(activeChar) : null;
            inventory.writeLock();

            try {
              long tax = SafeMath.mulAndCheck(entry.getTax(), this._amount);
              long slots = 0L;
              long weight = 0L;
              Iterator var18 = entry.getProduction().iterator();

              MultiSellIngredient ingridient;
              while(var18.hasNext()) {
                ingridient = (MultiSellIngredient)var18.next();
                if (ingridient.getItemId() > 0) {
                  ItemTemplate item = ItemHolder.getInstance().getTemplate(ingridient.getItemId());
                  weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(SafeMath.mulAndCheck(ingridient.getItemCount(), this._amount), (long)item.getWeight()));
                  if (item.isStackable()) {
                    if (inventory.getItemByItemId(ingridient.getItemId()) == null) {
                      ++slots;
                    }
                  } else {
                    slots = SafeMath.addAndCheck(slots, this._amount);
                  }
                }
              }

              if (!inventory.validateWeight(weight)) {
                activeChar.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                activeChar.sendActionFailed();
                return;
              }

              if (!inventory.validateCapacity(slots)) {
                activeChar.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                activeChar.sendActionFailed();
                return;
              }

              if (entry.getIngredients().size() == 0) {
                activeChar.sendActionFailed();
                activeChar.setMultisell((MultiSellListContainer)null);
                return;
              }

              var18 = entry.getIngredients().iterator();

              label807:
              while(true) {
                long totalAmount;
                int variation_stat1;
                if (!var18.hasNext()) {
                  int enchantLevel = 0;
                  ItemAttributes attributes = null;
                  variation_stat1 = 0;
                  int variation_stat2 = 0;
                  Iterator var22 = items.iterator();

                  ItemInstance product;
                  while(var22.hasNext()) {
                    RequestMultiSellChoose.ItemData id = (RequestMultiSellChoose.ItemData)var22.next();
                    totalAmount = id.getCount();
                    if (totalAmount > 0L) {
                      if (id.getId() == -200) {
                        activeChar.getClan().incReputation((int)(-totalAmount), false, "MultiSell");
                        activeChar.sendPacket((new SystemMessage(1787)).addNumber(totalAmount));
                      } else if (id.getId() == -100) {
                        activeChar.reducePcBangPoints((int)totalAmount);
                      } else {
                        product = id.getItem();
                        if (!inventory.destroyItem(id.getItem(), totalAmount)) {
                          return;
                        }

                        if (keepenchant && id.getItem().canBeEnchanted(true)) {
                          enchantLevel = id.getItem().getEnchantLevel();
                          attributes = id.getItem().getAttributes();
                          variation_stat1 = id.getItem().getVariationStat1();
                          variation_stat2 = id.getItem().getVariationStat2();
                        }

                        activeChar.sendPacket(SystemMessage2.removeItems(id.getId(), totalAmount));
                        Log.LogItem(activeChar, ItemLog.MultiSellIngredient, product, totalAmount, 0L, this._listId);
                      }
                    }
                  }

                  if (tax > 0L && !notax && castle != null) {
                    activeChar.sendMessage((new CustomMessage("trade.HavePaidTax", activeChar, new Object[0])).addNumber(tax));
                    if (merchant != null && merchant.getReflection() == ReflectionManager.DEFAULT) {
                      castle.addToTreasury(tax, true, false);
                    }
                  }

                  var22 = entry.getProduction().iterator();

                  while(true) {
                    if (!var22.hasNext()) {
                      break label807;
                    }

                    MultiSellIngredient in = (MultiSellIngredient)var22.next();
                    if (in.getItemId() <= 0) {
                      if (in.getItemId() == -200) {
                        activeChar.getClan().incReputation((int)(in.getItemCount() * this._amount), false, "MultiSell");
                        activeChar.sendPacket((new SystemMessage(1781)).addNumber(in.getItemCount() * this._amount));
                      } else if (in.getItemId() == -100) {
                        activeChar.addPcBangPoints((int)(in.getItemCount() * this._amount), false);
                      }
                    } else if (ItemHolder.getInstance().getTemplate(in.getItemId()).isStackable()) {
                      totalAmount = SafeMath.mulAndLimit(in.getItemCount(), this._amount);
                      inventory.addItem(in.getItemId(), totalAmount);
                      activeChar.sendPacket(SystemMessage2.obtainItems(in.getItemId(), totalAmount, 0));
                      Log.LogItem(activeChar, ItemLog.MultiSellProduct, in.getItemId(), totalAmount, 0L, this._listId);
                    } else {
                      for(int i = 0; (long)i < this._amount; ++i) {
                        for(int j = 0; (long)j < in.getItemCount(); ++j) {
                          product = ItemFunctions.createItem(in.getItemId());
                          if (keepenchant) {
                            if (product.canBeEnchanted(true)) {
                              product.setEnchantLevel(enchantLevel);
                              if (attributes != null) {
                                product.setAttributes(attributes.clone());
                              }

                              if (variation_stat1 != 0 || variation_stat2 != 0) {
                                product.setVariationStat1(variation_stat1);
                                product.setVariationStat2(variation_stat2);
                              }
                            }
                          } else {
                            product.setEnchantLevel(in.getItemEnchant());
                            product.setAttributes(in.getItemAttributes().clone());
                          }

                          inventory.addItem(product);
                          activeChar.sendPacket(SystemMessage2.obtainItems(product));
                          Log.LogItem(activeChar, ItemLog.MultiSellProduct, product, product.getCount(), 0L, this._listId);
                        }
                      }
                    }
                  }
                }

                ingridient = (MultiSellIngredient)var18.next();
                variation_stat1 = ingridient.getItemId();
                long ingridientItemCount = ingridient.getItemCount();
                int ingridientEnchant = ingridient.getItemEnchant();
                totalAmount = !ingridient.getMantainIngredient() ? SafeMath.mulAndCheck(ingridientItemCount, this._amount) : ingridientItemCount;
                if (variation_stat1 == -200) {
                  if (activeChar.getClan() == null) {
                    activeChar.sendPacket(Msg.YOU_ARE_NOT_A_CLAN_MEMBER);
                    return;
                  }

                  if ((long)activeChar.getClan().getReputationScore() < totalAmount) {
                    activeChar.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                    return;
                  }

                  if (activeChar.getClan().getLeaderId() != activeChar.getObjectId()) {
                    activeChar.sendPacket((new SystemMessage(9)).addString(activeChar.getName()));
                    return;
                  }

                  if (!ingridient.getMantainIngredient()) {
                    items.add(new RequestMultiSellChoose.ItemData(variation_stat1, totalAmount, (ItemInstance)null));
                  }
                } else if (variation_stat1 == -100) {
                  if ((long)activeChar.getPcBangPoints() < totalAmount) {
                    activeChar.sendPacket(Msg.YOU_ARE_SHORT_OF_ACCUMULATED_POINTS);
                    return;
                  }

                  if (!ingridient.getMantainIngredient()) {
                    items.add(new RequestMultiSellChoose.ItemData(variation_stat1, totalAmount, (ItemInstance)null));
                  }
                } else {
                  ItemTemplate template = ItemHolder.getInstance().getTemplate(variation_stat1);
                  if (!template.isStackable()) {
                    for(int i = 0; (long)i < ingridientItemCount * this._amount; ++i) {
                      List<ItemInstance> list = inventory.getItemsByItemId(variation_stat1);
                      ItemInstance itemToTake;
                      Iterator var30;
                      ItemInstance item;
                      if (keepenchant) {
                        itemToTake = null;
                        var30 = list.iterator();

                        while(var30.hasNext()) {
                          item = (ItemInstance)var30.next();
                          RequestMultiSellChoose.ItemData itmd = new RequestMultiSellChoose.ItemData(item.getItemId(), item.getCount(), item);
                          if ((item.getEnchantLevel() == ingridientEnchant || !item.getTemplate().isEquipment()) && !items.contains(itmd) && item.canBeExchanged(activeChar)) {
                            itemToTake = item;
                            break;
                          }
                        }

                        if (itemToTake == null) {
                          activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                          return;
                        }

                        if (!ingridient.getMantainIngredient()) {
                          items.add(new RequestMultiSellChoose.ItemData(itemToTake.getItemId(), 1L, itemToTake));
                        }
                      } else {
                        itemToTake = null;
                        var30 = list.iterator();

                        while(var30.hasNext()) {
                          item = (ItemInstance)var30.next();
                          if (!items.contains(new RequestMultiSellChoose.ItemData(item.getItemId(), item.getCount(), item)) && (itemToTake == null || item.getEnchantLevel() < itemToTake.getEnchantLevel()) && !item.isShadowItem() && !item.isTemporalItem() && (!item.isAugmented() || Config.ALT_ALLOW_DROP_AUGMENTED) && ItemFunctions.checkIfCanDiscard(activeChar, item)) {
                            itemToTake = item;
                            if (item.getEnchantLevel() == 0) {
                              break;
                            }
                          }
                        }

                        if (itemToTake == null) {
                          activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                          return;
                        }

                        if (!ingridient.getMantainIngredient()) {
                          items.add(new RequestMultiSellChoose.ItemData(itemToTake.getItemId(), 1L, itemToTake));
                        }
                      }
                    }
                  } else {
                    if (variation_stat1 == 57) {
                      totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(ingridientItemCount, this._amount));
                    }

                    ItemInstance item = inventory.getItemByItemId(variation_stat1);
                    if (item == null || item.getCount() < totalAmount) {
                      activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                      return;
                    }

                    if (!ingridient.getMantainIngredient()) {
                      items.add(new RequestMultiSellChoose.ItemData(item.getItemId(), totalAmount, item));
                    }
                  }
                }

                if (activeChar.getAdena() < totalPrice) {
                  activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                  return;
                }
              }
            } catch (ArithmeticException var36) {
              this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              return;
            } finally {
              inventory.writeUnlock();
            }

            activeChar.sendChanges();
            if (!list1.isShowAll()) {
              MultiSellHolder.getInstance().SeparateAndSend(list1, activeChar, castle == null ? 0.0D : castle.getTaxRate());
            }

          }
        }
      }
    }
  }

  private class ItemData {
    private final int _id;
    private final long _count;
    private final ItemInstance _item;

    public ItemData(int id, long count, ItemInstance item) {
      this._id = id;
      this._count = count;
      this._item = item;
    }

    public int getId() {
      return this._id;
    }

    public long getCount() {
      return this._count;
    }

    public ItemInstance getItem() {
      return this._item;
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof RequestMultiSellChoose.ItemData)) {
        return false;
      } else {
        RequestMultiSellChoose.ItemData i = (RequestMultiSellChoose.ItemData)obj;
        return this._id == i._id && this._count == i._count && this._item == i._item;
      }
    }
  }
}
