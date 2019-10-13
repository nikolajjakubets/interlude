//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;

public class NetPing extends L2GameClientPacket {
  public static final int MIN_CLIP_RANGE = 1433;
  public static final int MAX_CLIP_RANGE = 6144;
  private int _timestamp;
  private int _clippingRange;
  private int _fps;

  public NetPing() {
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    if (client.getRevision() == 0) {
      client.closeNow(false);
    } else {
      client.onPing(this._timestamp, this._fps, Math.max(1433, Math.min(this._clippingRange, 6144)));
    }

  }

  protected void readImpl() {
    this._timestamp = this.readD();
    this._fps = this.readD();
    this._clippingRange = this.readD();
  }
}
