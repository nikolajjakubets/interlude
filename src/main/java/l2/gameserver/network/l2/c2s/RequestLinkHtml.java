//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLinkHtml extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(RequestLinkHtml.class);
  private String _link;

  public RequestLinkHtml() {
  }

  protected void readImpl() {
    this._link = this.readS();
  }

  protected void runImpl() {
    Player actor = ((GameClient)this.getClient()).getActiveChar();
    if (actor != null) {
      if (!this._link.contains("..") && this._link.endsWith(".htm")) {
        try {
          NpcHtmlMessage msg = new NpcHtmlMessage(0);
          msg.setFile("" + this._link);
          this.sendPacket(msg);
        } catch (Exception var3) {
          _log.warn("Bad RequestLinkHtml: ", var3);
        }

      } else {
        _log.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + this._link + "', skipped");
      }
    }
  }
}
