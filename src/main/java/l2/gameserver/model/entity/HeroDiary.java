//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity;

import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.HtmlUtils;

public class HeroDiary {
  private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:** dd.MM.yyyy");
  public static final int ACTION_RAID_KILLED = 1;
  public static final int ACTION_HERO_GAINED = 2;
  public static final int ACTION_CASTLE_TAKEN = 3;
  private int _id;
  private long _time;
  private int _param;

  public HeroDiary(int id, long time, int param) {
    this._id = id;
    this._time = time;
    this._param = param;
  }

  public Entry<String, String> toString(Player player) {
    CustomMessage message = null;
    switch(this._id) {
      case 1:
        message = (new CustomMessage("l2p.gameserver.model.entity.Hero.RaidBossKilled", player, new Object[0])).addString(HtmlUtils.htmlNpcName(this._param));
        break;
      case 2:
        message = new CustomMessage("l2p.gameserver.model.entity.Hero.HeroGained", player, new Object[0]);
        break;
      case 3:
        message = (new CustomMessage("l2p.gameserver.model.entity.Hero.CastleTaken", player, new Object[0])).addString(HtmlUtils.htmlResidenceName(this._param));
        break;
      default:
        return null;
    }

    return new SimpleEntry(SIMPLE_FORMAT.format(this._time), message.toString());
  }
}
