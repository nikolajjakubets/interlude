//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.network.l2.s2c.ShortCutPacket.ShortcutInfo;

public class ShortCutInit extends ShortCutPacket {
  private List<ShortcutInfo> _shortCuts = Collections.emptyList();

  public ShortCutInit(Player pl) {
    Collection<ShortCut> shortCuts = pl.getAllShortCuts();
    this._shortCuts = new ArrayList(shortCuts.size());
    Iterator var3 = shortCuts.iterator();

    while(var3.hasNext()) {
      ShortCut shortCut = (ShortCut)var3.next();
      this._shortCuts.add(convert(pl, shortCut));
    }

  }

  protected final void writeImpl() {
    this.writeC(69);
    this.writeD(this._shortCuts.size());
    Iterator var1 = this._shortCuts.iterator();

    while(var1.hasNext()) {
      ShortcutInfo sc = (ShortcutInfo)var1.next();
      sc.write(this);
    }

  }
}
