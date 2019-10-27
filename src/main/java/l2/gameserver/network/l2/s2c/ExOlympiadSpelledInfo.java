//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import l2.gameserver.model.Player;

public class ExOlympiadSpelledInfo extends L2GameServerPacket {
  private int char_obj_id = 0;
  private ArrayList<ExOlympiadSpelledInfo.Effect> _effects = new ArrayList<>();

  public ExOlympiadSpelledInfo() {
  }

  public void addEffect(int skillId, int level, int duration) {
    this._effects.add(new ExOlympiadSpelledInfo.Effect(skillId, level, duration));
  }

  public void addSpellRecivedPlayer(Player cha) {
    if (cha != null) {
      this.char_obj_id = cha.getObjectId();
    }

  }

  protected final void writeImpl() {
    if (this.char_obj_id != 0) {
      this.writeEx(42);
      this.writeD(this.char_obj_id);
      this.writeD(this._effects.size());
      Iterator var1 = this._effects.iterator();

      while(var1.hasNext()) {
        ExOlympiadSpelledInfo.Effect temp = (ExOlympiadSpelledInfo.Effect)var1.next();
        this.writeD(temp.skillId);
        this.writeH(temp.level);
        this.writeD(temp.duration);
      }

    }
  }

  class Effect {
    int skillId;
    int level;
    int duration;

    public Effect(int skillId, int level, int duration) {
      this.skillId = skillId;
      this.level = level;
      this.duration = duration;
    }
  }
}
