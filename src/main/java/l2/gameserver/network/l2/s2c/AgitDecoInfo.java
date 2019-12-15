//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.residence.ClanHall;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgitDecoInfo extends L2GameServerPacket {
  private static int[] _buff = new int[]{0, 1, 1, 1, 2, 2, 2, 2, 2, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2};
  private static int[] _itCr8 = new int[]{0, 1, 2, 2};
  private int _id;
  private int hp_recovery;
  private int mp_recovery;
  private int exp_recovery;
  private int teleport;
  private int curtains;
  private int itemCreate;
  private int support;
  private int platform;

  public AgitDecoInfo(ClanHall clanHall) {
    this._id = clanHall.getId();
    this.hp_recovery = getHpRecovery(clanHall.isFunctionActive(3) ? clanHall.getFunction(3).getLevel() : 0);
    this.mp_recovery = getMpRecovery(clanHall.isFunctionActive(4) ? clanHall.getFunction(4).getLevel() : 0);
    this.exp_recovery = getExpRecovery(clanHall.isFunctionActive(5) ? clanHall.getFunction(5).getLevel() : 0);
    this.teleport = clanHall.isFunctionActive(1) ? clanHall.getFunction(1).getLevel() : 0;
    this.curtains = clanHall.isFunctionActive(7) ? clanHall.getFunction(7).getLevel() : 0;
    this.itemCreate = clanHall.isFunctionActive(2) ? _itCr8[clanHall.getFunction(2).getLevel()] : 0;
    this.support = clanHall.isFunctionActive(6) ? _buff[clanHall.getFunction(6).getLevel()] : 0;
    this.platform = clanHall.isFunctionActive(8) ? clanHall.getFunction(8).getLevel() : 0;
  }

  protected final void writeImpl() {
    this.writeC(247);
    this.writeD(this._id);
    this.writeC(this.hp_recovery);
    this.writeC(this.mp_recovery);
    this.writeC(this.mp_recovery);
    this.writeC(this.exp_recovery);
    this.writeC(this.teleport);
    this.writeC(0);
    this.writeC(this.curtains);
    this.writeC(this.itemCreate);
    this.writeC(this.support);
    this.writeC(this.support);
    this.writeC(this.platform);
    this.writeC(this.itemCreate);
    this.writeD(0);
    this.writeD(0);
  }

  private static int getHpRecovery(int percent) {
    switch (percent) {
      case 0:
        return 0;
      case 20:
      case 40:
      case 80:
      case 120:
      case 140:
        return 1;
      case 160:
      case 180:
      case 200:
      case 220:
      case 240:
      case 260:
      case 280:
      case 300:
        return 2;
      default:
        log.warn("Unsupported percent " + percent + " in hp recovery");
        return 0;
    }
  }

  private static int getMpRecovery(int percent) {
    switch (percent) {
      case 0:
        return 0;
      case 5:
      case 10:
      case 15:
      case 20:
        return 1;
      case 25:
      case 30:
      case 35:
      case 40:
      case 45:
      case 50:
        return 2;
      default:
        log.warn("Unsupported percent " + percent + " in mp recovery");
        return 0;
    }
  }

  private static int getExpRecovery(int percent) {
    switch (percent) {
      case 0:
        return 0;
      case 5:
      case 10:
      case 15:
      case 20:
        return 1;
      case 25:
      case 30:
      case 35:
      case 40:
      case 45:
      case 50:
        return 2;
      default:
        log.warn("Unsupported percent " + percent + " in exp recovery");
        return 0;
    }
  }
}
