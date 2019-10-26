//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class ExMpccPartymasterList extends L2GameServerPacket {
  private Set<String> _members = Collections.emptySet();

  public ExMpccPartymasterList(Set<String> s) {
    this._members = s;
  }

  protected void writeImpl() {
    this.writeEx(162);
    this.writeD(this._members.size());
    Iterator var1 = this._members.iterator();

    while(var1.hasNext()) {
      String t = (String)var1.next();
      this.writeS(t);
    }

  }
}
