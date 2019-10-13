//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.MonsterRace;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.DeleteObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MonRaceInfo;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.PlaySound.Type;
import l2.gameserver.utils.Location;

public class AdminMonsterRace implements IAdminCommandHandler {
  protected static int state = -1;

  public AdminMonsterRace() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminMonsterRace.Commands command = (AdminMonsterRace.Commands)comm;
    if (fullString.equalsIgnoreCase("admin_mons")) {
      if (!activeChar.getPlayerAccess().MonsterRace) {
        return false;
      }

      this.handleSendPacket(activeChar);
    }

    return true;
  }

  public Enum[] getAdminCommandEnum() {
    return AdminMonsterRace.Commands.values();
  }

  private void handleSendPacket(Player activeChar) {
    int[][] codes = new int[][]{{-1, 0}, {0, 15322}, {13765, -1}, {-1, 0}};
    MonsterRace race = MonsterRace.getInstance();
    if (state == -1) {
      ++state;
      race.newRace();
      race.newSpeeds();
      activeChar.broadcastPacket(new L2GameServerPacket[]{new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds())});
    } else if (state == 0) {
      ++state;
      activeChar.sendPacket(Msg.THEYRE_OFF);
      activeChar.broadcastPacket(new L2GameServerPacket[]{new PlaySound("S_Race")});
      activeChar.broadcastPacket(new L2GameServerPacket[]{new PlaySound(Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559))});
      activeChar.broadcastPacket(new L2GameServerPacket[]{new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds())});
      ThreadPoolManager.getInstance().schedule(new AdminMonsterRace.RunRace(codes, activeChar), 5000L);
    }

  }

  class RunEnd extends RunnableImpl {
    private Player activeChar;

    public RunEnd(Player activeChar) {
      this.activeChar = activeChar;
    }

    public void runImpl() throws Exception {
      for(int i = 0; i < 8; ++i) {
        NpcInstance obj = MonsterRace.getInstance().getMonsters()[i];
        this.activeChar.broadcastPacket(new L2GameServerPacket[]{new DeleteObject(obj)});
      }

      AdminMonsterRace.state = -1;
    }
  }

  class RunRace extends RunnableImpl {
    private int[][] codes;
    private Player activeChar;

    public RunRace(int[][] codes, Player activeChar) {
      this.codes = codes;
      this.activeChar = activeChar;
    }

    public void runImpl() throws Exception {
      this.activeChar.broadcastPacket(new L2GameServerPacket[]{new MonRaceInfo(this.codes[2][0], this.codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds())});
      ThreadPoolManager.getInstance().schedule(AdminMonsterRace.this.new RunEnd(this.activeChar), 30000L);
    }
  }

  private static enum Commands {
    admin_mons;

    private Commands() {
    }
  }
}
