//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.model.Player;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ExPVPMatchCCRecord extends L2GameServerPacket {
  private List<Pair<String, Integer>> _result;
  private int _len;
  private ExPVPMatchCCRecord.PVPMatchCCAction _action;

  public ExPVPMatchCCRecord(ExPVPMatchCCRecord.PVPMatchCCAction action) {
    this._action = action;
    this._len = 0;
    this._result = new LinkedList();
  }

  public void addPlayer(Player player, int points) {
    ++this._len;
    this._result.add(new ImmutablePair(player.getName(), points));
  }

  public void writeImpl() {
    this.writeEx(137);
    this.writeD(this._action.getVal());
    this.writeD(this._len);
    Iterator var1 = this._result.iterator();

    while(var1.hasNext()) {
      Pair<String, Integer> p = (Pair)var1.next();
      this.writeS((CharSequence)p.getLeft());
      this.writeD((Integer)p.getRight());
    }

  }

  public static enum PVPMatchCCAction {
    INIT(0),
    UPDATE(1),
    DONE(2);

    private final int _val;

    private PVPMatchCCAction(int val) {
      this._val = val;
    }

    public int getVal() {
      return this._val;
    }
  }
}
