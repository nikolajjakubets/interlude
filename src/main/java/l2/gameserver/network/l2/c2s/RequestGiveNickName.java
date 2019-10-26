//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.utils.Util;

public class RequestGiveNickName extends L2GameClientPacket {
  private String _target;
  private String _title;

  public RequestGiveNickName() {
  }

  protected void readImpl() {
    this._target = this.readS(Config.CNAME_MAXLEN);
    this._title = this.readS();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (!this._title.isEmpty() && !Util.isMatchingRegexp(this._title, Config.CLAN_TITLE_TEMPLATE)) {
        activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestGiveNickName.IncorrectTittle", activeChar, new Object[0]));
      } else if (activeChar.isNoble() && this._target.equals(activeChar.getName())) {
        activeChar.setTitle(this._title);
        activeChar.sendPacket(Msg.TITLE_HAS_CHANGED);
        activeChar.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(activeChar)});
      } else if ((activeChar.getClanPrivileges() & 4) == 4) {
        if (activeChar.getClan().getLevel() < 3) {
          activeChar.sendPacket(Msg.TITLE_ENDOWMENT_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3);
        } else {
          UnitMember member = activeChar.getClan().getAnyMember(this._target);
          if (member != null) {
            member.setTitle(this._title);
            if (member.isOnline()) {
              member.getPlayer().sendPacket(Msg.TITLE_HAS_CHANGED);
              member.getPlayer().sendChanges();
            }
          } else {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestGiveNickName.NotInClan", activeChar, new Object[0]));
          }

        }
      }
    }
  }
}
