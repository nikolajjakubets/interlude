//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;

public class ExListPartyMatchingWaitingRoom extends L2GameServerPacket {
  private List<ExListPartyMatchingWaitingRoom.PartyMatchingWaitingInfo> _waitingList = Collections.emptyList();
  private final int _fullSize;

  public ExListPartyMatchingWaitingRoom(Player searcher, int minLevel, int maxLevel, int page, int[] classes) {
    int first = (page - 1) * 64;
    int firstNot = page * 64;
    int i = 0;
    List<Player> temp = MatchingRoomManager.getInstance().getWaitingList(minLevel, maxLevel, classes);
    this._fullSize = temp.size();
    this._waitingList = new ArrayList(this._fullSize);
    Iterator var10 = temp.iterator();

    while(var10.hasNext()) {
      Player pc = (Player)var10.next();
      if (i >= first && i < firstNot) {
        this._waitingList.add(new ExListPartyMatchingWaitingRoom.PartyMatchingWaitingInfo(pc));
        ++i;
      }
    }

  }

  protected void writeImpl() {
    this.writeEx(54);
    this.writeD(this._fullSize);
    this.writeD(this._waitingList.size());
    Iterator var1 = this._waitingList.iterator();

    while(var1.hasNext()) {
      ExListPartyMatchingWaitingRoom.PartyMatchingWaitingInfo waiting_info = (ExListPartyMatchingWaitingRoom.PartyMatchingWaitingInfo)var1.next();
      this.writeS(waiting_info.name);
      this.writeD(waiting_info.classId);
      this.writeD(waiting_info.level);
    }

  }

  static class PartyMatchingWaitingInfo {
    public final int classId;
    public final int level;
    public final int currentInstance;
    public final String name;
    public final int[] instanceReuses;

    public PartyMatchingWaitingInfo(Player member) {
      this.name = member.getName();
      this.classId = member.getClassId().getId();
      this.level = member.getLevel();
      Reflection ref = member.getReflection();
      this.currentInstance = ref == null ? 0 : ref.getInstancedZoneId();
      this.instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
    }
  }
}
