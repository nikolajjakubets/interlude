//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Participant;

public class ExReceiveOlympiadResult extends L2GameServerPacket {
  private String _winner;
  private int _winner_side;
  private ArrayList<ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord> _Red;
  private ArrayList<ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord> _Blue;

  public ExReceiveOlympiadResult(int winner_side, String winner) {
    this._winner = winner;
    this._winner_side = winner_side;
    this._Red = new ArrayList();
    this._Blue = new ArrayList();
  }

  public void add(int side, Player player, int dmg, int points, int delta) {
    if (side == Participant.SIDE_RED) {
      this._Red.add(new ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord(player, dmg, points, delta));
    }

    if (side == Participant.SIDE_BLUE) {
      this._Blue.add(new ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord(player, dmg, points, delta));
    }

  }

  protected void writeImpl() {
    this.writeEx(212);
    this.writeD(1);
    if (this._winner_side != 0) {
      this.writeD(0);
      this.writeS(this._winner);
    } else {
      this.writeD(1);
      this.writeS("");
    }

    Iterator var1;
    ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord orb;
    if (this._winner_side == Participant.SIDE_RED) {
      this.writeD(1);
      this.writeD(this._Red.size());
      var1 = this._Red.iterator();

      while(var1.hasNext()) {
        orb = (ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord)var1.next();
        this.writeS(orb.name);
        this.writeS(orb.clan);
        this.writeD(orb.crest_id);
        this.writeD(orb.class_id);
        this.writeD(orb.dmg);
        this.writeD(orb.points);
        this.writeD(orb.delta);
      }

      this.writeD(0);
      this.writeD(this._Blue.size());
      var1 = this._Blue.iterator();

      while(var1.hasNext()) {
        orb = (ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord)var1.next();
        this.writeS(orb.name);
        this.writeS(orb.clan);
        this.writeD(orb.crest_id);
        this.writeD(orb.class_id);
        this.writeD(orb.dmg);
        this.writeD(orb.points);
        this.writeD(orb.delta);
      }
    } else {
      this.writeD(0);
      this.writeD(this._Blue.size());
      var1 = this._Blue.iterator();

      while(var1.hasNext()) {
        orb = (ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord)var1.next();
        this.writeS(orb.name);
        this.writeS(orb.clan);
        this.writeD(orb.crest_id);
        this.writeD(orb.class_id);
        this.writeD(orb.dmg);
        this.writeD(orb.points);
        this.writeD(orb.delta);
      }

      this.writeD(1);
      this.writeD(this._Red.size());
      var1 = this._Red.iterator();

      while(var1.hasNext()) {
        orb = (ExReceiveOlympiadResult.ExReceiveOlympiadResultRecord)var1.next();
        this.writeS(orb.name);
        this.writeS(orb.clan);
        this.writeD(orb.crest_id);
        this.writeD(orb.class_id);
        this.writeD(orb.dmg);
        this.writeD(orb.points);
        this.writeD(orb.delta);
      }
    }

  }

  private class ExReceiveOlympiadResultRecord {
    String name;
    String clan;
    int class_id;
    int crest_id;
    int dmg;
    int points;
    int delta;

    public ExReceiveOlympiadResultRecord(Player player, int _dmg, int _points, int _delta) {
      this.name = player.getName();
      this.class_id = player.getClassId().getId();
      this.clan = player.getClan() != null ? player.getClan().getName() : "";
      this.crest_id = player.getClanId();
      this.dmg = _dmg;
      this.points = _points;
      this.delta = _delta;
    }
  }
}
