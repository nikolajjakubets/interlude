//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.scripts.Scripts.ScriptClassAndMethod;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcHtmlMessage extends L2GameServerPacket {
  protected static final Logger _log = LoggerFactory.getLogger(NpcHtmlMessage.class);
  protected static final Pattern objectId = Pattern.compile("%objectId%");
  protected static final Pattern playername = Pattern.compile("%playername%");
  protected int _npcObjId;
  protected String _html;
  protected String _file;
  protected List<String> _replaces;
  protected boolean have_appends;

  public NpcHtmlMessage(Player player, int npcId, String filename, int val) {
    this._file = null;
    this._replaces = new ArrayList();
    this.have_appends = false;
    List<ScriptClassAndMethod> appends = (List)Scripts.dialogAppends.get(npcId);
    if (appends != null && appends.size() > 0) {
      this.have_appends = true;
      if (filename != null && filename.equalsIgnoreCase("npcdefault.htm")) {
        this.setHtml("");
      } else {
        this.setFile(filename);
      }

      String replaces = "";
      Object[] script_args = new Object[]{new Integer(val)};
      Iterator var8 = appends.iterator();

      while(var8.hasNext()) {
        ScriptClassAndMethod append = (ScriptClassAndMethod)var8.next();
        Object obj = Scripts.getInstance().callScripts(player, append.className, append.methodName, script_args);
        if (obj != null) {
          replaces = replaces + obj;
        }
      }

      if (!replaces.equals("")) {
        this.replace("</body>", "\n" + Strings.bbParse(replaces) + "</body>");
      }
    } else {
      this.setFile(filename);
    }

  }

  public NpcHtmlMessage(Player player, NpcInstance npc, String filename, int val) {
    this(player, npc.getNpcId(), filename, val);
    this._npcObjId = npc.getObjectId();
    player.setLastNpc(npc);
    this.replace("%npcId%", String.valueOf(npc.getNpcId()));
    this.replace("%npcname%", npc.getName());
    this.replace("%festivalMins%", SevenSignsFestival.getInstance().getTimeToNextFestivalStr());
  }

  public NpcHtmlMessage(Player player, NpcInstance npc) {
    this._file = null;
    this._replaces = new ArrayList();
    this.have_appends = false;
    if (npc == null) {
      this._npcObjId = 5;
      player.setLastNpc((NpcInstance)null);
    } else {
      this._npcObjId = npc.getObjectId();
      player.setLastNpc(npc);
    }

  }

  public NpcHtmlMessage(int npcObjId) {
    this._file = null;
    this._replaces = new ArrayList();
    this.have_appends = false;
    this._npcObjId = npcObjId;
  }

  public final NpcHtmlMessage setHtml(String text) {
    if (!text.contains("<html>")) {
      text = "<html><body>" + text + "</body></html>";
    }

    this._html = text;
    return this;
  }

  public final NpcHtmlMessage setFile(String file) {
    this._file = file;
    if (this._file.startsWith("data/html/")) {
      _log.info("NpcHtmlMessage: need fix : " + file, new Exception());
      this._file = this._file.replace("data/html/", "");
    }

    return this;
  }

  public NpcHtmlMessage replace(String pattern, String value) {
    if (pattern != null && value != null) {
      this._replaces.add(pattern);
      this._replaces.add(value);
      return this;
    } else {
      return this;
    }
  }

  public NpcHtmlMessage replaceNpcString(String pattern, NpcString npcString, Object... arg) {
    if (pattern == null) {
      return this;
    } else if (npcString.getSize() != arg.length) {
      throw new IllegalArgumentException("Not valid size of parameters: " + npcString);
    } else {
      this._replaces.add(pattern);
      this._replaces.add(HtmlUtils.htmlNpcString(npcString, arg));
      return this;
    }
  }

  public void processHtml(GameClient client) {
    Player player = client.getActiveChar();
    if (this._file != null) {
      if (player != null && player.isGM()) {
        Functions.sendDebugMessage(player, "HTML: " + this._file);
      }

      String content = HtmCache.getInstance().getNotNull(this._file, player);
      String content2 = HtmCache.getInstance().getNullable(this._file, player);
      if (content2 == null) {
        this.setHtml(this.have_appends && this._file.endsWith(".htm") ? "" : content);
      } else {
        this.setHtml(content);
      }
    }

    for(int i = 0; i < this._replaces.size(); i += 2) {
      this._html = this._html.replace((CharSequence)this._replaces.get(i), (CharSequence)this._replaces.get(i + 1));
    }

    if (this._html != null) {
      Matcher m = objectId.matcher(this._html);
      if (m != null) {
        this._html = m.replaceAll(String.valueOf(this._npcObjId));
      }

      if (player != null) {
        this._html = playername.matcher(this._html).replaceAll(player.getName());
      }

      client.cleanBypasses(false);
      this._html = client.encodeBypasses(this._html, false);
    }
  }

  protected void writeImpl() {
    if (this._html != null) {
      this.writeC(15);
      this.writeD(this._npcObjId);
      this.writeS(this._html);
      this.writeD(0);
    }

  }
}
