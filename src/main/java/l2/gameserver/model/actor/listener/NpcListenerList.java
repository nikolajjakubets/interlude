//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.listener;

import java.util.Iterator;
import l2.commons.listener.Listener;
import l2.gameserver.listener.actor.npc.OnDecayListener;
import l2.gameserver.listener.actor.npc.OnSpawnListener;
import l2.gameserver.model.instances.NpcInstance;

public class NpcListenerList extends CharListenerList {
  public NpcListenerList(NpcInstance actor) {
    super(actor);
  }

  public NpcInstance getActor() {
    return (NpcInstance)this.actor;
  }

  public void onSpawn() {
    Iterator var1;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var1 = global.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnSpawnListener.class.isInstance(listener)) {
          ((OnSpawnListener)listener).onSpawn(this.getActor());
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnSpawnListener.class.isInstance(listener)) {
          ((OnSpawnListener)listener).onSpawn(this.getActor());
        }
      }
    }

  }

  public void onDecay() {
    Iterator var1;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var1 = global.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnDecayListener.class.isInstance(listener)) {
          ((OnDecayListener)listener).onDecay(this.getActor());
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnDecayListener.class.isInstance(listener)) {
          ((OnDecayListener)listener).onDecay(this.getActor());
        }
      }
    }

  }
}
