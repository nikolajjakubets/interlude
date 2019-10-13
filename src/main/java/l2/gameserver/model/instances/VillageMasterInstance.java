//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.ClassType;
import l2.gameserver.model.base.PlayerClass;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PledgeReceiveSubPledgeCreated;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.PledgeStatusChanged;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.SiegeUtils;
import l2.gameserver.utils.Util;

public final class VillageMasterInstance extends NpcInstance {
  public VillageMasterInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (command.equals("create_clan_check")) {
        if (player.getLevel() < Config.CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE) {
          this.showChatWindow(player, "villagemaster/pl002.htm", new Object[0]);
        } else if (player.isClanLeader()) {
          this.showChatWindow(player, "villagemaster/pl003.htm", new Object[0]);
        } else if (player.getClan() != null) {
          this.showChatWindow(player, "villagemaster/pl004.htm", new Object[0]);
        } else {
          this.showChatWindow(player, "villagemaster/pl005.htm", new Object[0]);
        }
      } else if (command.equals("disband_clan_check")) {
        if (checkPlayerForClanLeader(this, player)) {
          this.showChatWindow(player, "villagemaster/pl007.htm", new Object[0]);
        }
      } else if (command.equals("restore_clan_check")) {
        if (checkPlayerForClanLeader(this, player)) {
          this.showChatWindow(player, "villagemaster/pl010.htm", new Object[0]);
        }
      } else {
        String val;
        if (command.startsWith("create_clan") && command.length() > 12) {
          val = command.substring(12);
          this.createClan(this, player, val);
        } else if (command.startsWith("create_academy") && command.length() > 15) {
          val = command.substring(15, command.length());
          this.createSubPledge(player, val, -1, 5, "");
        } else {
          String[] sub;
          if (command.startsWith("create_royal") && command.length() > 15) {
            sub = command.substring(13, command.length()).split(" ", 2);
            if (sub.length == 2) {
              this.createSubPledge(player, sub[1], 100, 6, sub[0]);
            }
          } else if (command.startsWith("create_knight") && command.length() > 16) {
            sub = command.substring(14, command.length()).split(" ", 2);
            if (sub.length == 2) {
              this.createSubPledge(player, sub[1], 1001, 7, sub[0]);
            }
          } else if (command.startsWith("assign_subpl_leader") && command.length() > 22) {
            sub = command.substring(20, command.length()).split(" ", 2);
            if (sub.length == 2) {
              this.assignSubPledgeLeader(player, sub[1], sub[0]);
            }
          } else if (command.startsWith("assign_new_clan_leader") && command.length() > 23) {
            val = command.substring(23);
            this.setLeader(player, val);
          } else if (command.startsWith("cancel_new_clan_leader")) {
            this.cancelNewLeader(player);
          } else if (command.startsWith("create_ally") && command.length() > 12) {
            val = command.substring(12);
            this.createAlly(player, val);
          } else if (command.startsWith("dissolve_ally")) {
            this.dissolveAlly(player);
          } else if (command.startsWith("dissolve_clan")) {
            dissolveClan(this, player);
          } else if (command.startsWith("restore_clan")) {
            restoreClan(this, player);
          } else if (command.startsWith("increase_clan_level")) {
            this.levelUpClan(player);
          } else if (command.startsWith("learn_clan_skills")) {
            showClanSkillList(player);
          } else if (command.startsWith("ShowCouponExchange")) {
            if (Functions.getItemCount(player, 8869) <= 0L && Functions.getItemCount(player, 8870) <= 0L) {
              command = "Link villagemaster/reflect_weapon_master_noticket.htm";
            } else {
              command = "Multisell 800";
            }

            super.onBypassFeedback(player, command);
          } else if (command.startsWith("Subclass")) {
            if (player.getPet() != null) {
              player.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
              return;
            }

            if (player.isActionsDisabled() || player.getTransformation() != 0 || player.isCursedWeaponEquipped()) {
              player.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
              return;
            }

            if (player.getWeightPenalty() >= 3) {
              player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
              return;
            }

            if ((double)player.getInventoryLimit() * 0.8D < (double)player.getInventory().getSize()) {
              player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
              return;
            }

            StringBuilder content = new StringBuilder("<html><body>");
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            Map<Integer, SubClass> playerClassList = player.getSubClasses();
            if (player.getLevel() < 40) {
              content.append("You must be level 40 or more to operate with your sub-classes.");
              content.append("</body></html>");
              html.setHtml(content.toString());
              player.sendPacket(html);
              return;
            }

            int classId = 0;
            int newClassId = 0;
            int intVal = 0;

            try {
              String[] var10 = command.substring(9, command.length()).split(" ");
              int var11 = var10.length;

              for(int var12 = 0; var12 < var11; ++var12) {
                String id = var10[var12];
                if (intVal == 0) {
                  intVal = Integer.parseInt(id);
                } else if (classId > 0) {
                  newClassId = Integer.parseInt(id);
                } else {
                  classId = Integer.parseInt(id);
                }
              }
            } catch (Exception var14) {
              var14.printStackTrace();
            }

            Set subsAvailable;
            Iterator var20;
            SubClass subClass;
            Iterator var24;
            label321:
            switch(intVal) {
              case 1:
                subsAvailable = this.getAvailableSubClasses(player, true);
                if (subsAvailable != null && !subsAvailable.isEmpty()) {
                  content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");
                  if (Config.ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM) {
                    content.append((new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubClassPriceForCustomItem", player, new Object[0])).addItemName(Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID).addNumber((long)Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT));
                    content.append("<br>");
                  }

                  Iterator var18 = subsAvailable.iterator();

                  while(true) {
                    if (!var18.hasNext()) {
                      break label321;
                    }

                    PlayerClass subClass = (PlayerClass)var18.next();
                    content.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_Subclass 4 ").append(subClass.ordinal()).append("\">").append(HtmlUtils.htmlClassName(subClass.ordinal(), player)).append("</a><br>");
                  }
                }

                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", player, new Object[0]));
                return;
              case 2:
                content.append("Change Subclass:<br>");
                int baseClassId = player.getBaseClassId();
                if (playerClassList.size() < 2) {
                  content.append("You can't change subclasses when you don't have a subclass to begin with.<br><a action=\"bypass -h npc_").append(this.getObjectId()).append("_Subclass 1\">Add subclass.</a>");
                  break;
                } else {
                  content.append("Which class would you like to switch to?<br>");
                  if (baseClassId == player.getActiveClassId()) {
                    content.append(HtmlUtils.htmlClassName(baseClassId, player)).append(" <font color=\"LEVEL\">(Base Class)</font><br><br>");
                  } else {
                    content.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_Subclass 5 ").append(baseClassId).append("\">").append(HtmlUtils.htmlClassName(baseClassId, player)).append("</a> <font color=\"LEVEL\">(Base Class)</font><br><br>");
                  }

                  var24 = playerClassList.values().iterator();

                  while(true) {
                    if (!var24.hasNext()) {
                      break label321;
                    }

                    subClass = (SubClass)var24.next();
                    if (!subClass.isBase()) {
                      int subClassId = subClass.getClassId();
                      if (subClassId == player.getActiveClassId()) {
                        content.append(HtmlUtils.htmlClassName(subClassId, player)).append("<br>");
                      } else {
                        content.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_Subclass 5 ").append(subClassId).append("\">").append(HtmlUtils.htmlClassName(subClassId, player)).append("</a><br>");
                      }
                    }
                  }
                }
              case 3:
                content.append("Change Subclass:<br>Which of the following sub-classes would you like to change?<br>");
                var24 = playerClassList.values().iterator();

                while(var24.hasNext()) {
                  subClass = (SubClass)var24.next();
                  content.append("<br>");
                  if (!subClass.isBase()) {
                    content.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_Subclass 6 ").append(subClass.getClassId()).append("\">").append(HtmlUtils.htmlClassName(subClass.getClassId(), player)).append("</a><br>");
                  }
                }

                content.append("<br>If you change a sub-class, you'll start at level 40 after the 2nd class transfer.");
                break;
              case 4:
                boolean allowAddition = true;
                if (player.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                  player.sendMessage((new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", player, new Object[0])).addNumber((long)Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
                  allowAddition = false;
                }

                if (!playerClassList.isEmpty()) {
                  var20 = playerClassList.values().iterator();

                  while(var20.hasNext()) {
                    SubClass subClass = (SubClass)var20.next();
                    if (subClass.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                      player.sendMessage((new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", player, new Object[0])).addNumber((long)Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
                      allowAddition = false;
                      break;
                    }
                  }
                }

                if (player.isInDuel()) {
                  allowAddition = false;
                }

                if (Config.OLY_ENABLED && (ParticipantPool.getInstance().isRegistred(player) || player.isOlyParticipant())) {
                  player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
                  return;
                }

                if (!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !playerClassList.isEmpty() && playerClassList.size() < 2) {
                  if (player.isQuestCompleted("_234_FatesWhisper")) {
                    allowAddition = player.isQuestCompleted("_235_MimirsElixir");
                    if (!allowAddition) {
                      player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestMimirsElixir", player, new Object[0]));
                    }
                  } else {
                    player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestFatesWhisper", player, new Object[0]));
                    allowAddition = false;
                  }
                }

                if (Config.ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM && allowAddition) {
                  if (ItemFunctions.getItemCount(player, Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID) < (long)Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT) {
                    if (Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID == 57) {
                      player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    } else {
                      player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                    }

                    return;
                  }

                  ItemFunctions.removeItem(player, Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID, (long)Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT, true);
                }

                if (allowAddition) {
                  if (!player.addSubClass(classId, true)) {
                    player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", player, new Object[0]));
                    return;
                  }

                  content.append("Add Subclass:<br>The subclass of <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(classId, player)).append("</font> has been added.");
                  player.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
                } else {
                  html.setFile("villagemaster/SubClass_Fail.htm");
                }
                break;
              case 5:
                if (Config.OLY_ENABLED && (ParticipantPool.getInstance().isRegistred(player) || player.isOlyParticipant())) {
                  player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
                  return;
                }

                if (player.isInDuel()) {
                  player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", player, new Object[0]));
                  return;
                }

                player.setActiveSubClass(classId, true);
                content.append("Change Subclass:<br>Your active subclass is now a <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(player.getActiveClassId(), player)).append("</font>.");
                player.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS);
                break;
              case 6:
                content.append("Please choose a subclass to change to. If the one you are looking for is not here, please seek out the appropriate master for that class.<br><font color=\"LEVEL\">Warning!</font> All classes and skills for this class will be removed.<br><br>");
                subsAvailable = this.getAvailableSubClasses(player, false);
                if (subsAvailable.isEmpty()) {
                  player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", player, new Object[0]));
                  return;
                }

                var20 = subsAvailable.iterator();

                while(true) {
                  if (!var20.hasNext()) {
                    break label321;
                  }

                  PlayerClass subClass = (PlayerClass)var20.next();
                  content.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_Subclass 7 ").append(classId).append(" ").append(subClass.ordinal()).append("\">").append(HtmlUtils.htmlClassName(subClass.ordinal(), player)).append("</a><br>");
                }
              case 7:
                if (Config.OLY_ENABLED && (ParticipantPool.getInstance().isRegistred(player) || player.isOlyParticipant())) {
                  player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
                  return;
                }

                if (!player.modifySubClass(classId, newClassId)) {
                  player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", player, new Object[0]));
                  return;
                }

                content.append("Change Subclass:<br>Your subclass has been changed to <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(newClassId, player)).append("</font>.");
                player.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
            }

            content.append("</body></html>");
            if (content.length() > 26) {
              html.setHtml(content.toString());
            }

            player.sendPacket(html);
          } else {
            super.onBypassFeedback(player, command);
          }
        }
      }

    }
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    return "villagemaster/" + pom + ".htm";
  }

  private void createClan(NpcInstance npc, Player player, String clanName) {
    if (player.getLevel() < Config.CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE) {
      player.sendPacket(Msg.YOU_ARE_NOT_QUALIFIED_TO_CREATE_A_CLAN);
    } else if (player.getClanId() != 0) {
      player.sendPacket(Msg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
    } else if (!player.canCreateClan()) {
      player.sendPacket(Msg.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
    } else if (clanName.length() > 16) {
      player.sendPacket(Msg.CLAN_NAMES_LENGTH_IS_INCORRECT);
    } else if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE)) {
      player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
    } else {
      Clan clan = ClanTable.getInstance().createClan(player, clanName);
      if (clan == null) {
        player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
      } else {
        player.sendPacket(clan.listAll());
        player.sendPacket(new IStaticPacket[]{new PledgeShowInfoUpdate(clan), Msg.CLAN_HAS_BEEN_CREATED});
        player.updatePledgeClass();
        player.broadcastCharInfo();
        npc.showChatWindow(player, "villagemaster/pl006.htm", new Object[0]);
      }
    }
  }

  private void cancelNewLeader(Player leader) {
    if (!leader.isClanLeader()) {
      this.showChatWindow(leader, "villagemaster/pl_err_master.htm", new Object[0]);
    } else if (leader.getEvent(SiegeEvent.class) != null) {
      leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", leader, new Object[0]));
    } else {
      Clan clan = leader.getClan();
      SubUnit mainUnit = clan.getSubUnit(0);
      UnitMember unitMember = mainUnit.getLeader();
      if (unitMember.getObjectId() == leader.getObjectId() && mainUnit.getNextLeaderObjectId() != 0 && mainUnit.getNextLeaderObjectId() != leader.getObjectId()) {
        setLeader(leader, clan, mainUnit, unitMember);
        this.showChatWindow(leader, "villagemaster/pl_cancel_success.htm", new Object[0]);
      } else {
        this.showChatWindow(leader, "villagemaster/pl_not_transfer.htm", new Object[0]);
      }
    }
  }

  private void setLeader(Player leader, String newLeader) {
    if (!leader.isClanLeader()) {
      this.showChatWindow(leader, "villagemaster/pl_err_master.htm", new Object[0]);
    } else if (leader.getClan().isPlacedForDisband()) {
      leader.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
    } else if (leader.getEvent(SiegeEvent.class) != null) {
      leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", leader, new Object[0]));
    } else {
      Clan clan = leader.getClan();
      SubUnit mainUnit = clan.getSubUnit(0);
      UnitMember member = mainUnit.getUnitMember(newLeader);
      if (member == null) {
        this.showChatWindow(leader, "villagemaster/clan-20.htm", new Object[0]);
      } else if (member.getLeaderOf() != -128) {
        leader.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.CannotAssignUnitLeader", leader, new Object[0]));
      } else if (mainUnit.getNextLeaderObjectId() != 0 && mainUnit.getNextLeaderObjectId() != leader.getObjectId()) {
        this.showChatWindow(leader, "villagemaster/pl_transfer_already.htm", new Object[0]);
      } else {
        setLeader(leader, clan, mainUnit, member);
        this.showChatWindow(leader, "villagemaster/pl_transfer_success.htm", new Object[0]);
      }
    }
  }

  public static void setLeader(Player player, Clan clan, SubUnit unit, UnitMember newLeader) {
    player.sendMessage((new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ClanLeaderWillBeChangedFromS1ToS2", player, new Object[0])).addString(clan.getLeaderName()).addString(newLeader.getName()));
    if (Config.CLAN_LEADER_CHANGE_METHOD) {
      if (clan.getLevel() >= 4) {
        Player newLeaderPlayer;
        if (clan.getLeader() != null) {
          newLeaderPlayer = clan.getLeader().getPlayer();
          if (newLeaderPlayer != null) {
            SiegeUtils.removeSiegeSkills(newLeaderPlayer);
          }
        }

        newLeaderPlayer = newLeader.getPlayer();
        if (newLeaderPlayer != null) {
          SiegeUtils.addSiegeSkills(newLeaderPlayer);
        }
      }

      synchronized(clan) {
        unit.setLeader(newLeader, true);
      }

      clan.broadcastClanStatus(true, true, false);
    } else {
      unit.updateDbLeader(newLeader);
      clan.broadcastClanStatus(true, true, false);
    }

  }

  private void createSubPledge(Player player, String clanName, int pledgeType, int minClanLvl, String leaderName) {
    UnitMember subLeader = null;
    Clan clan = player.getClan();
    if (clan != null && player.isClanLeader()) {
      if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE)) {
        player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
      } else {
        Collection<SubUnit> subPledge = clan.getAllSubUnits();
        Iterator var9 = subPledge.iterator();

        SubUnit element;
        do {
          if (!var9.hasNext()) {
            if (ClanTable.getInstance().getClanByName(clanName) != null) {
              player.sendPacket(Msg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
              return;
            }

            if (clan.getLevel() < minClanLvl) {
              player.sendPacket(Msg.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
              return;
            }

            SubUnit unit = clan.getSubUnit(0);
            if (pledgeType != -1) {
              subLeader = unit.getUnitMember(leaderName);
              if (subLeader == null) {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player, new Object[0]));
                return;
              }

              if (subLeader.getLeaderOf() != -128) {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", player, new Object[0]));
                return;
              }
            }

            pledgeType = clan.createSubPledge(player, pledgeType, subLeader, clanName);
            if (pledgeType == -128) {
              return;
            }

            clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeReceiveSubPledgeCreated(clan.getSubUnit(pledgeType))});
            SystemMessage sm;
            if (pledgeType == -1) {
              sm = new SystemMessage(1741);
              sm.addString(player.getClan().getName());
            } else if (pledgeType >= 1001) {
              sm = new SystemMessage(1794);
              sm.addString(player.getClan().getName());
            } else if (pledgeType >= 100) {
              sm = new SystemMessage(1795);
              sm.addString(player.getClan().getName());
            } else {
              sm = Msg.CLAN_HAS_BEEN_CREATED;
            }

            player.sendPacket(sm);
            if (subLeader != null) {
              clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeShowMemberListUpdate(subLeader)});
              if (subLeader.isOnline()) {
                subLeader.getPlayer().updatePledgeClass();
                subLeader.getPlayer().broadcastCharInfo();
              }
            }

            return;
          }

          element = (SubUnit)var9.next();
        } while(!element.getName().equals(clanName));

        player.sendPacket(Msg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
      }
    } else {
      player.sendPacket(Msg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
    }
  }

  private void assignSubPledgeLeader(Player player, String clanName, String leaderName) {
    Clan clan = player.getClan();
    if (clan == null) {
      player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ClanDoesntExist", player, new Object[0]));
    } else if (!player.isClanLeader()) {
      player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
    } else {
      SubUnit targetUnit = null;
      Iterator var6 = clan.getAllSubUnits().iterator();

      while(var6.hasNext()) {
        SubUnit unit = (SubUnit)var6.next();
        if (unit.getType() != 0 && unit.getType() != -1 && unit.getName().equalsIgnoreCase(clanName)) {
          targetUnit = unit;
        }
      }

      if (targetUnit == null) {
        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubUnitNotFound", player, new Object[0]));
      } else {
        SubUnit mainUnit = clan.getSubUnit(0);
        UnitMember subLeader = mainUnit.getUnitMember(leaderName);
        if (subLeader == null) {
          player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player, new Object[0]));
        } else if (subLeader.getObjectId() == mainUnit.getNextLeaderObjectId()) {
          player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player, new Object[0]));
        } else if (subLeader.getLeaderOf() != -128) {
          player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", player, new Object[0]));
        } else {
          targetUnit.setLeader(subLeader, true);
          clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeReceiveSubPledgeCreated(targetUnit)});
          clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeShowMemberListUpdate(subLeader)});
          if (subLeader.isOnline()) {
            subLeader.getPlayer().updatePledgeClass();
            subLeader.getPlayer().broadcastCharInfo();
          }

          player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NewSubUnitLeaderHasBeenAssigned", player, new Object[0]));
        }
      }
    }
  }

  private static boolean checkPlayerForClanLeader(NpcInstance npc, Player player) {
    if (player.getClan() == null) {
      npc.showChatWindow(player, "villagemaster/pl_no_pledgeman.htm", new Object[0]);
      return false;
    } else if (!player.isClanLeader()) {
      npc.showChatWindow(player, "villagemaster/pl_err_master.htm", new Object[0]);
      return false;
    } else {
      return true;
    }
  }

  private static void dissolveClan(NpcInstance npc, Player player) {
    if (player != null && player.getClan() != null) {
      Clan clan = player.getClan();
      if (!player.isClanLeader()) {
        player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
      } else if (clan.isPlacedForDisband()) {
        player.sendPacket(Msg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
      } else if (!clan.canDisband()) {
        player.sendPacket(Msg.YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION);
      } else if (clan.getAllyId() != 0) {
        player.sendPacket(Msg.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
      } else if (clan.isAtWar() > 0) {
        player.sendPacket(Msg.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
      } else if (clan.getCastle() == 0 && clan.getHasHideout() == 0) {
        Iterator var3 = ResidenceHolder.getInstance().getResidences().iterator();

        Residence r;
        do {
          if (!var3.hasNext()) {
            clan.placeForDisband();
            clan.broadcastClanStatus(true, true, false);
            npc.showChatWindow(player, "villagemaster/pl009.htm", new Object[0]);
            return;
          }

          r = (Residence)var3.next();
        } while(r.getSiegeEvent().getSiegeClan("attackers", clan) == null && r.getSiegeEvent().getSiegeClan("defenders", clan) == null && r.getSiegeEvent().getSiegeClan("defenders_waiting", clan) == null);

        player.sendPacket(SystemMsg.UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE);
      } else {
        player.sendPacket(Msg.UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS);
      }
    }
  }

  private static void restoreClan(VillageMasterInstance npc, Player player) {
    if (checkPlayerForClanLeader(npc, player)) {
      Clan clan = player.getClan();
      if (!clan.isPlacedForDisband()) {
        player.sendPacket(SystemMsg.THERE_ARE_NO_REQUESTS_TO_DISPERSE);
      } else {
        clan.unPlaceDisband();
        clan.broadcastClanStatus(true, true, false);
        npc.showChatWindow(player, "villagemaster/pl012.htm", new Object[0]);
      }
    }
  }

  private void levelUpClan(Player player) {
    Clan clan = player.getClan();
    if (clan != null) {
      if (!player.isClanLeader()) {
        player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
      } else if (player.getClan().isPlacedForDisband()) {
        player.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
      } else {
        boolean increaseClanLevel = false;
        switch(clan.getLevel()) {
          case 0:
            if (player.getSp() >= (long)Config.CLAN_FIRST_LEVEL_SP && player.getAdena() >= (long)Config.CLAN_FIRST_LEVEL_ADENA) {
              player.setSp(player.getSp() - (long)Config.CLAN_FIRST_LEVEL_SP);
              player.reduceAdena((long)Config.CLAN_FIRST_LEVEL_ADENA, true);
              increaseClanLevel = true;
            }
            break;
          case 1:
            if (player.getSp() >= (long)Config.CLAN_SECOND_LEVEL_SP && player.getAdena() >= (long)Config.CLAN_SECOND_LEVEL_ADENA) {
              player.setSp(player.getSp() - (long)Config.CLAN_SECOND_LEVEL_SP);
              player.reduceAdena((long)Config.CLAN_SECOND_LEVEL_ADENA, true);
              increaseClanLevel = true;
            }
            break;
          case 2:
            if (player.getSp() >= (long)Config.CLAN_THIRD_LEVEL_SP && player.getInventory().destroyItemByItemId(1419, 1L)) {
              player.setSp(player.getSp() - (long)Config.CLAN_THIRD_LEVEL_SP);
              increaseClanLevel = true;
            }
            break;
          case 3:
            if (player.getSp() >= (long)Config.CLAN_FOUR_LEVEL_SP && player.getInventory().destroyItemByItemId(3874, 1L)) {
              player.setSp(player.getSp() - (long)Config.CLAN_FOUR_LEVEL_SP);
              increaseClanLevel = true;
            }
            break;
          case 4:
            if (player.getSp() >= (long)Config.CLAN_FIVE_LEVEL_SP && player.getInventory().destroyItemByItemId(3870, 1L)) {
              player.setSp(player.getSp() - (long)Config.CLAN_FIVE_LEVEL_SP);
              increaseClanLevel = true;
            }
            break;
          case 5:
            if (clan.getReputationScore() >= Config.CLAN_SIX_LEVEL_CLAN_REPUTATION && clan.getAllSize() >= Config.CLAN_SIX_LEVEL_CLAN_MEMBER_COUNT) {
              clan.incReputation(-Config.CLAN_SIX_LEVEL_CLAN_REPUTATION, false, "LvlUpClan");
              increaseClanLevel = true;
            }
            break;
          case 6:
            if (clan.getReputationScore() >= Config.CLAN_SEVEN_LEVEL_CLAN_REPUTATION && clan.getAllSize() >= Config.CLAN_SEVEN_LEVEL_CLAN_MEMBER_COUNT) {
              clan.incReputation(-Config.CLAN_SEVEN_LEVEL_CLAN_REPUTATION, false, "LvlUpClan");
              increaseClanLevel = true;
            }
            break;
          case 7:
            if (clan.getReputationScore() >= Config.CLAN_EIGHT_LEVEL_CLAN_REPUTATION && clan.getAllSize() >= Config.CLAN_EIGHT_LEVEL_CLAN_MEMBER_COUNT) {
              clan.incReputation(-Config.CLAN_EIGHT_LEVEL_CLAN_REPUTATION, false, "LvlUpClan");
              increaseClanLevel = true;
            }
        }

        if (increaseClanLevel) {
          clan.setLevel(clan.getLevel() + 1);
          clan.updateClanInDB();
          player.broadcastCharInfo();
          this.doCast(SkillTable.getInstance().getInfo(5103, 1), player, true);
          if (clan.getLevel() >= 4) {
            SiegeUtils.addSiegeSkills(player);
          }

          if (clan.getLevel() == 5) {
            player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
          }

          PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
          PledgeStatusChanged ps = new PledgeStatusChanged(clan);
          Iterator var6 = clan.iterator();

          while(var6.hasNext()) {
            UnitMember mbr = (UnitMember)var6.next();
            if (mbr.isOnline()) {
              mbr.getPlayer().updatePledgeClass();
              mbr.getPlayer().sendPacket(new IStaticPacket[]{Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps});
              mbr.getPlayer().broadcastCharInfo();
            }
          }
        } else {
          player.sendPacket(Msg.CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL);
        }

      }
    }
  }

  private void createAlly(Player player, String allyName) {
    if (!player.isClanLeader()) {
      player.sendPacket(Msg.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
    } else if (player.getClan().getAllyId() != 0) {
      player.sendPacket(Msg.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
    } else if (player.getClan().isPlacedForDisband()) {
      player.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
    } else if (allyName.length() > 16) {
      player.sendPacket(Msg.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
    } else if (!Util.isMatchingRegexp(allyName, Config.ALLY_NAME_TEMPLATE)) {
      player.sendPacket(Msg.INCORRECT_ALLIANCE_NAME);
    } else if (player.getClan().getLevel() < 5) {
      player.sendPacket(Msg.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
    } else if (ClanTable.getInstance().getAllyByName(allyName) != null) {
      player.sendPacket(Msg.THIS_ALLIANCE_NAME_ALREADY_EXISTS);
    } else if (!player.getClan().canCreateAlly()) {
      player.sendPacket(Msg.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_AFTER_DISSOLUTION);
    } else {
      Alliance alliance = ClanTable.getInstance().createAlliance(player, allyName);
      if (alliance != null) {
        player.broadcastCharInfo();
        player.sendMessage((new CustomMessage("L2VillageMasterInstance.AllianceCreated", player, new Object[0])).addString(allyName));
      }
    }
  }

  private void dissolveAlly(Player player) {
    if (player != null && player.getAlliance() != null) {
      if (!player.isAllyLeader()) {
        player.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
      } else if (player.getAlliance().getMembersCount() > 1) {
        player.sendPacket(Msg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
      } else {
        ClanTable.getInstance().dissolveAlly(player);
      }
    }
  }

  private Set<PlayerClass> getAvailableSubClasses(Player player, boolean isNew) {
    int charClassId = player.getBaseClassId();
    Race npcRace = this.getVillageMasterRace();
    ClassType npcTeachType = this.getVillageMasterTeachType();
    PlayerClass currClass = PlayerClass.values()[charClassId];
    Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
    if (availSubs == null) {
      return Collections.emptySet();
    } else {
      availSubs.remove(currClass);
      Iterator var8 = availSubs.iterator();

      while(true) {
        while(true) {
          PlayerClass availSub;
          label52:
          do {
            if (!var8.hasNext()) {
              return availSubs;
            }

            availSub = (PlayerClass)var8.next();
            Iterator var10 = player.getSubClasses().values().iterator();

            while(true) {
              while(true) {
                if (!var10.hasNext()) {
                  continue label52;
                }

                SubClass subClass = (SubClass)var10.next();
                if (availSub.ordinal() == subClass.getClassId()) {
                  availSubs.remove(availSub);
                } else {
                  ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
                  if (parent != null && parent.getId() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                  } else {
                    ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
                    if (subParent != null && subParent.getId() == availSub.ordinal()) {
                      availSubs.remove(availSub);
                    }
                  }
                }
              }
            }
          } while(Config.ALTSUBCLASS_LIST_ALL);

          if (!availSub.isOfRace(Race.human) && !availSub.isOfRace(Race.elf)) {
            if (!availSub.isOfRace(npcRace)) {
              availSubs.remove(availSub);
            }
          } else if (!availSub.isOfType(npcTeachType) || npcRace != Race.human) {
            availSubs.remove(availSub);
          }
        }
      }
    }
  }

  private Race getVillageMasterRace() {
    switch(this.getTemplate().getRace()) {
      case 14:
        return Race.human;
      case 15:
        return Race.elf;
      case 16:
        return Race.darkelf;
      case 17:
        return Race.orc;
      case 18:
        return Race.dwarf;
      default:
        return null;
    }
  }

  private ClassType getVillageMasterTeachType() {
    switch(this.getNpcId()) {
      case 30017:
      case 30019:
      case 30033:
      case 30034:
      case 30035:
      case 30068:
      case 30069:
      case 30110:
      case 30111:
      case 30112:
      case 30114:
      case 30115:
      case 30144:
      case 30145:
      case 30154:
      case 30158:
      case 30171:
      case 30174:
      case 30175:
      case 30176:
      case 30189:
      case 30190:
      case 30194:
      case 30293:
      case 30330:
      case 30344:
      case 30375:
      case 30377:
      case 30461:
      case 30464:
      case 30473:
      case 30476:
      case 30609:
      case 30610:
      case 30612:
      case 30634:
      case 30635:
      case 30637:
      case 30638:
      case 30639:
      case 30640:
      case 30666:
      case 30680:
      case 30694:
      case 30695:
      case 30696:
      case 30701:
      case 30715:
      case 30717:
      case 30720:
      case 30721:
      case 30854:
      case 30855:
      case 30861:
      case 30864:
      case 30907:
      case 30908:
      case 30912:
      case 30915:
      case 30988:
      case 31001:
      case 31046:
      case 31047:
      case 31048:
      case 31049:
      case 31050:
      case 31051:
      case 31052:
      case 31053:
      case 31281:
      case 31282:
      case 31283:
      case 31285:
      case 31326:
      case 31330:
      case 31331:
      case 31332:
      case 31333:
      case 31337:
      case 31339:
      case 31359:
      case 31415:
      case 31425:
      case 31426:
      case 31427:
      case 31430:
      case 31431:
      case 31605:
      case 31608:
      case 31614:
      case 31620:
      case 31643:
      case 31740:
      case 31755:
      case 31953:
      case 31969:
      case 31970:
      case 31971:
      case 31972:
      case 31976:
      case 31977:
      case 31996:
      case 32056:
      case 32074:
      case 32082:
      case 32083:
      case 32084:
      case 32085:
      case 32086:
      case 32087:
      case 32088:
      case 32089:
      case 32098:
        return ClassType.Mystic;
      case 30022:
      case 30030:
      case 30031:
      case 30032:
      case 30036:
      case 30037:
      case 30067:
      case 30070:
      case 30116:
      case 30117:
      case 30118:
      case 30120:
      case 30129:
      case 30130:
      case 30131:
      case 30132:
      case 30133:
      case 30141:
      case 30188:
      case 30191:
      case 30289:
      case 30305:
      case 30358:
      case 30359:
      case 30404:
      case 30419:
      case 30421:
      case 30422:
      case 30424:
      case 30502:
      case 30507:
      case 30510:
      case 30515:
      case 30537:
      case 30538:
      case 30571:
      case 30572:
      case 30575:
      case 30598:
      case 30614:
      case 30657:
      case 30665:
      case 30682:
      case 30706:
      case 30857:
      case 30858:
      case 30859:
      case 30905:
      case 30906:
      case 30927:
      case 30981:
      case 31279:
      case 31290:
      case 31291:
      case 31328:
      case 31335:
      case 31336:
      case 31348:
      case 31349:
      case 31350:
      case 31424:
      case 31428:
      case 31429:
      case 31452:
      case 31454:
      case 31524:
      case 31581:
      case 31591:
      case 31602:
      case 31613:
      case 31644:
      case 31856:
      case 31968:
      case 31973:
      case 31979:
      case 31980:
      case 32008:
      case 32010:
      case 32019:
      case 32095:
        return ClassType.Priest;
      default:
        return ClassType.Fighter;
    }
  }
}
