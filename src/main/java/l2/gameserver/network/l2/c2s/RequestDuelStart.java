//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestDuelStart extends L2GameClientPacket {
  private String _name;
  private int _duelType;

  public RequestDuelStart() {
  }

  protected void readImpl() {
    this._name = this.readS(Config.CNAME_MAXLEN);
    this._duelType = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      if (player.isActionsDisabled()) {
        player.sendActionFailed();
      } else if (player.isProcessingRequest()) {
        player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
      } else {
        Player target = World.getPlayer(this._name);
        if (target != null && target != player) {
          DuelEvent duelEvent = (DuelEvent)EventHolder.getInstance().getEvent(EventType.PVP_EVENT, this._duelType);
          if (duelEvent != null) {
            if (duelEvent.canDuel(player, target, true)) {
              if (target.isBusy()) {
                player.sendPacket((new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK)).addName(target));
              } else {
                duelEvent.askDuel(player, target);
              }
            }
          }
        } else {
          player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
        }
      }
    }
  }
}
