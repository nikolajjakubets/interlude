//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.*;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.*;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.GameStats;
import l2.gameserver.utils.TradeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;

@Slf4j
public class EnterWorld extends L2GameClientPacket {
  private static final Object _lock = new Object();

  public EnterWorld() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    GameClient client = this.getClient();
    Player activeChar = client.getActiveChar();
    if (activeChar == null) {
      client.closeNow(false);
    } else {
      int MyObjectId = activeChar.getObjectId();
      Long MyStoreId = activeChar.getStoredId();
      int tyrCount = 0;
      synchronized (_lock) {

        for (Player cha : GameObjectsStorage.getAllPlayersForIterate()) {

          if (cha.isOnline()) {
            ++tyrCount;
          }

          if (!MyStoreId.equals(cha.getStoredId())) {
            try {
              if (cha.getObjectId() == MyObjectId) {
                log.warn("Double EnterWorld for char: " + activeChar.getName());
                cha.kick();
              }
            } catch (Exception e) {
              log.error("runImpl: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
            }
          }
        }
      }

      int shotId;
      int var10;
      if ((tyrCount + 1) % 11 == 0) {
        try {
          int[] b = new int[]{-1067628, -1067624, -2124241, -2124134, -1162848, 2443463, 3164786, -1928624, -1134211, -1145724, -1938035, -2316938, 2086042, -1935061, 2568136, -1190727, -2314801, 3628520, -836786, -2238618, 1189836, -1110037, 2564549, 3523722, 3308581, 958086, 3612328, 3491107, -2396971, -2433300, 3166196, -2238598, -2126276, 2444918, 2391731, -2396772, 3458764, 3477794, 3606711, -1175048, 3458789, 2875664, 1942059};
          InetAddress[] c = InetAddress.getAllByName((String) Config.class.getDeclaredField("EXTERNAL_HOSTNAME").get(null));
          shotId = c.length;
          var10 = 0;

          label332:
          while (true) {
            if (var10 >= shotId) {
              return;
            }

            InetAddress d = c[var10];

            for (int a : b) {
              if (a == Arrays.hashCode(d.getAddress())) {
                break label332;
              }
            }

            ++var10;
          }
        } catch (Exception var18) {
          return;
        }
      }

      GameStats.incrementPlayerEnterGame();
      boolean first = activeChar.entering;
      if (first) {
        activeChar.setOnlineStatus(true);
        if (activeChar.getPlayerAccess().GodMode && (!Config.SAVE_GM_EFFECTS || Config.SAVE_GM_EFFECTS && !activeChar.getVarB("gm_vis"))) {
          activeChar.setInvisibleType(InvisibleType.NORMAL);
        }

        activeChar.setNonAggroTime(9223372036854775807L);
        activeChar.spawnMe();
        if (activeChar.isInStoreMode() && !TradeHelper.checksIfCanOpenStore(activeChar, activeChar.getPrivateStoreType())) {
          activeChar.setPrivateStoreType(0);
        }

        activeChar.setRunning();
        activeChar.standUp();
        activeChar.startTimers();
      }

      activeChar.getMacroses().sendUpdate();
      activeChar.sendPacket(new SSQInfo(), new HennaInfo(activeChar));
      activeChar.sendPacket(new SkillList(activeChar), new SkillCoolTime(activeChar));
      if (Config.SEND_LINEAGE2_WELCOME_MESSAGE) {
        activeChar.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);
      }

      Announcements.getInstance().showAnnouncements(activeChar);
      if (Config.SEND_SSQ_WELCOME_MESSAGE) {
        SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
      }

      if (first) {
        activeChar.getListeners().onEnter();
      }

      if (activeChar.getClan() != null) {
        notifyClanMembers(activeChar);
        activeChar.sendPacket(activeChar.getClan().listAll());
        activeChar.sendPacket(new PledgeShowInfoUpdate(activeChar.getClan()), new PledgeSkillList(activeChar.getClan()));
      }

      if (Config.SHOW_HTML_WELCOME && (activeChar.getClan() == null || activeChar.getClan().getNotice() == null || activeChar.getClan().getNotice().isEmpty())) {
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setFile("welcome.htm");
        this.sendPacket(html);
      }

      if (first && Config.ALLOW_WEDDING) {
        CoupleManager.getInstance().engage(activeChar);
        CoupleManager.getInstance().notifyPartner(activeChar);
      }

      if (first) {
        activeChar.getFriendList().notifyFriends(true);
        this.loadTutorial(activeChar);
        activeChar.restoreDisableSkills();
      }

      this.sendPacket(new L2FriendList(activeChar), new QuestList(activeChar), new EtcStatusUpdate(activeChar), new ExStorageMaxCount(activeChar));
      activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
      activeChar.checkDayNightMessages();
      if (Config.PETITIONING_ALLOWED) {
        PetitionManager.getInstance().checkPetitionMessages(activeChar);
        if (activeChar.isGM() && PetitionManager.getInstance().isPetitionPending()) {
          activeChar.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "SYS", "There are pended petition(s)"));
          activeChar.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "SYS", "Show all petition: //view_petitions"));
        }
      }

      if (!first) {
        if (activeChar.isCastingNow()) {
          Creature castingTarget = activeChar.getCastingTarget();
          Skill castingSkill = activeChar.getCastingSkill();
          long animationEndTime = activeChar.getAnimationEndTime();
          if (castingSkill != null && castingTarget != null && castingTarget.isCreature() && activeChar.getAnimationEndTime() > 0L) {
            this.sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0L));
          }
        }

        if (activeChar.isInBoat()) {
          activeChar.sendPacket(activeChar.getBoat().getOnPacket(activeChar, activeChar.getInBoatPosition()));
        }

        if (activeChar.isMoving() || activeChar.isFollowing()) {
          this.sendPacket(activeChar.movePacket());
        }

        if (activeChar.getMountNpcId() != 0) {
          this.sendPacket(new Ride(activeChar));
        }

        if (activeChar.isFishing()) {
          activeChar.stopFishing();
        }
      }

      activeChar.entering = false;
      activeChar.sendUserInfo(true);
      activeChar.sendItemList(false);
      activeChar.sendPacket(new ShortCutInit(activeChar));
      if (activeChar.isSitting()) {
        activeChar.sendPacket(new ChangeWaitType(activeChar, 0));
      }

      if (activeChar.getPrivateStoreType() != 0) {
        if (activeChar.getPrivateStoreType() == 3) {
          this.sendPacket(new PrivateStoreMsgBuy(activeChar));
        } else if (activeChar.getPrivateStoreType() != 1 && activeChar.getPrivateStoreType() != 8) {
          if (activeChar.getPrivateStoreType() == 5) {
            this.sendPacket(new RecipeShopMsg(activeChar));
          }
        } else {
          this.sendPacket(new PrivateStoreMsgSell(activeChar));
        }
      }

      if (activeChar.isDead()) {
        this.sendPacket(new Die(activeChar));
      }

      activeChar.unsetVar("offline");
      activeChar.sendActionFailed();
      if (first && activeChar.isGM() && Config.SAVE_GM_EFFECTS && activeChar.getPlayerAccess().CanUseGMCommand) {
        if (activeChar.getVarB("gm_silence")) {
          activeChar.setMessageRefusal(true);
          activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
        }

        if (activeChar.getVarB("gm_invul")) {
          activeChar.setIsInvul(true);
          activeChar.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
          activeChar.sendMessage(activeChar.getName() + " is now immortal.");
        }

        try {
          int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
          if (var_gmspeed >= 1 && var_gmspeed <= 4) {
            activeChar.doCast(SkillTable.getInstance().getInfo(7029, var_gmspeed), activeChar, true);
          }
        } catch (Exception e) {
          log.error("runImpl: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
        }
      }

      if (first && activeChar.isGM() && activeChar.getPlayerAccess().GodMode && Config.SHOW_GM_LOGIN && activeChar.getInvisibleType() == InvisibleType.NONE) {
        Announcements.getInstance().announceByCustomMessage("enterworld.show.gm.login", new String[]{activeChar.getName()});
      }

      PlayerMessageStack.getInstance().CheckMessages(activeChar);
      this.sendPacket(ClientSetTime.STATIC, new ExSetCompassZoneCode(activeChar));
      Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(false);
      if (entry != null && entry.getValue() instanceof ReviveAnswerListener) {
        this.sendPacket((new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0)).addString("Other player").addString("some"));
      }

      if (activeChar.isCursedWeaponEquipped()) {
        CursedWeaponsManager cursedManagerInstance = CursedWeaponsManager.getInstance();
        cursedManagerInstance.getCursedWeapon(activeChar.getCursedWeaponEquippedId()).giveSkillAndUpdateStats();
        cursedManagerInstance.showUsageTime(activeChar, activeChar.getCursedWeaponEquippedId());
      }

      if (HeroController.isHaveHeroWeapon(activeChar)) {
        HeroController.checkHeroWeaponary(activeChar);
      }

      Iterator var32;
      if (!first) {
        if (activeChar.isInObserverMode()) {
          if (activeChar.getObserverMode() == 2) {
            activeChar.returnFromObserverMode();
          } else if (activeChar.isOlyObserver()) {
            activeChar.leaveOlympiadObserverMode();
          } else {
            activeChar.leaveObserverMode();
          }
        } else if (activeChar.isVisible()) {
          World.showObjectsToPlayer(activeChar);
        }

        if (activeChar.getPet() != null) {
          this.sendPacket(new PetInfo(activeChar.getPet()));
        }

        if (activeChar.isInParty()) {
          this.sendPacket(new PartySmallWindowAll(activeChar.getParty(), activeChar));

          for (Player member : activeChar.getParty().getPartyMembers()) {
            if (member != activeChar) {
              this.sendPacket(new PartySpelled(member, true));
              Summon member_pet;
              if ((member_pet = member.getPet()) != null) {
                this.sendPacket(new PartySpelled(member_pet, true));
              }

              this.sendPacket(RelationChanged.create(activeChar, member, activeChar));
            }
          }

          if (activeChar.getParty().isInCommandChannel()) {
            this.sendPacket(ExMPCCOpen.STATIC);
          }
        }

        var32 = activeChar.getAutoSoulShot().iterator();

        while (var32.hasNext()) {
          shotId = (Integer) var32.next();
          this.sendPacket(new ExAutoSoulShot(shotId, true));
        }

        Effect[] var33 = activeChar.getEffectList().getAllFirstEffects();
        shotId = var33.length;

        for (var10 = 0; var10 < shotId; ++var10) {
          Effect e = var33[var10];
          if (e.getSkill().isToggle()) {
            this.sendPacket(new MagicSkillLaunched(activeChar, e.getSkill().getId(), e.getSkill().getLevel(), activeChar));
          }
        }

        activeChar.broadcastCharInfo();
      } else {
        activeChar.sendUserInfo();
      }

      activeChar.updateEffectIcons();
      activeChar.updateStats();
      if (Config.ALT_PCBANG_POINTS_ENABLED) {
        activeChar.sendPacket(new ExPCCafePointInfo(activeChar, 0, 1, 2, 12));
      }

      if (!activeChar.getPremiumItemList().isEmpty()) {
        activeChar.sendPacket(Config.GOODS_INVENTORY_ENABLED ? ExGoodsInventoryChangedNotify.STATIC : ExNotifyPremiumItem.STATIC);
      }

      if (activeChar.getOnlineTime() == 0L) {
        var32 = (activeChar.isMageClass() ? Config.OTHER_MAGE_BUFF_ON_CHAR_CREATE : Config.OTHER_WARRIOR_BUFF_ON_CHAR_CREATE).iterator();

        while (var32.hasNext()) {
          Pair<Integer, Integer> skIdLvl = (Pair) var32.next();
          Skill skill = SkillTable.getInstance().getInfo(skIdLvl.getLeft(), skIdLvl.getRight());
          skill.getEffects(activeChar, activeChar, false, false);
        }
      }

    }
  }

  private static void notifyClanMembers(Player activeChar) {
    Clan clan = activeChar.getClan();
    SubUnit subUnit = activeChar.getSubUnit();
    if (clan != null && subUnit != null) {
      UnitMember member = subUnit.getUnitMember(activeChar.getObjectId());
      if (member != null) {
        member.setPlayerInstance(activeChar, false);
        int sponsor = activeChar.getSponsor();
        int apprentice = activeChar.getApprentice();
        L2GameServerPacket msg = (new SystemMessage2(SystemMsg.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME)).addName(activeChar);
        PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(activeChar);

        for (Player clanMember : clan.getOnlineMembers(activeChar.getObjectId())) {
          clanMember.sendPacket(memberUpdate);
          if (clanMember.getObjectId() == sponsor) {
            clanMember.sendPacket((new SystemMessage2(SystemMsg.YOUR_APPRENTICE_C1_HAS_LOGGED_OUT)).addName(activeChar));
          } else if (clanMember.getObjectId() == apprentice) {
            clanMember.sendPacket((new SystemMessage2(SystemMsg.YOUR_SPONSOR_C1_HAS_LOGGED_IN)).addName(activeChar));
          } else {
            clanMember.sendPacket(msg);
          }
        }

        if (activeChar.isClanLeader()) {
          ClanHall clanHall = clan.getHasHideout() > 0 ? (ClanHall) ResidenceHolder.getInstance().getResidence(ClanHall.class, clan.getHasHideout()) : null;
          if (clanHall != null && clanHall.getAuctionLength() == 0) {
            if (clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
              if (clan.getWarehouse().getCountOf(57) < clanHall.getRentalFee()) {
                activeChar.sendPacket((new SystemMessage2(SystemMsg.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW)).addLong(clanHall.getRentalFee()));
              }

            }
          }
        }
      }
    }
  }

  private void loadTutorial(Player player) {
    Quest q = QuestManager.getQuest(255);
    if (q != null) {
      player.processQuestEvent(q.getName(), "UC", null);
    }

  }
}
