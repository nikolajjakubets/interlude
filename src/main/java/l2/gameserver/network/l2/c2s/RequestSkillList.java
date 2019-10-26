//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SkillList;

public final class RequestSkillList extends L2GameClientPacket {
  private static final String _C__50_REQUESTSKILLLIST = "[C] 50 RequestSkillList";

  public RequestSkillList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player cha = ((GameClient)this.getClient()).getActiveChar();
    if (cha != null) {
      cha.sendPacket(new SkillList(cha));
    }

  }

  public String getType() {
    return "[C] 50 RequestSkillList";
  }
}
