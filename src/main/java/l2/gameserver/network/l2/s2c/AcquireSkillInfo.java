//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;

public class AcquireSkillInfo extends L2GameServerPacket {
  private SkillLearn _learn;
  private AcquireType _type;
  private List<AcquireSkillInfo.Require> _reqs;

  public AcquireSkillInfo(AcquireType type, SkillLearn learn) {
    this(type, learn, learn.getItemId(), (int)learn.getItemCount());
  }

  public AcquireSkillInfo(AcquireType type, SkillLearn learn, int itemId, int itemCount) {
    this._reqs = Collections.emptyList();
    this._type = type;
    this._learn = learn;
    if (itemId != 0) {
      this._reqs = new ArrayList(1);
      this._reqs.add(new AcquireSkillInfo.Require(99, itemId, (long)itemCount, 50));
    }

  }

  public void writeImpl() {
    this.writeC(139);
    this.writeD(this._learn.getId());
    this.writeD(this._learn.getLevel());
    this.writeD(this._learn.getCost());
    this.writeD(this._type.ordinal());
    this.writeD(this._reqs.size());
    Iterator var1 = this._reqs.iterator();

    while(var1.hasNext()) {
      AcquireSkillInfo.Require temp = (AcquireSkillInfo.Require)var1.next();
      this.writeD(temp.type);
      this.writeD(temp.itemId);
      this.writeD((int)temp.count);
      this.writeD(temp.unk);
    }

  }

  private static class Require {
    public int itemId;
    public long count;
    public int type;
    public int unk;

    public Require(int pType, int pItemId, long pCount, int pUnk) {
      this.itemId = pItemId;
      this.type = pType;
      this.count = pCount;
      this.unk = pUnk;
    }
  }
}
