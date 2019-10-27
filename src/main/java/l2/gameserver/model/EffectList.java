//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.gameserver.Config;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.skills.skillclasses.Transformation;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncTemplate;
import org.apache.commons.lang3.ArrayUtils;

public class EffectList {
  public static final int NONE_SLOT_TYPE = -1;
  public static final int BUFF_SLOT_TYPE = 0;
  public static final int MUSIC_SLOT_TYPE = 1;
  public static final int TRIGGER_SLOT_TYPE = 2;
  public static final int DEBUFF_SLOT_TYPE = 3;
  public static final int DEBUFF_LIMIT = 8;
  public static final int MUSIC_LIMIT = 12;
  public static final int TRIGGER_LIMIT = 12;
  private Creature _actor;
  private List<Effect> _effects;
  private Lock lock = new ReentrantLock();

  public EffectList(Creature owner) {
    this._actor = owner;
  }

  public int getEffectsCountForSkill(int skill_id) {
    if (this.isEmpty()) {
      return 0;
    } else {
      int count = 0;
      Iterator var3 = this._effects.iterator();

      while(var3.hasNext()) {
        Effect e = (Effect)var3.next();
        if (e.getSkill().getId() == skill_id) {
          ++count;
        }
      }

      return count;
    }
  }

  public Effect getEffectByType(EffectType et) {
    if (this.isEmpty()) {
      return null;
    } else {
      Iterator var2 = this._effects.iterator();

      Effect e;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        e = (Effect)var2.next();
      } while(e.getEffectType() != et);

      return e;
    }
  }

  public List<Effect> getEffectsBySkill(Skill skill) {
    return skill == null ? null : this.getEffectsBySkillId(skill.getId());
  }

  public int getActiveMusicCount(int skillId) {
    if (this.isEmpty()) {
      return 0;
    } else {
      int count = 0;
      Iterator var3 = this._effects.iterator();

      while(var3.hasNext()) {
        Effect e = (Effect)var3.next();
        if (Config.ALT_ADDITIONAL_DANCE_SONG_MANA_CONSUME && e.getSkill().isMusic() && e.getSkill().getId() != skillId && e.getTimeLeft() > Config.ALT_MUSIC_COST_GUARD_INTERVAL) {
          ++count;
        }
      }

      return count;
    }
  }

  public List<Effect> getEffectsBySkillId(int skillId) {
    if (this.isEmpty()) {
      return null;
    } else {
      List<Effect> list = new ArrayList(2);
      Iterator var3 = this._effects.iterator();

      while(var3.hasNext()) {
        Effect e = (Effect)var3.next();
        if (e.getSkill().getId() == skillId) {
          list.add(e);
        }
      }

      return list.isEmpty() ? null : list;
    }
  }

  public Effect getEffectByIndexAndType(int skillId, EffectType type) {
    if (this.isEmpty()) {
      return null;
    } else {
      Iterator var3 = this._effects.iterator();

      Effect e;
      do {
        if (!var3.hasNext()) {
          return null;
        }

        e = (Effect)var3.next();
      } while(e.getSkill().getId() != skillId || e.getEffectType() != type);

      return e;
    }
  }

  public Effect getEffectByStackType(String type) {
    if (this.isEmpty()) {
      return null;
    } else {
      Iterator var2 = this._effects.iterator();

      Effect e;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        e = (Effect)var2.next();
      } while(!e.getStackType().equals(type));

      return e;
    }
  }

  public boolean containEffectFromSkills(int... skillIds) {
    if (this.isEmpty()) {
      return false;
    } else {
      Iterator var3 = this._effects.iterator();

      int skillId;
      do {
        if (!var3.hasNext()) {
          return false;
        }

        Effect e = (Effect)var3.next();
        skillId = e.getSkill().getId();
      } while(!ArrayUtils.contains(skillIds, skillId));

      return true;
    }
  }

  public List<Effect> getAllEffects() {
    return (List)(this.isEmpty() ? Collections.emptyList() : new ArrayList(this._effects));
  }

  public boolean isEmpty() {
    return this._effects == null || this._effects.isEmpty();
  }

  public Effect[] getAllFirstEffects() {
    if (this.isEmpty()) {
      return Effect.EMPTY_L2EFFECT_ARRAY;
    } else {
      LinkedHashMap<Skill, Effect> map = new LinkedHashMap();
      Iterator var2 = this._effects.iterator();

      while(var2.hasNext()) {
        Effect e = (Effect)var2.next();
        if (!map.containsKey(e.getSkill())) {
          map.put(e.getSkill(), e);
        }
      }

      Collection<Effect> coll = map.values();
      return (Effect[])coll.toArray(new Effect[coll.size()]);
    }
  }

  private void checkSlotLimit(Effect newEffect) {
    if (this._effects != null) {
      int slotType = getSlotType(newEffect);
      if (slotType != -1) {
        int size = 0;
        TIntArrayList skillIds = new TIntArrayList();
        Iterator var5 = this._effects.iterator();

        while(var5.hasNext()) {
          Effect e = (Effect)var5.next();
          if (e.isInUse()) {
            if (e.getSkill().equals(newEffect.getSkill())) {
              return;
            }

            if (!skillIds.contains(e.getSkill().getId())) {
              int subType = getSlotType(e);
              if (subType == slotType) {
                ++size;
                skillIds.add(e.getSkill().getId());
              }
            }
          }
        }

        int limit = 0;
        switch(slotType) {
          case 0:
            limit = this._actor.getBuffLimit();
          case 1:
          default:
            break;
          case 2:
            limit = 12 + Config.ALT_TRIGGER_SLOT_ADDER;
            break;
          case 3:
            limit = Config.ALT_DEBUFF_LIMIT;
        }

        if (size >= limit) {
          int skillId = 0;
          Iterator var11 = this._effects.iterator();

          while(var11.hasNext()) {
            Effect e = (Effect)var11.next();
            if (e.isInUse() && getSlotType(e) == slotType) {
              skillId = e.getSkill().getId();
              break;
            }
          }

          if (skillId != 0) {
            this.stopEffect(skillId);
          }

        }
      }
    }
  }

  public static int getSlotType(Effect e) {
    if (!e.getSkill().isPassive() && !e.getSkill().isSlotNone() && !e.getSkill().isToggle() && !(e.getSkill() instanceof Transformation) && !e.isStackTypeMatch(new String[]{"HpRecoverCast"}) && e.getEffectType() != EffectType.Cubic) {
      if (e.getSkill().isOffensive()) {
        return 3;
      } else {
        return e.getSkill().isTrigger() ? 2 : 0;
      }
    } else {
      return -1;
    }
  }

  public static boolean checkStackType(EffectTemplate ef1, EffectTemplate ef2) {
    if (!ef1._stackType.equals("none") && ef1._stackType.equalsIgnoreCase(ef2._stackType)) {
      return true;
    } else if (!ef1._stackType.equals("none") && ef1._stackType.equalsIgnoreCase(ef2._stackType2)) {
      return true;
    } else if (!ef1._stackType2.equals("none") && ef1._stackType2.equalsIgnoreCase(ef2._stackType)) {
      return true;
    } else {
      return !ef1._stackType2.equals("none") && ef1._stackType2.equalsIgnoreCase(ef2._stackType2);
    }
  }

  public void addEffect(Effect effect) {
    double hp = this._actor.getCurrentHp();
    double mp = this._actor.getCurrentMp();
    double cp = this._actor.getCurrentCp();
    String stackType = effect.getStackType();
    boolean add = false;
    HashSet<Skill> removed = new HashSet<>();
    this.lock.lock();

    Iterator var11;
    try {
      if (this._effects == null) {
        this._effects = new CopyOnWriteArrayList();
      }

      Effect e;
      if (stackType.equals("none")) {
        var11 = this._effects.iterator();

        while(var11.hasNext()) {
          e = (Effect)var11.next();
          if (e.isInUse() && e.getSkill().getId() == effect.getSkill().getId() && e.getEffectType() == effect.getEffectType() && e.getStackType().equals("none")) {
            if (effect.getTimeLeft() <= e.getTimeLeft()) {
              return;
            }

            removed.add(e.getSkill());
            e.exit();
          }
        }
      } else {
        var11 = this._effects.iterator();

        while(var11.hasNext()) {
          e = (Effect)var11.next();
          if (e.isInUse() && checkStackType(e.getTemplate(), effect.getTemplate())) {
            if (e.getSkill().getId() == effect.getSkill().getId() && e.getEffectType() != effect.getEffectType()) {
              break;
            }

            if (e.getStackOrder() == -1) {
              return;
            }

            if (!e.maybeScheduleNext(effect)) {
              return;
            }

            removed.add(e.getSkill());
          }
        }
      }

      this.checkSlotLimit(effect);
      if (add = this._effects.add(effect)) {
        effect.setInUse(true);
      }
    } finally {
      this.lock.unlock();
    }

    if (add) {
      if (!removed.isEmpty()) {
        var11 = removed.iterator();

        while(var11.hasNext()) {
          Skill s = (Skill)var11.next();
          effect.getEffected().sendPacket((new SystemMessage(92)).addSkillName(s.getDisplayId(), s.getDisplayLevel()));
        }
      }

      effect.start();
      FuncTemplate[] var17 = effect.getTemplate().getAttachedFuncs();
      int var19 = var17.length;

      for(int var13 = 0; var13 < var19; ++var13) {
        FuncTemplate ft = var17[var13];
        if (ft._stat == Stats.MAX_HP) {
          this._actor.setCurrentHp(hp, false);
        } else if (ft._stat == Stats.MAX_MP) {
          this._actor.setCurrentMp(mp);
        } else if (ft._stat == Stats.MAX_CP) {
          this._actor.setCurrentCp(cp);
        }
      }

      this._actor.updateStats();
      this._actor.updateEffectIcons();
    }
  }

  public void removeEffect(Effect effect) {
    if (effect != null) {
      boolean remove = false;
      this.lock.lock();

      label59: {
        try {
          if (this._effects != null) {
            if (!(remove = this._effects.remove(effect))) {
              return;
            }
            break label59;
          }
        } finally {
          this.lock.unlock();
        }

        return;
      }

      if (remove) {
        this._actor.updateStats();
        this._actor.updateEffectIcons();
      }
    }
  }

  public void stopAllEffects() {
    if (!this.isEmpty()) {
      this.lock.lock();

      try {
        Iterator var1 = this._effects.iterator();

        while(var1.hasNext()) {
          Effect e = (Effect)var1.next();
          e.exit();
        }
      } finally {
        this.lock.unlock();
      }

      this._actor.updateStats();
      this._actor.updateEffectIcons();
    }
  }

  public void stopEffect(int skillId) {
    if (!this.isEmpty()) {
      Iterator var2 = this._effects.iterator();

      while(var2.hasNext()) {
        Effect e = (Effect)var2.next();
        if (e.getSkill().getId() == skillId) {
          e.exit();
        }
      }

    }
  }

  public void stopEffect(Skill skill) {
    if (skill != null) {
      this.stopEffect(skill.getId());
    }

  }

  public void stopEffectByDisplayId(int skillId) {
    if (!this.isEmpty()) {
      Iterator var2 = this._effects.iterator();

      while(var2.hasNext()) {
        Effect e = (Effect)var2.next();
        if (e.getSkill().getDisplayId() == skillId) {
          e.exit();
        }
      }

    }
  }

  public void stopEffects(EffectType type) {
    if (!this.isEmpty()) {
      Iterator var2 = this._effects.iterator();

      while(var2.hasNext()) {
        Effect e = (Effect)var2.next();
        if (e.getEffectType() == type) {
          e.exit();
        }
      }

    }
  }

  public void stopAllSkillEffects(EffectType type) {
    if (!this.isEmpty()) {
      TIntHashSet skillIds = new TIntHashSet();
      Iterator var3 = this._effects.iterator();

      while(var3.hasNext()) {
        Effect e = (Effect)var3.next();
        if (e.getEffectType() == type) {
          skillIds.add(e.getSkill().getId());
        }
      }

      int[] var7 = skillIds.toArray();
      int var8 = var7.length;

      for(int var5 = 0; var5 < var8; ++var5) {
        int skillId = var7[var5];
        this.stopEffect(skillId);
      }

    }
  }
}
