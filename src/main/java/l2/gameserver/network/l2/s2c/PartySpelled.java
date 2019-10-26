//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Effect.EEffectSlot;

public class PartySpelled extends L2GameServerPacket {
  private final int _type;
  private final int _objId;
  private final List<PartySpelled.Effect> _effects;

  public PartySpelled(Playable activeChar, boolean full) {
    this._objId = activeChar.getObjectId();
    this._type = activeChar.isPet() ? 1 : (activeChar.isSummon() ? 2 : 0);
    this._effects = new ArrayList();
    if (full) {
      l2.gameserver.model.Effect[] effects = activeChar.getEffectList().getAllFirstEffects();
      EEffectSlot[] var4 = EEffectSlot.VALUES;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
        EEffectSlot ees = var4[var6];
        l2.gameserver.model.Effect[] var8 = effects;
        int var9 = effects.length;

        for(int var10 = 0; var10 < var9; ++var10) {
          l2.gameserver.model.Effect effect = var8[var10];
          if (effect != null && effect.isInUse() && effect.getEffectSlot() == ees) {
            effect.addPartySpelledIcon(this);
          }
        }
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(238);
    this.writeD(this._type);
    this.writeD(this._objId);
    this.writeD(this._effects.size());
    Iterator var1 = this._effects.iterator();

    while(var1.hasNext()) {
      PartySpelled.Effect temp = (PartySpelled.Effect)var1.next();
      this.writeD(temp._skillId);
      this.writeH(temp._level);
      this.writeD(temp._duration);
    }

  }

  public void addPartySpelledEffect(int skillId, int level, int duration) {
    this._effects.add(new PartySpelled.Effect(skillId, level, duration));
  }

  static class Effect {
    final int _skillId;
    final int _level;
    final int _duration;

    public Effect(int skillId, int level, int duration) {
      this._skillId = skillId;
      this._level = level;
      this._duration = duration;
    }
  }
}
