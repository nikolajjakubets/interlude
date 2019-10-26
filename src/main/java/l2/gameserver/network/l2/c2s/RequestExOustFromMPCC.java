//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestExOustFromMPCC extends L2GameClientPacket {
  private String _name;

  public RequestExOustFromMPCC() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && activeChar.isInParty() && activeChar.getParty().isInCommandChannel()) {
      Player target = World.getPlayer(this._name);
      if (target == null) {
        activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
      } else if (activeChar != target) {
        if (target.isInParty() && target.getParty().isInCommandChannel() && activeChar.getParty().getCommandChannel() == target.getParty().getCommandChannel()) {
          if (activeChar.getParty().getCommandChannel().getChannelLeader() != activeChar) {
            activeChar.sendPacket(Msg.ONLY_THE_CREATOR_OF_A_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND);
          } else {
            target.getParty().getCommandChannel().getChannelLeader().sendPacket((new SystemMessage(1584)).addString(target.getName()));
            target.getParty().getCommandChannel().removeParty(target.getParty());
            target.getParty().broadCast(new IStaticPacket[]{Msg.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL});
          }
        } else {
          activeChar.sendPacket(Msg.INVALID_TARGET);
        }
      }
    }
  }
}
