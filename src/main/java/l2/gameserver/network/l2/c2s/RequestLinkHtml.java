//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLinkHtml extends L2GameClientPacket {
  private String _link;

  public RequestLinkHtml() {
  }

  protected void readImpl() {
    this._link = this.readS();
  }

  protected void runImpl() {
    Player actor = this.getClient().getActiveChar();
    if (actor != null) {
      if (!this._link.contains("..") && this._link.endsWith(".htm")) {
        try {
          NpcHtmlMessage msg = new NpcHtmlMessage(0);
          msg.setFile("" + this._link);
          this.sendPacket(msg);
        } catch (Exception var3) {
          log.warn("Bad RequestLinkHtml: ", var3);
        }

      } else {
        log.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + this._link + "', skipped");
      }
    }
  }
}
