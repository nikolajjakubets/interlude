//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class TutorialShowHtml extends L2GameServerPacket {
  private String _html;

  public TutorialShowHtml(String html) {
    this._html = html;
  }

  protected final void writeImpl() {
    this.writeC(160);
    this.writeS(this._html);
  }
}
