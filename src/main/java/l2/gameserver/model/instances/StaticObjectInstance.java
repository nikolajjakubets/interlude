//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.reference.L2Reference;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ShowTownMap;
import l2.gameserver.network.l2.s2c.StaticObject;
import l2.gameserver.scripts.Events;
import l2.gameserver.templates.StaticObjectTemplate;
import l2.gameserver.utils.Location;

public class StaticObjectInstance extends GameObject {
  private final HardReference<StaticObjectInstance> reference;
  private final StaticObjectTemplate _template;
  private int _meshIndex;

  public StaticObjectInstance(int objectId, StaticObjectTemplate template) {
    super(objectId);
    this._template = template;
    this.reference = new L2Reference(this);
  }

  public HardReference<StaticObjectInstance> getRef() {
    return this.reference;
  }

  public int getUId() {
    return this._template.getUId();
  }

  public int getType() {
    return this._template.getType();
  }

  public void onAction(Player player, boolean shift) {
    if (!Events.onAction(player, this, shift)) {
      if (player.getTarget() != this) {
        player.setTarget(this);
        player.sendPacket(new MyTargetSelected(this.getObjectId(), 0));
      } else {
        MyTargetSelected my = new MyTargetSelected(this.getObjectId(), 0);
        player.sendPacket(my);
        if (!this.isInRange(player, (long)this.getActingRange())) {
          if (!player.getAI().isIntendingInteract(this)) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
          }

        } else {
          if (this._template.getType() == 0) {
            player.sendPacket(new NpcHtmlMessage(player, this.getUId(), "newspaper/arena.htm", 0));
          } else if (this._template.getType() == 2) {
            player.sendPacket(new ShowTownMap(this._template.getFilePath(), this._template.getMapX(), this._template.getMapY()));
            player.sendActionFailed();
          }

        }
      }
    }
  }

  public int getActingRange() {
    switch(this._template.getType()) {
      case 1:
        return 150;
      default:
        return 300;
    }
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    return Collections.singletonList(new StaticObject(this));
  }

  public boolean isAttackable(Creature attacker) {
    return false;
  }

  public void broadcastInfo(boolean force) {
    StaticObject p = new StaticObject(this);
    Iterator var3 = World.getAroundPlayers(this).iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      player.sendPacket(p);
    }

  }

  public int getGeoZ(Location loc) {
    return loc.z;
  }

  public int getMeshIndex() {
    return this._meshIndex;
  }

  public void setMeshIndex(int meshIndex) {
    this._meshIndex = meshIndex;
  }
}
