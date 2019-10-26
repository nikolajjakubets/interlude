//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class NickNameChanged extends L2GameServerPacket {
  private final int objectId;
  private final String title;

  public NickNameChanged(Creature cha) {
    this.objectId = cha.getObjectId();
    this.title = cha.getTitle();
  }

  protected void writeImpl() {
    this.writeC(204);
    this.writeD(this.objectId);
    this.writeS(this.title);
  }
}
