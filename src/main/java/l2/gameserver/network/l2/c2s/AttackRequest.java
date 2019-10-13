//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;

public class AttackRequest extends L2GameClientPacket {
  private int _objectId;
  private int _originX;
  private int _originY;
  private int _originZ;
  private int _attackId;

  public AttackRequest() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._originX = this.readD();
    this._originY = this.readD();
    this._originZ = this.readD();
    this._attackId = this.readC();
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    Player activeChar = client.getActiveChar();
    if (activeChar != null) {
      activeChar.setActive();
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (!activeChar.getPlayerAccess().CanAttack) {
        activeChar.sendActionFailed();
      } else {
        GameObject target = activeChar.getVisibleObject(this._objectId);
        if (target == null) {
          activeChar.sendActionFailed();
        } else if (activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != target && !activeChar.getAggressionTarget().isDead()) {
          activeChar.sendActionFailed();
        } else if (!target.isPlayer() || !activeChar.isInBoat() && !target.isInBoat()) {
          if (target.isPlayable()) {
            if (activeChar.isInZonePeace()) {
              activeChar.sendPacket(new IStaticPacket[]{Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE, ActionFail.STATIC});
              return;
            }

            if (((Playable)target).isInZonePeace()) {
              activeChar.sendPacket(new IStaticPacket[]{Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE, ActionFail.STATIC});
              return;
            }
          }

          long now = System.currentTimeMillis();
          if (now - client.getLastIncomePacketTimeStamp(AttackRequest.class) < (long)Config.ATTACK_PACKET_DELAY) {
            activeChar.sendActionFailed();
          } else {
            client.setLastIncomePacketTimeStamp(AttackRequest.class, now);
            if (activeChar.getTarget() != target) {
              target.onAction(activeChar, this._attackId == 1);
            } else {
              if (target.getObjectId() != activeChar.getObjectId() && !activeChar.isInStoreMode() && !activeChar.isProcessingRequest()) {
                target.onForcedAttack(activeChar, this._attackId == 1);
              }

            }
          }
        } else {
          activeChar.sendPacket(new IStaticPacket[]{Msg.THIS_IS_NOT_ALLOWED_WHILE_USING_A_FERRY, ActionFail.STATIC});
        }
      }
    }
  }
}
