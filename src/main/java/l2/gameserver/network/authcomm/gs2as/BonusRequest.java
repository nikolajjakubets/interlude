//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class BonusRequest extends SendablePacket {
  private String account;
  private double bonus;
  private int bonusExpire;

  public BonusRequest(String account, double bonus, int bonusExpire) {
    this.account = account;
    this.bonus = bonus;
    this.bonusExpire = bonusExpire;
  }

  protected void writeImpl() {
    this.writeC(16);
    this.writeS(this.account);
    this.writeF(this.bonus);
    this.writeD(this.bonusExpire);
  }
}
