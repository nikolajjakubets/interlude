//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.listener;

import java.util.Iterator;
import l2.commons.listener.Listener;
import l2.gameserver.listener.actor.player.OnGainExpSpListener;
import l2.gameserver.listener.actor.player.OnOlyCompetitionListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.listener.actor.player.OnQuestStateChangeListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.quest.QuestState;

public class PlayerListenerList extends CharListenerList {
  public PlayerListenerList(Player actor) {
    super(actor);
  }

  public Player getActor() {
    return (Player)this.actor;
  }

  public void onEnter() {
    Iterator var1;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var1 = global.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerEnterListener.class.isInstance(listener)) {
          ((OnPlayerEnterListener)listener).onPlayerEnter(this.getActor());
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerEnterListener.class.isInstance(listener)) {
          ((OnPlayerEnterListener)listener).onPlayerEnter(this.getActor());
        }
      }
    }

  }

  public void onExit() {
    Iterator var1;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var1 = global.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerExitListener.class.isInstance(listener)) {
          ((OnPlayerExitListener)listener).onPlayerExit(this.getActor());
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerExitListener.class.isInstance(listener)) {
          ((OnPlayerExitListener)listener).onPlayerExit(this.getActor());
        }
      }
    }

  }

  public void onTeleport(int x, int y, int z, Reflection reflection) {
    Iterator var5;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var5 = global.getListeners().iterator();

      while(var5.hasNext()) {
        listener = (Listener)var5.next();
        if (OnTeleportListener.class.isInstance(listener)) {
          ((OnTeleportListener)listener).onTeleport(this.getActor(), x, y, z, reflection);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var5 = this.getListeners().iterator();

      while(var5.hasNext()) {
        listener = (Listener)var5.next();
        if (OnTeleportListener.class.isInstance(listener)) {
          ((OnTeleportListener)listener).onTeleport(this.getActor(), x, y, z, reflection);
        }
      }
    }

  }

  public void onQuestStateChange(QuestState questState) {
    Iterator var2;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var2 = global.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnQuestStateChangeListener.class.isInstance(listener)) {
          ((OnQuestStateChangeListener)listener).onQuestStateChange(this.getActor(), questState);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnQuestStateChangeListener.class.isInstance(listener)) {
          ((OnQuestStateChangeListener)listener).onQuestStateChange(this.getActor(), questState);
        }
      }
    }

  }

  public void onOlyCompetitionCompleted(Competition competition, boolean isWin) {
    Iterator var3;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var3 = global.getListeners().iterator();

      while(var3.hasNext()) {
        listener = (Listener)var3.next();
        if (OnOlyCompetitionListener.class.isInstance(listener)) {
          ((OnOlyCompetitionListener)listener).onOlyCompetitionCompleted(this.getActor(), competition, isWin);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
        listener = (Listener)var3.next();
        if (OnOlyCompetitionListener.class.isInstance(listener)) {
          ((OnOlyCompetitionListener)listener).onOlyCompetitionCompleted(this.getActor(), competition, isWin);
        }
      }
    }

  }

  public void onGainExpSp(long exp, long sp) {
    Iterator var5;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var5 = global.getListeners().iterator();

      while(var5.hasNext()) {
        listener = (Listener)var5.next();
        if (OnGainExpSpListener.class.isInstance(listener)) {
          ((OnGainExpSpListener)listener).onGainExpSp(this.getActor(), exp, sp);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var5 = this.getListeners().iterator();

      while(var5.hasNext()) {
        listener = (Listener)var5.next();
        if (OnGainExpSpListener.class.isInstance(listener)) {
          ((OnGainExpSpListener)listener).onGainExpSp(this.getActor(), exp, sp);
        }
      }
    }

  }

  public void onPvpPkKill(Player victim, boolean isPk) {
    Iterator var3;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var3 = global.getListeners().iterator();

      while(var3.hasNext()) {
        listener = (Listener)var3.next();
        if (OnPvpPkKillListener.class.isInstance(listener)) {
          ((OnPvpPkKillListener)listener).onPvpPkKill(this.getActor(), victim, isPk);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
        listener = (Listener)var3.next();
        if (OnPvpPkKillListener.class.isInstance(listener)) {
          ((OnPvpPkKillListener)listener).onPvpPkKill(this.getActor(), victim, isPk);
        }
      }
    }

  }

  public void onPartyInvite() {
    Iterator var1;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var1 = global.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerPartyInviteListener.class.isInstance(listener)) {
          ((OnPlayerPartyInviteListener)listener).onPartyInvite(this.getActor());
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerPartyInviteListener.class.isInstance(listener)) {
          ((OnPlayerPartyInviteListener)listener).onPartyInvite(this.getActor());
        }
      }
    }

  }

  public void onPartyLeave() {
    Iterator var1;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var1 = global.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerPartyLeaveListener.class.isInstance(listener)) {
          ((OnPlayerPartyLeaveListener)listener).onPartyLeave(this.getActor());
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        listener = (Listener)var1.next();
        if (OnPlayerPartyLeaveListener.class.isInstance(listener)) {
          ((OnPlayerPartyLeaveListener)listener).onPartyLeave(this.getActor());
        }
      }
    }

  }
}
