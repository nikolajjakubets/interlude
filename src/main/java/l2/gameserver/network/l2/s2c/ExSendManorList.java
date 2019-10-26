//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;

public class ExSendManorList extends L2GameServerPacket {
  public ExSendManorList() {
  }

  protected void writeImpl() {
    this.writeEx(27);
    Collection<Castle> residences = ResidenceHolder.getInstance().getResidenceList(Castle.class);
    this.writeD(residences.size());
    Iterator var2 = residences.iterator();

    while(var2.hasNext()) {
      Residence castle = (Residence)var2.next();
      this.writeD(castle.getId());
      this.writeS(castle.getName().toLowerCase());
    }

  }
}
