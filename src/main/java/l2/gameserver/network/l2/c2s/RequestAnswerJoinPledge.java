//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.JoinPledge;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListAdd;
import l2.gameserver.network.l2.s2c.PledgeSkillList;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestAnswerJoinPledge extends L2GameClientPacket {
  private int _response;

  public RequestAnswerJoinPledge() {
  }

  protected void readImpl() {
    this._response = this._buf.hasRemaining() ? this.readD() : 0;
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Request request = player.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.CLAN)) {
        if (!request.isInProgress()) {
          request.cancel();
          player.sendActionFailed();
        } else if (player.isOutOfControl()) {
          request.cancel();
          player.sendActionFailed();
        } else {
          Player requestor = request.getRequestor();
          if (requestor == null) {
            request.cancel();
            player.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            player.sendActionFailed();
          } else if (requestor.getRequest() != request) {
            request.cancel();
            player.sendActionFailed();
          } else {
            Clan clan = requestor.getClan();
            if (clan == null) {
              request.cancel();
              player.sendActionFailed();
            } else if (this._response == 0) {
              request.cancel();
              requestor.sendPacket((new SystemMessage2(SystemMsg.S1_DECLINED_YOUR_CLAN_INVITATION)).addName(player));
            } else if (!player.canJoinClan()) {
              request.cancel();
              player.sendPacket(SystemMsg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
            } else {
              try {
                player.sendPacket(new JoinPledge(requestor.getClanId()));
                int pledgeType = request.getInteger("pledgeType");
                SubUnit subUnit = clan.getSubUnit(pledgeType);
                if (subUnit != null) {
                  UnitMember member = new UnitMember(clan, player.getName(), player.getTitle(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), pledgeType, player.getPowerGrade(), player.getApprentice(), player.getSex(), -128);
                  subUnit.addUnitMember(member);
                  player.setPledgeType(pledgeType);
                  player.setClan(clan);
                  member.setPlayerInstance(player, false);
                  if (pledgeType == -1) {
                    player.setLvlJoinedAcademy(player.getLevel());
                  }

                  member.setPowerGrade(clan.getAffiliationRank(player.getPledgeType()));
                  clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(member), player);
                  clan.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN)).addString(player.getName()), new PledgeShowInfoUpdate(clan)});
                  player.sendPacket(SystemMsg.ENTERED_THE_CLAN);
                  player.sendPacket(player.getClan().listAll());
                  player.setLeaveClanTime(0L);
                  player.updatePledgeClass();
                  clan.addSkillsQuietly(player);
                  player.sendPacket(new PledgeSkillList(clan));
                  player.sendPacket(new SkillList(player));
                  EventHolder.getInstance().findEvent(player);
                  player.broadcastCharInfo();
                  player.store(false);
                  return;
                }
              } finally {
                request.done();
              }

            }
          }
        }
      }
    }
  }
}
