//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.skills.TimeStamp;

public abstract class ShortCutPacket extends L2GameServerPacket {
  public ShortCutPacket() {
  }

  public static ShortCutPacket.ShortcutInfo convert(Player player, ShortCut shortCut) {
    ShortCutPacket.ShortcutInfo shortcutInfo = null;
    int page = shortCut.getSlot() + shortCut.getPage() * 12;
    switch(shortCut.getType()) {
      case 1:
        int reuseGroup = -1;
        int currentReuse = 0;
        int reuse = 0;
        int variation1 = 0;
        int variation2 = 0;
        ItemInstance item = player.getInventory().getItemByObjectId(shortCut.getId());
        if (item != null) {
          variation1 = item.getVariationStat1();
          variation2 = item.getVariationStat2();
          reuseGroup = item.getTemplate().getDisplayReuseGroup();
          if (item.getTemplate().getReuseDelay() > 0) {
            TimeStamp timeStamp = player.getSharedGroupReuse(item.getTemplate().getReuseGroup());
            if (timeStamp != null) {
              currentReuse = (int)(timeStamp.getReuseCurrent() / 1000L);
              reuse = (int)(timeStamp.getReuseBasic() / 1000L);
            }
          }
        }

        shortcutInfo = new ShortCutPacket.ItemShortcutInfo(shortCut.getType(), page, shortCut.getId(), reuseGroup, currentReuse, reuse, variation1, variation2, shortCut.getCharacterType());
        break;
      case 2:
        shortcutInfo = new ShortCutPacket.SkillShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getLevel(), shortCut.getCharacterType());
        break;
      default:
        shortcutInfo = new ShortCutPacket.ShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getCharacterType());
    }

    return (ShortCutPacket.ShortcutInfo)shortcutInfo;
  }

  protected static class ShortcutInfo {
    protected final int _type;
    protected final int _page;
    protected final int _id;
    protected final int _characterType;

    public ShortcutInfo(int type, int page, int id, int characterType) {
      this._type = type;
      this._page = page;
      this._id = id;
      this._characterType = characterType;
    }

    protected void write(ShortCutPacket p) {
      p.writeD(this._type);
      p.writeD(this._page);
      this.write0(p);
    }

    protected void write0(ShortCutPacket p) {
      p.writeD(this._id);
      p.writeD(this._characterType);
    }
  }

  protected static class SkillShortcutInfo extends ShortCutPacket.ShortcutInfo {
    private final int _level;

    public SkillShortcutInfo(int type, int page, int id, int level, int characterType) {
      super(type, page, id, characterType);
      this._level = level;
    }

    public int getLevel() {
      return this._level;
    }

    protected void write0(ShortCutPacket p) {
      p.writeD(this._id);
      p.writeD(this._level);
      p.writeC(0);
      p.writeD(this._characterType);
    }
  }

  protected static class ItemShortcutInfo extends ShortCutPacket.ShortcutInfo {
    private int _reuseGroup;
    private int _currentReuse;
    private int _basicReuse;
    private int _varia1;
    private int _varia2;

    public ItemShortcutInfo(int type, int page, int id, int reuseGroup, int currentReuse, int basicReuse, int variation1, int variation2, int characterType) {
      super(type, page, id, characterType);
      this._reuseGroup = reuseGroup;
      this._currentReuse = currentReuse;
      this._basicReuse = basicReuse;
      this._varia1 = variation1;
      this._varia2 = variation2;
    }

    protected void write0(ShortCutPacket p) {
      p.writeD(this._id);
      p.writeD(this._characterType);
      p.writeD(this._reuseGroup);
      p.writeD(this._currentReuse);
      p.writeD(this._basicReuse);
      p.writeH(this._varia1);
      p.writeH(this._varia2);
    }
  }
}
