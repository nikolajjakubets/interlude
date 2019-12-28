//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ActionFail extends L2GameServerPacket {

  private static final L2GameServerPacket STATIC = new ActionFail();

  public static L2GameServerPacket getStatic() {
//    new Exception().printStackTrace();
    return STATIC;
  }

  protected final void writeImpl() {
    this.writeC(37);
  }
}
