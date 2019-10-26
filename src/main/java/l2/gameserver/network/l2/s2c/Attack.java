//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;

public class Attack extends L2GameServerPacket {
  public final int _attackerId;
  public final boolean _soulshot;
  private final int _grade;
  private final int _x;
  private final int _y;
  private final int _z;
  private final int _tx;
  private final int _ty;
  private final int _tz;
  private Attack.Hit[] hits;

  public Attack(Creature attacker, Creature target, boolean ss, int grade) {
    this._attackerId = attacker.getObjectId();
    this._soulshot = ss;
    this._grade = grade;
    this._x = attacker.getX();
    this._y = attacker.getY();
    this._z = attacker.getZ();
    this._tx = target.getX();
    this._ty = target.getY();
    this._tz = target.getZ();
    this.hits = new Attack.Hit[0];
  }

  public void addHit(GameObject target, int damage, boolean miss, boolean crit, boolean shld) {
    int pos = this.hits.length;
    Attack.Hit[] tmp = new Attack.Hit[pos + 1];
    System.arraycopy(this.hits, 0, tmp, 0, this.hits.length);
    tmp[pos] = new Attack.Hit(target, damage, miss, crit, shld);
    this.hits = tmp;
  }

  public boolean hasHits() {
    return this.hits.length > 0;
  }

  protected final void writeImpl() {
    this.writeC(5);
    this.writeD(this._attackerId);
    this.writeD(this.hits[0]._targetId);
    this.writeD(this.hits[0]._damage);
    this.writeC(this.hits[0]._flags);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
    this.writeH(this.hits.length - 1);

    for(int i = 1; i < this.hits.length; ++i) {
      this.writeD(this.hits[i]._targetId);
      this.writeD(this.hits[i]._damage);
      this.writeC(this.hits[i]._flags);
    }

  }

  private class Hit {
    int _targetId;
    int _damage;
    int _flags;

    Hit(GameObject target, int damage, boolean miss, boolean crit, boolean shld) {
      this._targetId = target.getObjectId();
      this._damage = damage;
      if (Attack.this._soulshot) {
        this._flags |= 16 | Attack.this._grade;
      }

      if (crit) {
        this._flags |= 32;
      }

      if (shld) {
        this._flags |= 64;
      }

      if (miss) {
        this._flags |= 128;
      }

    }
  }
}
