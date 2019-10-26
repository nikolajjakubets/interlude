//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.*;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SymbolInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.stats.Env;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class EffectSymbol extends Effect {
  private static final Logger _log = LoggerFactory.getLogger(EffectSymbol.class);
  private NpcInstance _symbol = null;

  public EffectSymbol(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    if (this.getSkill().getTargetType() != SkillTargetType.TARGET_SELF) {
      _log.error("Symbol skill with target != self, id = " + this.getSkill().getId());
      return false;
    } else {
      Skill skill = this.getSkill().getFirstAddedSkill();
      if (skill == null) {
        _log.error("Not implemented symbol skill, id = " + this.getSkill().getId());
        return false;
      } else {
        return super.checkCondition();
      }
    }
  }

  public void onStart() {
    super.onStart();
    Skill skill = this.getSkill().getFirstAddedSkill();
    skill.setMagicType(this.getSkill().getMagicType());
    Location loc = this._effected.getLoc();
    if (this._effected.isPlayer() && ((Player)this._effected).getGroundSkillLoc() != null) {
      loc = ((Player)this._effected).getGroundSkillLoc();
      ((Player)this._effected).setGroundSkillLoc((Location)null);
    }

    NpcTemplate template = NpcHolder.getInstance().getTemplate(this._skill.getSymbolId());
    if (this.getTemplate()._count <= 1) {
      this._symbol = new SymbolInstance(IdFactory.getInstance().getNextId(), template, this._effected, skill);
    } else {
      this._symbol = new NpcInstance(IdFactory.getInstance().getNextId(), template);
    }

    this._symbol.setLevel(this._effected.getLevel());
    this._symbol.setReflection(this._effected.getReflection());
    this._symbol.spawnMe(loc);
  }

  public void onExit() {
    super.onExit();
    if (this._symbol != null && this._symbol.isVisible()) {
      this._symbol.deleteMe();
    }

    this._symbol = null;
  }

  public boolean onActionTime() {
    if (this.getTemplate()._count <= 1) {
      return false;
    } else {
      Creature effector = this.getEffector();
      Skill skill = this.getSkill().getFirstAddedSkill();
      NpcInstance symbol = this._symbol;
      double mpConsume = this.getSkill().getMpConsume();
      if (effector != null && skill != null && symbol != null) {
        if (mpConsume > effector.getCurrentMp()) {
          effector.sendPacket(Msg.NOT_ENOUGH_MP);
          return false;
        } else {
          effector.reduceCurrentMp(mpConsume, effector);
          Iterator var6 = World.getAroundCharacters(symbol, this.getSkill().getSkillRadius(), 200).iterator();

          while(true) {
            Creature cha;
            do {
              do {
                do {
                  do {
                    if (!var6.hasNext()) {
                      return true;
                    }

                    cha = (Creature)var6.next();
                  } while(cha.isDoor());
                } while(cha.getEffectList().getEffectsBySkill(skill) != null);
              } while(skill.checkTarget(effector, cha, cha, false, false) != null);
            } while(skill.isOffensive() && !GeoEngine.canSeeTarget(symbol, cha, false));

            List<Creature> targets = new ArrayList(1);
            targets.add(cha);
            effector.callSkill(skill, targets, true);
            effector.broadcastPacket(new L2GameServerPacket[]{new MagicSkillLaunched(symbol, this.getSkill().getDisplayId(), this.getSkill().getDisplayLevel(), cha)});
          }
        }
      } else {
        return false;
      }
    }
  }
}
