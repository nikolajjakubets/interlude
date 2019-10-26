//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.EffectList;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public final class EffectTemplate extends StatTemplate {
  private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);
  public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];
  public static final String NO_STACK = "none";
  public static final String HP_RECOVER_CAST = "HpRecoverCast";
  public Condition _attachCond;
  public final double _value;
  public final int _count;
  public final long _period;
  public AbnormalEffect _abnormalEffect;
  public AbnormalEffect _abnormalEffect2;
  public AbnormalEffect _abnormalEffect3;
  public final EffectType _effectType;
  public final String _stackType;
  public final String _stackType2;
  public final int _stackOrder;
  public final int _displayId;
  public final int _displayLevel;
  public final boolean _applyOnCaster;
  public final boolean _applyOnSummon;
  public final boolean _cancelOnAction;
  public final boolean _isReflectable;
  private final Boolean _isSaveable;
  private final Boolean _isCancelable;
  private final Boolean _isOffensive;
  private final StatsSet _paramSet;
  private final int _chance;

  public EffectTemplate(StatsSet set) {
    this._value = set.getDouble("value");
    this._count = set.getInteger("count", 1) < 0 ? 2147483647 : set.getInteger("count", 1);
    this._period = (long)Math.min(2147483647, 1000 * (set.getInteger("time", 1) < 0 ? 2147483647 : set.getInteger("time", 1)));
    this._abnormalEffect = (AbnormalEffect)set.getEnum("abnormal", AbnormalEffect.class);
    this._abnormalEffect2 = (AbnormalEffect)set.getEnum("abnormal2", AbnormalEffect.class);
    this._abnormalEffect3 = (AbnormalEffect)set.getEnum("abnormal3", AbnormalEffect.class);
    this._stackType = set.getString("stackType", "none");
    this._stackType2 = set.getString("stackType2", "none");
    this._stackOrder = set.getInteger("stackOrder", this._stackType.equals("none") && this._stackType2.equals("none") ? 1 : 0);
    this._applyOnCaster = set.getBool("applyOnCaster", false);
    this._applyOnSummon = set.getBool("applyOnSummon", false);
    this._cancelOnAction = set.getBool("cancelOnAction", false);
    this._isReflectable = set.getBool("isReflectable", true);
    this._isSaveable = set.isSet("isSaveable") ? set.getBool("isSaveable") : null;
    this._isCancelable = set.isSet("isCancelable") ? set.getBool("isCancelable") : null;
    this._isOffensive = set.isSet("isOffensive") ? set.getBool("isOffensive") : null;
    this._displayId = set.getInteger("displayId", 0);
    this._displayLevel = set.getInteger("displayLevel", 0);
    this._effectType = (EffectType)set.getEnum("name", EffectType.class);
    this._chance = set.getInteger("chance", 2147483647);
    this._paramSet = set;
  }

  public Effect getEffect(Env env) {
    if (this._attachCond != null && !this._attachCond.test(env)) {
      return null;
    } else {
      try {
        return this._effectType.makeEffect(env, this);
      } catch (Exception var3) {
        _log.error("", var3);
        return null;
      }
    }
  }

  public void attachCond(Condition c) {
    this._attachCond = c;
  }

  public int getCount() {
    return this._count;
  }

  public long getPeriod() {
    return this._period;
  }

  public EffectType getEffectType() {
    return this._effectType;
  }

  public Effect getSameByStackType(List<Effect> list) {
    Iterator var2 = list.iterator();

    Effect ef;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      ef = (Effect)var2.next();
    } while(ef == null || !EffectList.checkStackType(ef.getTemplate(), this));

    return ef;
  }

  public Effect getSameByStackType(EffectList list) {
    return this.getSameByStackType(list.getAllEffects());
  }

  public Effect getSameByStackType(Creature actor) {
    return this.getSameByStackType(actor.getEffectList().getAllEffects());
  }

  public StatsSet getParam() {
    return this._paramSet;
  }

  public int chance(int val) {
    return this._chance == 2147483647 ? val : this._chance;
  }

  public boolean isSaveable(boolean def) {
    return this._isSaveable != null ? this._isSaveable : def;
  }

  public boolean isCancelable(boolean def) {
    return this._isCancelable != null ? this._isCancelable : def;
  }

  public boolean isOffensive(boolean def) {
    return this._isOffensive != null ? this._isOffensive : def;
  }
}
