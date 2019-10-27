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

public class PartySmallWindowAll extends L2GameServerPacket {
  private int leaderId;
  private int loot;
  private List<PartySmallWindowAll.PartySmallWindowMemberInfo> members = new ArrayList<>();

  public PartySmallWindowAll(Party party, Player exclude) {
    this.leaderId = party.getPartyLeader().getObjectId();
    this.loot = party.getLootDistribution();
    Iterator var3 = party.getPartyMembers().iterator();

    while(var3.hasNext()) {
      Player member = (Player)var3.next();
      if (member != exclude) {
        this.members.add(new PartySmallWindowAll.PartySmallWindowMemberInfo(member));
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(78);
    this.writeD(this.leaderId);
    this.writeD(this.loot);
    this.writeD(this.members.size());
    Iterator var1 = this.members.iterator();

    while(var1.hasNext()) {
      PartySmallWindowAll.PartySmallWindowMemberInfo member = (PartySmallWindowAll.PartySmallWindowMemberInfo)var1.next();
      this.writeD(member._id);
      this.writeS(member._name);
      this.writeD(member.curCp);
      this.writeD(member.maxCp);
      this.writeD(member.curHp);
      this.writeD(member.maxHp);
      this.writeD(member.curMp);
      this.writeD(member.maxMp);
      this.writeD(member.level);
      this.writeD(member.class_id);
      this.writeD(0);
      this.writeD(member.race_id);
    }

  }

  public static class PartySmallWindowMemberInfo {
    public String _name;
    public int _id;
    public int curCp;
    public int maxCp;
    public int curHp;
    public int maxHp;
    public int curMp;
    public int maxMp;
    public int level;
    public int class_id;
    public int race_id;

    public PartySmallWindowMemberInfo(Player member) {
      this._name = member.getName();
      this._id = member.getObjectId();
      this.curCp = (int)member.getCurrentCp();
      this.maxCp = member.getMaxCp();
      this.curHp = (int)member.getCurrentHp();
      this.maxHp = member.getMaxHp();
      this.curMp = (int)member.getCurrentMp();
      this.maxMp = member.getMaxMp();
      this.level = member.getLevel();
      this.class_id = member.getClassId().getId();
      this.race_id = member.getRace().ordinal();
    }
  }
}
