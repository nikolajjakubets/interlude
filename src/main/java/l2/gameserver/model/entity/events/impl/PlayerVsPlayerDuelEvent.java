//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.base.TeamType;
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

public class PlayerVsPlayerDuelEvent extends DuelEvent {
  public PlayerVsPlayerDuelEvent(MultiValueSet<String> set) {
    super(set);
  }

  protected PlayerVsPlayerDuelEvent(int id, String name) {
    super(id, name);
  }

  public boolean canDuel(Player player, Player target, boolean first) {
    IStaticPacket sm = this.canDuel0(player, target);
    if (sm != null) {
      player.sendPacket(sm);
      return false;
    } else {
      sm = this.canDuel0(target, player);
      if (sm != null) {
        player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
        return false;
      } else {
        return true;
      }
    }
  }

  public void askDuel(Player player, Player target) {
    Request request = (new Request(L2RequestType.DUEL, player, target)).setTimeout(10000L);
    request.set("duelType", 0);
    player.setRequest(request);
    target.setRequest(request);
    player.sendPacket((new SystemMessage2(SystemMsg.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL)).addName(target));
    target.sendPacket(new IStaticPacket[]{(new SystemMessage2(SystemMsg.C1_HAS_CHALLENGED_YOU_TO_A_DUEL)).addName(player), new ExDuelAskStart(player.getName(), 0)});
  }

  public void createDuel(Player player, Player target) {
    PlayerVsPlayerDuelEvent duelEvent = new PlayerVsPlayerDuelEvent(this.getDuelType(), player.getObjectId() + "_" + target.getObjectId() + "_duel");
    this.cloneTo(duelEvent);
    duelEvent.addObject(BLUE_TEAM, new DuelSnapshotObject(player, TeamType.BLUE));
    duelEvent.addObject(RED_TEAM, new DuelSnapshotObject(target, TeamType.RED));
    duelEvent.sendPacket(new ExDuelReady(this));
    duelEvent.reCalcNextTime(false);
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
          this.sendPacket((new SystemMessage2(SystemMsg.C1_HAS_WON_THE_DUEL)).addName(((DuelSnapshotObject)winners.get(0)).getPlayer()));
          Iterator var8 = lossers.iterator();

          while(var8.hasNext()) {
            DuelSnapshotObject d = (DuelSnapshotObject)var8.next();
            Player player = d.getPlayer();
            if (player != null) {
              player.broadcastPacket(new L2GameServerPacket[]{new SocialAction(d.getPlayer().getObjectId(), 7)});
            }
          }
      }
    }

    this.removeObjects(RED_TEAM);
    this.removeObjects(BLUE_TEAM);
  }

  public void onDie(Player player) {
    TeamType team = player.getTeam();
    if (team != TeamType.NONE && !this._aborted) {
      player.stopAttackStanceTask();
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
    return 0;
  }

  public void playerExit(Player player) {
    if (this._winner == TeamType.NONE && !this._aborted) {
      this._winner = player.getTeam().revert();
      this._aborted = false;
      this.stopEvent();
    }
  }

  public void packetSurrender(Player player) {
    this.playerExit(player);
  }

  protected long startTimeMillis() {
    return System.currentTimeMillis() + 5000L;
  }
}
