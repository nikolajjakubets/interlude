//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import l2.commons.collections.LazyArrayList;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExAskModifyPartyLooting;
import l2.gameserver.network.l2.s2c.ExMPCCClose;
import l2.gameserver.network.l2.s2c.ExMPCCOpen;
import l2.gameserver.network.l2.s2c.ExSetPartyLooting;
import l2.gameserver.network.l2.s2c.GetItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PartyMemberPosition;
import l2.gameserver.network.l2.s2c.PartySmallWindowAdd;
import l2.gameserver.network.l2.s2c.PartySmallWindowAll;
import l2.gameserver.network.l2.s2c.PartySmallWindowDelete;
import l2.gameserver.network.l2.s2c.PartySmallWindowDeleteAll;
import l2.gameserver.network.l2.s2c.PartySpelled;
import l2.gameserver.network.l2.s2c.RelationChanged;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class Party implements PlayerGroup {
  public static final int MAX_SIZE = 9;
  public static final int ITEM_LOOTER = 0;
  public static final int ITEM_RANDOM = 1;
  public static final int ITEM_RANDOM_SPOIL = 2;
  public static final int ITEM_ORDER = 3;
  public static final int ITEM_ORDER_SPOIL = 4;
  private final List<Player> _members = new CopyOnWriteArrayList();
  private int _partyLvl = 0;
  private int _itemDistribution = 0;
  private int _itemOrder = 0;
  private int _dimentionalRift;
  private Reflection _reflection;
  private CommandChannel _commandChannel;
  public double _rateExp;
  public double _rateSp;
  public double _rateDrop;
  public double _rateAdena;
  public double _rateSpoil;
  private ScheduledFuture<?> positionTask;
  private int _requestChangeLoot = -1;
  private long _requestChangeLootTimer = 0L;
  private Set<Integer> _changeLootAnswers = null;
  private static final int[] LOOT_SYSSTRINGS = new int[]{487, 488, 798, 799, 800};
  private Future<?> _checkTask = null;

  public Party(Player leader, int itemDistribution) {
    this._itemDistribution = itemDistribution;
    this._members.add(leader);
    this._partyLvl = leader.getLevel();
    this._rateExp = (double)leader.getBonus().getRateXp();
    this._rateSp = (double)leader.getBonus().getRateSp();
    this._rateAdena = (double)leader.getBonus().getDropAdena();
    this._rateDrop = (double)leader.getBonus().getDropItems();
    this._rateSpoil = (double)leader.getBonus().getDropSpoil();
  }

  public int getMemberCount() {
    return this._members.size();
  }

  public int getMemberCountInRange(Player player, int range) {
    int count = 0;
    Iterator var4 = this._members.iterator();

    while(true) {
      Player member;
      do {
        if (!var4.hasNext()) {
          return count;
        }

        member = (Player)var4.next();
      } while(member != player && !member.isInRangeZ(player, (long)range));

      ++count;
    }
  }

  public List<Player> getPartyMembers() {
    return this._members;
  }

  public List<Integer> getPartyMembersObjIds() {
    List<Integer> result = new ArrayList(this._members.size());
    Iterator var2 = this._members.iterator();

    while(var2.hasNext()) {
      Player member = (Player)var2.next();
      result.add(member.getObjectId());
    }

    return result;
  }

  public List<Playable> getPartyMembersWithPets() {
    List<Playable> result = new ArrayList();
    Iterator var2 = this._members.iterator();

    while(var2.hasNext()) {
      Player member = (Player)var2.next();
      result.add(member);
      if (member.getPet() != null) {
        result.add(member.getPet());
      }
    }

    return result;
  }

  private Player getNextLooterInRange(Player player, ItemInstance item, int range) {
    synchronized(this._members) {
      int antiloop = this._members.size();

      Player ret;
      do {
        --antiloop;
        if (antiloop <= 0) {
          return player;
        }

        int looter = this._itemOrder++;
        if (this._itemOrder > this._members.size() - 1) {
          this._itemOrder = 0;
        }

        ret = looter < this._members.size() ? (Player)this._members.get(looter) : player;
      } while(ret == null || ret.isDead() || !ret.isInRangeZ(player, (long)range) || !ret.getInventory().validateCapacity(item) || !ret.getInventory().validateWeight(item));

      return ret;
    }
  }

  public boolean isLeader(Player player) {
    return this.getPartyLeader() == player;
  }

  public Player getPartyLeader() {
    synchronized(this._members) {
      return this._members.size() == 0 ? null : (Player)this._members.get(0);
    }
  }

  public void broadCast(IStaticPacket... msg) {
    Iterator var2 = this._members.iterator();

    while(var2.hasNext()) {
      Player member = (Player)var2.next();
      member.sendPacket(msg);
    }

  }

  public void broadcastMessageToPartyMembers(String msg) {
    this.broadCast(new SystemMessage(msg));
  }

  public void broadcastToPartyMembers(Player exclude, L2GameServerPacket msg) {
    Iterator var3 = this._members.iterator();

    while(var3.hasNext()) {
      Player member = (Player)var3.next();
      if (exclude != member) {
        member.sendPacket(msg);
      }
    }

  }

  public void broadcastToPartyMembersInRange(Player player, L2GameServerPacket msg, int range) {
    Iterator var4 = this._members.iterator();

    while(var4.hasNext()) {
      Player member = (Player)var4.next();
      if (player.isInRangeZ(member, (long)range)) {
        member.sendPacket(msg);
      }
    }

  }

  public boolean containsMember(Player player) {
    return this._members.contains(player);
  }

  public boolean addPartyMember(Player player) {
    Player leader = this.getPartyLeader();
    if (leader == null) {
      return false;
    } else {
      synchronized(this._members) {
        if (this._members.isEmpty()) {
          return false;
        }

        if (this._members.contains(player)) {
          return false;
        }

        if (this._members.size() == Config.ALT_MAX_PARTY_SIZE) {
          return false;
        }

        this._members.add(player);
      }

      if (this._requestChangeLoot != -1) {
        this.finishLootRequest(false);
      }

      player.setParty(this);
      player.getListeners().onPartyInvite();
      List<L2GameServerPacket> addInfo = new ArrayList(4 + this._members.size() * 4);
      List<L2GameServerPacket> pplayer = new ArrayList(20);
      pplayer.add(new PartySmallWindowAll(this, player));
      pplayer.add((new SystemMessage(106)).addName(leader));
      addInfo.add((new SystemMessage(107)).addName(player));
      addInfo.add(new PartySpelled(player, true));
      Summon pet;
      if ((pet = player.getPet()) != null) {
        addInfo.add(new PartySpelled(pet, true));
      }

      PartyMemberPosition pmp = new PartyMemberPosition();
      Iterator var8 = this._members.iterator();

      while(var8.hasNext()) {
        Player member = (Player)var8.next();
        if (member != player) {
          List<L2GameServerPacket> pmember = new ArrayList(addInfo.size() + 4);
          pmember.add(new PartySmallWindowAdd(member, player));
          pmember.addAll(addInfo);
          pmember.add(RelationChanged.create(member, player, member));
          pmember.add((new PartyMemberPosition()).add(player));
          member.sendPacket(pmember);
          pplayer.add(new PartySpelled(member, true));
          if ((pet = member.getPet()) != null) {
            pplayer.add(new PartySpelled(pet, true));
          }

          pplayer.add(RelationChanged.create(player, member, player));
          pmp.add(member);
        }
      }

      pplayer.add(pmp);
      if (this.isInCommandChannel()) {
        pplayer.add(ExMPCCOpen.STATIC);
      }

      player.sendPacket(pplayer);
      this.startUpdatePositionTask();
      this.recalculatePartyData();
      if (this.isInReflection() && this.getReflection() instanceof DimensionalRift) {
        ((DimensionalRift)this.getReflection()).partyMemberInvited();
      }

      return true;
    }
  }

  public void dissolveParty() {
    Iterator var1 = this._members.iterator();

    while(var1.hasNext()) {
      Player p = (Player)var1.next();
      p.sendPacket(PartySmallWindowDeleteAll.STATIC);
      p.setParty((Party)null);
    }

    synchronized(this._members) {
      this._members.clear();
    }

    this.setDimensionalRift((DimensionalRift)null);
    this.setCommandChannel((CommandChannel)null);
    this.stopUpdatePositionTask();
  }

  public boolean removePartyMember(Player player, boolean kick) {
    boolean isLeader = this.isLeader(player);
    boolean dissolve = false;
    synchronized(this._members) {
      if (!this._members.remove(player)) {
        return false;
      }

      dissolve = this._members.size() == 1;
    }

    player.getListeners().onPartyLeave();
    player.setParty((Party)null);
    this.recalculatePartyData();
    List<L2GameServerPacket> pplayer = new ArrayList(4 + this._members.size() * 2);
    if (this.isInCommandChannel()) {
      pplayer.add(ExMPCCClose.STATIC);
    }

    if (kick) {
      pplayer.add(Msg.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY);
    } else {
      pplayer.add(Msg.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
    }

    pplayer.add(PartySmallWindowDeleteAll.STATIC);
    List<L2GameServerPacket> outsInfo = new ArrayList(3);
    outsInfo.add(new PartySmallWindowDelete(player));
    if (kick) {
      outsInfo.add((new SystemMessage(201)).addName(player));
    } else {
      outsInfo.add((new SystemMessage(108)).addName(player));
    }

    Iterator var8 = this._members.iterator();

    Player leader;
    while(var8.hasNext()) {
      leader = (Player)var8.next();
      List<L2GameServerPacket> pmember = new ArrayList(2 + outsInfo.size());
      pmember.addAll(outsInfo);
      pmember.add(RelationChanged.create(leader, player, leader));
      leader.sendPacket(pmember);
      pplayer.add(RelationChanged.create(player, leader, player));
    }

    player.sendPacket(pplayer);
    Reflection reflection = this.getReflection();
    if (reflection instanceof DarknessFestival) {
      ((DarknessFestival)reflection).partyMemberExited();
    } else if (this.isInReflection() && this.getReflection() instanceof DimensionalRift) {
      ((DimensionalRift)this.getReflection()).partyMemberExited(player);
    }

    if (reflection != null && player.getReflection() == reflection && reflection.getReturnLoc() != null) {
      player.teleToLocation(reflection.getReturnLoc(), ReflectionManager.DEFAULT);
    }

    leader = this.getPartyLeader();
    if (dissolve) {
      if (this.isInCommandChannel()) {
        this._commandChannel.removeParty(this);
      } else if (reflection != null && reflection.getInstancedZone() != null && reflection.getInstancedZone().isCollapseOnPartyDismiss()) {
        if (reflection.getParty() == this) {
          reflection.startCollapseTimer((long)(reflection.getInstancedZone().getTimerOnCollapse() * 1000));
        }

        if (leader != null && leader.getReflection() == reflection) {
          leader.broadcastPacket(new L2GameServerPacket[]{(new SystemMessage(2106)).addNumber(1)});
        }
      }

      this.dissolveParty();
    } else {
      if (this.isInCommandChannel() && this._commandChannel.getChannelLeader() == player) {
        this._commandChannel.setChannelLeader(leader);
      }

      if (isLeader) {
        this.updateLeaderInfo();
      }
    }

    if (this._checkTask != null) {
      this._checkTask.cancel(true);
      this._checkTask = null;
    }

    return true;
  }

  public boolean changePartyLeader(Player player) {
    Player leader = this.getPartyLeader();
    synchronized(this._members) {
      int index = this._members.indexOf(player);
      if (index == -1) {
        return false;
      }

      this._members.set(0, player);
      this._members.set(index, leader);
    }

    this.updateLeaderInfo();
    if (this.isInCommandChannel() && this._commandChannel.getChannelLeader() == leader) {
      this._commandChannel.setChannelLeader(player);
    }

    return true;
  }

  private void updateLeaderInfo() {
    Player leader = this.getPartyLeader();
    if (leader != null) {
      SystemMessage msg = (new SystemMessage(1384)).addName(leader);
      Iterator var3 = this._members.iterator();

      Player member;
      while(var3.hasNext()) {
        member = (Player)var3.next();
        member.sendPacket(new IStaticPacket[]{PartySmallWindowDeleteAll.STATIC, new PartySmallWindowAll(this, member), msg});
      }

      var3 = this._members.iterator();

      while(var3.hasNext()) {
        member = (Player)var3.next();
        this.broadcastToPartyMembers(member, new PartySpelled(member, true));
      }

    }
  }

  public Player getPlayerByName(String name) {
    Iterator var2 = this._members.iterator();

    Player member;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      member = (Player)var2.next();
    } while(!name.equalsIgnoreCase(member.getName()));

    return member;
  }

  public void distributeItem(Player player, ItemInstance item, NpcInstance fromNpc) {
    switch(item.getItemId()) {
      case 57:
        this.distributeAdena(player, item, fromNpc);
        break;
      default:
        this.distributeItem0(player, item, fromNpc);
    }

  }

  private void distributeItem0(Player player, ItemInstance item, NpcInstance fromNpc) {
    Player target = null;
    List<Player> ret = null;
    switch(this._itemDistribution) {
      case 0:
      default:
        target = player;
        break;
      case 1:
      case 2:
        List<Player> playerList = new ArrayList<>(this._members.size());

        for (Player member : this._members) {
          if (member.isInRangeZ(player, (long) Config.ALT_PARTY_DISTRIBUTION_RANGE) && !member.isDead() && member.getInventory().validateCapacity(item) && member.getInventory().validateWeight(item)) {
            playerList.add(member);
          }
        }

        target = playerList.isEmpty() ? null : (Player)playerList.get(Rnd.get(playerList.size()));
        break;
      case 3:
      case 4:
        synchronized(this._members) {
          playerList = new CopyOnWriteArrayList(this._members);

          while(true) {
            if (target != null || playerList.isEmpty()) {
              break;
            }

            int looter = this._itemOrder++;
            if (this._itemOrder > playerList.size() - 1) {
              this._itemOrder = 0;
            }

            Player looterPlayer = looter < playerList.size() ? (Player)playerList.get(looter) : null;
            if (looterPlayer != null) {
              if (!looterPlayer.isDead() && looterPlayer.isInRangeZ(player, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE) && ItemFunctions.canAddItem(looterPlayer, item)) {
                target = looterPlayer;
              } else {
                playerList.remove(looterPlayer);
              }
            }
          }
        }

        if (target == null) {
          return;
        }
    }

    if (target == null) {
      target = player;
    }

    if (target.pickupItem(item, ItemLog.PartyPickup)) {
      if (fromNpc == null) {
        player.broadcastPacket(new L2GameServerPacket[]{new GetItem(item, player.getObjectId())});
      }

      player.broadcastPickUpMsg(item);
      item.pickupMe();
      this.broadcastToPartyMembers(target, SystemMessage2.obtainItemsBy(item, target));
    } else {
      item.dropToTheGround(player, fromNpc);
    }

  }

  private void distributeAdena(Player player, ItemInstance item, NpcInstance fromNpc) {
    if (player != null) {
      List<Player> membersInRange = new ArrayList();
      if (item.getCount() < (long)this._members.size()) {
        membersInRange.add(player);
      } else {
        Iterator var5 = this._members.iterator();

        label52:
        while(true) {
          Player member;
          do {
            do {
              if (!var5.hasNext()) {
                break label52;
              }

              member = (Player)var5.next();
            } while(member.isDead());
          } while(member != player && !player.isInRangeZ(member, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE));

          if (ItemFunctions.canAddItem(player, item)) {
            membersInRange.add(member);
          }
        }
      }

      if (membersInRange.isEmpty()) {
        membersInRange.add(player);
      }

      long totalAdena = item.getCount();
      long amount = totalAdena / (long)membersInRange.size();
      long ost = totalAdena % (long)membersInRange.size();
      Iterator var11 = membersInRange.iterator();

      while(var11.hasNext()) {
        Player member = (Player)var11.next();
        long count = member.equals(player) ? amount + ost : amount;
        member.getInventory().addAdena(count);
        member.sendPacket(SystemMessage2.obtainItems(57, count, 0));
      }

      if (fromNpc == null) {
        player.broadcastPacket(new L2GameServerPacket[]{new GetItem(item, player.getObjectId())});
      }

      item.pickupMe();
    }
  }

  public void distributeXpAndSp(double xpReward, double spReward, List<Player> rewardedMembers, Creature lastAttacker, MonsterInstance monster) {
    this.recalculatePartyData();
    List<Player> mtr = new ArrayList();
    int partyLevel = lastAttacker.getLevel();
    int partyLvlSum = 0;
    Iterator var11 = rewardedMembers.iterator();

    Player member;
    while(var11.hasNext()) {
      member = (Player)var11.next();
      if (monster.isInRangeZ(member, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
        partyLevel = Math.max(partyLevel, member.getLevel());
      }
    }

    var11 = rewardedMembers.iterator();

    while(var11.hasNext()) {
      member = (Player)var11.next();
      if (monster.isInRangeZ(member, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE) && member.getLevel() > partyLevel - Config.ALT_PARTY_DISTRIBUTION_DIFF_LEVEL_LIMIT) {
        partyLvlSum += member.getLevel();
        mtr.add(member);
      }
    }

    if (!mtr.isEmpty()) {
      double bonus = Config.ALT_PARTY_BONUS[mtr.size() - 1];
      double XP = xpReward * bonus;
      double SP = spReward * bonus;

      for (Player next : mtr) {
        double lvlPenalty = Experience.penaltyModifier((long) monster.calculateLevelDiffForDrop(next.getLevel()), 9.0D);
        int lvlDiff = partyLevel - next.getLevel();
        if (lvlDiff >= Config.PARTY_PENALTY_EXP_SP_MAX_LEVEL && lvlDiff <= Config.PARTY_PENALTY_EXP_SP_MIN_LEVEL) {
          lvlPenalty *= 0.3D;
        }

        double memberXp = XP * lvlPenalty * (double) next.getLevel() / (double) partyLvlSum;
        double memberSp = SP * lvlPenalty * (double) next.getLevel() / (double) partyLvlSum;
        memberXp = Math.min(memberXp, xpReward);
        memberSp = Math.min(memberSp, spReward);
        next.addExpAndCheckBonus(monster, (double) ((long) memberXp), (double) ((long) memberSp));
      }

      this.recalculatePartyData();
    }
  }

  public void recalculatePartyData() {
    this._partyLvl = 0;
    double rateExp = 0.0D;
    double rateSp = 0.0D;
    double rateDrop = 0.0D;
    double rateAdena = 0.0D;
    double rateSpoil = 0.0D;
    double minRateExp = 1.7976931348623157E308D;
    double minRateSp = 1.7976931348623157E308D;
    double minRateDrop = 1.7976931348623157E308D;
    double minRateAdena = 1.7976931348623157E308D;
    double minRateSpoil = 1.7976931348623157E308D;
    int count = 0;

    Player member;
    for(Iterator var22 = this._members.iterator(); var22.hasNext(); minRateSpoil = Math.min(minRateSpoil, (double)member.getBonus().getDropSpoil())) {
      member = (Player)var22.next();
      int level = member.getLevel();
      this._partyLvl = Math.max(this._partyLvl, level);
      ++count;
      rateExp += (double)member.getBonus().getRateXp();
      rateSp += (double)member.getBonus().getRateSp();
      rateDrop += (double)member.getBonus().getDropItems();
      rateAdena += (double)member.getBonus().getDropAdena();
      rateSpoil += (double)member.getBonus().getDropSpoil();
      minRateExp = Math.min(minRateExp, (double)member.getBonus().getRateXp());
      minRateSp = Math.min(minRateSp, (double)member.getBonus().getRateSp());
      minRateDrop = Math.min(minRateDrop, (double)member.getBonus().getDropItems());
      minRateAdena = Math.min(minRateAdena, (double)member.getBonus().getDropAdena());
    }

    this._rateExp = Config.RATE_PARTY_MIN ? minRateExp : rateExp / (double)count;
    this._rateSp = Config.RATE_PARTY_MIN ? minRateSp : rateSp / (double)count;
    this._rateDrop = Config.RATE_PARTY_MIN ? minRateDrop : rateDrop / (double)count;
    this._rateAdena = Config.RATE_PARTY_MIN ? minRateAdena : rateAdena / (double)count;
    this._rateSpoil = Config.RATE_PARTY_MIN ? minRateSpoil : rateSpoil / (double)count;
  }

  public int getLevel() {
    return this._partyLvl;
  }

  public int getLootDistribution() {
    return this._itemDistribution;
  }

  public boolean isDistributeSpoilLoot() {
    boolean rv = false;
    if (this._itemDistribution == 2 || this._itemDistribution == 4) {
      rv = true;
    }

    return rv;
  }

  public boolean isInDimensionalRift() {
    return this._dimentionalRift > 0 && this.getDimensionalRift() != null;
  }

  public void setDimensionalRift(DimensionalRift dr) {
    this._dimentionalRift = dr == null ? 0 : dr.getId();
  }

  public DimensionalRift getDimensionalRift() {
    return this._dimentionalRift == 0 ? null : (DimensionalRift)ReflectionManager.getInstance().get(this._dimentionalRift);
  }

  public boolean isInReflection() {
    if (this._reflection != null) {
      return true;
    } else {
      return this._commandChannel != null ? this._commandChannel.isInReflection() : false;
    }
  }

  public void setReflection(Reflection reflection) {
    this._reflection = reflection;
  }

  public Reflection getReflection() {
    if (this._reflection != null) {
      return this._reflection;
    } else {
      return this._commandChannel != null ? this._commandChannel.getReflection() : null;
    }
  }

  public boolean isInCommandChannel() {
    return this._commandChannel != null;
  }

  public CommandChannel getCommandChannel() {
    return this._commandChannel;
  }

  public void setCommandChannel(CommandChannel channel) {
    this._commandChannel = channel;
  }

  public void Teleport(int x, int y, int z) {
    TeleportParty(this.getPartyMembers(), new Location(x, y, z));
  }

  public void Teleport(Location dest) {
    TeleportParty(this.getPartyMembers(), dest);
  }

  public void Teleport(Territory territory) {
    RandomTeleportParty(this.getPartyMembers(), territory);
  }

  public void Teleport(Territory territory, Location dest) {
    TeleportParty(this.getPartyMembers(), territory, dest);
  }

  public static void TeleportParty(List<Player> members, Location dest) {
    Iterator var2 = members.iterator();

    while(var2.hasNext()) {
      Player _member = (Player)var2.next();
      if (_member != null) {
        _member.teleToLocation(dest);
      }
    }

  }

  public static void TeleportParty(List<Player> members, Territory territory, Location dest) {
    if (!territory.isInside(dest.x, dest.y)) {
      Log.add("TeleportParty: dest is out of territory", "errors");
      Thread.dumpStack();
    } else {
      int base_x = ((Player)members.get(0)).getX();
      int base_y = ((Player)members.get(0)).getY();
      Iterator var5 = members.iterator();

      while(true) {
        Player _member;
        do {
          if (!var5.hasNext()) {
            return;
          }

          _member = (Player)var5.next();
        } while(_member == null);

        int diff_x = _member.getX() - base_x;
        int diff_y = _member.getY() - base_y;
        Location loc = new Location(dest.x + diff_x, dest.y + diff_y, dest.z);

        while(!territory.isInside(loc.x, loc.y)) {
          diff_x = loc.x - dest.x;
          diff_y = loc.y - dest.y;
          if (diff_x != 0) {
            loc.x -= diff_x / Math.abs(diff_x);
          }

          if (diff_y != 0) {
            loc.y -= diff_y / Math.abs(diff_y);
          }
        }

        _member.teleToLocation(loc);
      }
    }
  }

  public static void RandomTeleportParty(List<Player> members, Territory territory) {
    Iterator var2 = members.iterator();

    while(var2.hasNext()) {
      Player member = (Player)var2.next();
      member.teleToLocation(Territory.getRandomLoc(territory, member.getGeoIndex()));
    }

  }

  private void startUpdatePositionTask() {
    if (this.positionTask == null) {
      this.positionTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(new Party.UpdatePositionTask(), 1000L, 1000L);
    }

  }

  private void stopUpdatePositionTask() {
    if (this.positionTask != null) {
      this.positionTask.cancel(false);
    }

  }

  public void requestLootChange(byte type) {
    if (this._requestChangeLoot != -1) {
      if (System.currentTimeMillis() <= this._requestChangeLootTimer) {
        return;
      }

      this.finishLootRequest(false);
    }

    this._requestChangeLoot = type;
    int additionalTime = '꿈';
    this._requestChangeLootTimer = System.currentTimeMillis() + (long)additionalTime;
    this._changeLootAnswers = new CopyOnWriteArraySet();
    this._checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Party.ChangeLootCheck(), (long)(additionalTime + 1000), 5000L);
    this.broadcastToPartyMembers(this.getPartyLeader(), new ExAskModifyPartyLooting(this.getPartyLeader().getName(), type));
    SystemMessage sm = new SystemMessage(3135);
    sm.addSystemString(LOOT_SYSSTRINGS[type]);
    this.getPartyLeader().sendPacket(sm);
  }

  public synchronized void answerLootChangeRequest(Player member, boolean answer) {
    if (this._requestChangeLoot != -1) {
      if (!this._changeLootAnswers.contains(member.getObjectId())) {
        if (!answer) {
          this.finishLootRequest(false);
        } else {
          this._changeLootAnswers.add(member.getObjectId());
          if (this._changeLootAnswers.size() >= this.getMemberCount() - 1) {
            this.finishLootRequest(true);
          }

        }
      }
    }
  }

  private synchronized void finishLootRequest(boolean success) {
    if (this._requestChangeLoot != -1) {
      if (this._checkTask != null) {
        this._checkTask.cancel(false);
        this._checkTask = null;
      }

      if (success) {
        this.broadCast(new ExSetPartyLooting(1, this._requestChangeLoot));
        this._itemDistribution = this._requestChangeLoot;
        SystemMessage sm = new SystemMessage(3138);
        sm.addSystemString(LOOT_SYSSTRINGS[this._requestChangeLoot]);
        this.broadCast(sm);
      } else {
        this.broadCast(new ExSetPartyLooting(0, 0));
        this.broadCast(new SystemMessage(3137));
      }

      this._changeLootAnswers = null;
      this._requestChangeLoot = -1;
      this._requestChangeLootTimer = 0L;
    }
  }

  public Iterator<Player> iterator() {
    return this._members.iterator();
  }

  private class ChangeLootCheck extends RunnableImpl {
    private ChangeLootCheck() {
    }

    public void runImpl() throws Exception {
      if (System.currentTimeMillis() > Party.this._requestChangeLootTimer) {
        Party.this.finishLootRequest(false);
      }

    }
  }

  private class UpdatePositionTask extends RunnableImpl {
    private UpdatePositionTask() {
    }

    public void runImpl() throws Exception {
      LazyArrayList<Player> update = LazyArrayList.newInstance();
      Iterator var2 = Party.this._members.iterator();

      while(true) {
        Player member;
        Location loc;
        do {
          if (!var2.hasNext()) {
            if (!update.isEmpty()) {
              var2 = Party.this._members.iterator();

              while(var2.hasNext()) {
                member = (Player)var2.next();
                PartyMemberPosition pmp = new PartyMemberPosition();
                Iterator var5 = update.iterator();

                while(var5.hasNext()) {
                  Player m = (Player)var5.next();
                  if (m != member) {
                    pmp.add(m);
                  }
                }

                if (pmp.size() > 0) {
                  member.sendPacket(pmp);
                }
              }
            }

            LazyArrayList.recycle(update);
            return;
          }

          member = (Player)var2.next();
          loc = member.getLastPartyPosition();
        } while(loc != null && member.getDistance(loc) <= 256.0D);

        member.setLastPartyPosition(member.getLoc());
        update.add(member);
      }
    }
  }
}
