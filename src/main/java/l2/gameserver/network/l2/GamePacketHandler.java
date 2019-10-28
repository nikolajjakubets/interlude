//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2;

import l2.commons.net.nio.impl.*;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.l2.c2s.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public final class GamePacketHandler implements IPacketHandler<GameClient>, IClientFactory<GameClient>, IMMOExecutor<GameClient> {
  private static final Logger _log = LoggerFactory.getLogger(GamePacketHandler.class);

  public GamePacketHandler() {
  }

  public ReceivablePacket<GameClient> handlePacket(ByteBuffer buf, GameClient client) {
    int id = buf.get() & 255;
    ReceivablePacket<GameClient> msg = null;
    if (CGMHelper.isActive()) {
      msg = CGMHelper.getInstance().handle(client, id);
      if (msg != null) {
        return msg;
      }
    }

    try {
      switch(client.getState()) {
        case CONNECTED:
          switch(id) {
            case 0:
              msg = new ProtocolVersion();
              return msg;
            case 8:
              msg = new AuthLogin();
              return msg;
            case 168:
              msg = new NetPing();
              return msg;
            case 203:
              msg = new ReplyGameGuardQuery();
              return msg;
            default:
              client.onUnknownPacket();
              return msg;
          }
        case AUTHED:
          switch(id) {
            case 9:
              msg = new Logout();
              return msg;
            case 11:
              msg = new CharacterCreate();
              return msg;
            case 12:
              msg = new CharacterDelete();
              return msg;
            case 13:
              msg = new CharacterSelected();
              return msg;
            case 14:
              msg = new NewCharacter();
              return msg;
            case 33:
              msg = new RequestBypassToServer();
              return msg;
            case 98:
              msg = new CharacterRestore();
              return msg;
            case 168:
              msg = new NetPing();
              return msg;
            case 202:
              msg = new ReplyGameGuardQuery();
              return msg;
            case 208:
              int id3 = buf.getShort() & '\uffff';
              switch(id3) {
                case 54:
                  msg = new GotoLobby();
                  return msg;
                case 147:
                  msg = new RequestEx2ndPasswordCheck();
                  return msg;
                case 148:
                  msg = new RequestEx2ndPasswordVerify();
                  return msg;
                case 149:
                  msg = new RequestEx2ndPasswordReq();
                  return msg;
                default:
                  client.onUnknownPacket();
                  return msg;
              }
            default:
              client.onUnknownPacket();
              return msg;
          }
        case IN_GAME:
          switch(id) {
            case 1:
              msg = new MoveBackwardToLocation();
              break;
            case 2:
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
            case 12:
            case 13:
            case 14:
            case 19:
            case 24:
            case 25:
            case 26:
            case 28:
            case 29:
            case 35:
            case 46:
            case 52:
            case 57:
            case 58:
            case 59:
            case 61:
            case 62:
            case 64:
            case 76:
            case 78:
            case 80:
            case 81:
            case 82:
            case 84:
            case 86:
            case 90:
            case 98:
            case 101:
            case 105:
            case 141:
            case 169:
            case 201:
            case 203:
            default:
              client.onUnknownPacket();
              break;
            case 3:
              msg = new EnterWorld();
              break;
            case 4:
              msg = new Action();
              break;
            case 9:
              msg = new Logout();
              break;
            case 10:
              msg = new AttackRequest();
              break;
            case 15:
              msg = new RequestItemList();
            case 16:
            case 40:
            case 45:
            case 73:
            case 106:
            case 117:
            case 120:
            case 122:
            case 146:
            case 149:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 161:
            case 176:
            case 206:
              break;
            case 17:
              msg = new RequestUnEquipItem();
              break;
            case 18:
              msg = new RequestDropItem();
              break;
            case 20:
              msg = new UseItem();
              break;
            case 21:
              msg = new TradeRequest();
              break;
            case 22:
              msg = new AddTradeItem();
              break;
            case 23:
              msg = new TradeDone();
              break;
            case 27:
              msg = new RequestSocialAction();
              break;
            case 30:
              msg = new RequestSellItem();
              break;
            case 31:
              msg = new RequestBuyItem();
              break;
            case 32:
              msg = new RequestLinkHtml();
              break;
            case 33:
              msg = new RequestBypassToServer();
              break;
            case 34:
              msg = new RequestBBSwrite();
              break;
            case 36:
              msg = new RequestJoinPledge();
              break;
            case 37:
              msg = new RequestAnswerJoinPledge();
              break;
            case 38:
              msg = new RequestWithdrawalPledge();
              break;
            case 39:
              msg = new RequestOustPledgeMember();
              break;
            case 41:
              msg = new RequestJoinParty();
              break;
            case 42:
              msg = new RequestAnswerJoinParty();
              break;
            case 43:
              msg = new RequestWithDrawalParty();
              break;
            case 44:
              msg = new RequestOustPartyMember();
              break;
            case 47:
              msg = new RequestMagicSkillUse();
              break;
            case 48:
              msg = new Appearing();
              break;
            case 49:
              if (Config.ALLOW_WAREHOUSE) {
                msg = new SendWareHouseDepositList();
              }
              break;
            case 50:
              msg = new SendWareHouseWithDrawList();
              break;
            case 51:
              msg = new RequestShortCutReg();
              break;
            case 53:
              msg = new RequestShortCutDel();
              break;
            case 54:
              msg = new CannotMoveAnymore();
              break;
            case 55:
              msg = new RequestTargetCanceld();
              break;
            case 56:
              msg = new Say2C();
              break;
            case 60:
              msg = new RequestPledgeMemberList();
              break;
            case 63:
              msg = new RequestSkillList();
              break;
            case 65:
              msg = new MoveWithDelta();
              break;
            case 66:
              msg = new RequestGetOnVehicle();
              break;
            case 67:
              msg = new RequestGetOffVehicle();
              break;
            case 68:
              msg = new AnswerTradeRequest();
              break;
            case 69:
              msg = new RequestActionUse();
              break;
            case 70:
              msg = new RequestRestart();
              break;
            case 71:
              msg = new RequestSiegeInfo();
              break;
            case 72:
              msg = new ValidatePosition();
              break;
            case 74:
              msg = new StartRotatingC();
              break;
            case 75:
              msg = new FinishRotatingC();
              break;
            case 77:
              msg = new RequestStartPledgeWar();
              break;
            case 79:
              msg = new RequestStopPledgeWar();
              break;
            case 83:
              msg = new RequestSetPledgeCrest();
              break;
            case 85:
              msg = new RequestGiveNickName();
              break;
            case 87:
              msg = new RequestShowBoard();
              break;
            case 88:
              msg = new RequestEnchantItem();
              break;
            case 89:
              msg = new RequestDestroyItem();
              break;
            case 91:
              msg = new SendBypassBuildCmd();
              break;
            case 92:
              msg = new RequestMoveToLocationInVehicle();
              break;
            case 93:
              msg = new CannotMoveAnymoreInVehicle();
              break;
            case 94:
              msg = new RequestFriendInvite();
              break;
            case 95:
              msg = new RequestFriendAddReply();
              break;
            case 96:
              msg = new RequestFriendList();
              break;
            case 97:
              msg = new RequestFriendDel();
              break;
            case 99:
              msg = new RequestQuestList();
              break;
            case 100:
              msg = new RequestQuestAbort();
              break;
            case 102:
              msg = new RequestPledgeInfo();
              break;
            case 103:
              msg = new RequestPledgeExtendedInfo();
              break;
            case 104:
              msg = new RequestPledgeCrest();
              break;
            case 107:
              msg = new RequestAquireSkillInfo();
              break;
            case 108:
              msg = new RequestAquireSkill();
              break;
            case 109:
              msg = new RequestRestartPoint();
              break;
            case 110:
              msg = new RequestGMCommand();
              break;
            case 111:
              msg = new RequestPartyMatchConfig();
              break;
            case 112:
              msg = new RequestPartyMatchList();
              break;
            case 113:
              msg = new RequestPartyMatchDetail();
              break;
            case 114:
              msg = new RequestCrystallizeItem();
              break;
            case 115:
              msg = new RequestPrivateStoreSell();
              break;
            case 116:
              msg = new SetPrivateStoreSellList();
              break;
            case 118:
              msg = new RequestPrivateStoreQuitSell();
              break;
            case 119:
              msg = new SetPrivateStoreMsgSell();
              break;
            case 121:
              msg = new RequestPrivateStoreManageBuy();
              break;
            case 123:
              msg = new RequestTutorialLinkHtml();
              break;
            case 124:
              msg = new RequestTutorialPassCmdToServer();
              break;
            case 125:
              msg = new RequestTutorialQuestionMark();
              break;
            case 126:
              msg = new RequestTutorialClientEvent();
              break;
            case 127:
              msg = new RequestPetition();
              break;
            case 128:
              msg = new RequestPetitionCancel();
              break;
            case 129:
              msg = new RequestGmList();
              break;
            case 130:
              msg = new RequestJoinAlly();
              break;
            case 131:
              msg = new RequestAnswerJoinAlly();
              break;
            case 132:
              msg = new RequestWithdrawAlly();
              break;
            case 133:
              msg = new RequestOustAlly();
              break;
            case 134:
              msg = new RequestDismissAlly();
              break;
            case 135:
              msg = new RequestSetAllyCrest();
              break;
            case 136:
              msg = new RequestAllyCrest();
              break;
            case 137:
              msg = new RequestChangePetName();
              break;
            case 138:
              msg = new RequestPetUseItem();
              break;
            case 139:
              msg = new RequestGiveItemToPet();
              break;
            case 140:
              msg = new RequestGetItemFromPet();
              break;
            case 142:
              msg = new RequestAllyInfo();
              break;
            case 143:
              msg = new RequestPetGetItem();
              break;
            case 144:
              msg = new RequestPrivateStoreBuy();
              break;
            case 145:
              msg = new SetPrivateStoreBuyList();
              break;
            case 147:
              msg = new RequestPrivateStoreQuitBuy();
              break;
            case 148:
              msg = new SetPrivateStoreMsgBuy();
              break;
            case 150:
              msg = new RequestPrivateStoreBuySellList();
              break;
            case 158:
              msg = new RequestPackageSendableItemList();
              break;
            case 159:
              msg = new RequestPackageSend();
              break;
            case 160:
              msg = new RequestBlock();
              break;
            case 162:
              msg = new RequestCastleSiegeAttackerList();
              break;
            case 163:
              msg = new RequestCastleSiegeDefenderList();
              break;
            case 164:
              msg = new RequestJoinCastleSiege();
              break;
            case 165:
              msg = new RequestConfirmCastleSiegeWaitingList();
              break;
            case 166:
              msg = new RequestSetCastleSiegeTime();
              break;
            case 167:
              msg = new RequestMultiSellChoose();
              break;
            case 168:
              msg = new NetPing();
              break;
            case 170:
              msg = new BypassUserCmd();
              break;
            case 171:
              msg = new SnoopQuit();
              break;
            case 172:
              msg = new RequestRecipeBookOpen();
              break;
            case 173:
              msg = new RequestRecipeItemDelete();
              break;
            case 174:
              msg = new RequestRecipeItemMakeInfo();
              break;
            case 175:
              msg = new RequestRecipeItemMakeSelf();
              break;
            case 177:
              msg = new RequestRecipeShopMessageSet();
              break;
            case 178:
              msg = new RequestRecipeShopListSet();
              break;
            case 179:
              msg = new RequestRecipeShopManageQuit();
              break;
            case 180:
              msg = new SnoopQuit();
              break;
            case 181:
              msg = new RequestRecipeShopMakeInfo();
              break;
            case 182:
              msg = new RequestRecipeShopMakeDo();
              break;
            case 183:
              msg = new RequestRecipeShopSellList();
              break;
            case 184:
              msg = new RequestObserverEnd();
              break;
            case 185:
              msg = new RequestVoteNew();
              break;
            case 186:
              msg = new RequestHennaList();
              break;
            case 187:
              msg = new RequestHennaItemInfo();
              break;
            case 188:
              msg = new RequestHennaEquip();
              break;
            case 189:
              msg = new RequestHennaUnequipList();
              break;
            case 190:
              msg = new RequestHennaUnequipInfo();
              break;
            case 191:
              msg = new RequestHennaUnequip();
              break;
            case 192:
              msg = new RequestPledgePower();
              break;
            case 193:
              msg = new RequestMakeMacro();
              break;
            case 194:
              msg = new RequestDeleteMacro();
              break;
            case 195:
              msg = new RequestHennaItemInfo();
              break;
            case 196:
              msg = new RequestBuySeed();
              break;
            case 197:
              msg = new ConfirmDlg();
              break;
            case 198:
              msg = new RequestPreviewItem();
              break;
            case 199:
              msg = new RequestSSQStatus();
              break;
            case 200:
              msg = new PetitionVote();
              break;
            case 202:
              msg = new ReplyGameGuardQuery();
              break;
            case 204:
              msg = new RequestSendL2FriendSay();
              break;
            case 205:
              msg = new RequestShowMiniMap();
              break;
            case 207:
              msg = new RequestReload();
              break;
            case 208:
              if (buf.remaining() >= 2) {
                int id2 = buf.getShort() & '\uffff';
                switch(id2) {
                  case 1:
                    msg = new RequestOustFromPartyRoom();
                    break;
                  case 2:
                    msg = new RequestDismissPartyRoom();
                    break;
                  case 3:
                    msg = new RequestWithdrawPartyRoom();
                    break;
                  case 4:
                    msg = new RequestHandOverPartyMaster();
                    break;
                  case 5:
                    msg = new RequestAutoSoulShot();
                    break;
                  case 6:
                    msg = new RequestExEnchantSkillInfo();
                    break;
                  case 7:
                    msg = new RequestExEnchantSkill();
                    break;
                  case 8:
                    msg = new RequestManorList();
                    break;
                  case 9:
                    msg = new RequestProcureCropList();
                    break;
                  case 10:
                    msg = new RequestSetSeed();
                    break;
                  case 11:
                    msg = new RequestSetCrop();
                    break;
                  case 12:
                    msg = new RequestWriteHeroWords();
                    break;
                  case 13:
                    msg = new RequestExMPCCAskJoin();
                    break;
                  case 14:
                    msg = new RequestExMPCCAcceptJoin();
                    break;
                  case 15:
                    msg = new RequestExOustFromMPCC();
                    break;
                  case 16:
                    msg = new RequestPledgeCrestLarge();
                    break;
                  case 17:
                    msg = new RequestSetPledgeCrestLarge();
                    break;
                  case 18:
                    msg = new RequestOlympiadObserverEnd();
                    break;
                  case 19:
                    msg = new RequestOlympiadMatchList();
                    break;
                  case 20:
                    msg = new RequestAskJoinPartyRoom();
                    break;
                  case 21:
                    msg = new AnswerJoinPartyRoom();
                    break;
                  case 22:
                    msg = new RequestListPartyMatchingWaitingRoom();
                    break;
                  case 23:
                    msg = new RequestExitPartyMatchingWaitingRoom();
                    break;
                  case 24:
                    msg = new RequestGetBossRecord();
                    break;
                  case 25:
                    msg = new RequestPledgeSetAcademyMaster();
                    break;
                  case 26:
                    msg = new RequestPledgePowerGradeList();
                    break;
                  case 27:
                    msg = new RequestPledgeMemberPowerInfo();
                    break;
                  case 28:
                    msg = new RequestPledgeSetMemberPowerGrade();
                    break;
                  case 29:
                    msg = new RequestPledgeMemberInfo();
                    break;
                  case 30:
                    msg = new RequestPledgeWarList();
                    break;
                  case 31:
                    msg = new RequestExFishRanking();
                    break;
                  case 32:
                    msg = new RequestPCCafeCouponUse();
                    break;
                  case 33:
                  case 37:
                  default:
                    client.onUnknownPacket();
                    break;
                  case 34:
                    msg = new RequestCursedWeaponList();
                    break;
                  case 35:
                    msg = new RequestCursedWeaponLocation();
                    break;
                  case 36:
                    msg = new RequestPledgeReorganizeMember();
                    break;
                  case 38:
                    msg = new RequestExMPCCShowPartyMembersInfo();
                    break;
                  case 39:
                    msg = new RequestDuelStart();
                    break;
                  case 40:
                    msg = new RequestDuelAnswerStart();
                    break;
                  case 41:
                    msg = new RequestConfirmTargetItem();
                    break;
                  case 42:
                    msg = new RequestConfirmRefinerItem();
                    break;
                  case 43:
                    msg = new RequestConfirmGemStone();
                    break;
                  case 44:
                    msg = new RequestRefine();
                    break;
                  case 45:
                    msg = new RequestConfirmCancelItem();
                    break;
                  case 46:
                    msg = new RequestRefineCancel();
                    break;
                  case 47:
                    msg = new RequestExMagicSkillUseGround();
                    break;
                  case 48:
                    msg = new RequestDuelSurrender();
                }
              } else {
                _log.warn("Client: " + client.toString() + " sent a 0xd0 without the second opcode.");
              }
          }
      }
    } catch (BufferUnderflowException var7) {
      client.onPacketReadFail();
    }

    return msg;
  }

  public GameClient create(MMOConnection<GameClient> con) {
    return new GameClient(con);
  }

  public void execute(Runnable r) {
    ThreadPoolManager.getInstance().execute(r);
  }
}
