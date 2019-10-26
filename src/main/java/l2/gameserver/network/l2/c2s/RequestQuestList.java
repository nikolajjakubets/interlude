//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.QuestList;

public class RequestQuestList extends L2GameClientPacket {
  public RequestQuestList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    this.sendPacket(new QuestList(((GameClient)this.getClient()).getActiveChar()));
  }
}
