//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.templates.StatsSet;

public class SSQStatus extends L2GameServerPacket {
  private Player _player;
  private int _page;
  private int period;

  public SSQStatus(Player player, int recordPage) {
    this._player = player;
    this._page = recordPage;
    this.period = SevenSigns.getInstance().getCurrentPeriod();
  }

  protected final void writeImpl() {
    this.writeC(245);
    this.writeC(this._page);
    this.writeC(this.period);
    int totalDawnProportion;
    int i;
    int dawnProportion;
    int duskProportion;
    int dawnProp;
    int duskProp;
    label131:
    switch(this._page) {
      case 1:
        this.writeD(SevenSigns.getInstance().getCurrentCycle());
        switch(this.period) {
          case 0:
            this.writeD(1183);
            break;
          case 1:
            this.writeD(1176);
            break;
          case 2:
            this.writeD(1184);
            break;
          case 3:
            this.writeD(1177);
        }

        switch(this.period) {
          case 0:
          case 2:
            this.writeD(1287);
            break;
          case 1:
          case 3:
            this.writeD(1286);
        }

        this.writeC(SevenSigns.getInstance().getPlayerCabal(this._player));
        this.writeC(SevenSigns.getInstance().getPlayerSeal(this._player));
        this.writeD((int)SevenSigns.getInstance().getPlayerStoneContrib(this._player));
        this.writeD((int)SevenSigns.getInstance().getPlayerAdenaCollect(this._player));
        long dawnStoneScore = SevenSigns.getInstance().getCurrentStoneScore(2);
        long dawnFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(2);
        long dawnTotalScore = SevenSigns.getInstance().getCurrentScore(2);
        long duskStoneScore = SevenSigns.getInstance().getCurrentStoneScore(1);
        long duskFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(1);
        long duskTotalScore = SevenSigns.getInstance().getCurrentScore(1);
        long totalStoneScore = duskStoneScore + dawnStoneScore;
        totalStoneScore = totalStoneScore == 0L ? 1L : totalStoneScore;
        long duskStoneScoreProp = Math.round((double)duskStoneScore * 500.0D / (double)totalStoneScore);
        long dawnStoneScoreProp = Math.round((double)dawnStoneScore * 500.0D / (double)totalStoneScore);
        long totalOverallScore = duskTotalScore + dawnTotalScore;
        totalOverallScore = totalOverallScore == 0L ? 1L : totalOverallScore;
        long dawnPercent = Math.round((double)dawnTotalScore * 110.0D / (double)totalOverallScore);
        long duskPercent = Math.round((double)duskTotalScore * 110.0D / (double)totalOverallScore);
        this.writeD((int)duskStoneScoreProp);
        this.writeD((int)duskFestivalScore);
        this.writeD((int)duskTotalScore);
        this.writeC((int)duskPercent);
        this.writeD((int)dawnStoneScoreProp);
        this.writeD((int)dawnFestivalScore);
        this.writeD((int)dawnTotalScore);
        this.writeC((int)dawnPercent);
        break;
      case 2:
        this.writeH(1);
        this.writeC(5);
        totalDawnProportion = 0;

        while(true) {
          if (totalDawnProportion >= 5) {
            break label131;
          }

          this.writeC(totalDawnProportion + 1);
          this.writeD(SevenSignsFestival.FESTIVAL_LEVEL_SCORES[totalDawnProportion]);
          long duskScore = SevenSignsFestival.getInstance().getHighestScore(1, totalDawnProportion);
          long dawnScore = SevenSignsFestival.getInstance().getHighestScore(2, totalDawnProportion);
          this.writeQ(duskScore);
          StatsSet highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(1, totalDawnProportion);
          String[] partyMembers;
          String[] var41;
          String partyMember;
          if (duskScore > 0L) {
            partyMembers = highScoreData.getString("names").split(",");
            this.writeC(partyMembers.length);
            var41 = partyMembers;
            dawnProp = partyMembers.length;

            for(duskProp = 0; duskProp < dawnProp; ++duskProp) {
              partyMember = var41[duskProp];
              this.writeS(partyMember);
            }
          } else {
            this.writeC(0);
          }

          this.writeQ(dawnScore);
          highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(2, totalDawnProportion);
          if (dawnScore > 0L) {
            partyMembers = highScoreData.getString("names").split(",");
            this.writeC(partyMembers.length);
            var41 = partyMembers;
            dawnProp = partyMembers.length;

            for(duskProp = 0; duskProp < dawnProp; ++duskProp) {
              partyMember = var41[duskProp];
              this.writeS(partyMember);
            }
          } else {
            this.writeC(0);
          }

          ++totalDawnProportion;
        }
      case 3:
        this.writeC(10);
        this.writeC(35);
        this.writeC(3);
        totalDawnProportion = 1;
        int totalDuskProportion = 1;

        for(i = 1; i <= 3; ++i) {
          totalDawnProportion += SevenSigns.getInstance().getSealProportion(i, 2);
          totalDuskProportion += SevenSigns.getInstance().getSealProportion(i, 1);
        }

        totalDawnProportion = Math.max(1, totalDawnProportion);
        totalDuskProportion = Math.max(1, totalDuskProportion);
        i = 1;

        while(true) {
          if (i > 3) {
            break label131;
          }

          dawnProportion = SevenSigns.getInstance().getSealProportion(i, 2);
          duskProportion = SevenSigns.getInstance().getSealProportion(i, 1);
          this.writeC(i);
          this.writeC(SevenSigns.getInstance().getSealOwner(i));
          this.writeC(duskProportion * 100 / totalDuskProportion);
          this.writeC(dawnProportion * 100 / totalDawnProportion);
          ++i;
        }
      case 4:
        i = SevenSigns.getInstance().getCabalHighestScore();
        this.writeC(i);
        this.writeC(3);
        dawnProportion = SevenSigns.getInstance().getTotalMembers(2);
        duskProportion = SevenSigns.getInstance().getTotalMembers(1);

        for(int i = 1; i < 4; ++i) {
          this.writeC(i);
          int dawnSealPlayers = SevenSigns.getInstance().getSealProportion(i, 2);
          int duskSealPlayers = SevenSigns.getInstance().getSealProportion(i, 1);
          dawnProp = dawnProportion > 0 ? dawnSealPlayers * 100 / dawnProportion : 0;
          duskProp = duskProportion > 0 ? duskSealPlayers * 100 / duskProportion : 0;
          int curSealOwner = SevenSigns.getInstance().getSealOwner(i);
          if (Math.max(dawnProp, duskProp) < 10) {
            this.writeC(0);
            if (curSealOwner == 0) {
              this.writeD(1292);
            } else {
              this.writeD(1291);
            }
          } else if (Math.max(dawnProp, duskProp) < 35) {
            this.writeC(curSealOwner);
            if (curSealOwner == 0) {
              this.writeD(1292);
            } else {
              this.writeD(1289);
            }
          } else if (dawnProp == duskProp) {
            this.writeC(0);
            this.writeD(1293);
          } else {
            int sealWinning = dawnProp > duskProp ? 2 : 1;
            this.writeC(sealWinning);
            if (sealWinning == curSealOwner) {
              this.writeD(1289);
            } else {
              this.writeD(1290);
            }
          }
        }
    }

    this._player = null;
  }
}
