//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.templates.PlayerTemplate;

public class NewCharacterSuccess extends L2GameServerPacket {
  private List<PlayerTemplate> _chars = new ArrayList();

  public NewCharacterSuccess() {
  }

  public void addChar(PlayerTemplate template) {
    this._chars.add(template);
  }

  protected final void writeImpl() {
    this.writeC(23);
    this.writeD(this._chars.size());
    Iterator var1 = this._chars.iterator();

    while(var1.hasNext()) {
      PlayerTemplate temp = (PlayerTemplate)var1.next();
      this.writeD(temp.race.ordinal());
      this.writeD(temp.classId.getId());
      this.writeD(70);
      this.writeD(temp.baseSTR);
      this.writeD(10);
      this.writeD(70);
      this.writeD(temp.baseDEX);
      this.writeD(10);
      this.writeD(70);
      this.writeD(temp.baseCON);
      this.writeD(10);
      this.writeD(70);
      this.writeD(temp.baseINT);
      this.writeD(10);
      this.writeD(70);
      this.writeD(temp.baseWIT);
      this.writeD(10);
      this.writeD(70);
      this.writeD(temp.baseMEN);
      this.writeD(10);
    }

  }
}
