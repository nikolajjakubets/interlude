//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.collections.JoinedIterator;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcFriendInstance;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExMPCCClose;
import l2.gameserver.network.l2.s2c.ExMPCCOpen;
import l2.gameserver.network.l2.s2c.ExMPCCPartyInfoUpdate;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class CommandChannel implements PlayerGroup {
  public static final int STRATEGY_GUIDE_ID = 8871;
  public static final int CLAN_IMPERIUM_ID = 391;
  private final List<Party> _commandChannelParties = new CopyOnWriteArrayList();
  private Player _commandChannelLeader;
  private int _commandChannelLvl;
  private Reflection _reflection;
  private MatchingRoom _matchingRoom;

  public CommandChannel(Player leader) {
    this._commandChannelLeader = leader;
    this._commandChannelParties.add(leader.getParty());
    this._commandChannelLvl = leader.getParty().getLevel();
    leader.getParty().setCommandChannel(this);
    this.broadCast(ExMPCCOpen.STATIC);
  }

  public void addParty(Party party) {
    this.broadCast(new ExMPCCPartyInfoUpdate(party, 1));
    this._commandChannelParties.add(party);
    this.refreshLevel();
    party.setCommandChannel(this);
    Iterator var2 = party.iterator();

    while(var2.hasNext()) {
      Player $member = (Player)var2.next();
      $member.sendPacket(ExMPCCOpen.STATIC);
      if (this._matchingRoom != null) {
        this._matchingRoom.broadcastPlayerUpdate($member);
      }
    }

  }

  public void removeParty(Party party) {
    this._commandChannelParties.remove(party);
    this.refreshLevel();
    party.setCommandChannel((CommandChannel)null);
    party.broadCast(new IStaticPacket[]{ExMPCCClose.STATIC});
    Reflection reflection = this.getReflection();
    Iterator var3;
    Player $member;
    if (reflection != null) {
      var3 = party.getPartyMembers().iterator();

      while(var3.hasNext()) {
        $member = (Player)var3.next();
        $member.teleToLocation(reflection.getReturnLoc(), 0);
      }
    }

    if (this._commandChannelParties.size() < 2) {
      this.disbandChannel();
    } else {
      var3 = party.iterator();

      while(var3.hasNext()) {
        $member = (Player)var3.next();
        $member.sendPacket(new ExMPCCPartyInfoUpdate(party, 0));
        if (this._matchingRoom != null) {
          this._matchingRoom.broadcastPlayerUpdate($member);
        }
      }
    }

  }

  public void disbandChannel() {
    this.broadCast(Msg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);
    Iterator var1 = this._commandChannelParties.iterator();

    while(var1.hasNext()) {
      Party party = (Party)var1.next();
      party.setCommandChannel((CommandChannel)null);
      party.broadCast(new IStaticPacket[]{ExMPCCClose.STATIC});
      if (this.isInReflection()) {
        party.broadCast(new IStaticPacket[]{(new SystemMessage(2106)).addNumber(1)});
      }
    }

    Reflection reflection = this.getReflection();
    if (reflection != null) {
      reflection.startCollapseTimer(60000L);
      this.setReflection((Reflection)null);
    }

    if (this._matchingRoom != null) {
      this._matchingRoom.disband();
    }

    this._commandChannelParties.clear();
    this._commandChannelLeader = null;
  }

  public int getMemberCount() {
    int count = 0;

    Party party;
    for(Iterator var2 = this._commandChannelParties.iterator(); var2.hasNext(); count += party.getMemberCount()) {
      party = (Party)var2.next();
    }

    return count;
  }

  public void broadCast(IStaticPacket... gsp) {
    Iterator var2 = this._commandChannelParties.iterator();

    while(var2.hasNext()) {
      Party party = (Party)var2.next();
      party.broadCast(gsp);
    }

  }

  public void broadcastToChannelPartyLeaders(L2GameServerPacket gsp) {
    Iterator var2 = this._commandChannelParties.iterator();

    while(var2.hasNext()) {
      Party party = (Party)var2.next();
      Player leader = party.getPartyLeader();
      if (leader != null) {
        leader.sendPacket(gsp);
      }
    }

  }

  public List<Party> getParties() {
    return this._commandChannelParties;
  }

  /** @deprecated */
  @Deprecated
  public List<Player> getMembers() {
    List<Player> members = new ArrayList(this._commandChannelParties.size());
    Iterator var2 = this.getParties().iterator();

    while(var2.hasNext()) {
      Party party = (Party)var2.next();
      members.addAll(party.getPartyMembers());
    }

    return members;
  }

  public Iterator<Player> iterator() {
    List<Iterator<Player>> iterators = new ArrayList(this._commandChannelParties.size());
    Iterator var2 = this.getParties().iterator();

    while(var2.hasNext()) {
      Party p = (Party)var2.next();
      iterators.add(p.getPartyMembers().iterator());
    }

    return new JoinedIterator(iterators);
  }

  public int getLevel() {
    return this._commandChannelLvl;
  }

  public void setChannelLeader(Player newLeader) {
    this._commandChannelLeader = newLeader;
    this.broadCast((new SystemMessage(1589)).addString(newLeader.getName()));
  }

  public Player getChannelLeader() {
    return this._commandChannelLeader;
  }

  public boolean meetRaidWarCondition(NpcFriendInstance npc) {
    if (!npc.isRaid()) {
      return false;
    } else {
      int npcId = npc.getNpcId();
      switch(npcId) {
        case 29001:
        case 29006:
        case 29014:
        case 29022:
          return this.getMemberCount() > 36;
        case 29019:
          return this.getMemberCount() > 225;
        case 29020:
          return this.getMemberCount() > 56;
        case 29028:
          return this.getMemberCount() > 99;
        default:
          return this.getMemberCount() > 18;
      }
    }
  }

  private void refreshLevel() {
    this._commandChannelLvl = 0;
    Iterator var1 = this._commandChannelParties.iterator();

    while(var1.hasNext()) {
      Party pty = (Party)var1.next();
      if (pty.getLevel() > this._commandChannelLvl) {
        this._commandChannelLvl = pty.getLevel();
      }
    }

  }

  public boolean isInReflection() {
    return this._reflection != null;
  }

  public void setReflection(Reflection reflection) {
    this._reflection = reflection;
  }

  public Reflection getReflection() {
    return this._reflection;
  }

  public static boolean checkAuthority(Player creator) {
    if (creator.getClan() != null && creator.isInParty() && creator.getParty().isLeader(creator) && (!Config.CHECK_CLAN_RANK_ON_COMMAND_CHANNEL_CREATE || creator.getPledgeClass() >= 4)) {
      boolean haveSkill = creator.getSkillLevel(391) > 0;
      boolean haveItem = creator.getInventory().getItemByItemId(8871) != null;
      if (!haveSkill && !haveItem) {
        creator.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
        return false;
      } else {
        return true;
      }
    } else {
      creator.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
      return false;
    }
  }

  public MatchingRoom getMatchingRoom() {
    return this._matchingRoom;
  }

  public void setMatchingRoom(MatchingRoom matchingRoom) {
    this._matchingRoom = matchingRoom;
  }
}
