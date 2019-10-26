//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExMpccPartymasterList;

public class RequestExMpccPartymasterList extends L2GameClientPacket {
  public RequestExMpccPartymasterList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      MatchingRoom room = player.getMatchingRoom();
      if (room != null && room.getType() == MatchingRoom.CC_MATCHING) {
        Set<String> set = new HashSet();
        Iterator var4 = room.getPlayers().iterator();

        while(var4.hasNext()) {
          Player $member = (Player)var4.next();
          if ($member.getParty() != null) {
            set.add($member.getParty().getPartyLeader().getName());
          }
        }

        player.sendPacket(new ExMpccPartymasterList(set));
      }
    }
  }
}
