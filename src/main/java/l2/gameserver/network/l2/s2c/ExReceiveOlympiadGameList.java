//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.entity.oly.Stadium;

public class ExReceiveOlympiadGameList extends L2GameServerPacket {
  private ArrayList<ExReceiveOlympiadGameList.GameRec> _games = new ArrayList<>();

  public ExReceiveOlympiadGameList() {
  }

  public void add(Stadium sid, CompetitionType _type, int _state, String p0, String p1) {
    this._games.add(new ExReceiveOlympiadGameList.GameRec(sid, _type, _state, p0, p1));
  }

  protected void writeImpl() {
    this.writeEx(212);
    this.writeD(0);
    this.writeD(this._games.size());
    this.writeD(0);
    Iterator var1 = this._games.iterator();

    while(var1.hasNext()) {
      ExReceiveOlympiadGameList.GameRec gr = (ExReceiveOlympiadGameList.GameRec)var1.next();
      this.writeD(gr.stadium_id);
      this.writeD(gr.type);
      this.writeD(gr.state);
      this.writeS(gr.player0name);
      this.writeS(gr.player1name);
    }

  }

  private class GameRec {
    int stadium_id;
    int type;
    int state;
    String player0name;
    String player1name;

    public GameRec(Stadium sid, CompetitionType _type, int _state, String p0, String p1) {
      this.stadium_id = sid.getStadiumId();
      this.type = _type.getTypeIdx();
      this.state = _state;
      this.player0name = p0;
      this.player1name = p1;
    }
  }
}
