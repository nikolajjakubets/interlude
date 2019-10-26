//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.ItemInfoCache;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.model.chat.chatfilter.ChatMsg;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.MapUtils;
import l2.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Say2C extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(Say2C.class);
  private static final Pattern EX_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+[\\s]+\tID=([0-9]+)[\\s]+\tColor=[0-9]+[\\s]+\tUnderline=[0-9]+[\\s]+\tTitle=\u001b(.[^\u001b]*)[^\b]");
  private static final Pattern SKIP_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+(.[^\b]*)[\b]");
  private String _text;
  private ChatType _type;
  private String _target;

  public Say2C() {
  }

  protected void readImpl() {
    this._text = this.readS(Config.CHAT_MESSAGE_MAX_LEN);
    this._type = (ChatType)ArrayUtils.valid(ChatType.VALUES, this.readD());
    this._target = this._type == ChatType.TELL ? this.readS(Config.CNAME_MAXLEN) : null;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._type != null && this._text != null && this._text.length() != 0) {
        this._text = this._text.replaceAll("\\\\n", "\n");
        if (this._text.contains("\n")) {
          String[] lines = this._text.split("\n");
          this._text = "";

          for(int i = 0; i < lines.length; ++i) {
            lines[i] = lines[i].trim();
            if (lines[i].length() != 0) {
              if (this._text.length() > 0) {
                this._text = this._text + "\n  >";
              }

              this._text = this._text + lines[i];
            }
          }
        }

        if (this._text.length() == 0) {
          activeChar.sendActionFailed();
        } else if (this._text.startsWith(".")) {
          String fullcmd = this._text.substring(1).trim();
          String command = fullcmd.split("\\s+")[0];
          String args = fullcmd.substring(command.length()).trim();
          if (command.length() > 0) {
            IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
            if (vch != null) {
              vch.useVoicedCommand(command, activeChar, args);
              return;
            }
          }

          activeChar.sendMessage(new CustomMessage("common.command404", activeChar, new Object[0]));
        } else {
          Player receiver = this._target == null ? null : World.getPlayer(this._target);
          long currentTimeMillis = System.currentTimeMillis();
          int objectId;
          if (!activeChar.getPlayerAccess().CanAnnounce) {
            ChatFilter[] var5 = ChatFilters.getinstance().getFilters();
            int var6 = var5.length;

            label309:
            for(objectId = 0; objectId < var6; ++objectId) {
              ChatFilter f = var5[objectId];
              if (f.isMatch(activeChar, this._type, this._text, receiver)) {
                switch(f.getAction()) {
                  case 1:
                    activeChar.updateNoChannel((long)Integer.parseInt(f.getValue()) * 1000L);
                    break label309;
                  case 2:
                    activeChar.sendMessage(new CustomMessage(f.getValue(), activeChar, new Object[0]));
                    return;
                  case 3:
                    this._text = f.getValue();
                    break label309;
                  case 4:
                    this._type = ChatType.valueOf(f.getValue());
                }
              }
            }
          }

          if (activeChar.getNoChannel() > 0L && org.apache.commons.lang3.ArrayUtils.contains(Config.BAN_CHANNEL_LIST, this._type)) {
            if (activeChar.getNoChannelRemained() > 0L) {
              long timeRemained = activeChar.getNoChannelRemained() / 60000L;
              activeChar.sendMessage((new CustomMessage("common.ChatBanned", activeChar, new Object[0])).addNumber(timeRemained));
              return;
            }

            activeChar.updateNoChannel(0L);
          }

          if (!this._text.isEmpty()) {
            Matcher m = EX_ITEM_LINK_PATTERN.matcher(this._text);

            while(m.find()) {
              objectId = Integer.parseInt(m.group(1));
              ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
              if (item == null) {
                activeChar.sendActionFailed();
                break;
              }

              ItemInfoCache.getInstance().put(item);
            }

            String translit = activeChar.getVar("translit");
            if (translit != null) {
              m = SKIP_ITEM_LINK_PATTERN.matcher(this._text);
              StringBuilder sb = new StringBuilder();
              int end = 0;

              while(m.find()) {
                sb.append(Strings.fromTranslit(this._text.substring(end, end = m.start()), translit.equals("tl") ? 1 : 2));
                sb.append(this._text.substring(end, end = m.end()));
              }

              this._text = sb.append(Strings.fromTranslit(this._text.substring(end, this._text.length()), translit.equals("tl") ? 1 : 2)).toString();
            }

            Say2 cs = new Say2(activeChar.getObjectId(), this._type, activeChar.getName(), this._text);
            Player player;
            Iterator var26;
            label277:
            switch(this._type) {
              case TELL:
                if (receiver == null) {
                  activeChar.sendPacket((new SystemMessage(3)).addString(this._target));
                  return;
                }

                if (receiver.isInOfflineMode()) {
                  activeChar.sendMessage(new CustomMessage("common.PlayerInOfflineTrade", activeChar, new Object[0]));
                  return;
                }

                if (receiver.isInBlockList(activeChar) || receiver.isBlockAll()) {
                  activeChar.sendPacket(Msg.YOU_HAVE_BEEN_BLOCKED_FROM_THE_CONTACT_YOU_SELECTED);
                  return;
                }

                if (receiver.getMessageRefusal()) {
                  activeChar.sendPacket(Msg.THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE);
                  return;
                }

                if (activeChar.canTalkWith(receiver)) {
                  receiver.sendPacket(cs);
                }

                cs = new Say2(activeChar.getObjectId(), this._type, "->" + receiver.getName(), this._text);
                activeChar.sendPacket(cs);
                break;
              case SHOUT:
                if (activeChar.isCursedWeaponEquipped()) {
                  activeChar.sendMessage(new CustomMessage("SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON", activeChar, new Object[0]));
                  return;
                }

                if (activeChar.isInObserverMode()) {
                  activeChar.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
                  return;
                }

                if (Config.GLOBAL_SHOUT && activeChar.getLevel() > Config.GLOBAL_SHOUT_MIN_LEVEL && activeChar.getPvpKills() >= Config.GLOBAL_SHOUT_MIN_PVP_COUNT) {
                  announce(activeChar, cs);
                } else {
                  shout(activeChar, cs);
                }

                activeChar.sendPacket(cs);
                break;
              case TRADE:
                if (activeChar.isCursedWeaponEquipped()) {
                  activeChar.sendMessage(new CustomMessage("SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON", activeChar, new Object[0]));
                  return;
                }

                if (activeChar.isInObserverMode()) {
                  activeChar.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
                  return;
                }

                if (Config.GLOBAL_TRADE_CHAT && activeChar.getLevel() > Config.GLOBAL_TRADE_CHAT_MIN_LEVEL && activeChar.getPvpKills() >= Config.GLOBAL_TRADE_MIN_PVP_COUNT) {
                  announce(activeChar, cs);
                } else {
                  shout(activeChar, cs);
                }

                activeChar.sendPacket(cs);
                break;
              case ALL:
                if (activeChar.isCursedWeaponEquipped()) {
                  cs = new Say2(activeChar.getObjectId(), this._type, activeChar.getTransformationName(), this._text);
                }

                List<Player> list = null;
                list = World.getAroundPlayers(activeChar);
                if (list != null) {
                  Iterator var25 = list.iterator();

                  while(var25.hasNext()) {
                    Player player = (Player)var25.next();
                    if (player != activeChar && player.getReflection() == activeChar.getReflection() && !player.isBlockAll() && !player.isInBlockList(activeChar)) {
                      player.sendPacket(cs);
                    }
                  }
                }

                activeChar.sendPacket(cs);
                break;
              case CLAN:
                if (activeChar.getClan() != null) {
                  activeChar.getClan().broadcastToOnlineMembers(new L2GameServerPacket[]{cs});
                }
                break;
              case ALLIANCE:
                if (activeChar.getClan() != null && activeChar.getClan().getAlliance() != null) {
                  activeChar.getClan().getAlliance().broadcastToOnlineMembers(cs);
                }
                break;
              case PARTY:
                if (activeChar.isInParty()) {
                  activeChar.getParty().broadCast(new IStaticPacket[]{cs});
                }
                break;
              case PARTY_ROOM:
                MatchingRoom r = activeChar.getMatchingRoom();
                if (r != null && r.getType() == MatchingRoom.PARTY_MATCHING) {
                  r.broadCast(new IStaticPacket[]{cs});
                }
                break;
              case COMMANDCHANNEL_ALL:
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
                  activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                  return;
                }

                if (activeChar.getParty().getCommandChannel().getChannelLeader() == activeChar) {
                  activeChar.getParty().getCommandChannel().broadCast(new IStaticPacket[]{cs});
                } else {
                  activeChar.sendPacket(Msg.ONLY_CHANNEL_OPENER_CAN_GIVE_ALL_COMMAND);
                }
                break;
              case COMMANDCHANNEL_COMMANDER:
                if (activeChar.isInParty() && activeChar.getParty().isInCommandChannel()) {
                  if (activeChar.getParty().isLeader(activeChar)) {
                    activeChar.getParty().getCommandChannel().broadcastToChannelPartyLeaders(cs);
                  } else {
                    activeChar.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL);
                  }
                  break;
                }

                activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                return;
              case HERO_VOICE:
                if (activeChar.isHero() || activeChar.getPlayerAccess().CanAnnounce || activeChar.getPvpKills() >= Config.PVP_COUNT_TO_CHAT && Config.PVP_HERO_CHAT_SYSTEM) {
                  var26 = GameObjectsStorage.getAllPlayersForIterate().iterator();

                  while(var26.hasNext()) {
                    player = (Player)var26.next();
                    if (!player.isInBlockList(activeChar) && !player.isBlockAll()) {
                      player.sendPacket(cs);
                    }
                  }
                }
                break;
              case PETITION_PLAYER:
              case PETITION_GM:
                if (!PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
                  activeChar.sendPacket(new SystemMessage(745));
                  return;
                }

                PetitionManager.getInstance().sendActivePetitionMessage(activeChar, this._text);
                break;
              case BATTLEFIELD:
                if (activeChar.getBattlefieldChatId() == 0) {
                  return;
                }

                var26 = GameObjectsStorage.getAllPlayersForIterate().iterator();

                while(true) {
                  if (!var26.hasNext()) {
                    break label277;
                  }

                  player = (Player)var26.next();
                  if (!player.isInBlockList(activeChar) && !player.isBlockAll() && player.getBattlefieldChatId() == activeChar.getBattlefieldChatId()) {
                    player.sendPacket(cs);
                  }
                }
              case MPCC_ROOM:
                MatchingRoom r2 = activeChar.getMatchingRoom();
                if (r2 != null && r2.getType() == MatchingRoom.CC_MATCHING) {
                  r2.broadCast(new IStaticPacket[]{cs});
                }
                break;
              default:
                _log.warn("Character " + activeChar.getName() + " used unknown chat type: " + this._type.ordinal() + ".");
            }

            Log.LogChat(this._type.name(), activeChar.getName(), this._target, this._text, 0);
            activeChar.getMessageBucket().addLast(new ChatMsg(this._type, receiver == null ? 0 : receiver.getObjectId(), this._text.hashCode(), (int)(currentTimeMillis / 1000L)));
          }
        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }

  private static void shout(Player activeChar, Say2 cs) {
    int rx = MapUtils.regionX(activeChar);
    int ry = MapUtils.regionY(activeChar);
    int offset = Config.SHOUT_OFFSET;
    Iterator var5 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(true) {
      Player player;
      int tx;
      int ty;
      do {
        do {
          do {
            do {
              do {
                if (!var5.hasNext()) {
                  return;
                }

                player = (Player)var5.next();
              } while(player == activeChar);
            } while(activeChar.getReflection() != player.getReflection());
          } while(player.isBlockAll());
        } while(player.isInBlockList(activeChar));

        tx = MapUtils.regionX(player);
        ty = MapUtils.regionY(player);
      } while((tx < rx - offset || tx > rx + offset || ty < ry - offset || ty > ry + offset) && !activeChar.isInRangeZ(player, (long)Config.CHAT_RANGE));

      player.sendPacket(cs);
    }
  }

  private static void announce(Player activeChar, Say2 cs) {
    Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      if (player != activeChar && activeChar.getReflection() == player.getReflection() && !player.isBlockAll() && !player.isInBlockList(activeChar)) {
        player.sendPacket(cs);
      }
    }

  }
}
