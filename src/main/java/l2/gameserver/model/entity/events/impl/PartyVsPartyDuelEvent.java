//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.collections.JoinedIterator;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.impl.DuelEvent.DuelState;
import l2.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExDuelAskStart;
import l2.gameserver.network.l2.s2c.ExDuelEnd;
import l2.gameserver.network.l2.s2c.ExDuelReady;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class PartyVsPartyDuelEvent extends DuelEvent {
  public PartyVsPartyDuelEvent(MultiValueSet<String> set) {
    super(set);
  }

  protected PartyVsPartyDuelEvent(int id, String name) {
    super(id, name);
  }

  public void stopEvent() {
    this.clearActions();
    if (this._duelState.compareAndSet(DuelState.EInProgress, DuelState.EEnd)) {
      this.updatePlayers(false, false);
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
        DuelSnapshotObject d = (DuelSnapshotObject)var1.next();
        d.getPlayer().sendPacket(new ExDuelEnd(this));
        GameObject target = d.getPlayer().getTarget();
        if (target != null) {
          d.getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);
        }
      }

      switch(this._winner) {
        case NONE:
          this.sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
          break;
        case RED:
        case BLUE:
          List<DuelSnapshotObject> winners = this.getObjects(this._winner.name());
          List<DuelSnapshotObject> lossers = this.getObjects(this._winner.revert().name());
          DuelSnapshotObject winner = (DuelSnapshotObject)CollectionUtils.safeGet(winners, 0);
          if (winner != null) {
            this.sendPacket((new SystemMessage2(SystemMsg.C1S_PARTY_HAS_WON_THE_DUEL)).addName(((DuelSnapshotObject)winners.get(0)).getPlayer()));
            Iterator var4 = lossers.iterator();

            while(var4.hasNext()) {
              DuelSnapshotObject d = (DuelSnapshotObject)var4.next();
              d.getPlayer().broadcastPacket(new L2GameServerPacket[]{new SocialAction(d.getPlayer().getObjectId(), 7)});
            }
          } else {
            this.sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
          }
      }

      this.updatePlayers(false, true);
    }

    this.removeObjects(RED_TEAM);
    this.removeObjects(BLUE_TEAM);
  }

  public void teleportPlayers(String name) {
    InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(1);
    Reflection reflection = new Reflection();
    reflection.init(instantZone);
    List<DuelSnapshotObject> team = this.getObjects(BLUE_TEAM);

    int i;
    DuelSnapshotObject $member;
    for(i = 0; i < team.size(); ++i) {
      $member = (DuelSnapshotObject)team.get(i);
      $member.getPlayer()._stablePoint = $member.getLoc();
      $member.getPlayer().teleToLocation((Location)instantZone.getTeleportCoords().get(i), reflection);
    }

    team = this.getObjects(RED_TEAM);

    for(i = 0; i < team.size(); ++i) {
      $member = (DuelSnapshotObject)team.get(i);
      $member.getPlayer()._stablePoint = $member.getLoc();
      $member.getPlayer().teleToLocation((Location)instantZone.getTeleportCoords().get(9 + i), reflection);
    }

  }

  public boolean canDuel(Player player, Player target, boolean first) {
    if (player.getParty() == null) {
      player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
      return false;
    } else if (target.getParty() == null) {
      player.sendPacket(SystemMsg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
      return false;
    } else {
      Party party1 = player.getParty();
      Party party2 = target.getParty();
      if (player == party1.getPartyLeader() && target == party2.getPartyLeader()) {
        JoinedIterator iterator = new JoinedIterator(new Iterator[]{party1.iterator(), party2.iterator()});

        Player $member;
        IStaticPacket packet;
        do {
          if (!iterator.hasNext()) {
            return true;
          }

          $member = (Player)iterator.next();
          packet = null;
        } while((packet = this.canDuel0(player, $member)) == null);

        player.sendPacket(packet);
        target.sendPacket(packet);
        return false;
      } else {
        player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
        return false;
      }
    }
  }

  public void askDuel(Player player, Player target) {
    Request request = (new Request(L2RequestType.DUEL, player, target)).setTimeout(10000L);
    request.set("duelType", 1);
    player.setRequest(request);
    target.setRequest(request);
    player.sendPacket((new SystemMessage2(SystemMsg.C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL)).addName(target));
    target.sendPacket(new IStaticPacket[]{(new SystemMessage2(SystemMsg.C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL)).addName(player), new ExDuelAskStart(player.getName(), 1)});
  }

  public void createDuel(Player player, Player target) {
    PartyVsPartyDuelEvent duelEvent = new PartyVsPartyDuelEvent(this.getDuelType(), player.getObjectId() + "_" + target.getObjectId() + "_duel");
    this.cloneTo(duelEvent);
    Iterator var4 = player.getParty().iterator();

    Player $member;
    while(var4.hasNext()) {
      $member = (Player)var4.next();
      duelEvent.addObject(BLUE_TEAM, new DuelSnapshotObject($member, TeamType.BLUE));
    }

    var4 = target.getParty().iterator();

    while(var4.hasNext()) {
      $member = (Player)var4.next();
      duelEvent.addObject(RED_TEAM, new DuelSnapshotObject($member, TeamType.RED));
    }

    duelEvent.sendPacket(new ExDuelReady(this));
    duelEvent.reCalcNextTime(false);
  }

  public void playerExit(Player player) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      DuelSnapshotObject $snapshot = (DuelSnapshotObject)var2.next();
      if ($snapshot.getPlayer() == player) {
        this.removeObject($snapshot.getTeam().name(), $snapshot);
      }

      List<DuelSnapshotObject> objects = this.getObjects($snapshot.getTeam().name());
      if (objects.isEmpty()) {
        this._winner = $snapshot.getTeam().revert();
        this.stopEvent();
      }
    }

  }

  public void packetSurrender(Player player) {
  }

  public void onDie(Player player) {
    TeamType team = player.getTeam();
    if (team != TeamType.NONE && !this._aborted) {
      this.sendPacket(SystemMsg.THE_OTHER_PARTY_IS_FROZEN, new String[]{team.revert().name()});
      player.stopAttackStanceTask();
      player.startFrozen();
      player.setTeam(TeamType.NONE);
      Iterator var3 = World.getAroundPlayers(player).iterator();

      while(var3.hasNext()) {
        Player $player = (Player)var3.next();
        $player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
        if (player.getPet() != null) {
          $player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player.getPet());
        }
      }

      player.sendChanges();
      boolean allDead = true;
      List<DuelSnapshotObject> objs = this.getObjects(team.name());
      Iterator var5 = objs.iterator();

      while(var5.hasNext()) {
        DuelSnapshotObject obj = (DuelSnapshotObject)var5.next();
        if (obj.getPlayer() == player) {
          obj.setDead();
        }

        if (!obj.isDead()) {
          allDead = false;
        }
      }

      if (allDead) {
        this._winner = team.revert();
        this.stopEvent();
      }

    }
  }

  public int getDuelType() {
    return 1;
  }

  protected long startTimeMillis() {
    return System.currentTimeMillis() + 30000L;
  }
}
