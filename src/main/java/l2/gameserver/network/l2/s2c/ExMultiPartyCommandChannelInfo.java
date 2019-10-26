//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;

public class ExMultiPartyCommandChannelInfo extends L2GameServerPacket {
  private String ChannelLeaderName;
  private int MemberCount;
  private List<ExMultiPartyCommandChannelInfo.ChannelPartyInfo> parties;

  public ExMultiPartyCommandChannelInfo(CommandChannel channel) {
    this.ChannelLeaderName = channel.getChannelLeader().getName();
    this.MemberCount = channel.getMemberCount();
    this.parties = new ArrayList();
    Iterator var2 = channel.getParties().iterator();

    while(var2.hasNext()) {
      Party party = (Party)var2.next();
      Player leader = party.getPartyLeader();
      if (leader != null) {
        this.parties.add(new ExMultiPartyCommandChannelInfo.ChannelPartyInfo(leader.getName(), leader.getObjectId(), party.getMemberCount()));
      }
    }

  }

  protected void writeImpl() {
    this.writeEx(48);
    this.writeS(this.ChannelLeaderName);
    this.writeD(0);
    this.writeD(this.MemberCount);
    this.writeD(this.parties.size());
    Iterator var1 = this.parties.iterator();

    while(var1.hasNext()) {
      ExMultiPartyCommandChannelInfo.ChannelPartyInfo party = (ExMultiPartyCommandChannelInfo.ChannelPartyInfo)var1.next();
      this.writeS(party.Leader_name);
      this.writeD(party.Leader_obj_id);
      this.writeD(party.MemberCount);
    }

  }

  static class ChannelPartyInfo {
    public String Leader_name;
    public int Leader_obj_id;
    public int MemberCount;

    public ChannelPartyInfo(String _Leader_name, int _Leader_obj_id, int _MemberCount) {
      this.Leader_name = _Leader_name;
      this.Leader_obj_id = _Leader_obj_id;
      this.MemberCount = _MemberCount;
    }
  }
}
