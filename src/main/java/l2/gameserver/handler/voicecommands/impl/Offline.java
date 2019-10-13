//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class Offline extends Functions implements IVoicedCommandHandler {
  private String[] _commandList = new String[]{"offline"};

  public Offline() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String args) {
    if (!Config.SERVICES_OFFLINE_TRADE_ALLOW) {
      return false;
    } else if (!activeChar.isOlyParticipant() && !ParticipantPool.getInstance().isRegistred(activeChar) && activeChar.getKarma() <= 0) {
      if (activeChar.getLevel() < Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL) {
        show((new CustomMessage("voicedcommandhandlers.Offline.LowLevel", activeChar, new Object[0])).addNumber((long)Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL), activeChar);
        return false;
      } else if (!activeChar.isInZone(ZoneType.offshore) && Config.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE) {
        show(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore", activeChar, new Object[0]), activeChar);
        return false;
      } else if (!activeChar.isInStoreMode()) {
        show(new CustomMessage("voicedcommandhandlers.Offline.IncorrectUse", activeChar, new Object[0]), activeChar);
        return false;
      } else if (activeChar.getNoChannelRemained() > 0L) {
        show(new CustomMessage("voicedcommandhandlers.Offline.BanChat", activeChar, new Object[0]), activeChar);
        return false;
      } else if (activeChar.isActionBlocked("open_private_store")) {
        show(new CustomMessage("trade.OfflineNoTradeZone", activeChar, new Object[0]), activeChar);
        return false;
      } else {
        if (Config.SERVICES_OFFLINE_TRADE_PRICE > 0 && Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM > 0) {
          if (getItemCount(activeChar, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM) < (long)Config.SERVICES_OFFLINE_TRADE_PRICE) {
            show((new CustomMessage("voicedcommandhandlers.Offline.NotEnough", activeChar, new Object[0])).addItemName(Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM).addNumber((long)Config.SERVICES_OFFLINE_TRADE_PRICE), activeChar);
            return false;
          }

          removeItem(activeChar, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM, (long)Config.SERVICES_OFFLINE_TRADE_PRICE);
        }

        activeChar.offline();
        return true;
      }
    } else {
      activeChar.sendActionFailed();
      return false;
    }
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }
}
