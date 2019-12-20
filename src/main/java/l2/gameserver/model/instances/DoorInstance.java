//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.commons.geometry.Shape;
import l2.commons.listener.Listener;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DoorAI;
import l2.gameserver.geodata.GeoCollision;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.listener.actor.door.OnOpenCloseListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.scripts.Events;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.DoorTemplate.DoorType;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class DoorInstance extends Creature implements GeoCollision {
  private boolean _open = true;
  private boolean _geoOpen = true;
  private Lock _openLock = new ReentrantLock();
  private int _upgradeHp;
  private byte[][] _geoAround;
  protected ScheduledFuture<?> _autoActionTask;

  public DoorInstance(int objectId, DoorTemplate template) {
    super(objectId, template);
  }

  public boolean isUnlockable() {
    return this.getTemplate().isUnlockable();
  }

  public String getName() {
    return this.getTemplate().getName();
  }

  public int getLevel() {
    return 1;
  }

  public int getDoorId() {
    return this.getTemplate().getNpcId();
  }

  public boolean isOpen() {
    return this._open;
  }

  protected boolean setOpen(boolean open) {
    if (this._open == open) {
      return false;
    } else {
      this._open = open;
      return true;
    }
  }

  public void scheduleAutoAction(boolean open, long actionDelay) {
    if (this._autoActionTask != null) {
      this._autoActionTask.cancel(false);
      this._autoActionTask = null;
    }

    this._autoActionTask = ThreadPoolManager.getInstance().schedule(new DoorInstance.AutoOpenClose(open), actionDelay);
  }

  public int getDamage() {
    int dmg = 6 - (int)Math.ceil(this.getCurrentHpRatio() * 6.0D);
    return Math.max(0, Math.min(6, dmg));
  }

  public boolean isAutoAttackable(Creature attacker) {
    return this.isAttackable(attacker);
  }

  public boolean isAttackable(Creature attacker) {
    if (attacker != null && !this.isOpen()) {
      SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
      return !this.isInvul();
    } else {
      return false;
    }
  }

  public void sendChanges() {
  }

  public ItemInstance getActiveWeaponInstance() {
    return null;
  }

  public WeaponTemplate getActiveWeaponItem() {
    return null;
  }

  public ItemInstance getSecondaryWeaponInstance() {
    return null;
  }

  public WeaponTemplate getSecondaryWeaponItem() {
    return null;
  }

  public Location getCenterPoint() {
    Shape shape = this.getShape();
    return new Location(shape.getXmin() + (shape.getXmax() - shape.getXmin() >> 1), shape.getYmin() + (shape.getYmax() - shape.getYmin() >> 1), shape.getZmin() + (shape.getZmax() - shape.getZmin() >> 1));
  }

  public void onAction(Player player, boolean shift) {
    if (!Events.onAction(player, this, shift)) {
      if (this != player.getTarget()) {
        player.setTarget(this);
        player.sendPacket(new MyTargetSelected(this.getObjectId(), player.getLevel()));
        if (this.isAutoAttackable(player)) {
          player.sendPacket(new DoorInfo(this, player));
        }

        player.sendPacket(new ValidateLocation(this));
      } else {
        player.sendPacket(new MyTargetSelected(this.getObjectId(), 0));
        if (this.isAutoAttackable(player)) {
          player.getAI().Attack(this, false, shift);
          return;
        }

        if (!this.isInActingRange(player)) {
          if (!player.getAI().isIntendingInteract(this)) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
          }

          return;
        }

        this.getAI().onEvtTwiceClick(player);
      }

    }
  }

  public int getActingRange() {
    return 150;
  }

  public DoorAI getAI() {
    if (this._ai == null) {
      synchronized(this) {
        if (this._ai == null) {
          this._ai = this.getTemplate().getNewAI(this);
        }
      }
    }

    return (DoorAI)this._ai;
  }

  public void broadcastStatusUpdate() {

    for (Player player : World.getAroundPlayers(this)) {
      if (player != null) {
        player.sendPacket(new DoorStatusUpdate(this, player));
      }
    }

  }

  public boolean openMe() {
    return this.openMe(null, true);
  }

  public boolean openMe(Player opener, boolean autoClose) {
    this._openLock.lock();

    label76: {
      boolean var3;
      try {
        if (this.setOpen(true)) {
          this.setGeoOpen(true);
          break label76;
        }

        var3 = false;
      } finally {
        this._openLock.unlock();
      }

      return var3;
    }

    this.broadcastStatusUpdate();
    if (autoClose && this.getTemplate().getCloseTime() > 0) {
      this.scheduleAutoAction(false, (long)this.getTemplate().getCloseTime() * 1000L);
    }

    this.getAI().onEvtOpen(opener);
    Iterator var7 = this.getListeners().getListeners().iterator();

    while(var7.hasNext()) {
      Listener<Creature> l = (Listener)var7.next();
      if (l instanceof OnOpenCloseListener) {
        ((OnOpenCloseListener)l).onOpen(this);
      }
    }

    return true;
  }

  public boolean closeMe() {
    return this.closeMe(null, true);
  }

  public boolean closeMe(Player closer, boolean autoOpen) {
    if (this.isDead()) {
      return false;
    } else {
      this._openLock.lock();

      label89: {
        boolean var3;
        try {
          if (this.setOpen(false)) {
            this.setGeoOpen(false);
            break label89;
          }

          var3 = false;
        } finally {
          this._openLock.unlock();
        }

        return var3;
      }

      this.broadcastStatusUpdate();
      if (autoOpen && this.getTemplate().getOpenTime() > 0) {
        long openDelay = (long)this.getTemplate().getOpenTime() * 1000L;
        if (this.getTemplate().getRandomTime() > 0) {
          openDelay += (long)Rnd.get(0, this.getTemplate().getRandomTime()) * 1000L;
        }

        this.scheduleAutoAction(true, openDelay);
      }

      this.getAI().onEvtClose(closer);

      for (Listener<Creature> creatureListener : this.getListeners().getListeners()) {
        if (creatureListener instanceof OnOpenCloseListener) {
          ((OnOpenCloseListener) creatureListener).onClose(this);
        }
      }

      return true;
    }
  }

  public String toString() {
    return "[Door " + this.getDoorId() + "]";
  }

  protected void onDeath(Creature killer) {
    this._openLock.lock();

    try {
      this.setGeoOpen(true);
    } finally {
      this._openLock.unlock();
    }

    SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
    if (siegeEvent != null && siegeEvent.isInProgress()) {
      Log.add(this.toString(), this.getDoorType() + " destroyed by " + killer + ", " + siegeEvent);
    }

    super.onDeath(killer);
  }

  protected void onRevive() {
    super.onRevive();
    this._openLock.lock();

    try {
      if (!this.isOpen()) {
        this.setGeoOpen(false);
      }
    } finally {
      this._openLock.unlock();
    }

  }

  protected void onSpawn() {
    super.onSpawn();
    this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
    this.closeMe(null, true);
  }

  protected void onDespawn() {
    if (this._autoActionTask != null) {
      this._autoActionTask.cancel(false);
      this._autoActionTask = null;
    }

    super.onDespawn();
  }

  public boolean isHPVisible() {
    return this.getTemplate().isHPVisible();
  }

  public int getMaxHp() {
    return super.getMaxHp() + this._upgradeHp;
  }

  public void setUpgradeHp(int hp) {
    this._upgradeHp = hp;
  }

  public int getUpgradeHp() {
    return this._upgradeHp;
  }

  public int getPDef(Creature target) {
    switch(SevenSigns.getInstance().getSealOwner(3)) {
      case 1:
        return (int)((double)super.getPDef(target) * 0.3D);
      case 2:
        return (int)((double)super.getPDef(target) * 1.2D);
      default:
        return super.getPDef(target);
    }
  }

  public int getMDef(Creature target, Skill skill) {
    switch(SevenSigns.getInstance().getSealOwner(3)) {
      case 1:
        return (int)((double)super.getMDef(target, skill) * 0.3D);
      case 2:
        return (int)((double)super.getMDef(target, skill) * 1.2D);
      default:
        return super.getMDef(target, skill);
    }
  }

  public boolean isInvul() {
    if (!this.getTemplate().isHPVisible()) {
      return true;
    } else {
      SiegeEvent<?, ?> siegeEvent = (SiegeEvent)this.getEvent(SiegeEvent.class);
      return (siegeEvent == null || !siegeEvent.isInProgress()) && super.isInvul();
    }
  }

  protected boolean setGeoOpen(boolean open) {
    if (this._geoOpen == open) {
      return false;
    } else {
      this._geoOpen = open;
      if (Config.ALLOW_GEODATA) {
        if (open) {
          GeoEngine.removeGeoCollision(this, this.getGeoIndex());
        } else {
          GeoEngine.applyGeoCollision(this, this.getGeoIndex());
        }
      }

      return true;
    }
  }

  public boolean isMovementDisabled() {
    return true;
  }

  public boolean isActionsDisabled() {
    return true;
  }

  public boolean isFearImmune() {
    return true;
  }

  public boolean isParalyzeImmune() {
    return true;
  }

  public boolean isLethalImmune() {
    return true;
  }

  public boolean isConcrete() {
    return true;
  }

  public boolean isHealBlocked() {
    return true;
  }

  public boolean isEffectImmune() {
    return true;
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    return Arrays.asList(new DoorInfo(this, forPlayer), new DoorStatusUpdate(this, forPlayer));
  }

  public boolean isDoor() {
    return true;
  }

  public Shape getShape() {
    return this.getTemplate().getPolygon();
  }

  public byte[][] getGeoAround() {
    return this._geoAround;
  }

  public void setGeoAround(byte[][] geo) {
    this._geoAround = geo;
  }

  public DoorTemplate getTemplate() {
    return (DoorTemplate)super.getTemplate();
  }

  public DoorType getDoorType() {
    return this.getTemplate().getDoorType();
  }

  public int getKey() {
    return this.getTemplate().getKey();
  }

  private class AutoOpenClose extends RunnableImpl {
    private boolean _open;

    public AutoOpenClose(boolean open) {
      this._open = open;
    }

    public void runImpl() throws Exception {
      if (this._open) {
        DoorInstance.this.openMe(null, true);
      } else {
        DoorInstance.this.closeMe(null, true);
      }

    }
  }
}
