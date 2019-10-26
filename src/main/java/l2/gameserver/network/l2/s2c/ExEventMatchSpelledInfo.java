//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;

public class ExEventMatchSpelledInfo extends L2GameServerPacket {
  private int char_obj_id = 0;
  private List<ExEventMatchSpelledInfo.Effect> _effects = new ArrayList();

  public ExEventMatchSpelledInfo() {
  }

  public void addEffect(int skillId, int dat, int duration) {
    this._effects.add(new ExEventMatchSpelledInfo.Effect(skillId, dat, duration));
  }

  public void addSpellRecivedPlayer(Player cha) {
    if (cha != null) {
      this.char_obj_id = cha.getObjectId();
    }

  }

  protected void writeImpl() {
    this.writeEx(4);
    this.writeD(this.char_obj_id);
    this.writeD(this._effects.size());
    Iterator var1 = this._effects.iterator();

    while(var1.hasNext()) {
      ExEventMatchSpelledInfo.Effect temp = (ExEventMatchSpelledInfo.Effect)var1.next();
      this.writeD(temp.skillId);
      this.writeH(temp.dat);
      this.writeD(temp.duration);
    }

  }

  class Effect {
    int skillId;
    int dat;
    int duration;

    public Effect(int skillId, int dat, int duration) {
      this.skillId = skillId;
      this.dat = dat;
      this.duration = duration;
    }
  }
}
