//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.network.l2.GameClient;

public class RequestChangePetName extends L2GameClientPacket {
  private String _name;

  public RequestChangePetName() {
  }

  protected void readImpl() {
    this._name = this.readS();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    PetInstance pet = activeChar.getPet() != null && activeChar.getPet().isPet() ? (PetInstance)activeChar.getPet() : null;
    if (pet != null) {
      if (pet.isDefaultName()) {
        if (this._name.length() < 1 || this._name.length() > 8) {
          this.sendPacket(Msg.YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS);
          return;
        }

        pet.setName(this._name);
        pet.broadcastCharInfo();
        pet.updateControlItem();
      }

    }
  }
}
