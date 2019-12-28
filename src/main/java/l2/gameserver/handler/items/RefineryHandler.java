//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.items;

import l2.commons.util.RandomUtils;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.VariationChanceHolder;
import l2.gameserver.data.xml.holder.VariationGroupHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.support.VariationChanceData;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class RefineryHandler implements IRefineryHandler {
  private static final RefineryHandler _instance = new RefineryHandler();
  protected static final Logger LOG = LoggerFactory.getLogger(RefineryHandler.class);

  public static RefineryHandler getInstance() {
    return _instance;
  }

  private RefineryHandler() {
  }

  public void onInitRefinery(Player player) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendActionFailed();
    } else {
      player.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowRefineryInterface.STATIC);
    }
  }

  public void onPutTargetItem(Player player, ItemInstance targetItem) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendActionFailed();
    } else if (targetItem.isAugmented()) {
      player.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.getStatic());
    } else {
      List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
      if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
        player.sendPacket(Msg.SELECT_THE_CATALYST_FOR_AUGMENTATION, new ExPutItemResultForVariationMake(targetItem.getObjectId(), true));
      } else {
        player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
      }
    }
  }

  public void onPutMineralItem(Player player, ItemInstance targetItem, ItemInstance mineralItem) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendPacket(ActionFail.getStatic());
    } else if (targetItem.isAugmented()) {
      player.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.getStatic());
    } else {
      List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
      if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
        int mineralItemId = mineralItem.getItemId();
        VariationGroupData variationGroupDataOfMineral = null;
        Pair<VariationChanceData, VariationChanceData> variationChanceData = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(mineralItemId);

        for (VariationGroupData variationGroupData : variationGroupDataList) {
          if (variationGroupData.getMineralItemId() == mineralItemId) {
            variationGroupDataOfMineral = variationGroupData;
            break;
          }
        }

        if (null != variationGroupDataOfMineral && null != variationChanceData) {
          if (targetItem.getTemplate().isMageItem() && variationChanceData.getRight() == null) {
            LOG.warn("No mage variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
            player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
          } else if (!targetItem.getTemplate().isMageItem() && variationChanceData.getLeft() == null) {
            LOG.warn("No warrior variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
            player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
          } else if (!mineralItem.getTemplate().testCondition(player, mineralItem, true)) {
            player.sendPacket(ActionFail.getStatic());
          } else {
            player.sendPacket(new ExPutIntensiveResultForVariationMake(mineralItem.getObjectId(), mineralItemId, variationGroupDataOfMineral.getGemstoneItemId(), variationGroupDataOfMineral.getGemstoneItemCnt(), true), (new SystemMessage(1959)).addNumber(variationGroupDataOfMineral.getGemstoneItemCnt()).addItemName(variationGroupDataOfMineral.getGemstoneItemId()));
          }
        } else {
          player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
        }
      } else {
        player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
      }
    }
  }

  public void onPutGemstoneItem(Player player, ItemInstance targetItem, ItemInstance mineralItem, ItemInstance gemstoneItem, long gemstoneItemCnt) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendPacket(ActionFail.getStatic());
    } else if (targetItem.isAugmented()) {
      player.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.getStatic());
    } else {
      List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
      if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
        int mineralItemId = mineralItem.getItemId();
        int gemstoneItemId = gemstoneItem.getItemId();
        VariationGroupData variationGroupDataOfMineral = null;
        Pair<VariationChanceData, VariationChanceData> variationChanceData = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(mineralItemId);

        for (VariationGroupData variationGroupData : variationGroupDataList) {
          if (variationGroupData.getMineralItemId() == mineralItemId && variationGroupData.getGemstoneItemId() == gemstoneItemId) {
            variationGroupDataOfMineral = variationGroupData;
            break;
          }
        }

        if (null != variationGroupDataOfMineral && variationChanceData != null) {
          if ((variationChanceData.getLeft() == null || variationChanceData.getLeft().getMineralItemId() == variationGroupDataOfMineral.getMineralItemId()) && (variationChanceData.getRight() == null || variationChanceData.getRight().getMineralItemId() == variationGroupDataOfMineral.getMineralItemId())) {
            if (targetItem.getTemplate().isMageItem() && variationChanceData.getRight() == null) {
              LOG.warn("No mage variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
              player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
            } else if (!targetItem.getTemplate().isMageItem() && variationChanceData.getLeft() == null) {
              LOG.warn("No warrior variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
              player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
            } else if (!mineralItem.getTemplate().testCondition(player, mineralItem, true)) {
              player.sendPacket(ActionFail.getStatic());
            } else if (variationGroupDataOfMineral.getGemstoneItemCnt() <= gemstoneItemCnt && player.getInventory().getCountOf(gemstoneItemId) >= variationGroupDataOfMineral.getGemstoneItemCnt()) {
              player.sendPacket(new ExPutCommissionResultForVariationMake(gemstoneItem.getObjectId(), variationGroupDataOfMineral.getGemstoneItemCnt()), Msg.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN);
            } else {
              player.sendPacket(Msg.GEMSTONE_QUANTITY_IS_INCORRECT, ActionFail.getStatic());
            }
          } else {
            player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
          }
        } else {
          player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
        }
      } else {
        player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
      }
    }
  }

  public void onRequestRefine(Player player, ItemInstance targetItem, ItemInstance mineralItem, ItemInstance gemstoneItem, long gemstoneItemCnt) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendPacket(ActionFail.getStatic());
    } else if (targetItem.isAugmented()) {
      player.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.getStatic());
    } else {
      List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
      if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
        int mineralItemId = mineralItem.getItemId();
        int gemstoneItemId = gemstoneItem.getItemId();
        VariationGroupData variationGroupDataOfMineral = null;
        Pair<VariationChanceData, VariationChanceData> variationChanceData = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(mineralItemId);
        Iterator variation1Groups = variationGroupDataList.iterator();

        List<Pair<List<Pair<Integer, Double>>, Double>> variation1;
        List<Pair<List<Pair<Integer, Double>>, Double>> variation2;

        VariationGroupData variation2Groups;
        while(variation1Groups.hasNext()) {
          variation2Groups = (VariationGroupData)variation1Groups.next();
          if (variation2Groups.getMineralItemId() == mineralItemId && variation2Groups.getGemstoneItemId() == gemstoneItemId) {
            variationGroupDataOfMineral = variation2Groups;
            break;
          }
        }

        if (null != variationGroupDataOfMineral && variationChanceData != null) {
          if ((variationChanceData.getLeft() == null || variationChanceData.getLeft().getMineralItemId() == variationGroupDataOfMineral.getMineralItemId()) && (variationChanceData.getRight() == null || variationChanceData.getRight().getMineralItemId() == variationGroupDataOfMineral.getMineralItemId())) {
            if (targetItem.getTemplate().isMageItem() && variationChanceData.getRight() == null) {
              LOG.warn("No mage variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
              player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
            } else if (!targetItem.getTemplate().isMageItem() && variationChanceData.getLeft() == null) {
              LOG.warn("No warrior variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
              player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
            } else if (!mineralItem.getTemplate().testCondition(player, mineralItem, true)) {
              player.sendPacket(ActionFail.getStatic());
            } else if (variationGroupDataOfMineral.getGemstoneItemCnt() <= gemstoneItemCnt && player.getInventory().getCountOf(gemstoneItemId) >= variationGroupDataOfMineral.getGemstoneItemCnt()) {
//              List variation1Groups;
//              List variation2Groups;
              if (targetItem.getTemplate().isMageItem()) {
                variation1 = variationChanceData.getRight().getVariation1();
                 variation2 = variationChanceData.getRight().getVariation2();
              } else {
                variation1 = variationChanceData.getLeft().getVariation1();
                variation2 = variationChanceData.getLeft().getVariation2();
              }

              List<Pair<Integer, Double>> pickRandomSortedGroup = RandomUtils.pickRandomSortedGroup(variation1, 100.0D);
              List<Pair<Integer, Double>> pickRandomSortedGroup1 = RandomUtils.pickRandomSortedGroup(variation2, 100.0D);
              Integer option1 = pickRandomSortedGroup != null ? RandomUtils.pickRandomSortedGroup(pickRandomSortedGroup, 100.0D) : 0;
              Integer option2 = pickRandomSortedGroup1 != null ? RandomUtils.pickRandomSortedGroup(pickRandomSortedGroup1, 100.0D) : 0;
              if (player.getInventory().destroyItem(gemstoneItem, variationGroupDataOfMineral.getGemstoneItemCnt())) {
                if (player.getInventory().destroyItem(mineralItem, 1L)) {
                  boolean equipped;
                  if (equipped = targetItem.isEquipped()) {
                    player.getInventory().unEquipItem(targetItem);
                  }

                  targetItem.setVariationStat1(option1);
                  targetItem.setVariationStat2(option2);
                  if (equipped) {
                    player.getInventory().equipItem(targetItem);
                  }

                  player.sendPacket((new InventoryUpdate()).addModifiedItem(targetItem));

                  for (ShortCut sc : player.getAllShortCuts()) {
                    if (sc.getId() == targetItem.getObjectId() && sc.getType() == 1) {
                      player.sendPacket(new ShortCutRegister(player, sc));
                    }
                  }

                  player.sendChanges();
                  player.sendPacket(new ExVariationResult(option1, option2, 1));
                }
              }
            } else {
              player.sendPacket(Msg.GEMSTONE_QUANTITY_IS_INCORRECT, ActionFail.getStatic());
            }
          } else {
            player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
          }
        } else {
          player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
        }
      } else {
        player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
      }
    }
  }

  public void onInitRefineryCancel(Player player) {
    if (Functions.CheckPlayerConditions(player)) {
      player.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
    }
  }

  public void onPutTargetCancelItem(Player player, ItemInstance targetCancelItem) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendPacket(ActionFail.getStatic());
    } else {
      List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetCancelItem.getItemId());
      if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
        if (!targetCancelItem.isAugmented()) {
          player.sendPacket(Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM, ActionFail.getStatic());
        } else {
          VariationGroupData variationGroupData = variationGroupDataList.get(0);
          if (variationGroupData == null) {
            player.sendPacket(ActionFail.getStatic());
          } else {
            player.sendPacket(new ExPutItemResultForVariationCancel(targetCancelItem, variationGroupData.getCancelPrice(), true));
          }
        }
      } else {
        player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
      }
    }
  }

  public void onRequestCancelRefine(Player player, ItemInstance targetCancelItem) {
    if (!Functions.CheckPlayerConditions(player)) {
      player.sendPacket(ActionFail.getStatic());
    } else {
      List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetCancelItem.getItemId());
      if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
        if (!targetCancelItem.isAugmented()) {
          player.sendPacket(Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM, ActionFail.getStatic());
        } else {
          VariationGroupData variationGroupData = variationGroupDataList.get(0);
          if (variationGroupData == null) {
            player.sendPacket(ActionFail.getStatic());
          } else {
            long price = variationGroupData.getCancelPrice();
            if (price < 0L) {
              player.sendPacket(new ExVariationCancelResult(0));
            }

            if (!player.reduceAdena(price, true)) {
              player.sendPacket(ActionFail.getStatic(), Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
              boolean equipped;
              if (equipped = targetCancelItem.isEquipped()) {
                player.getInventory().unEquipItem(targetCancelItem);
              }

              targetCancelItem.setVariationStat1(0);
              targetCancelItem.setVariationStat2(0);
              if (equipped) {
                player.getInventory().equipItem(targetCancelItem);
              }

              InventoryUpdate iu = (new InventoryUpdate()).addModifiedItem(targetCancelItem);
              SystemMessage sm = new SystemMessage(1965);
              sm.addItemName(targetCancelItem.getItemId());
              player.sendPacket(new ExVariationCancelResult(1), iu, sm);

              for (ShortCut sc : player.getAllShortCuts()) {
                if (sc.getId() == targetCancelItem.getObjectId() && sc.getType() == 1) {
                  player.sendPacket(new ShortCutRegister(player, sc));
                }
              }

              player.sendChanges();
            }
          }
        }
      } else {
        player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.getStatic());
      }
    }
  }
}
