//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EnchantItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.EnchantResult;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.support.EnchantScroll;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestEnchantItem extends L2GameClientPacket {
  private int _objectId;

  public RequestEnchantItem() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    if (client != null) {
      Player player = client.getActiveChar();
      if (player != null) {
        if (player.isActionsDisabled()) {
          player.setEnchantScroll((ItemInstance)null);
          player.sendActionFailed();
        } else {
          long now = System.currentTimeMillis();
          if (now - client.getLastIncomePacketTimeStamp(RequestEnchantItem.class) < (long)Config.ENCHANT_PACKET_DELAY) {
            player.setEnchantScroll((ItemInstance)null);
            player.sendActionFailed();
          } else {
            client.setLastIncomePacketTimeStamp(RequestEnchantItem.class, now);
            if (player.isInTrade()) {
              player.setEnchantScroll((ItemInstance)null);
              player.sendActionFailed();
            } else if (player.isInStoreMode()) {
              player.setEnchantScroll((ItemInstance)null);
              player.sendPacket(EnchantResult.CANCEL);
              player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
              player.sendActionFailed();
            } else {
              PcInventory inventory = player.getInventory();
              inventory.writeLock();

              try {
                ItemInstance item = inventory.getItemByObjectId(this._objectId);
                ItemInstance scroll = player.getEnchantScroll();
                if (item == null || scroll == null) {
                  player.sendActionFailed();
                  return;
                }

                EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scroll.getItemId());
                if (enchantScroll != null) {
                  if (!item.canBeEnchanted(false)) {
                    player.sendPacket(EnchantResult.CANCEL);
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                    player.sendActionFailed();
                    return;
                  }

                  if (!enchantScroll.isUsableWith(item)) {
                    player.sendPacket(EnchantResult.CANCEL);
                    player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                    player.sendActionFailed();
                    return;
                  }

                  double chanceMod = 1.0D + enchantScroll.getChanceMod();
                  int toLvl = item.getEnchantLevel() + enchantScroll.getIncrement();
                  chanceMod *= (double)player.getBonus().getEnchantItemMul();
                  if (!inventory.destroyItem(scroll, 1L)) {
                    player.sendPacket(EnchantResult.CANCEL);
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                    player.sendActionFailed();
                    return;
                  }

                  double chance = ItemFunctions.getEnchantChance(item);
                  if (!enchantScroll.isInfallible() && !Rnd.chance(chance * chanceMod)) {
                    switch(enchantScroll.getOnFailAction()) {
                      case CRYSTALIZE:
                        this.onCrystallizeItem(player, item);
                        return;
                      case RESET:
                        this.onResetItem(player, item, enchantScroll.getFailResultLevel());
                        return;
                      case NONE:
                        this.onEnchantNone(player, item);
                        return;
                      default:
                        return;
                    }
                  }

                  this.onEnchantSuccess(player, item, toLvl);
                  return;
                }

                player.sendActionFailed();
              } finally {
                inventory.writeUnlock();
                player.setEnchantScroll((ItemInstance)null);
                player.updateStats();
              }

            }
          }
        }
      }
    }
  }

  private void onEnchantSuccess(Player player, ItemInstance item, int toLvl) {
    PcInventory inventory = player.getInventory();
    if (toLvl >= 65535) {
      Log.LogItem(player, ItemLog.EnchantFail, item);
      player.sendPacket(new IStaticPacket[]{EnchantResult.CANCEL, SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL});
      player.sendActionFailed();
    } else {
      boolean equipped = item.isEquipped();
      int itemSlot = item.getBodyPart();
      if (equipped) {
        item.setEquipped(false);
        inventory.getListeners().onUnequip(itemSlot, item);
      }

      try {
        item.setEnchantLevel(toLvl);
        Log.LogItem(player, ItemLog.EnchantSuccess, item);
      } finally {
        if (equipped) {
          inventory.getListeners().onEquip(itemSlot, item);
          item.setEquipped(true);
        }

        item.save();
      }

      player.sendPacket(new IStaticPacket[]{(new InventoryUpdate()).addModifiedItem(item), (new SystemMessage(63)).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()), EnchantResult.SUCESS});
      if (Config.SHOW_ENCHANT_EFFECT_RESULT) {
        broadcastResult(player, item);
      }

    }
  }

  private void onCrystallizeItem(Player player, ItemInstance item) {
    PcInventory inventory = player.getInventory();
    boolean equipped = item.isEquipped();
    int itemId = item.getItemId();
    int itemEnchantLevel = item.getEnchantLevel();
    int itemCrystalId = item.getCrystalType().cry;
    int itemCrystalCount = item.getTemplate().getCrystalCount();
    if (equipped) {
      player.sendDisarmMessage(item);
      inventory.unEquipItem(item);
    }

    Log.LogItem(player, ItemLog.EnchantFail, item);
    if (!inventory.destroyItem(item, 1L)) {
      player.sendActionFailed();
    } else {
      if (itemCrystalId > 0 && itemCrystalCount > 0) {
        int crystalAmount = (int)((double)itemCrystalCount * 0.87D);
        if (itemEnchantLevel > 3) {
          crystalAmount = (int)((double)crystalAmount + (double)itemCrystalCount * 0.25D * (double)(itemEnchantLevel - 3));
        }

        if (crystalAmount < 1) {
          crystalAmount = 1;
        }

        player.sendPacket(new IStaticPacket[]{new EnchantResult(1, itemCrystalId, (long)crystalAmount), (new SystemMessage(65)).addNumber(itemEnchantLevel).addItemName(itemId)});
        ItemFunctions.addItem(player, itemCrystalId, (long)crystalAmount, true);
      } else {
        player.sendPacket(new IStaticPacket[]{EnchantResult.FAILED_NO_CRYSTALS, (new SystemMessage(64)).addItemName(item.getItemId())});
      }

    }
  }

  private void onResetItem(Player player, ItemInstance item, int scrollResetLevel) {
    PcInventory inventory = player.getInventory();
    boolean equipped = item.isEquipped();
    int itemSlot = item.getBodyPart();
    int resetLvl = Math.min(item.getEnchantLevel(), scrollResetLevel);
    if (equipped) {
      item.setEquipped(false);
      inventory.getListeners().onUnequip(itemSlot, item);
    }

    try {
      item.setEnchantLevel(resetLvl);
      Log.LogItem(player, ItemLog.EnchantFail, item);
    } finally {
      if (equipped) {
        inventory.getListeners().onEquip(itemSlot, item);
        item.setEquipped(true);
      }

      item.save();
    }

    player.sendPacket(new IStaticPacket[]{(new InventoryUpdate()).addModifiedItem(item), EnchantResult.BLESSED_FAILED, SystemMsg.THE_BLESSED_ENCHANT_FAILED});
  }

  private void onEnchantNone(Player player, ItemInstance item) {
    Log.LogItem(player, ItemLog.EnchantFail, item);
    player.sendPacket(new IStaticPacket[]{EnchantResult.ANCIENT_FAILED, SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS});
  }

  private static final void broadcastResult(Player enchanter, ItemInstance item) {
    MagicSkillUse msu;
    List players;
    Iterator var4;
    Player player;
    if (item.getTemplate().getType2() == 0) {
      if (item.getEnchantLevel() == 7 || item.getEnchantLevel() == 15) {
        msu = new MagicSkillUse(enchanter, enchanter, 2025, 1, 500, 1500L);
        players = World.getAroundPlayers(enchanter);
        var4 = players.iterator();

        while(var4.hasNext()) {
          player = (Player)var4.next();
          player.sendMessage(new CustomMessage("_C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3", player, new Object[]{enchanter, item, item.getEnchantLevel()}));
          player.sendPacket(msu);
        }
      }
    } else if (item.getEnchantLevel() == 6) {
      msu = new MagicSkillUse(enchanter, enchanter, 2025, 1, 500, 1500L);
      players = World.getAroundPlayers(enchanter);
      var4 = players.iterator();

      while(var4.hasNext()) {
        player = (Player)var4.next();
        player.sendMessage(new CustomMessage("_C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3", player, new Object[]{enchanter, item, item.getEnchantLevel()}));
        player.sendPacket(msu);
      }
    }

  }
}
