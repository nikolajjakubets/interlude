//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.templates.manor.CropProcure;

public class RequestSetCrop extends L2GameClientPacket {
  private int _count;
  private int _manorId;
  private long[] _items;

  public RequestSetCrop() {
  }

  protected void readImpl() {
    this._manorId = this.readD();
    this._count = this.readD();
    if (this._count * 13 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new long[this._count * 4];

      for(int i = 0; i < this._count; ++i) {
        this._items[i * 4 + 0] = (long)this.readD();
        this._items[i * 4 + 1] = (long)this.readD();
        this._items[i * 4 + 2] = (long)this.readD();
        this._items[i * 4 + 3] = (long)this.readC();
        if (this._items[i * 4 + 0] < 1L || this._items[i * 4 + 1] < 0L || this._items[i * 4 + 2] < 0L) {
          this._count = 0;
          return;
        }
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._count != 0) {
      if (activeChar.getClan() == null) {
        activeChar.sendActionFailed();
      } else {
        Castle caslte = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
        if (caslte.getOwnerId() == activeChar.getClanId() && (activeChar.getClanPrivileges() & 65536) == 65536) {
          List<CropProcure> crops = new ArrayList(this._count);

          for(int i = 0; i < this._count; ++i) {
            int id = (int)this._items[i * 4 + 0];
            long sales = this._items[i * 4 + 1];
            long price = this._items[i * 4 + 2];
            int type = (int)this._items[i * 4 + 3];
            if (id > 0) {
              CropProcure s = CastleManorManager.getInstance().getNewCropProcure(id, sales, type, price, sales);
              crops.add(s);
            }
          }

          caslte.setCropProcure(crops, 1);
          caslte.saveCropData(1);
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
