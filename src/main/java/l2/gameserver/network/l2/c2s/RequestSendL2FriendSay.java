//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2FriendSay;
import l2.gameserver.utils.Log;

public class RequestSendL2FriendSay extends L2GameClientPacket {
  private String _message;
  private String _reciever;

  public RequestSendL2FriendSay() {
  }

  protected void readImpl() {
    this._message = this.readS(2048);
    this._reciever = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Player targetPlayer = World.getPlayer(this._reciever);
      if (targetPlayer == null) {
        activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
      } else if (targetPlayer.isBlockAll()) {
        activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
      } else {
        if (!activeChar.getPlayerAccess().CanAnnounce) {
          ChatFilter[] var3 = ChatFilters.getinstance().getFilters();
          int var4 = var3.length;

          label49:
          for(int var5 = 0; var5 < var4; ++var5) {
            ChatFilter f = var3[var5];
            if (f.isMatch(activeChar, ChatType.L2FRIEND, this._message, targetPlayer)) {
              switch(f.getAction()) {
                case 1:
                  activeChar.updateNoChannel((long)Integer.parseInt(f.getValue()) * 1000L);
                  break label49;
                case 2:
                  activeChar.sendMessage(new CustomMessage(f.getValue(), activeChar, new Object[0]));
                  return;
                case 3:
                  this._message = f.getValue();
                  break label49;
              }
            }
          }
        }

        if (activeChar.getNoChannel() > 0L && ArrayUtils.contains(Config.BAN_CHANNEL_LIST, ChatType.L2FRIEND)) {
          if (activeChar.getNoChannelRemained() > 0L) {
            long timeRemained = activeChar.getNoChannelRemained() / 60000L + 1L;
            activeChar.sendMessage((new CustomMessage("common.ChatBanned", activeChar, new Object[0])).addNumber(timeRemained));
            return;
          }

          activeChar.updateNoChannel(0L);
        }

        if (activeChar.getFriendList().getList().containsKey(targetPlayer.getObjectId())) {
          if (activeChar.canTalkWith(targetPlayer)) {
            L2FriendSay frm = new L2FriendSay(activeChar.getName(), this._reciever, this._message);
            targetPlayer.sendPacket(frm);
            Log.LogChat("FRIENDTELL", activeChar.getName(), this._reciever, this._message, 0);
          }

        }
      }
    }
  }
}
