//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbnormalStatusUpdate extends L2GameServerPacket {
  public static final int INFINITIVE_EFFECT = -1;
  private List<AbnormalStatusUpdate.Effect> _effects = new ArrayList();

  public AbnormalStatusUpdate() {
  }

  public void addEffect(int skillId, int dat, int duration) {
    this._effects.add(new AbnormalStatusUpdate.Effect(skillId, dat, duration));
  }

  protected final void writeImpl() {
    this.writeC(127);
    this.writeH(this._effects.size());
    Iterator var1 = this._effects.iterator();

    while(var1.hasNext()) {
      AbnormalStatusUpdate.Effect temp = (AbnormalStatusUpdate.Effect)var1.next();
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
