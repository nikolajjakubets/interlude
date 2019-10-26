//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.FriendAddRequest;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import org.apache.commons.lang3.StringUtils;

public class RequestFriendInvite extends L2GameClientPacket {
  private String _name;

  public RequestFriendInvite() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && !StringUtils.isEmpty(this._name)) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isProcessingRequest()) {
        activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
      } else {
        Player target = World.getPlayer(this._name);
        if (target == null) {
          activeChar.sendPacket(SystemMsg.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
        } else if (target == activeChar) {
          activeChar.sendPacket(SystemMsg.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
        } else if (!target.isBlockAll() && !target.isInBlockList(activeChar) && !target.getMessageRefusal()) {
          if (activeChar.getFriendList().getList().containsKey(target.getObjectId())) {
            activeChar.sendPacket((new SystemMessage2(SystemMsg.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST)).addName(target));
          } else if (activeChar.getFriendList().getList().size() >= 128) {
            activeChar.sendPacket(SystemMsg.YOU_CAN_ONLY_ENTER_UP_128_NAMES_IN_YOUR_FRIENDS_LIST);
          } else if (target.getFriendList().getList().size() >= 128) {
            activeChar.sendPacket(SystemMsg.THE_FRIENDS_LIST_OF_THE_PERSON_YOU_ARE_TRYING_TO_ADD_IS_FULL_SO_REGISTRATION_IS_NOT_POSSIBLE);
          } else if (target.isOlyParticipant()) {
            activeChar.sendPacket(SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
          } else {
            (new Request(L2RequestType.FRIEND, activeChar, target)).setTimeout(10000L);
            activeChar.sendPacket((new SystemMessage2(SystemMsg.C1_HAS_SENT_A_FRIEND_REQUEST)).addName(target));
            target.sendPacket(new IStaticPacket[]{(new SystemMessage2(SystemMsg.C1_HAS_SENT_A_FRIEND_REQUEST)).addName(activeChar), new FriendAddRequest(activeChar.getName())});
          }
        } else {
          activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
        }
      }
    }
  }
}
