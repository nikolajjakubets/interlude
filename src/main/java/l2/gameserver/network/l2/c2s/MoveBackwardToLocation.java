//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharMoveToLocation;
import l2.gameserver.utils.Location;

public class MoveBackwardToLocation extends L2GameClientPacket {
  private Location _targetLoc = new Location();
  private Location _originLoc = new Location();
  private int _moveMovement;

  public MoveBackwardToLocation() {
  }

  protected void readImpl() {
    this._targetLoc.x = this.readD();
    this._targetLoc.y = this.readD();
    this._targetLoc.z = this.readD();
    this._originLoc.x = this.readD();
    this._originLoc.y = this.readD();
    this._originLoc.z = this.readD();
    if (this._buf.hasRemaining()) {
      this._moveMovement = this.readD();
    }

  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    if (client != null) {
      Player activeChar = client.getActiveChar();
      if (activeChar != null) {
        activeChar.setActive();
        if (activeChar.isTeleporting()) {
          activeChar.sendActionFailed();
        } else if (activeChar.isFrozen()) {
          activeChar.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC});
        } else if (activeChar.isOlyObserver()) {
          if (activeChar.getOlyObservingStadium().getObservingLoc().distance(this._targetLoc) < 8192.0D) {
            activeChar.sendPacket(new CharMoveToLocation(activeChar.getObjectId(), this._originLoc, this._targetLoc));
          } else {
            activeChar.sendActionFailed();
          }

        } else if (activeChar.isOutOfControl()) {
          activeChar.sendActionFailed();
        } else if (Config.ALT_ALLOW_DELAY_NPC_TALK && !activeChar.canMoveAfterInteraction()) {
          activeChar.sendMessage(new CustomMessage("YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC__ONE_MOMENT_PLEASE", activeChar, new Object[0]));
          activeChar.sendActionFailed();
        } else if (activeChar.getTeleMode() > 0) {
          if (activeChar.getTeleMode() == 1) {
            activeChar.setTeleMode(0);
          }

          activeChar.sendActionFailed();
          activeChar.teleToLocation(this._targetLoc);
        } else {
          if (activeChar.isInFlyingTransform()) {
            this._targetLoc.z = Math.min(5950, Math.max(50, this._targetLoc.z));
          }

          activeChar.moveBackwardToLocationForPacket(this._targetLoc, this._moveMovement != 0 && !activeChar.getVarB("no_pf"));
        }
      }
    }
  }
}
