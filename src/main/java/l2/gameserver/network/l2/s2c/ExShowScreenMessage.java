//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExShowScreenMessage extends L2GameServerPacket {
  public static final int SYSMSG_TYPE = 0;
  public static final int STRING_TYPE = 1;
  private String _text;
  private int _type;
  private int _sysMessageId;
  private boolean _big_font;
  private boolean _effect;
  private ExShowScreenMessage.ScreenMessageAlign _text_align;
  private int _time;

  /** @deprecated */
  @Deprecated
  public ExShowScreenMessage(String text, int time, ExShowScreenMessage.ScreenMessageAlign text_align, boolean big_font) {
    this(text, time, 0, text_align, big_font, 1, -1, false);
  }

  public ExShowScreenMessage(String text, int time, int sysMsgId, ExShowScreenMessage.ScreenMessageAlign text_align, boolean big_font, int type, int messageId, boolean showEffect) {
    this._type = type;
    this._sysMessageId = messageId;
    this._time = time;
    this._text_align = text_align;
    this._big_font = big_font;
    this._effect = showEffect;
    this._type = type;
    this._sysMessageId = sysMsgId;
    this._time = time;
    this._text_align = text_align;
    this._big_font = big_font;
    this._effect = showEffect;
    this._text = text;
  }

  protected final void writeImpl() {
    this.writeEx(56);
    this.writeD(this._type);
    this.writeD(this._sysMessageId);
    this.writeD(this._text_align.ordinal() + 1);
    this.writeD(0);
    this.writeD(this._big_font ? 0 : 1);
    this.writeD(0);
    this.writeD(0);
    this.writeD(this._effect ? 1 : 0);
    this.writeD(this._time);
    this.writeD(1);
    this.writeS(this._text);
  }

  public static enum ScreenMessageAlign {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_CENTER,
    MIDDLE_RIGHT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    private ScreenMessageAlign() {
    }
  }
}
