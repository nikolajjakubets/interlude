//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.dao.CharacterPostFriendDAO;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.IntObjectMap.Entry;

public class RequestExDeletePostFriendForPostBox extends L2GameClientPacket {
  private String _name;

  public RequestExDeletePostFriendForPostBox() {
  }

  protected void readImpl() throws Exception {
    this._name = this.readS();
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      if (!StringUtils.isEmpty(this._name)) {
        int key = 0;
        IntObjectMap<String> postFriends = player.getPostFriends();
        Iterator var4 = postFriends.entrySet().iterator();

        while(var4.hasNext()) {
          Entry<String> entry = (Entry)var4.next();
          if (((String)entry.getValue()).equalsIgnoreCase(this._name)) {
            key = entry.getKey();
          }
        }

        if (key == 0) {
          player.sendPacket(SystemMsg.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
        } else {
          player.getPostFriends().remove(key);
          CharacterPostFriendDAO.getInstance().delete(player, key);
          player.sendPacket((new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST)).addString(this._name));
        }
      }
    }
  }
}
