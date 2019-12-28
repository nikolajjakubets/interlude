//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GameClient.GameClientState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.scripts.Functions;

public class Relog extends Functions implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"relog", "restart"};

  public Relog() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player activeChar, String target) {
    if (!Config.ALT_ALLOW_RELOG_COMMAND) {
      return false;
    } else if (!command.equals("relog") && !command.equals("restart")) {
      return false;
    } else if (activeChar == null) {
      return false;
    } else if (activeChar.isInObserverMode()) {
      activeChar.sendPacket(new IStaticPacket[]{Msg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.getStatic()});
      return false;
    } else if (activeChar.isInCombat()) {
      activeChar.sendPacket(new IStaticPacket[]{Msg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.getStatic()});
      return false;
    } else if (activeChar.isFishing()) {
      activeChar.sendPacket(new IStaticPacket[]{Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING, RestartResponse.FAIL, ActionFail.getStatic()});
      return false;
    } else if (activeChar.isBlocked() && !activeChar.isFlying()) {
      activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.OutOfControl", activeChar, new Object[0]));
      activeChar.sendPacket(new IStaticPacket[]{RestartResponse.FAIL, ActionFail.getStatic()});
      return false;
    } else if (activeChar.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized()) {
      activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.Festival", activeChar, new Object[0]));
      activeChar.sendPacket(new IStaticPacket[]{RestartResponse.FAIL, ActionFail.getStatic()});
      return false;
    } else {
      final GameClient client = activeChar.getNetConnection();
      if (client != null && client.isConnected()) {
        client.setState(GameClientState.AUTHED);
        synchronized(activeChar) {
          final int objId = activeChar.getObjectId();
          Runnable doSelect = new RunnableImpl() {
            public void runImpl() throws Exception {
              if (client != null && client.isConnected() && client.isAuthed()) {
                if (!Config.USE_SECOND_PASSWORD_AUTH || client.isSecondPasswordAuthed()) {
                  int slotIdx = client.getSlotForObjectId(objId);
                  if (slotIdx >= 0) {
                    Player activeChar = client.loadCharFromDisk(slotIdx);
                    client.setState(GameClientState.IN_GAME);
                    client.sendPacket(new CharSelected(activeChar, client.getSessionKey().playOkID1));
                  }
                }
              }
            }
          };
          activeChar.restart();
          CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
          client.sendPacket(new L2GameServerPacket[]{RestartResponse.OK, cl});
          client.setCharSelection(cl.getCharInfo());
          ThreadPoolManager.getInstance().schedule(doSelect, 333L);
          return true;
        }
      } else {
        return false;
      }
    }
  }
}
