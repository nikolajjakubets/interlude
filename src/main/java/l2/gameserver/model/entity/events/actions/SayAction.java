//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.Say2;

public class SayAction implements EventAction {
  private int _range;
  private ChatType _chatType;
  private String _how;
  private NpcString _text;
  private SysString _sysString;
  private SystemMsg _systemMsg;

  protected SayAction(int range, ChatType type) {
    this._range = range;
    this._chatType = type;
  }

  public SayAction(int range, ChatType type, SysString sysString, SystemMsg systemMsg) {
    this(range, type);
    this._sysString = sysString;
    this._systemMsg = systemMsg;
  }

  public SayAction(int range, ChatType type, String how, NpcString string) {
    this(range, type);
    this._text = string;
    this._how = how;
  }

  public void call(GlobalEvent event) {
    List<Player> players = event.broadcastPlayers(this._range);
    Iterator var3 = players.iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      this.packet(player);
    }

  }

  private void packet(Player player) {
    if (player != null) {
      L2GameServerPacket packet = null;
      if (this._sysString != null) {
        packet = new Say2(0, this._chatType, this._sysString, this._systemMsg);
      } else {
        packet = new Say2(0, this._chatType, this._how, this._text, new String[0]);
      }

      player.sendPacket(packet);
    }
  }
}
