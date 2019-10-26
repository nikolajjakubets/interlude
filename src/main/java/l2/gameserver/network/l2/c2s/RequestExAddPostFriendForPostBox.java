//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.dao.CharacterPostFriendDAO;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExConfirmAddingPostFriend;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import org.napile.primitive.maps.IntObjectMap;

public class RequestExAddPostFriendForPostBox extends L2GameClientPacket {
  private String _name;

  public RequestExAddPostFriendForPostBox() {
  }

  protected void readImpl() throws Exception {
    this._name = this.readS(Config.CNAME_MAXLEN);
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      int targetObjectId = CharacterDAO.getInstance().getObjectIdByName(this._name);
      if (targetObjectId == 0) {
        player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.NAME_IS_NOT_EXISTS));
      } else if (this._name.equalsIgnoreCase(player.getName())) {
        player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.NAME_IS_NOT_REGISTERED));
      } else {
        IntObjectMap<String> postFriend = player.getPostFriends();
        if (postFriend.size() >= 100) {
          player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.LIST_IS_FULL));
        } else if (postFriend.containsKey(targetObjectId)) {
          player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.ALREADY_ADDED));
        } else {
          CharacterPostFriendDAO.getInstance().insert(player, targetObjectId);
          postFriend.put(targetObjectId, CharacterDAO.getInstance().getNameByObjectId(targetObjectId));
          player.sendPacket(new IStaticPacket[]{(new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST)).addString(this._name), new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.SUCCESS)});
        }
      }
    }
  }
}
