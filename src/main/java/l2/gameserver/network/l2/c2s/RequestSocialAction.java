//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SocialAction;

public class RequestSocialAction extends L2GameClientPacket {
  private int _actionId;

  public RequestSocialAction() {
  }

  protected void readImpl() throws Exception {
    this._actionId = this.readD();
  }

  protected void runImpl() throws Exception {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (!activeChar.isOutOfControl() && activeChar.getTransformation() == 0 && !activeChar.isCursedWeaponEquipped() && !activeChar.isActionsDisabled() && !activeChar.isSitting() && activeChar.getPrivateStoreType() == 0 && !activeChar.isProcessingRequest()) {
        if (activeChar.isFishing()) {
          activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
        } else {
          if (this._actionId > 1 && this._actionId < 14) {
            activeChar.broadcastPacket(new L2GameServerPacket[]{new SocialAction(activeChar.getObjectId(), this._actionId)});
          }

        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
