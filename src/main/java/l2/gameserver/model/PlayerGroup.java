//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.Iterator;
import l2.commons.collections.EmptyIterator;
import l2.gameserver.network.l2.components.IStaticPacket;

public interface PlayerGroup extends Iterable<Player> {
  PlayerGroup EMPTY = new PlayerGroup() {
    public void broadCast(IStaticPacket... packet) {
    }

    public Iterator<Player> iterator() {
      return EmptyIterator.getInstance();
    }
  };

  void broadCast(IStaticPacket... var1);
}
