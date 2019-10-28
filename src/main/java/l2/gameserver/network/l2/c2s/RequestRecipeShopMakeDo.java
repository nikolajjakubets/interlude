//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RecipeShopItemInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.TradeHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.List;

public class RequestRecipeShopMakeDo extends L2GameClientPacket {
  private int _manufacturerId;
  private int _recipeId;
  private long _price;

  public RequestRecipeShopMakeDo() {
  }

  protected void readImpl() {
    this._manufacturerId = this.readD();
    this._recipeId = this.readD();
    this._price = (long)this.readD();
  }

  protected void runImpl() {
    Player buyer = ((GameClient)this.getClient()).getActiveChar();
    if (buyer != null) {
      if (buyer.isActionsDisabled()) {
        buyer.sendActionFailed();
      } else if (buyer.isInStoreMode()) {
        buyer.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (buyer.isInTrade()) {
        buyer.sendActionFailed();
      } else if (buyer.isFishing()) {
        buyer.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
      } else if (!buyer.getPlayerAccess().UseTrade) {
        buyer.sendPacket(Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
      } else {
        Player manufacturer = (Player)buyer.getVisibleObject(this._manufacturerId);
        if (manufacturer != null && manufacturer.getPrivateStoreType() == 5 && manufacturer.isInActingRange(buyer)) {
          Recipe recipe = null;
          Iterator var4 = manufacturer.getCreateList().iterator();

          while(var4.hasNext()) {
            ManufactureItem mi = (ManufactureItem)var4.next();
            if (mi.getRecipeId() == this._recipeId && this._price == mi.getCost()) {
              recipe = RecipeHolder.getInstance().getRecipeById(this._recipeId);
              break;
            }
          }

          if (recipe == null) {
            buyer.sendActionFailed();
          } else {
            int success = 0;
            if (!recipe.getProducts().isEmpty() && !recipe.getMaterials().isEmpty()) {
              if (!manufacturer.findRecipe(this._recipeId)) {
                buyer.sendActionFailed();
              } else if (manufacturer.getCurrentMp() < (double)recipe.getMpConsume()) {
                manufacturer.sendPacket(Msg.NOT_ENOUGH_MP);
                buyer.sendPacket(new IStaticPacket[]{Msg.NOT_ENOUGH_MP, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success)});
              } else {
                List<Pair<ItemTemplate, Long>> materials = recipe.getMaterials();
                List<Pair<ItemTemplate, Long>> products = recipe.getProducts();
                buyer.getInventory().writeLock();

                Iterator var7;
                Pair product;
                long tax;
                try {
                  if (buyer.getAdena() < this._price) {
                    buyer.sendPacket(new IStaticPacket[]{Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success)});
                    return;
                  }

                  var7 = materials.iterator();

                  while(var7.hasNext()) {
                    product = (Pair)var7.next();
                    ItemTemplate materialItem = (ItemTemplate)product.getKey();
                    tax = (Long)product.getValue();
                    if (tax > 0L) {
                      ItemInstance item = buyer.getInventory().getItemByItemId(materialItem.getItemId());
                      if (item == null || item.getCount() < tax) {
                        buyer.sendPacket(new IStaticPacket[]{Msg.NOT_ENOUGH_MATERIALS, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success)});
                        return;
                      }
                    }
                  }

                  int totalWeight = 0;
                  long totalSlotCount = 0L;

                  Pair material;
                  Iterator var26;
                  for(var26 = products.iterator(); var26.hasNext(); totalSlotCount += ((ItemTemplate)material.getKey()).isStackable() ? 1L : (Long)material.getValue()) {
                    material = (Pair)var26.next();
                    totalWeight = (int)((long)totalWeight + (long)((ItemTemplate)material.getKey()).getWeight() * (Long)material.getValue());
                  }

                  if (!buyer.getInventory().validateWeight((long)totalWeight) || !buyer.getInventory().validateCapacity(totalSlotCount)) {
                    buyer.sendPacket(new IStaticPacket[]{Msg.THE_WEIGHT_AND_VOLUME_LIMIT_OF_INVENTORY_MUST_NOT_BE_EXCEEDED, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success)});
                    return;
                  }

                  if (!buyer.reduceAdena(this._price, false)) {
                    buyer.sendPacket(new IStaticPacket[]{Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success)});
                    return;
                  }

                  var26 = materials.iterator();

                  while(var26.hasNext()) {
                    material = (Pair)var26.next();
                    ItemTemplate materialItem = (ItemTemplate)material.getKey();
                    long materialAmount = (Long)material.getValue();
                    if (materialAmount > 0L) {
                      buyer.getInventory().destroyItemByItemId(materialItem.getItemId(), materialAmount);
                      buyer.sendPacket(SystemMessage2.removeItems(materialItem.getItemId(), materialAmount));
                    }
                  }

                  tax = TradeHelper.getTax(manufacturer, this._price);
                  if (tax > 0L) {
                    this._price -= tax;
                    manufacturer.sendMessage((new CustomMessage("trade.HavePaidTax", manufacturer, new Object[0])).addNumber(tax));
                  }

                  manufacturer.addAdena(this._price);
                } finally {
                  buyer.getInventory().writeUnlock();
                }

                var7 = products.iterator();

                while(var7.hasNext()) {
                  product = (Pair)var7.next();
                  manufacturer.sendMessage((new CustomMessage("l2p.gameserver.RecipeController.GotOrder", manufacturer, new Object[0])).addItemName((ItemTemplate)product.getKey()));
                }

                manufacturer.reduceCurrentMp((double)recipe.getMpConsume(), (Creature)null);
                manufacturer.sendStatusUpdate(false, false, new int[]{11});
                if (Rnd.chance(recipe.getSuccessRate())) {
                  var7 = products.iterator();

                  while(var7.hasNext()) {
                    product = (Pair)var7.next();
                    int itemId = ((ItemTemplate)product.getKey()).getItemId();
                    tax = (Long)product.getValue();
                    ItemFunctions.addItem(buyer, itemId, tax, true);
                  }

                  success = 1;
                }

                SystemMessage sm;
                Iterator var24;
                Pair pair;
                int itemId;
                if (success == 0) {
                  var24 = products.iterator();

                  while(var24.hasNext()) {
                    pair = (Pair) var24.next();
                    itemId = ((ItemTemplate) pair.getKey()).getItemId();
                    sm = new SystemMessage(1150);
                    sm.addString(manufacturer.getName());
                    sm.addItemName(itemId);
                    sm.addNumber(this._price);
                    buyer.sendPacket(sm);
                    sm = new SystemMessage(1149);
                    sm.addString(buyer.getName());
                    sm.addItemName(itemId);
                    sm.addNumber(this._price);
                    manufacturer.sendPacket(sm);
                  }
                } else {
                  var24 = products.iterator();

                  while(var24.hasNext()) {
                    pair = (Pair) var24.next();
                    itemId = ((ItemTemplate) pair.getKey()).getItemId();
                    long count = (Long) pair.getValue();
                    if (count > 1L) {
                      sm = new SystemMessage(1148);
                      sm.addString(manufacturer.getName());
                      sm.addItemName(itemId);
                      sm.addNumber(count);
                      sm.addNumber(this._price);
                      buyer.sendPacket(sm);
                      sm = new SystemMessage(1152);
                      sm.addString(buyer.getName());
                      sm.addItemName(itemId);
                      sm.addNumber(count);
                      sm.addNumber(this._price);
                      manufacturer.sendPacket(sm);
                    } else {
                      sm = new SystemMessage(1146);
                      sm.addString(manufacturer.getName());
                      sm.addItemName(itemId);
                      sm.addNumber(this._price);
                      buyer.sendPacket(sm);
                      sm = new SystemMessage(1151);
                      sm.addString(buyer.getName());
                      sm.addItemName(itemId);
                      sm.addNumber(this._price);
                      manufacturer.sendPacket(sm);
                    }
                  }
                }

                buyer.sendChanges();
                buyer.sendPacket(new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success));
              }
            } else {
              manufacturer.sendMessage((new CustomMessage("l2p.gameserver.RecipeController.NoRecipe", manufacturer, new Object[0])).addItemName(recipe.getItem()));
              buyer.sendMessage((new CustomMessage("l2p.gameserver.RecipeController.NoRecipe", manufacturer, new Object[0])).addItemName(recipe.getItem()));
            }
          }
        } else {
          buyer.sendActionFailed();
        }
      }
    }
  }
}
