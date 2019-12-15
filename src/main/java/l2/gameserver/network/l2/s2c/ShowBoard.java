//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ShowBoard extends L2GameServerPacket {
  private static final Charset BBS_CHARSET = StandardCharsets.UTF_16LE;
  private String _htmlCode;
  private String _id;
  private List<String> _arg;
  private String _addFav;

  public static void separateAndSend(String html, Player player) {
    html = player.getNetConnection().encodeBypasses(html, true);
    byte[] htmlBytes = html.getBytes(BBS_CHARSET);
    if (htmlBytes.length < 8180) {
      player.sendPacket(new ShowBoard(html, "101", player, false));
      player.sendPacket(new ShowBoard(null, "102", player, false));
      player.sendPacket(new ShowBoard(null, "103", player, false));
    } else if (htmlBytes.length < 16360) {
      player.sendPacket(new ShowBoard(new String(htmlBytes, 0, 8180, BBS_CHARSET), "101", player, false));
      player.sendPacket(new ShowBoard(new String(htmlBytes, 8180, htmlBytes.length - 8180, BBS_CHARSET), "102", player, false));
      player.sendPacket(new ShowBoard(null, "103", player, false));
    } else {
      player.sendPacket(new ShowBoard(new String(htmlBytes, 0, 8180, BBS_CHARSET), "101", player, false));
      player.sendPacket(new ShowBoard(new String(htmlBytes, 8180, htmlBytes.length - 8180, BBS_CHARSET), "102", player, false));
      player.sendPacket(new ShowBoard(new String(htmlBytes, 16360, htmlBytes.length - 16360, BBS_CHARSET), "103", player, false));
    }

  }

  private ShowBoard(String htmlCode, String id, Player player, boolean encodeBypasses) {
    this._addFav = "";
    if (htmlCode != null && htmlCode.length() > 8192) {
      log.warn("Html '" + htmlCode + "' is too long! this will crash the client!");
      this._htmlCode = "<html><body>Html was too long</body></html>";
    } else {
      this._id = id;
      if (player.getSessionVar("add_fav") != null) {
        this._addFav = "bypass _bbsaddfav_List";
      }

      if (htmlCode != null) {
        if (encodeBypasses) {
          this._htmlCode = player.getNetConnection().encodeBypasses(htmlCode, true);
        } else {
          this._htmlCode = htmlCode;
        }
      } else {
        this._htmlCode = null;
      }

    }
  }

  public ShowBoard(String htmlCode, String id, Player player) {
    this(htmlCode, id, player, true);
  }

  public ShowBoard(List<String> arg) {
    this._addFav = "";
    this._id = "1002";
    this._htmlCode = null;
    this._arg = arg;
  }

  protected final void writeImpl() {
    this.writeC(110);
    this.writeC(1);
    this.writeS("bypass _bbshome");
    this.writeS("bypass _bbsgetfav");
    this.writeS("bypass _bbsloc");
    this.writeS("bypass _bbsclan");
    this.writeS("bypass _bbsmemo");
    this.writeS("bypass _maillist_0_1_0_");
    this.writeS("bypass _friendlist_0_");
    this.writeS(this._addFav);
    String str = this._id + "\b";
    String arg;
    if (!this._id.equals("1002")) {
      if (this._htmlCode != null) {
        str = str + this._htmlCode;
      }
    } else {
      for(Iterator var2 = this._arg.iterator(); var2.hasNext(); str = str + arg + " \b") {
        arg = (String)var2.next();
      }
    }

    this.writeS(str);
  }
}
