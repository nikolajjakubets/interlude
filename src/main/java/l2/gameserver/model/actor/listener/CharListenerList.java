//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.listener;

import java.util.Iterator;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.listener.actor.OnAttackHitListener;
import l2.gameserver.listener.actor.OnAttackListener;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnCurrentMpReduceListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.listener.actor.OnMagicHitListener;
import l2.gameserver.listener.actor.OnMagicUseListener;
import l2.gameserver.listener.actor.ai.OnAiEventListener;
import l2.gameserver.listener.actor.ai.OnAiIntentionListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;

public class CharListenerList extends ListenerList<Creature> {
  static final ListenerList<Creature> global = new ListenerList();
  protected final Creature actor;

  public CharListenerList(Creature actor) {
    this.actor = actor;
  }

  public Creature getActor() {
    return this.actor;
  }

  public static final boolean addGlobal(Listener<Creature> listener) {
    return global.add(listener);
  }

  public static final boolean removeGlobal(Listener<Creature> listener) {
    return global.remove(listener);
  }

  public void onAiIntention(CtrlIntention intention, Object arg0, Object arg1) {
    if (!this.getListeners().isEmpty()) {
      Iterator var4 = this.getListeners().iterator();

      while(var4.hasNext()) {
        Listener<Creature> listener = (Listener)var4.next();
        if (OnAiIntentionListener.class.isInstance(listener)) {
          ((OnAiIntentionListener)listener).onAiIntention(this.getActor(), intention, arg0, arg1);
        }
      }
    }

  }

  public void onAiEvent(CtrlEvent evt, Object[] args) {
    if (!this.getListeners().isEmpty()) {
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
        Listener<Creature> listener = (Listener)var3.next();
        if (OnAiEventListener.class.isInstance(listener)) {
          ((OnAiEventListener)listener).onAiEvent(this.getActor(), evt, args);
        }
      }
    }

  }

  public void onAttack(Creature target) {
    Iterator var2;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var2 = global.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnAttackListener.class.isInstance(listener)) {
          ((OnAttackListener)listener).onAttack(this.getActor(), target);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnAttackListener.class.isInstance(listener)) {
          ((OnAttackListener)listener).onAttack(this.getActor(), target);
        }
      }
    }

  }

  public void onAttackHit(Creature attacker) {
    Iterator var2;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var2 = global.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnAttackHitListener.class.isInstance(listener)) {
          ((OnAttackHitListener)listener).onAttackHit(this.getActor(), attacker);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnAttackHitListener.class.isInstance(listener)) {
          ((OnAttackHitListener)listener).onAttackHit(this.getActor(), attacker);
        }
      }
    }

  }

  public void onMagicUse(Skill skill, Creature target, boolean alt) {
    Iterator var4;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var4 = global.getListeners().iterator();

      while(var4.hasNext()) {
        listener = (Listener)var4.next();
        if (OnMagicUseListener.class.isInstance(listener)) {
          ((OnMagicUseListener)listener).onMagicUse(this.getActor(), skill, target, alt);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var4 = this.getListeners().iterator();

      while(var4.hasNext()) {
        listener = (Listener)var4.next();
        if (OnMagicUseListener.class.isInstance(listener)) {
          ((OnMagicUseListener)listener).onMagicUse(this.getActor(), skill, target, alt);
        }
      }
    }

  }

  public void onMagicHit(Skill skill, Creature caster) {
    Iterator var3;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var3 = global.getListeners().iterator();

      while(var3.hasNext()) {
        listener = (Listener)var3.next();
        if (OnMagicHitListener.class.isInstance(listener)) {
          ((OnMagicHitListener)listener).onMagicHit(this.getActor(), skill, caster);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
        listener = (Listener)var3.next();
        if (OnMagicHitListener.class.isInstance(listener)) {
          ((OnMagicHitListener)listener).onMagicHit(this.getActor(), skill, caster);
        }
      }
    }

  }

  public void onDeath(Creature killer) {
    Iterator var2;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var2 = global.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnDeathListener.class.isInstance(listener)) {
          ((OnDeathListener)listener).onDeath(this.getActor(), killer);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnDeathListener.class.isInstance(listener)) {
          ((OnDeathListener)listener).onDeath(this.getActor(), killer);
        }
      }
    }

  }

  public void onKill(Creature victim) {
    Iterator var2;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var2 = global.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnKillListener.class.isInstance(listener) && !((OnKillListener)listener).ignorePetOrSummon()) {
          ((OnKillListener)listener).onKill(this.getActor(), victim);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnKillListener.class.isInstance(listener) && !((OnKillListener)listener).ignorePetOrSummon()) {
          ((OnKillListener)listener).onKill(this.getActor(), victim);
        }
      }
    }

  }

  public void onKillIgnorePetOrSummon(Creature victim) {
    Iterator var2;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var2 = global.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnKillListener.class.isInstance(listener) && ((OnKillListener)listener).ignorePetOrSummon()) {
          ((OnKillListener)listener).onKill(this.getActor(), victim);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
        listener = (Listener)var2.next();
        if (OnKillListener.class.isInstance(listener) && ((OnKillListener)listener).ignorePetOrSummon()) {
          ((OnKillListener)listener).onKill(this.getActor(), victim);
        }
      }
    }

  }

  public void onCurrentHpDamage(double damage, Creature attacker, Skill skill) {
    Iterator var5;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var5 = global.getListeners().iterator();

      while(var5.hasNext()) {
        listener = (Listener)var5.next();
        if (OnCurrentHpDamageListener.class.isInstance(listener)) {
          ((OnCurrentHpDamageListener)listener).onCurrentHpDamage(this.getActor(), damage, attacker, skill);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var5 = this.getListeners().iterator();

      while(var5.hasNext()) {
        listener = (Listener)var5.next();
        if (OnCurrentHpDamageListener.class.isInstance(listener)) {
          ((OnCurrentHpDamageListener)listener).onCurrentHpDamage(this.getActor(), damage, attacker, skill);
        }
      }
    }

  }

  public void onCurrentMpReduce(double consumed, Creature attacker) {
    Iterator var4;
    Listener listener;
    if (!global.getListeners().isEmpty()) {
      var4 = global.getListeners().iterator();

      while(var4.hasNext()) {
        listener = (Listener)var4.next();
        if (OnCurrentMpReduceListener.class.isInstance(listener)) {
          ((OnCurrentMpReduceListener)listener).onCurrentMpReduce(this.getActor(), consumed, attacker);
        }
      }
    }

    if (!this.getListeners().isEmpty()) {
      var4 = this.getListeners().iterator();

      while(var4.hasNext()) {
        listener = (Listener)var4.next();
        if (OnCurrentMpReduceListener.class.isInstance(listener)) {
          ((OnCurrentMpReduceListener)listener).onCurrentMpReduce(this.getActor(), consumed, attacker);
        }
      }
    }

  }
}
