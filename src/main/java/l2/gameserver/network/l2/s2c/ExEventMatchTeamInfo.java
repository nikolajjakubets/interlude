//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;

public class ExEventMatchTeamInfo extends L2GameServerPacket {
  private int leader_id;
  private int loot;
  private List<ExEventMatchTeamInfo.EventMatchTeamInfo> members = new ArrayList<>();

  public ExEventMatchTeamInfo(List<Player> party, Player exclude) {
    this.leader_id = ((Player)party.get(0)).getObjectId();
    this.loot = ((Player)party.get(0)).getParty().getLootDistribution();
    Iterator var3 = party.iterator();

    while(var3.hasNext()) {
      Player member = (Player)var3.next();
      if (!member.equals(exclude)) {
        this.members.add(new ExEventMatchTeamInfo.EventMatchTeamInfo(member));
      }
    }

  }

  protected void writeImpl() {
    this.writeEx(28);
  }

  public static class EventMatchTeamInfo {
    public String _name;
    public String pet_Name;
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
    public int pet_id;
    public int pet_NpcId;
    public int pet_curHp;
    public int pet_maxHp;
    public int pet_curMp;
    public int pet_maxMp;
    public int pet_level;

    public EventMatchTeamInfo(Player member) {
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
      Summon pet = member.getPet();
      if (pet != null) {
        this.pet_id = pet.getObjectId();
        this.pet_NpcId = pet.getNpcId() + 1000000;
        this.pet_Name = pet.getName();
        this.pet_curHp = (int)pet.getCurrentHp();
        this.pet_maxHp = pet.getMaxHp();
        this.pet_curMp = (int)pet.getCurrentMp();
        this.pet_maxMp = pet.getMaxMp();
        this.pet_level = pet.getLevel();
      } else {
        this.pet_id = 0;
      }

    }
  }
}
