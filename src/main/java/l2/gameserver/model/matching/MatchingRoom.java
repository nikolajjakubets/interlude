//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.matching;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.PlayerGroup;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public abstract class MatchingRoom implements PlayerGroup {
  public static int PARTY_MATCHING = 0;
  public static int CC_MATCHING = 1;
  public static int WAIT_PLAYER = 0;
  public static int ROOM_MASTER = 1;
  public static int PARTY_MEMBER = 2;
  public static int UNION_LEADER = 3;
  public static int UNION_PARTY = 4;
  public static int WAIT_PARTY = 5;
  public static int WAIT_NORMAL = 6;
  private final int _id;
  private int _minLevel;
  private int _maxLevel;
  private int _maxMemberSize;
  private int _lootType;
  private String _topic;
  private final MatchingRoom.PartyListenerImpl _listener = new MatchingRoom.PartyListenerImpl();
  protected final Player _leader;
  protected Set<Player> _members = new CopyOnWriteArraySet();

  public MatchingRoom(Player leader, int minLevel, int maxLevel, int maxMemberSize, int lootType, String topic) {
    this._leader = leader;
    this._id = MatchingRoomManager.getInstance().addMatchingRoom(this);
    this._minLevel = minLevel;
    this._maxLevel = maxLevel;
    this._maxMemberSize = maxMemberSize;
    this._lootType = lootType;
    this._topic = topic;
    this.addMember0(leader, (L2GameServerPacket)null);
  }

  public boolean addMember(Player player) {
    if (this._members.contains(player)) {
      return true;
    } else if (player.getLevel() >= this.getMinLevel() && player.getLevel() <= this.getMaxLevel() && this.getPlayers().size() < this.getMaxMembersSize()) {
      return this.addMember0(player, (new SystemMessage2(this.enterMessage())).addName(player));
    } else {
      player.sendPacket(this.notValidMessage());
      return false;
    }
  }

  private boolean addMember0(Player player, L2GameServerPacket p) {
    if (!this._members.isEmpty()) {
      player.addListener(this._listener);
    }

    this._members.add(player);
    player.setMatchingRoom(this);
    Iterator var3 = this.iterator();

    while(var3.hasNext()) {
      Player $member = (Player)var3.next();
      if ($member != player) {
        $member.sendPacket(new IStaticPacket[]{p, this.addMemberPacket($member, player)});
      }
    }

    MatchingRoomManager.getInstance().removeFromWaitingList(player);
    player.sendPacket(new IStaticPacket[]{this.infoRoomPacket(), this.membersPacket(player)});
    player.sendChanges();
    return true;
  }

  public void removeMember(Player member, boolean oust) {
    if (this._members.remove(member)) {
      member.removeListener(this._listener);
      member.setMatchingRoom((MatchingRoom)null);
      if (this._members.isEmpty()) {
        this.disband();
      } else {
        L2GameServerPacket infoPacket = this.infoRoomPacket();
        SystemMsg exitMessage0 = this.exitMessage(true, oust);
        L2GameServerPacket exitMessage = exitMessage0 != null ? (SystemMessage2)(new SystemMessage2(exitMessage0)).addName(member) : null;
        Iterator var6 = this.iterator();

        while(var6.hasNext()) {
          Player player = (Player)var6.next();
          player.sendPacket(new IStaticPacket[]{infoPacket, this.removeMemberPacket(player, member), exitMessage});
        }
      }

      member.sendPacket(new IStaticPacket[]{this.closeRoomPacket(), this.exitMessage(false, oust)});
      MatchingRoomManager.getInstance().addToWaitingList(member);
      member.sendChanges();
    }
  }

  public void broadcastPlayerUpdate(Player player) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      Player $member = (Player)var2.next();
      $member.sendPacket(this.updateMemberPacket($member, player));
    }

  }

  public void disband() {
    Iterator var1 = this.iterator();

    while(var1.hasNext()) {
      Player player = (Player)var1.next();
      player.removeListener(this._listener);
      player.sendPacket(this.closeRoomMessage());
      player.sendPacket(this.closeRoomPacket());
      player.setMatchingRoom((MatchingRoom)null);
      player.sendChanges();
      MatchingRoomManager.getInstance().addToWaitingList(player);
    }

    this._members.clear();
    MatchingRoomManager.getInstance().removeMatchingRoom(this);
  }

  public abstract SystemMsg notValidMessage();

  public abstract SystemMsg enterMessage();

  public abstract SystemMsg exitMessage(boolean var1, boolean var2);

  public abstract SystemMsg closeRoomMessage();

  public abstract L2GameServerPacket closeRoomPacket();

  public abstract L2GameServerPacket infoRoomPacket();

  public abstract L2GameServerPacket addMemberPacket(Player var1, Player var2);

  public abstract L2GameServerPacket removeMemberPacket(Player var1, Player var2);

  public abstract L2GameServerPacket updateMemberPacket(Player var1, Player var2);

  public abstract L2GameServerPacket membersPacket(Player var1);

  public abstract int getType();

  public abstract int getMemberType(Player var1);

  public void broadCast(IStaticPacket... arg) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(arg);
    }

  }

  public int getId() {
    return this._id;
  }

  public int getMinLevel() {
    return this._minLevel;
  }

  public int getMaxLevel() {
    return this._maxLevel;
  }

  public String getTopic() {
    return this._topic;
  }

  public int getMaxMembersSize() {
    return this._maxMemberSize;
  }

  public int getLocationId() {
    return MatchingRoomManager.getInstance().getLocation(this._leader);
  }

  public Player getLeader() {
    return this._leader;
  }

  public Collection<Player> getPlayers() {
    return this._members;
  }

  public int getLootType() {
    return this._lootType;
  }

  public Iterator<Player> iterator() {
    return this._members.iterator();
  }

  public void setMinLevel(int minLevel) {
    this._minLevel = minLevel;
  }

  public void setMaxLevel(int maxLevel) {
    this._maxLevel = maxLevel;
  }

  public void setTopic(String topic) {
    this._topic = topic;
  }

  public void setMaxMemberSize(int maxMemberSize) {
    this._maxMemberSize = maxMemberSize;
  }

  public void setLootType(int lootType) {
    this._lootType = lootType;
  }

  private class PartyListenerImpl implements OnPlayerPartyInviteListener, OnPlayerPartyLeaveListener {
    private PartyListenerImpl() {
    }

    public void onPartyInvite(Player player) {
      MatchingRoom.this.broadcastPlayerUpdate(player);
    }

    public void onPartyLeave(Player player) {
      MatchingRoom.this.broadcastPlayerUpdate(player);
    }
  }
}
