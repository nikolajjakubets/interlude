//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class EtcStatusUpdate extends L2GameServerPacket {
  private int IncreasedForce;
  private int WeightPenalty;
  private int MessageRefusal;
  private int DangerArea;
  private int GradeExpertisePenalty;
  private int CharmOfCourage;
  private int DeathPenaltyLevel;
  private int ConsumedSouls;

  public EtcStatusUpdate(Player player) {
    this.IncreasedForce = player.getIncreasedForce();
    this.WeightPenalty = player.getWeightPenalty();
    this.MessageRefusal = !player.getMessageRefusal() && player.getNoChannel() == 0L && !player.isBlockAll() ? 0 : 1;
    this.DangerArea = player.isInDangerArea() ? 1 : 0;
    this.GradeExpertisePenalty = player.getGradePenalty();
    this.CharmOfCourage = player.isCharmOfCourage() ? 1 : 0;
    this.DeathPenaltyLevel = player.getDeathPenalty() == null ? 0 : player.getDeathPenalty().getLevel();
    this.ConsumedSouls = player.getConsumedSouls();
  }

  protected final void writeImpl() {
    this.writeC(243);
    this.writeD(this.IncreasedForce);
    this.writeD(this.WeightPenalty);
    this.writeD(this.MessageRefusal);
    this.writeD(this.DangerArea);
    this.writeD(this.GradeExpertisePenalty);
    this.writeD(this.CharmOfCourage);
    this.writeD(this.DeathPenaltyLevel);
  }
}
