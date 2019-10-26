//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;

public class GMViewQuestInfo extends L2GameServerPacket {
  private final Player _cha;

  public GMViewQuestInfo(Player cha) {
    this._cha = cha;
  }

  protected final void writeImpl() {
    this.writeC(147);
    this.writeS(this._cha.getName());
    Quest[] quests = this._cha.getAllActiveQuests();
    if (quests.length == 0) {
      this.writeC(0);
      this.writeH(0);
      this.writeH(0);
    } else {
      this.writeH(quests.length);
      Quest[] var2 = quests;
      int var3 = quests.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Quest q = var2[var4];
        this.writeD(q.getQuestIntId());
        QuestState qs = this._cha.getQuestState(q.getName());
        this.writeD(qs == null ? 0 : qs.getInt("cond"));
      }

    }
  }
}
