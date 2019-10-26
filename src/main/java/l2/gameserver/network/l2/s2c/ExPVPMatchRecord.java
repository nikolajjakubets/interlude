//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.LinkedList;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;

public class ExPVPMatchRecord extends L2GameServerPacket {
  private ExPVPMatchRecord.PVPMatchAction _action;
  private LinkedList<ExPVPMatchRecord.PVPMatchRecord> _red_records;
  private LinkedList<ExPVPMatchRecord.PVPMatchRecord> _blue_records;
  private int _blue_cnt;
  private int _red_cnt;
  private TeamType _winner;
  private int _blue;
  private int _red;

  public ExPVPMatchRecord(ExPVPMatchRecord.PVPMatchAction action, TeamType winner, int blue, int red) {
    this._action = action;
    this._red_records = new LinkedList();
    this._blue_records = new LinkedList();
    this._winner = winner;
    this._blue = blue;
    this._red = red;
  }

  public void addRecord(Player player, int kills, int dies) {
    if (player.getTeam() == TeamType.RED) {
      this._red_records.add(new ExPVPMatchRecord.PVPMatchRecord(player.getName(), kills, dies));
      ++this._red_cnt;
    } else if (player.getTeam() == TeamType.BLUE) {
      this._blue_records.add(new ExPVPMatchRecord.PVPMatchRecord(player.getName(), kills, dies));
      ++this._blue_cnt;
    }

  }

  protected void writeImpl() {
    this.writeEx(126);
    this.writeD(this._action.getVal());
    if (this._winner == TeamType.RED) {
      this.writeD(2);
      this.writeD(1);
    } else if (this._winner == TeamType.BLUE) {
      this.writeD(1);
      this.writeD(2);
    } else {
      this.writeD(0);
      this.writeD(0);
    }

    this.writeD(this._blue);
    this.writeD(this._red);
    Iterator var1;
    ExPVPMatchRecord.PVPMatchRecord record;
    if (this._blue_cnt > 0) {
      this.writeD(this._blue_records.size());
      var1 = this._blue_records.iterator();

      while(var1.hasNext()) {
        record = (ExPVPMatchRecord.PVPMatchRecord)var1.next();
        this.writeS(record.name);
        this.writeD(record.kill);
        this.writeD(record.die);
      }
    } else {
      this.writeD(0);
    }

    if (this._red_cnt > 0) {
      this.writeD(this._red_records.size());
      var1 = this._red_records.iterator();

      while(var1.hasNext()) {
        record = (ExPVPMatchRecord.PVPMatchRecord)var1.next();
        this.writeS(record.name);
        this.writeD(record.kill);
        this.writeD(record.die);
      }
    } else {
      this.writeD(0);
    }

  }

  private class PVPMatchRecord {
    public final String name;
    public final int kill;
    public final int die;

    public PVPMatchRecord(String _name, int _kill, int _die) {
      this.name = _name;
      this.kill = _kill;
      this.die = _die;
    }
  }

  public static enum PVPMatchAction {
    INIT(0),
    UPDATE(1),
    DONE(2);

    private final int _val;

    private PVPMatchAction(int val) {
      this._val = val;
    }

    public int getVal() {
      return this._val;
    }
  }
}
