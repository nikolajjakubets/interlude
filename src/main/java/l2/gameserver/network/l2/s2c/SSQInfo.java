//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.SevenSigns;

public class SSQInfo extends L2GameServerPacket {
  private int _state = 0;

  public SSQInfo() {
    int compWinner = SevenSigns.getInstance().getCabalHighestScore();
    if (SevenSigns.getInstance().isSealValidationPeriod()) {
      if (compWinner == 2) {
        this._state = 2;
      } else if (compWinner == 1) {
        this._state = 1;
      }
    }

  }

  public SSQInfo(int state) {
    this._state = state;
  }

  protected final void writeImpl() {
    this.writeC(248);
    switch(this._state) {
      case 1:
        this.writeH(257);
        break;
      case 2:
        this.writeH(258);
        break;
      default:
        this.writeH(256);
    }

  }
}
