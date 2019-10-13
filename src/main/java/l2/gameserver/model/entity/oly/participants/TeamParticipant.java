//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly.participants;

import java.util.ArrayList;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class TeamParticipant extends Participant {
  private final HardReference<Player>[] _playerRefs;
  private final int[] _playerClassIds;
  private double[] _damage;
  private boolean[] _alive;
  private String _name = "";

  public TeamParticipant(int side, Competition comp, Player[] players) {
    super(side, comp);
    this._damage = new double[players.length];
    this._alive = new boolean[players.length];
    this._playerClassIds = new int[players.length];
    this._playerRefs = new HardReference[players.length];
    this._name = players[0].getName();

    for(int i = 0; i < players.length; ++i) {
      this._playerRefs[i] = players[i].getRef();
      this._playerClassIds[i] = players[i].getActiveClassId();
      this._damage[i] = 0.0D;
      this._alive[i] = true;
    }

  }

  public void OnStart() {
    Player[] var1 = this.getPlayers();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Player player = var1[var3];
      player.setOlyParticipant(this);
    }

  }

  public void OnFinish() {
    Player[] var1 = this.getPlayers();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Player player = var1[var3];
      if (player.isDead()) {
        player.doRevive(100.0D);
      }

      player.setOlyParticipant((Participant)null);
    }

  }

  public void OnDamaged(Player player, Creature attacker, double damage, double hp) {
    if (!player.isOlyCompetitionFinished()) {
      for(int i = 0; i < this._playerRefs.length; ++i) {
        if (this._alive[i] && this._playerRefs[i].get() == player) {
          if (attacker.isPlayer()) {
            double[] var10000 = this._damage;
            var10000[i] += Math.min(damage, hp);
          }

          if (damage >= hp) {
            this._alive[i] = false;
            attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            attacker.abortAttack(true, true);
            if (attacker.isCastingNow()) {
              attacker.abortCast(true, false);
            }

            attacker.sendActionFailed();
            this.getCompetition().ValidateWinner();
          }
        }
      }

    }
  }

  public void OnDisconnect(Player player) {
    if (!player.isOlyCompetitionFinished()) {
      for(int i = 0; i < this._playerRefs.length; ++i) {
        this._alive[i] = false;
      }

      this.getCompetition().ValidateWinner();
    }
  }

  public void sendPacket(L2GameServerPacket gsp) {
    Player[] var2 = this.getPlayers();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Player player = var2[var4];
      player.sendPacket(gsp);
    }

  }

  public String getName() {
    return this._name;
  }

  public boolean isAlive() {
    boolean[] var1 = this._alive;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      boolean alv = var1[var3];
      if (alv) {
        return true;
      }
    }

    return false;
  }

  public boolean isPlayerLoose(Player player) {
    for(int i = 0; i < this._playerRefs.length; ++i) {
      if (this._playerRefs[i].get() == player) {
        return !this._alive[i];
      }
    }

    return false;
  }

  public double getDamageOf(Player player) {
    for(int i = 0; i < this._playerRefs.length; ++i) {
      if (this._playerRefs[i].get() == player) {
        return this._damage[i];
      }
    }

    return 0.0D;
  }

  public Player[] getPlayers() {
    List<Player> result = new ArrayList();
    HardReference[] var2 = this._playerRefs;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      HardReference<Player> playerRef = var2[var4];
      Player player = (Player)playerRef.get();
      if (player != null) {
        result.add(player);
      }
    }

    return (Player[])result.toArray(new Player[result.size()]);
  }

  public double getTotalDamage() {
    double rdmg = 0.0D;
    double[] var3 = this._damage;
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      double dmg = var3[var5];
      rdmg += dmg;
    }

    return rdmg;
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

    int refIdx = 0;

    while(refIdx < this._playerRefs.length) {
      Player player = (Player)this._playerRefs[refIdx].get();
      if (player != null && player.isOnline() && !player.isLogoutStarted()) {
        if (player.isDead()) {
          this.sendPacket((new SystemMessage(1858)).addName(player));
          oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
          return false;
        }

        if (player.getActiveClassId() == this._playerClassIds[refIdx] && player.getActiveClass().isBase()) {
          if (player.isCursedWeaponEquipped()) {
            this.sendPacket((new SystemMessage(1857)).addName(player).addItemName(player.getCursedWeaponEquippedId()));
            oponent.sendPacket((new SystemMessage(1856)).addItemName(player.getCursedWeaponEquippedId()));
            return false;
          }

          if (!player.isInPeaceZone()) {
            oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
          }

          SystemMessage msg = Competition.checkPlayer(player);
          if (msg != null) {
            this.sendPacket(msg);
            oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
          }

          ++refIdx;
          continue;
        }

        player.sendPacket((new SystemMessage(1692)).addName(player));
        oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
        return false;
      }

      oponent.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
      return false;
    }

    return true;
  }
}
