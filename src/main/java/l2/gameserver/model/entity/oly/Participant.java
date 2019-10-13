//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public abstract class Participant {
  public static int SIDE_RED = 2;
  public static int SIDE_BLUE = 1;
  private final int _side;
  private final Competition _comp;

  protected Participant(int side, Competition comp) {
    this._side = side;
    this._comp = comp;
  }

  public final Competition getCompetition() {
    return this._comp;
  }

  public final int getSide() {
    return this._side;
  }

  public abstract void OnStart();

  public abstract void OnFinish();

  public abstract void OnDamaged(Player var1, Creature var2, double var3, double var5);

  public abstract void OnDisconnect(Player var1);

  public abstract void sendPacket(L2GameServerPacket var1);

  public abstract String getName();

  public abstract boolean isAlive();

  public abstract boolean isPlayerLoose(Player var1);

  public abstract double getDamageOf(Player var1);

  public abstract Player[] getPlayers();

  public abstract double getTotalDamage();

  public abstract boolean validateThis();
}
