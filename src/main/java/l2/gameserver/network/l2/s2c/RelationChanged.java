//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;

public class RelationChanged extends L2GameServerPacket {
  public static final int RELATION_PARTY1 = 1;
  public static final int RELATION_PARTY2 = 2;
  public static final int RELATION_PARTY3 = 4;
  public static final int RELATION_PARTY4 = 8;
  public static final int RELATION_PARTYLEADER = 16;
  public static final int RELATION_HAS_PARTY = 32;
  public static final int RELATION_CLAN_MEMBER = 64;
  public static final int RELATION_LEADER = 128;
  public static final int RELATION_INSIEGE = 512;
  public static final int RELATION_ATTACKER = 1024;
  public static final int RELATION_ALLY = 2048;
  public static final int RELATION_ENEMY = 4096;
  public static final int RELATION_MUTUAL_WAR = 32768;
  public static final int RELATION_1SIDED_WAR = 65536;
  private final int _charObjId;
  private final boolean _isAutoAttackable;
  private final int _relation;
  private final int _karma;
  private final int _pvpFlag;

  protected RelationChanged(Playable cha, boolean isAutoAttackable, int relation) {
    this._isAutoAttackable = isAutoAttackable;
    this._relation = relation;
    this._charObjId = cha.getObjectId();
    this._karma = cha.getKarma();
    this._pvpFlag = cha.getPvpFlag();
  }

  protected void writeImpl() {
    this.writeC(206);
    this.writeD(this._charObjId);
    this.writeD(this._relation);
    this.writeD(this._isAutoAttackable);
    this.writeD(this._karma);
    this.writeD(this._pvpFlag);
  }

  public static L2GameServerPacket create(Player sendTo, Playable targetPlayable, Player activeChar) {
    if (sendTo != null && targetPlayable != null && activeChar != null) {
      Player targetPlayer = targetPlayable.getPlayer();
      int relation = targetPlayer == null ? 0 : targetPlayer.getRelation(activeChar);
      return new RelationChanged(targetPlayable, targetPlayable.isAutoAttackable(activeChar), relation);
    } else {
      return null;
    }
  }
}
