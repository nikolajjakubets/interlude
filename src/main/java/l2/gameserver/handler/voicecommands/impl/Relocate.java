//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.skills.skillclasses.Call;
import l2.gameserver.utils.Location;

public class Relocate extends Functions implements IVoicedCommandHandler {
  private static final String LAST_USE_TIMESTAMP_VAR = "summonClanLastUse";
  private static final String DISPLAY_NAME = "Clan Summon";
  private final String[] _commandList = new String[]{"summon_clan", "km-all-to-me", "rcm"};

  public Relocate() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player player, String args) {
    if (!Config.SERVICES_CLAN_SUMMON_COMMAND_ENABLE) {
      return false;
    } else {
      Clan cl = player.getClan();
      if (cl == null) {
        player.sendMessage(new CustomMessage("voicecommands.Relocate.NotMember", player, new Object[0]));
        return false;
      } else if (cl.getLeaderId() != player.getObjectId()) {
        player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
        return false;
      } else {
        SystemMessage msg = Call.canSummonHere(player);
        if (msg != null) {
          player.sendMessage("Clan Summon is started");
          player.sendPacket(msg);
          return false;
        } else {
          List<Player> clanMembersOnline = cl.getOnlineMembers(player.getObjectId());
          if (clanMembersOnline.size() < 1) {
            player.sendMessage(new CustomMessage("voicecommands.Relocate.NoClanMember", player, new Object[0]));
            return false;
          } else if (getItemCount(player, Config.SERVICES_CLAN_SUMMON_COMMAND_SELL_ITEM) < (long)Config.SERVICES_CLAN_SUMMON_COMMAND_SELL_PRICE) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
          } else {
            long now = System.currentTimeMillis() / 1000L;
            long lastUseTimestamp = player.getVarLong("summonClanLastUse", 0L);
            if (now - lastUseTimestamp < Config.REUSE_DELAY_FOR_CLAN_SUMMON) {
              player.sendPacket((new SystemMessage(48)).addString("Clan Summon"));
              return false;
            } else {
              removeItem(player, Config.SERVICES_CLAN_SUMMON_COMMAND_SELL_ITEM, (long)Config.SERVICES_CLAN_SUMMON_COMMAND_SELL_PRICE);
              player.sendMessage("Clan Summon is started");
              player.setVar("summonClanLastUse", now, -1L);
              Iterator var11 = clanMembersOnline.iterator();

              while(var11.hasNext()) {
                Player member = (Player)var11.next();
                if (Call.canBeSummoned(member) == null) {
                  member.summonCharacterRequest(player, Location.findPointToStay(player.getX(), player.getY(), player.getZ(), 100, 150, player.getReflection().getGeoIndex()), Config.SERVICES_CLAN_SUMMON_COMMAND_SUMMON_CRYSTAL_COUNT);
                }
              }

              return true;
            }
          }
        }
      }
    }
  }
}
