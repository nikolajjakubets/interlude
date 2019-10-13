//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.NpcSay;
import l2.gameserver.utils.MapUtils;

public class NpcSayAction implements EventAction {
  private int _npcId;
  private int _range;
  private ChatType _chatType;
  private NpcString _text;

  public NpcSayAction(int npcId, int range, ChatType type, NpcString string) {
    this._npcId = npcId;
    this._range = range;
    this._chatType = type;
    this._text = string;
  }

  public void call(GlobalEvent event) {
    NpcInstance npc = GameObjectsStorage.getByNpcId(this._npcId);
    if (npc != null) {
      if (this._range <= 0) {
        int rx = MapUtils.regionX(npc);
        int ry = MapUtils.regionY(npc);
        int offset = Config.SHOUT_OFFSET;
        Iterator var6 = GameObjectsStorage.getAllPlayersForIterate().iterator();

        while(var6.hasNext()) {
          Player player = (Player)var6.next();
          if (npc.getReflection() == player.getReflection()) {
            int tx = MapUtils.regionX(player);
            int ty = MapUtils.regionY(player);
            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset) {
              this.packet(npc, player);
            }
          }
        }
      } else {
        Iterator var10 = World.getAroundPlayers(npc, this._range, Math.max(this._range / 2, 200)).iterator();

        while(var10.hasNext()) {
          Player player = (Player)var10.next();
          if (npc.getReflection() == player.getReflection()) {
            this.packet(npc, player);
          }
        }
      }

    }
  }

  private void packet(NpcInstance npc, Player player) {
    player.sendPacket(new NpcSay(npc, this._chatType, this._text, new String[0]));
  }
}
