//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ChooseInventoryItem extends L2GameServerPacket {
  private int ItemID;

  public ChooseInventoryItem(int id) {
    this.ItemID = id;
  }

  protected final void writeImpl() {
    this.writeC(111);
    this.writeD(this.ItemID);
  }
}
