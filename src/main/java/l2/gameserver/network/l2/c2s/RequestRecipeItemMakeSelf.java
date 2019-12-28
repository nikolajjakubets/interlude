//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.RecipeItemMakeInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.List;

public class RequestRecipeItemMakeSelf extends L2GameClientPacket {
  private int _recipeId;

  public RequestRecipeItemMakeSelf() {
  }

  protected void readImpl() {
    this._recipeId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = this.getClient().getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isProcessingRequest()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else {
        Recipe recipe = RecipeHolder.getInstance().getRecipeById(this._recipeId);
        if (recipe != null && !recipe.getMaterials().isEmpty() && !recipe.getProducts().isEmpty()) {
          if (activeChar.getCurrentMp() < (double)recipe.getMpConsume()) {
            activeChar.sendPacket(Msg.NOT_ENOUGH_MP, new RecipeItemMakeInfo(activeChar, recipe, 0));
          } else if (!activeChar.findRecipe(this._recipeId)) {
            activeChar.sendPacket(Msg.PLEASE_REGISTER_A_RECIPE, ActionFail.getStatic());
          } else {
            boolean succeed = false;
            List<Pair<ItemTemplate, Long>> materials = recipe.getMaterials();
            List<Pair<ItemTemplate, Long>> products = recipe.getProducts();
            activeChar.getInventory().writeLock();

            Iterator var6;
            Pair product;
            long materialAmount;
            try {
              var6 = materials.iterator();

              label288:
              while(true) {
                if (!var6.hasNext()) {
                  int totalWeight = 0;
                  long totalSlotCount = 0L;

                  Pair material;
                  Iterator var20;
                  for(var20 = products.iterator(); var20.hasNext(); totalSlotCount += ((ItemTemplate)material.getKey()).isStackable() ? 1L : (Long)material.getValue()) {
                    material = (Pair)var20.next();
                    totalWeight = (int)((long)totalWeight + (long)((ItemTemplate)material.getKey()).getWeight() * (Long)material.getValue());
                  }

                  if (activeChar.getInventory().validateWeight(totalWeight) && activeChar.getInventory().validateCapacity(totalSlotCount)) {
                    var20 = materials.iterator();

                    while(true) {
                      while(true) {
                        long materialAmountCounter;
                        ItemTemplate materialItem;
                        do {
                          if (!var20.hasNext()) {
                            break label288;
                          }

                          material = (Pair)var20.next();
                          materialItem = (ItemTemplate)material.getKey();
                          materialAmountCounter = (Long)material.getValue();
                        } while(materialAmountCounter <= 0L);

                        if (Config.ALT_GAME_UNREGISTER_RECIPE && materialItem.getItemType() == EtcItemType.RECIPE) {
                          activeChar.unregisterRecipe(RecipeHolder.getInstance().getRecipeByItem(materialItem).getId());
                        } else if (activeChar.getInventory().destroyItemByItemId(materialItem.getItemId(), materialAmountCounter)) {
                          activeChar.sendPacket(SystemMessage2.removeItems(materialItem.getItemId(), materialAmountCounter));
                        }
                      }
                    }
                  }

                  activeChar.sendPacket(Msg.WEIGHT_AND_VOLUME_LIMIT_HAS_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE, new RecipeItemMakeInfo(activeChar, recipe, 0));
                  return;
                }

                product = (Pair)var6.next();
                ItemTemplate materialItem = (ItemTemplate)product.getKey();
                materialAmount = (Long)product.getValue();
                if (materialAmount > 0L) {
                  if (Config.ALT_GAME_UNREGISTER_RECIPE && materialItem.getItemType() == EtcItemType.RECIPE) {
                    Recipe recipe1 = RecipeHolder.getInstance().getRecipeByItem(materialItem);
                    if (!activeChar.hasRecipe(recipe1)) {
                      activeChar.sendPacket(Msg.NOT_ENOUGH_MATERIALS, new RecipeItemMakeInfo(activeChar, recipe, 0));
                      return;
                    }
                  } else {
                    ItemInstance item = activeChar.getInventory().getItemByItemId(materialItem.getItemId());
                    if (item == null || item.getCount() < materialAmount) {
                      activeChar.sendPacket(Msg.NOT_ENOUGH_MATERIALS, new RecipeItemMakeInfo(activeChar, recipe, 0));
                      return;
                    }
                  }
                }
              }
            } finally {
              activeChar.getInventory().writeUnlock();
            }

            activeChar.resetWaitSitTime();
            activeChar.reduceCurrentMp(recipe.getMpConsume(), null);
            if (Rnd.chance(recipe.getSuccessRate())) {
              var6 = products.iterator();

              while(var6.hasNext()) {
                product = (Pair)var6.next();
                int itemId = ((ItemTemplate)product.getKey()).getItemId();
                materialAmount = (Long)product.getValue();
                ItemFunctions.addItem(activeChar, itemId, materialAmount, true);
              }

              succeed = true;
            }

            if (!succeed) {
              var6 = products.iterator();

              while(var6.hasNext()) {
                product = (Pair)var6.next();
                activeChar.sendPacket((new SystemMessage(960)).addItemName(((ItemTemplate)product.getKey()).getItemId()));
              }
            }

            activeChar.sendPacket(new RecipeItemMakeInfo(activeChar, recipe, succeed ? 0 : 1));
          }
        } else {
          activeChar.sendPacket(Msg.THE_RECIPE_IS_INCORRECT);
        }
      }
    }
  }
}
