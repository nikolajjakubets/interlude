//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly.participants;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class SinglePlayerParticipant extends Participant {
  private final HardReference<Player> _playerRef;
  private final int _playerClassId;
  private double _damage;
  private boolean _alive;
  private String _name;

  public SinglePlayerParticipant(int side, Competition comp, Player player) {
    super(side, comp);
    this._playerRef = player.getRef();
    this._playerClassId = player.getActiveClassId();
    this._name = player.getName();
    this._alive = true;
  }

  private Player getPlayer() {
    return (Player)this._playerRef.get();
  }

  public void OnStart() {
    Player player = this.getPlayer();
    if (player != null) {
      player.setOlyParticipant(this);
    }

  }

  public void OnFinish() {
    Player player = this.getPlayer();
    if (player != null) {
      player.setOlyParticipant((Participant)null);
    }

  }

  public void OnDamaged(Player player, Creature attacker, double damage, double hp) {
    if (player.isOlyCompetitionStarted()) {
      if (attacker.isPlayer()) {
        this._damage += Math.min(damage, hp);
      }

      if (damage >= hp) {
        this._alive = false;
        attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        attacker.abortAttack(true, true);
        if (attacker.isCastingNow()) {
          attacker.abortCast(true, false);
        }

        attacker.sendActionFailed();
        this.getCompetition().ValidateWinner();
        player.setCurrentHp(1.0D, false);
      }

    }
  }

  public void OnDisconnect(Player player) {
    if (!player.isOlyCompetitionFinished()) {
      this._alive = false;
      this.getCompetition().ValidateWinner();
    }
  }

  public void sendPacket(L2GameServerPacket gsp) {
    Player player = this.getPlayer();
    if (player != null) {
      player.sendPacket(gsp);
    }

  }

  public String getName() {
    return this._name;
  }

  public boolean isAlive() {
    return this._alive;
  }

  public boolean isPlayerLoose(Player player) {
    if (player != null && player == this._playerRef.get()) {
      return !this._alive;
    } else {
      return false;
    }
  }

  public double getDamageOf(Player player) {
    return player != null && player == this._playerRef.get() ? this._damage : 0.0D;
  }

  public Player[] getPlayers() {
    return this.getPlayer() != null ? new Player[]{this.getPlayer()} : new Player[0];
  }

  public double getTotalDamage() {
    return this._damage;
  }

  public boolean validateThis() {
    Participant oponent = null;
    Participant[] var2 = this.getCompetition().getParticipants();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Participant p = var2[var4];
      if (p != this) {
        oponent = p;
      }
    }

    Player player = (Player)this._playerRef.get();
    if (player != null && player.isOnline() && !player.isLogoutStarted()) {
      if (player.isDead()) {
        this.sendPacket((new SystemMessage(1858)).addName(player));
        oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
        return false;
      } else if (player.getActiveClassId() == this._playerClassId && player.getActiveClass().isBase()) {
        if (player.isCursedWeaponEquipped()) {
          this.sendPacket((new SystemMessage(1857)).addName(player).addItemName(player.getCursedWeaponEquippedId()));
          oponent.sendPacket((new SystemMessage(1856)).addItemName(player.getCursedWeaponEquippedId()));
          return false;
        } else if (!player.isInPeaceZone()) {
          oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
          return false;
        } else {
          SystemMessage msg = Competition.checkPlayer(player);
          if (msg != null) {
            this.sendPacket(msg);
            oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
          } else {
            return true;
          }
        }
      } else {
        player.sendPacket((new SystemMessage(1692)).addName(player));
        oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
        return false;
      }
    } else {
      oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
      return false;
    }
  }
}
