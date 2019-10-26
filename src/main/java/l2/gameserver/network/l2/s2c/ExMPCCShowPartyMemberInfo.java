//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;

public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket {
  private List<ExMPCCShowPartyMemberInfo.PartyMemberInfo> members = new ArrayList();

  public ExMPCCShowPartyMemberInfo(Party party) {
    Iterator var2 = party.getPartyMembers().iterator();

    while(var2.hasNext()) {
      Player _member = (Player)var2.next();
      this.members.add(new ExMPCCShowPartyMemberInfo.PartyMemberInfo(_member.getName(), _member.getObjectId(), _member.getClassId().getId()));
    }

  }

  protected final void writeImpl() {
    this.writeEx(74);
    this.writeD(this.members.size());
    Iterator var1 = this.members.iterator();

    while(var1.hasNext()) {
      ExMPCCShowPartyMemberInfo.PartyMemberInfo member = (ExMPCCShowPartyMemberInfo.PartyMemberInfo)var1.next();
      this.writeS(member.name);
      this.writeD(member.object_id);
      this.writeD(member.class_id);
    }

  }

  static class PartyMemberInfo {
    public String name;
    public int object_id;
    public int class_id;

    public PartyMemberInfo(String _name, int _object_id, int _class_id) {
      this.name = _name;
      this.object_id = _object_id;
      this.class_id = _class_id;
    }
  }
}
