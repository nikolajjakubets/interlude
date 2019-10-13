//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.Iterator;
import java.util.concurrent.Future;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public final class TamedBeastInstance extends FeedableBeastInstance {
  private static final int MAX_DISTANCE_FROM_OWNER = 2000;
  private static final int MAX_DISTANCE_FOR_BUFF = 300;
  private static final int MAX_DURATION = 1200000;
  private static final int DURATION_CHECK_INTERVAL = 60000;
  private static final int DURATION_INCREASE_INTERVAL = 20000;
  private static final int BUFF_INTERVAL = 30000;
  private HardReference<Player> _playerRef = HardReferences.emptyRef();
  private int _foodSkillId;
  private int _remainingTime = 1200000;
  private Future<?> _durationCheckTask = null;
  private Future<?> _buffTask = null;
  private Skill[] _skills = new Skill[0];
  private static final int Recharge = 5200;
  private static final int GreaterHeal = 5195;
  private static final Skill[][] TAMED_SKILLS = new Skill[6][];

  public TamedBeastInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this._hasRandomWalk = false;
    this._hasChatWindow = false;
    this._hasRandomAnimation = false;
  }

  public boolean isAutoAttackable(Creature attacker) {
    return false;
  }

  private void onReceiveFood() {
    this._remainingTime += 20000;
    if (this._remainingTime > 1200000) {
      this._remainingTime = 1200000;
    }

  }

  public int getRemainingTime() {
    return this._remainingTime;
  }

  public void setRemainingTime(int duration) {
    this._remainingTime = duration;
  }

  public int getFoodType() {
    return this._foodSkillId;
  }

  public void setTameType(Player activeChar) {
    switch(this.getNpcId()) {
      case 16013:
      case 16014:
      case 16015:
      case 16016:
      case 16017:
      case 16018:
      default:
        Skill[] skills = TAMED_SKILLS[Rnd.get(TAMED_SKILLS.length)];
        this._skills = (Skill[])skills.clone();
    }
  }

  public void buffOwner() {
    if (!this.isInRange(this.getPlayer(), 300L)) {
      this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this.getPlayer());
    } else {
      int delay = 0;
      Skill[] var2 = this._skills;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Skill skill = var2[var4];
        if (this.getPlayer().getEffectList().getEffectsCountForSkill(skill.getId()) <= 0) {
          ThreadPoolManager.getInstance().schedule(new TamedBeastInstance.Buff(this, this.getPlayer(), skill), (long)delay);
          delay = delay + skill.getHitTime() + 500;
        }
      }

    }
  }

  public void setFoodType(int foodItemId) {
    if (foodItemId > 0) {
      this._foodSkillId = foodItemId;
      if (this._durationCheckTask != null) {
        this._durationCheckTask.cancel(false);
      }

      this._durationCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TamedBeastInstance.CheckDuration(this), 60000L, 60000L);
    }

  }

  protected void onDeath(Creature killer) {
    super.onDeath(killer);
    if (this._durationCheckTask != null) {
      this._durationCheckTask.cancel(false);
      this._durationCheckTask = null;
    }

    if (this._buffTask != null) {
      this._buffTask.cancel(false);
      this._buffTask = null;
    }

    Player owner = this.getPlayer();
    if (owner != null && owner.getTrainedBeast() == this) {
      owner.setTrainedBeast((TamedBeastInstance)null);
    }

    this._foodSkillId = 0;
    this._remainingTime = 0;
  }

  public Player getPlayer() {
    return (Player)this._playerRef.get();
  }

  public void setOwner(Player owner) {
    this._playerRef = owner == null ? HardReferences.emptyRef() : owner.getRef();
    if (owner != null) {
      if (owner.getTrainedBeast() != null) {
        owner.getTrainedBeast().doDespawn();
      }

      owner.setTrainedBeast(this);
      Iterator var2 = World.getAroundPlayers(this).iterator();

      while(var2.hasNext()) {
        Player player = (Player)var2.next();
        player.sendPacket(new NpcInfo(this, player));
      }

      this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner);
      this._buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
        public void runImpl() throws Exception {
          TamedBeastInstance.this.buffOwner();
        }
      }, 30000L, 30000L);
    } else {
      this.doDespawn();
    }

  }

  public void despawnWithDelay(int delay) {
    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        TamedBeastInstance.this.doDespawn();
      }
    }, (long)delay);
  }

  public void doDespawn() {
    this.stopMove();
    if (this._durationCheckTask != null) {
      this._durationCheckTask.cancel(false);
      this._durationCheckTask = null;
    }

    if (this._buffTask != null) {
      this._buffTask.cancel(false);
      this._buffTask = null;
    }

    Player owner = this.getPlayer();
    if (owner != null && owner.getTrainedBeast() == this) {
      owner.setTrainedBeast((TamedBeastInstance)null);
    }

    this.setTarget((GameObject)null);
    this._foodSkillId = 0;
    this._remainingTime = 0;
    this.onDecay();
  }

  static {
    TAMED_SKILLS[0] = new Skill[]{SkillTable.getInstance().getInfo(5186, 1), SkillTable.getInstance().getInfo(5188, 1), SkillTable.getInstance().getInfo(5189, 1), SkillTable.getInstance().getInfo(5187, 1), SkillTable.getInstance().getInfo(5191, 1), SkillTable.getInstance().getInfo(5195, 1)};
    TAMED_SKILLS[1] = new Skill[]{SkillTable.getInstance().getInfo(5192, 1), SkillTable.getInstance().getInfo(5193, 1), SkillTable.getInstance().getInfo(5201, 1), SkillTable.getInstance().getInfo(5194, 1), SkillTable.getInstance().getInfo(5190, 1), SkillTable.getInstance().getInfo(5200, 1)};
    TAMED_SKILLS[2] = new Skill[]{SkillTable.getInstance().getInfo(5186, 1), SkillTable.getInstance().getInfo(5187, 1), SkillTable.getInstance().getInfo(5188, 1), SkillTable.getInstance().getInfo(5189, 1), SkillTable.getInstance().getInfo(5191, 1), SkillTable.getInstance().getInfo(5195, 1)};
    TAMED_SKILLS[3] = new Skill[]{SkillTable.getInstance().getInfo(5192, 1), SkillTable.getInstance().getInfo(5193, 1), SkillTable.getInstance().getInfo(5201, 1), SkillTable.getInstance().getInfo(5194, 1), SkillTable.getInstance().getInfo(5190, 1), SkillTable.getInstance().getInfo(5200, 1)};
    TAMED_SKILLS[4] = new Skill[]{SkillTable.getInstance().getInfo(5186, 1), SkillTable.getInstance().getInfo(5187, 1), SkillTable.getInstance().getInfo(5188, 1), SkillTable.getInstance().getInfo(5189, 1), SkillTable.getInstance().getInfo(5191, 1), SkillTable.getInstance().getInfo(5195, 1)};
    TAMED_SKILLS[5] = new Skill[]{SkillTable.getInstance().getInfo(5192, 1), SkillTable.getInstance().getInfo(5193, 1), SkillTable.getInstance().getInfo(5201, 1), SkillTable.getInstance().getInfo(5194, 1), SkillTable.getInstance().getInfo(5190, 1), SkillTable.getInstance().getInfo(5200, 1)};
  }

  private static class CheckDuration extends RunnableImpl {
    private TamedBeastInstance _tamedBeast;

    CheckDuration(TamedBeastInstance tamedBeast) {
      this._tamedBeast = tamedBeast;
    }

    public void runImpl() throws Exception {
      Player owner = this._tamedBeast.getPlayer();
      if (owner != null && owner.isOnline()) {
        if (this._tamedBeast.getDistance(owner) > 2000.0D) {
          this._tamedBeast.doDespawn();
        } else {
          int foodTypeSkillId = this._tamedBeast.getFoodType();
          this._tamedBeast.setRemainingTime(this._tamedBeast.getRemainingTime() - '\uea60');
          ItemInstance item = null;
          int foodItemId = this._tamedBeast.getItemIdBySkillId(foodTypeSkillId);
          if (foodItemId > 0) {
            item = owner.getInventory().getItemByItemId(foodItemId);
          }

          if (item != null && item.getCount() >= 1L) {
            this._tamedBeast.onReceiveFood();
            owner.getInventory().destroyItem(item, 1L);
          } else if (this._tamedBeast.getRemainingTime() < 900000) {
            this._tamedBeast.setRemainingTime(-1);
          }

          if (this._tamedBeast.getRemainingTime() <= 0) {
            this._tamedBeast.doDespawn();
          }

        }
      } else {
        this._tamedBeast.doDespawn();
      }
    }
  }

  public static class Buff extends RunnableImpl {
    private NpcInstance _actor;
    private Player _owner;
    private Skill _skill;

    public Buff(NpcInstance actor, Player owner, Skill skill) {
      this._actor = actor;
      this._owner = owner;
      this._skill = skill;
    }

    public void runImpl() throws Exception {
      if (this._actor != null) {
        this._actor.doCast(this._skill, this._owner, true);
      }

    }
  }
}
