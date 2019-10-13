//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class CTBTeamObject implements SpawnableObject {
  private CTBSiegeClanObject _siegeClan;
  private final NpcTemplate _mobTemplate;
  private final NpcTemplate _flagTemplate;
  private final Location _flagLoc;
  private NpcInstance _flag;
  private CTBBossInstance _mob;

  public CTBTeamObject(int mobTemplate, int flagTemplate, Location flagLoc) {
    this._mobTemplate = NpcHolder.getInstance().getTemplate(mobTemplate);
    this._flagTemplate = NpcHolder.getInstance().getTemplate(flagTemplate);
    this._flagLoc = flagLoc;
  }

  public void spawnObject(GlobalEvent event) {
    if (this._flag == null) {
      this._flag = new NpcInstance(IdFactory.getInstance().getNextId(), this._flagTemplate);
      this._flag.setCurrentHpMp((double)this._flag.getMaxHp(), (double)this._flag.getMaxMp());
      this._flag.setHasChatWindow(false);
      this._flag.spawnMe(this._flagLoc);
    } else {
      if (this._mob != null) {
        throw new IllegalArgumentException("Cant spawn twice");
      }

      NpcTemplate template = this._siegeClan != null && this._siegeClan.getParam() != 0L ? NpcHolder.getInstance().getTemplate((int)this._siegeClan.getParam()) : this._mobTemplate;
      this._mob = (CTBBossInstance)template.getNewInstance();
      this._mob.setCurrentHpMp((double)this._mob.getMaxHp(), (double)this._mob.getMaxMp());
      this._mob.setMatchTeamObject(this);
      this._mob.addEvent(event);
      int x = (int)((double)this._flagLoc.x + 300.0D * Math.cos(this._mob.headingToRadians(this._flag.getHeading() - '耀')));
      int y = (int)((double)this._flagLoc.y + 300.0D * Math.sin(this._mob.headingToRadians(this._flag.getHeading() - '耀')));
      Location loc = new Location(x, y, this._flag.getZ(), this._flag.getHeading());
      this._mob.setSpawnedLoc(loc);
      this._mob.spawnMe(loc);
    }

  }

  public void despawnObject(GlobalEvent event) {
    if (this._mob != null) {
      this._mob.deleteMe();
      this._mob = null;
    }

    if (this._flag != null) {
      this._flag.deleteMe();
      this._flag = null;
    }

    this._siegeClan = null;
  }

  public void refreshObject(GlobalEvent event) {
  }

  public CTBSiegeClanObject getSiegeClan() {
    return this._siegeClan;
  }

  public void setSiegeClan(CTBSiegeClanObject siegeClan) {
    this._siegeClan = siegeClan;
  }

  public boolean isParticle() {
    return this._flag != null && this._mob != null;
  }

  public NpcInstance getFlag() {
    return this._flag;
  }
}
