//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.Config.RateBonusInfo;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.World;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.PlayerClass;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExPCCafePointInfo;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminEditChar implements IAdminCommandHandler {
  public AdminEditChar() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminEditChar.Commands command = (AdminEditChar.Commands)comm;
    RateBonusInfo[] player;
    String msgUsage;
    int count;
    Player player;
    Player player;
    GameObject target;
    if (activeChar.getPlayerAccess().CanRename) {
      if (fullString.startsWith("admin_settitle")) {
        try {
          msgUsage = fullString.substring(15);
          target = activeChar.getTarget();
          player = null;
          if (target == null) {
            return false;
          }

          if (target.isPlayer()) {
            player = (Player)target;
            player.setTitle(msgUsage);
            player.sendMessage("Your title has been changed by a GM");
            player.sendChanges();
          } else if (target.isNpc()) {
            ((NpcInstance)target).setTitle(msgUsage);
            target.decayMe();
            target.spawnMe();
          }

          return true;
        } catch (StringIndexOutOfBoundsException var12) {
          activeChar.sendMessage("You need to specify the new title.");
          return false;
        }
      }

      if (fullString.startsWith("admin_setclass")) {
        try {
          msgUsage = fullString.substring(15);
          count = Integer.parseInt(msgUsage.trim());
          GameObject target = activeChar.getTarget();
          if (target == null || !((GameObject)target).isPlayer()) {
            target = activeChar;
          }

          if (count > 118) {
            activeChar.sendMessage("There are no classes over 118 id.");
            return false;
          }

          player = ((GameObject)target).getPlayer();
          player.setClassId(count, false, false);
          player.sendMessage("Your class has been changed by a GM");
          player.broadcastCharInfo();
          return true;
        } catch (StringIndexOutOfBoundsException var24) {
          activeChar.sendMessage("You need to specify the new class id.");
          return false;
        }
      }

      if (fullString.startsWith("admin_setname")) {
        try {
          msgUsage = fullString.substring(14);
          target = activeChar.getTarget();
          if (target != null && target.isPlayer()) {
            player = (Player)target;
            if (mysql.simple_get_int("count(*)", "characters", "`char_name` like '" + msgUsage + "'") > 0) {
              activeChar.sendMessage("Name already exist.");
              return false;
            }

            Log.add("Character " + player.getName() + " renamed to " + msgUsage + " by GM " + activeChar.getName(), "renames");
            player.reName(msgUsage);
            player.sendMessage("Your name has been changed by a GM");
            return true;
          }

          return false;
        } catch (StringIndexOutOfBoundsException var13) {
          activeChar.sendMessage("You need to specify the new name.");
          return false;
        }
      }
    }

    if (!activeChar.getPlayerAccess().CanEditChar && !activeChar.getPlayerAccess().CanViewChar) {
      return false;
    } else {
      if (fullString.equals("admin_current_player")) {
        showCharacterList(activeChar, (Player)null);
      } else {
        Player target;
        if (fullString.startsWith("admin_character_list")) {
          try {
            msgUsage = fullString.substring(21);
            target = GameObjectsStorage.getPlayer(msgUsage);
            showCharacterList(activeChar, target);
          } catch (StringIndexOutOfBoundsException var23) {
          }
        } else {
          String[] vals;
          if (fullString.startsWith("admin_show_characters_by_ip")) {
            try {
              msgUsage = fullString.substring(28).trim();
              vals = msgUsage.split("\\s+");
              this.listCharactersByIp(activeChar, vals[0], vals.length > 1 ? Integer.parseInt(vals[1]) : 0);
            } catch (StringIndexOutOfBoundsException var22) {
            }
          } else if (fullString.startsWith("admin_show_characters")) {
            try {
              msgUsage = fullString.substring(22);
              count = Integer.parseInt(msgUsage);
              this.listCharacters(activeChar, count);
            } catch (StringIndexOutOfBoundsException var21) {
            }
          } else if (fullString.startsWith("admin_find_character")) {
            try {
              msgUsage = fullString.substring(21);
              this.findCharacter(activeChar, msgUsage);
            } catch (StringIndexOutOfBoundsException var20) {
              activeChar.sendMessage("You didnt enter a character name to find.");
              this.listCharacters(activeChar, 0);
            }
          } else {
            if (!activeChar.getPlayerAccess().CanEditChar) {
              return false;
            }

            if (fullString.equals("admin_edit_character")) {
              this.editCharacter(activeChar);
            } else if (fullString.equals("admin_character_actions")) {
              this.showCharacterActions(activeChar);
            } else if (fullString.equals("admin_nokarma")) {
              this.setTargetKarma(activeChar, 0);
            } else if (fullString.startsWith("admin_setkarma")) {
              try {
                msgUsage = fullString.substring(15);
                count = Integer.parseInt(msgUsage);
                this.setTargetKarma(activeChar, count);
              } catch (StringIndexOutOfBoundsException var19) {
                activeChar.sendMessage("Please specify new karma value.");
              }
            } else if (fullString.startsWith("admin_save_modifications")) {
              try {
                msgUsage = fullString.substring(24);
                this.adminModifyCharacter(activeChar, msgUsage);
              } catch (StringIndexOutOfBoundsException var18) {
                activeChar.sendMessage("Error while modifying character.");
                this.listCharacters(activeChar, 0);
              }
            } else {
              RateBonusInfo rateBonusInfo;
              GameObject target;
              if (fullString.equals("admin_rec")) {
                target = activeChar.getTarget();
                rateBonusInfo = null;
                if (target == null || !target.isPlayer()) {
                  return false;
                }

                target = (Player)target;
                target.setGivableRec(target.getGivableRec() + 1);
                target.sendMessage("You have been recommended by a GM");
                target.broadcastCharInfo();
              } else if (fullString.startsWith("admin_rec")) {
                try {
                  msgUsage = fullString.substring(10);
                  count = Integer.parseInt(msgUsage);
                  GameObject target = activeChar.getTarget();
                  player = null;
                  if (target == null || !target.isPlayer()) {
                    return false;
                  }

                  player = (Player)target;
                  player.setGivableRec(player.getGivableRec() + count);
                  player.sendMessage("You have been recommended by a GM");
                  player.broadcastCharInfo();
                } catch (NumberFormatException var25) {
                  activeChar.sendMessage("Command format is //rec <number>");
                }
              } else if (fullString.startsWith("admin_sethero")) {
                target = activeChar.getTarget();
                if (wordList.length > 1 && wordList[1] != null) {
                  target = GameObjectsStorage.getPlayer(wordList[1]);
                  if (target == null) {
                    activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
                    return false;
                  }
                } else {
                  if (target == null || !target.isPlayer()) {
                    activeChar.sendMessage("You must specify the name or target character.");
                    return false;
                  }

                  target = (Player)target;
                }

                if (target.isHero()) {
                  target.setHero(false);
                  target.updatePledgeClass();
                  target.removeSkill(SkillTable.getInstance().getInfo(395, 1));
                  target.removeSkill(SkillTable.getInstance().getInfo(396, 1));
                  target.removeSkill(SkillTable.getInstance().getInfo(1374, 1));
                  target.removeSkill(SkillTable.getInstance().getInfo(1375, 1));
                  target.removeSkill(SkillTable.getInstance().getInfo(1376, 1));
                } else {
                  target.setHero(true);
                  target.updatePledgeClass();
                  target.addSkill(SkillTable.getInstance().getInfo(395, 1));
                  target.addSkill(SkillTable.getInstance().getInfo(396, 1));
                  target.addSkill(SkillTable.getInstance().getInfo(1374, 1));
                  target.addSkill(SkillTable.getInstance().getInfo(1375, 1));
                  target.addSkill(SkillTable.getInstance().getInfo(1376, 1));
                }

                target.sendPacket(new SkillList(target));
                target.sendMessage("Admin has changed your hero status.");
                target.broadcastUserInfo(true);
              } else if (fullString.startsWith("admin_setnoble")) {
                target = activeChar.getTarget();
                if (wordList.length > 1 && wordList[1] != null) {
                  target = GameObjectsStorage.getPlayer(wordList[1]);
                  if (target == null) {
                    activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
                    return false;
                  }
                } else {
                  if (target == null || !target.isPlayer()) {
                    activeChar.sendMessage("You must specify the name or target character.");
                    return false;
                  }

                  target = (Player)target;
                }

                if (target.isNoble()) {
                  target.setNoble(false);
                  NoblesController.getInstance().addNoble(target);
                  target.sendMessage("Admin changed your noble status, now you are not nobless.");
                } else {
                  target.setNoble(true);
                  NoblesController.getInstance().addNoble(target);
                  target.sendMessage("Admin changed your noble status, now you are Nobless.");
                }

                target.updatePledgeClass();
                target.updateNobleSkills();
                target.sendPacket(new SkillList(target));
                target.broadcastUserInfo(true);
              } else if (fullString.startsWith("admin_setsex")) {
                target = activeChar.getTarget();
                rateBonusInfo = null;
                if (target == null || !target.isPlayer()) {
                  return false;
                }

                target = (Player)target;
                target.changeSex();
                target.sendMessage("Your gender has been changed by a GM");
                target.broadcastUserInfo(true);
              } else if (fullString.startsWith("admin_setcolor")) {
                try {
                  msgUsage = fullString.substring(15);
                  target = activeChar.getTarget();
                  player = null;
                  if (target == null || !target.isPlayer()) {
                    return false;
                  }

                  player = (Player)target;
                  player.setNameColor(Integer.decode("0x" + msgUsage));
                  player.sendMessage("Your name color has been changed by a GM");
                  player.broadcastUserInfo(true);
                } catch (StringIndexOutOfBoundsException var26) {
                  activeChar.sendMessage("You need to specify the new color.");
                }
              } else if (fullString.startsWith("admin_add_exp_sp_to_character")) {
                this.addExpSp(activeChar);
              } else {
                int sp;
                if (fullString.startsWith("admin_add_exp_sp")) {
                  try {
                    msgUsage = fullString.substring(16).trim();
                    vals = msgUsage.split(" ");
                    long exp = NumberUtils.toLong(vals[0], 0L);
                    sp = vals.length > 1 ? NumberUtils.toInt(vals[1], 0) : 0;
                    this.adminAddExpSp(activeChar, exp, sp);
                  } catch (Exception var17) {
                    activeChar.sendMessage("Usage: //add_exp_sp <exp> <sp>");
                  }
                } else {
                  boolean help;
                  if (fullString.startsWith("admin_trans")) {
                    StringTokenizer st = new StringTokenizer(fullString);
                    if (st.countTokens() > 1) {
                      st.nextToken();
                      help = false;

                      try {
                        count = Integer.parseInt(st.nextToken());
                      } catch (Exception var16) {
                        activeChar.sendMessage("Specify a valid integer value.");
                        return false;
                      }

                      if (count != 0 && activeChar.getTransformation() != 0) {
                        activeChar.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return false;
                      }

                      activeChar.setTransformation(count);
                      activeChar.sendMessage("Transforming...");
                    } else {
                      activeChar.sendMessage("Usage: //trans <ID>");
                    }
                  } else if (fullString.startsWith("admin_setsubclass")) {
                    target = activeChar.getTarget();
                    if (target == null || !target.isPlayer()) {
                      activeChar.sendPacket(Msg.SELECT_TARGET);
                      return false;
                    }

                    target = (Player)target;
                    StringTokenizer st = new StringTokenizer(fullString);
                    if (st.countTokens() > 1) {
                      st.nextToken();
                      int classId = Short.parseShort(st.nextToken());
                      if (!target.addSubClass(classId, true)) {
                        activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", activeChar, new Object[0]));
                        return false;
                      }

                      target.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);
                    } else {
                      this.setSubclass(activeChar, target);
                    }
                  } else if (fullString.startsWith("admin_setbday")) {
                    msgUsage = "Usage: //setbday YYYY-MM-DD";
                    String date = fullString.substring(14);
                    if (date.length() != 10 || !Util.isMatchingRegexp(date, "[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                      activeChar.sendMessage(msgUsage);
                      return false;
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                      dateFormat.parse(date);
                    } catch (ParseException var15) {
                      activeChar.sendMessage(msgUsage);
                    }

                    if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                      activeChar.sendMessage("Please select a character.");
                      return false;
                    }

                    if (!mysql.set("update characters set createtime = UNIX_TIMESTAMP('" + date + "') where obj_Id = " + activeChar.getTarget().getObjectId())) {
                      activeChar.sendMessage(msgUsage);
                      return false;
                    }

                    activeChar.sendMessage("New Birthday for " + activeChar.getTarget().getName() + ": " + date);
                    activeChar.getTarget().getPlayer().sendMessage("Admin changed your birthday to: " + date);
                  } else {
                    int id;
                    if (fullString.startsWith("admin_give_item")) {
                      if (wordList.length < 3) {
                        activeChar.sendMessage("Usage: //give_item id count <target>");
                        return false;
                      }

                      id = Integer.parseInt(wordList[1]);
                      count = Integer.parseInt(wordList[2]);
                      if (id < 1 || count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                        activeChar.sendMessage("Usage: //give_item id count <target>");
                        return false;
                      }

                      ItemFunctions.addItem(activeChar.getTarget().getPlayer(), id, (long)count, true);
                    } else {
                      int count;
                      if (!fullString.startsWith("admin_set_pa")) {
                        if (fullString.startsWith("admin_remove_item")) {
                          Player target = null;
                          help = false;
                          if (wordList.length >= 3) {
                            int id = Integer.parseInt(wordList[1]);
                            count = Integer.parseInt(wordList[2]);
                            if (wordList.length > 3) {
                              target = World.getPlayer(wordList[3]);
                            }

                            if (target == null && activeChar.getTarget() != null) {
                              target = activeChar.getTarget().getPlayer();
                            }

                            if (target != null && id > 0 && count > 0) {
                              long haveCount = ItemFunctions.getItemCount(target, id);
                              if (haveCount < (long)count) {
                                help = true;
                                activeChar.sendMessage("Failed: '" + target.getName() + "' have only " + haveCount + " items.");
                              } else {
                                help = true;
                                activeChar.sendMessage("Removed " + ItemFunctions.removeItem(target, id, (long)count, true) + " from '" + target.getName() + "'");
                              }
                            }
                          }

                          if (!help) {
                            activeChar.sendMessage("Usage: //remove_item id count <target>");
                            return false;
                          }
                        } else if (fullString.startsWith("admin_add_bang")) {
                          if (!Config.ALT_PCBANG_POINTS_ENABLED) {
                            activeChar.sendMessage("Error! Pc Bang Points service disabled!");
                            return true;
                          }

                          if (wordList.length < 1) {
                            activeChar.sendMessage("Usage: //add_bang count <target>");
                            return false;
                          }

                          id = Integer.parseInt(wordList[1]);
                          if (id < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                            activeChar.sendMessage("Usage: //add_bang count <target>");
                            return false;
                          }

                          target = activeChar.getTarget().getPlayer();
                          target.addPcBangPoints(id, false);
                          activeChar.sendMessage("You have added " + id + " Pc Bang Points to " + target.getName());
                        } else if (fullString.startsWith("admin_set_bang")) {
                          if (!Config.ALT_PCBANG_POINTS_ENABLED) {
                            activeChar.sendMessage("Error! Pc Bang Points service disabled!");
                            return true;
                          }

                          if (wordList.length < 1) {
                            activeChar.sendMessage("Usage: //set_bang count <target>");
                            return false;
                          }

                          id = Integer.parseInt(wordList[1]);
                          if (id < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                            activeChar.sendMessage("Usage: //set_bang count <target>");
                            return false;
                          }

                          target = activeChar.getTarget().getPlayer();
                          target.setPcBangPoints(id);
                          target.sendMessage("Your Pc Bang Points count is now " + id);
                          target.sendPacket(new ExPCCafePointInfo(target, id, 1, 2, 12));
                          activeChar.sendMessage("You have set " + target.getName() + "'s Pc Bang Points to " + id);
                        }
                      } else {
                        if (!Config.SERVICES_RATE_ENABLED) {
                          activeChar.sendMessage("Service Premium Account is Disabled");
                          return false;
                        }

                        if (wordList.length < 2) {
                          activeChar.sendMessage("USAGE: //set_pa <id>");
                          return false;
                        }

                        try {
                          id = Integer.parseInt(wordList[1]);
                        } catch (Exception var14) {
                          activeChar.sendMessage("You need select target");
                          return false;
                        }

                        rateBonusInfo = null;
                        player = Config.SERVICES_RATE_BONUS_INFO;
                        count = player.length;

                        for(sp = 0; sp < count; ++sp) {
                          RateBonusInfo rbi = player[sp];
                          if (rbi.id == id) {
                            rateBonusInfo = rbi;
                          }
                        }

                        if (rateBonusInfo == null) {
                          activeChar.sendMessage("Undefined bonus!");
                          return false;
                        }

                        if (id < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                          activeChar.sendMessage("Please select a character.");
                          return false;
                        }

                        AccountBonusDAO.getInstance().store(activeChar.getAccountName(), rateBonusInfo.makeBonus());
                        activeChar.stopBonusTask();
                        activeChar.startBonusTask();
                        if (activeChar.getParty() != null) {
                          activeChar.getParty().recalculatePartyData();
                        }

                        activeChar.broadcastUserInfo(true);
                        Log.add("Admin Command PA Bonus added " + activeChar.getName() + "|" + activeChar.getObjectId() + "|rate bonus|" + rateBonusInfo.id + "|" + rateBonusInfo.bonusTimeSeconds + "|", "services");
                        activeChar.sendMessage("SYS: Premium Account added for " + activeChar.getName() + " id bonus is " + rateBonusInfo.id);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminEditChar.Commands.values();
  }

  private void listCharactersByIp(Player activeChar, String IP, int page) {
    List<Player> allPlayers = GameObjectsStorage.getAllPlayers();
    List<Player> players = new LinkedList();
    Iterator var6 = allPlayers.iterator();

    while(var6.hasNext()) {
      Player player = (Player)var6.next();
      if (player != null && !player.isInOfflineMode() && player.isConnected() && player.getNetConnection() != null) {
        String playerIp = player.getNetConnection().getIpAddr();
        if (playerIp != null && IP.trim().equals(playerIp)) {
          players.add(player);
        }
      }
    }

    int MaxCharactersPerPage = 20;
    int MaxPages = players.size() / MaxCharactersPerPage;
    if (players.size() > MaxCharactersPerPage * MaxPages) {
      ++MaxPages;
    }

    if (page > MaxPages) {
      page = MaxPages;
    }

    int CharactersStart = MaxCharactersPerPage * page;
    int CharactersEnd = players.size();
    if (CharactersEnd - CharactersStart > MaxCharactersPerPage) {
      CharactersEnd = CharactersStart + MaxCharactersPerPage;
    }

    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    StringBuilder replyMSG = new StringBuilder("<html><body>");
    replyMSG.append("<table width=260><tr>");
    replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
    replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr></table>");
    replyMSG.append("<br><br>");
    replyMSG.append("<center>Characters with IP \"").append(IP).append("\"</center>");

    int i;
    for(i = 0; i < MaxPages; ++i) {
      int pagenr = i + 1;
      replyMSG.append("<center><a action=\"bypass -h admin_show_characters_by_ip " + IP + " " + i + "\">Page " + pagenr + "</a></center>");
    }

    replyMSG.append("<br>");
    replyMSG.append("<table width=270>");
    replyMSG.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");

    for(i = CharactersStart; i < CharactersEnd; ++i) {
      Player p = (Player)players.get(i);
      replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + p.getName() + "\">" + p.getName() + "</a></td><td width=110>" + p.getTemplate().className + "</td><td width=40>" + p.getLevel() + "</td></tr>");
    }

    replyMSG.append("</table>");
    replyMSG.append("</body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  private void listCharacters(Player activeChar, int page) {
    List<Player> players = GameObjectsStorage.getAllPlayers();
    int MaxCharactersPerPage = 20;
    int MaxPages = players.size() / MaxCharactersPerPage;
    if (players.size() > MaxCharactersPerPage * MaxPages) {
      ++MaxPages;
    }

    if (page > MaxPages) {
      page = MaxPages;
    }

    int CharactersStart = MaxCharactersPerPage * page;
    int CharactersEnd = players.size();
    if (CharactersEnd - CharactersStart > MaxCharactersPerPage) {
      CharactersEnd = CharactersStart + MaxCharactersPerPage;
    }

    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    StringBuilder replyMSG = new StringBuilder("<html><body>");
    replyMSG.append("<table width=260><tr>");
    replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
    replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr></table>");
    replyMSG.append("<br><br>");
    replyMSG.append("<table width=270>");
    replyMSG.append("<tr><td width=270>You can find a character by writing his name and</td></tr>");
    replyMSG.append("<tr><td width=270>clicking Find bellow.<br></td></tr>");
    replyMSG.append("<tr><td width=270>Note: Names should be written case sensitive.</td></tr>");
    replyMSG.append("</table><br>");
    replyMSG.append("<center><table><tr><td>");
    replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
    replyMSG.append("</td></tr></table></center><br><br>");

    int i;
    for(i = 0; i < MaxPages; ++i) {
      int pagenr = i + 1;
      replyMSG.append("<center><a action=\"bypass -h admin_show_characters " + i + "\">Page " + pagenr + "</a></center>");
    }

    replyMSG.append("<br>");
    replyMSG.append("<table width=270>");
    replyMSG.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");

    for(i = CharactersStart; i < CharactersEnd; ++i) {
      Player p = (Player)players.get(i);
      replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + p.getName() + "\">" + p.getName() + "</a></td><td width=110>" + p.getTemplate().className + "</td><td width=40>" + p.getLevel() + "</td></tr>");
    }

    replyMSG.append("</table>");
    replyMSG.append("</body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  public static void showCharacterList(Player activeChar, Player player) {
    if (player == null) {
      GameObject target = activeChar.getTarget();
      if (target == null || !target.isPlayer()) {
        return;
      }

      player = (Player)target;
    } else {
      activeChar.setTarget(player);
    }

    String clanName = "No Clan";
    if (player.getClan() != null) {
      clanName = player.getClan().getName() + "/" + player.getClan().getLevel();
    }

    NumberFormat df = NumberFormat.getNumberInstance(Locale.ENGLISH);
    df.setMaximumFractionDigits(4);
    df.setMinimumFractionDigits(1);
    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    StringBuilder replyMSG = new StringBuilder("<html><body>");
    replyMSG.append("<table width=260><tr>");
    replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
    replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr></table><br1>");
    replyMSG.append("<table width=270>");
    replyMSG.append("<tr><td width=100>Account/IP:</td><td>" + player.getAccountName() + "/<a action=\"bypass -h admin_show_characters_by_ip " + player.getIP() + "\">" + player.getIP() + "</a></td></tr>");
    if (player.getNetConnection() != null && player.getNetConnection().getHwid() != null && !player.getNetConnection().getHwid().isEmpty()) {
      String hwid = player.getNetConnection().getHwid();
      replyMSG.append("<tr><td width=100>HWID:</td><td>" + hwid + "</td></tr>");
    }

    replyMSG.append("<tr><td width=100>Name/Level:</td><td>" + player.getName() + "/" + player.getLevel() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Class/Id:</td><td>" + player.getTemplate().className + "/" + player.getClassId().getId() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Clan/Level:</td><td>" + clanName + "</td></tr>");
    replyMSG.append("<tr><td width=100>Exp/Sp:</td><td>" + player.getExp() + "/" + player.getSp() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Cur/Max Hp:</td><td>" + (int)player.getCurrentHp() + "/" + player.getMaxHp() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Cur/Max Mp:</td><td>" + (int)player.getCurrentMp() + "/" + player.getMaxMp() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Cur/Max Load:</td><td>" + player.getCurrentLoad() + "/" + player.getMaxLoad() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Patk/Matk:</td><td>" + player.getPAtk((Creature)null) + "/" + player.getMAtk((Creature)null, (Skill)null) + "</td></tr>");
    replyMSG.append("<tr><td width=100>Pdef/Mdef:</td><td>" + player.getPDef((Creature)null) + "/" + player.getMDef((Creature)null, (Skill)null) + "</td></tr>");
    replyMSG.append("<tr><td width=100>PAtkSpd/MAtkSpd:</td><td>" + player.getPAtkSpd() + "/" + player.getMAtkSpd() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Acc/Evas:</td><td>" + player.getAccuracy() + "/" + player.getEvasionRate((Creature)null) + "</td></tr>");
    replyMSG.append("<tr><td width=100>Crit/MCrit:</td><td>" + player.getCriticalHit((Creature)null, (Skill)null) + "/" + df.format(player.getMagicCriticalRate((Creature)null, (Skill)null)) + "%</td></tr>");
    replyMSG.append("<tr><td width=100>Walk/Run:</td><td>" + player.getWalkSpeed() + "/" + player.getRunSpeed() + "</td></tr>");
    replyMSG.append("<tr><td width=100>PvP/PK:</td><td>" + player.getPvpKills() + "/" + player.getPkKills() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Coordinates:</td><td>" + player.getX() + "," + player.getY() + "," + player.getZ() + "</td></tr>");
    replyMSG.append("<tr><td width=100>AI:</td><td>" + player.getAI().getIntention() + "/" + player.getAI().getNextAction() + "</td></tr>");
    replyMSG.append("<tr><td width=100>Direction:</td><td>" + PositionUtils.getDirectionTo(player, activeChar) + "</td></tr>");
    replyMSG.append("</table><br1>");
    replyMSG.append("<table<tr>");
    replyMSG.append("<td><button value=\"Skills\" action=\"bypass -h admin_show_skills\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td><button value=\"Effects\" action=\"bypass -h admin_show_effects\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td><button value=\"Actions\" action=\"bypass -h admin_character_actions\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr><tr>");
    replyMSG.append("<td><button value=\"Stats\" action=\"bypass -h admin_edit_character\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td><button value=\"Exp & Sp\" action=\"bypass -h admin_add_exp_sp_to_character\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td></td>");
    replyMSG.append("</tr></table></body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  private void setTargetKarma(Player activeChar, int newKarma) {
    GameObject target = activeChar.getTarget();
    if (target == null) {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    } else if (target.isPlayer()) {
      Player player = (Player)target;
      if (newKarma >= 0) {
        int oldKarma = player.getKarma();
        player.setKarma(newKarma);
        player.sendMessage("Admin has changed your karma from " + oldKarma + " to " + newKarma + ".");
        activeChar.sendMessage("Successfully Changed karma for " + player.getName() + " from (" + oldKarma + ") to (" + newKarma + ").");
      } else {
        activeChar.sendMessage("You must enter a value for karma greater than or equal to 0.");
      }

    }
  }

  private void adminModifyCharacter(Player activeChar, String modifications) {
    GameObject target = activeChar.getTarget();
    if (target != null && target.isPlayer()) {
      Player player = (Player)target;
      String[] strvals = modifications.split("&");
      Integer[] vals = new Integer[strvals.length];

      for(int i = 0; i < strvals.length; ++i) {
        strvals[i] = strvals[i].trim();
        vals[i] = strvals[i].isEmpty() ? null : Integer.valueOf(strvals[i]);
      }

      if (vals[0] != null) {
        player.setCurrentHp((double)vals[0], false);
      }

      if (vals[1] != null) {
        player.setCurrentMp((double)vals[1]);
      }

      if (vals[2] != null) {
        player.setKarma(vals[2]);
      }

      if (vals[3] != null) {
        player.setPvpFlag(vals[3]);
      }

      if (vals[4] != null) {
        player.setPvpKills(vals[4]);
      }

      if (vals[5] != null) {
        player.setClassId(vals[5], true, false);
      }

      this.editCharacter(activeChar);
      player.broadcastCharInfo();
      player.decayMe();
      player.spawnMe(activeChar.getLoc());
    } else {
      activeChar.sendPacket(Msg.SELECT_TARGET);
    }
  }

  private void editCharacter(Player activeChar) {
    GameObject target = activeChar.getTarget();
    if (target != null && target.isPlayer()) {
      Player player = (Player)target;
      NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
      StringBuilder replyMSG = new StringBuilder("<html><body>");
      replyMSG.append("<table width=260><tr>");
      replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
      replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("</tr></table>");
      replyMSG.append("<br><br>");
      replyMSG.append("<center>Editing character: " + player.getName() + "</center><br>");
      replyMSG.append("<table width=250>");
      replyMSG.append("<tr><td width=40></td><td width=70>Curent:</td><td width=70>Max:</td><td width=70></td></tr>");
      replyMSG.append("<tr><td width=40>HP:</td><td width=70>" + player.getCurrentHp() + "</td><td width=70>" + player.getMaxHp() + "</td><td width=70>Karma: " + player.getKarma() + "</td></tr>");
      replyMSG.append("<tr><td width=40>MP:</td><td width=70>" + player.getCurrentMp() + "</td><td width=70>" + player.getMaxMp() + "</td><td width=70>Pvp Kills: " + player.getPvpKills() + "</td></tr>");
      replyMSG.append("<tr><td width=40>Load:</td><td width=70>" + player.getCurrentLoad() + "</td><td width=70>" + player.getMaxLoad() + "</td><td width=70>Pvp Flag: " + player.getPvpFlag() + "</td></tr>");
      replyMSG.append("</table>");
      replyMSG.append("<table width=270><tr><td>Class<?> Template Id: " + player.getClassId() + "/" + player.getClassId().getId() + "</td></tr></table><br>");
      replyMSG.append("<table width=270>");
      replyMSG.append("<tr><td>Note: Fill all values before saving the modifications.</td></tr>");
      replyMSG.append("</table><br>");
      replyMSG.append("<table width=270>");
      replyMSG.append("<tr><td width=50>Hp:</td><td><edit var=\"hp\" width=50></td><td width=50>Mp:</td><td><edit var=\"mp\" width=50></td></tr>");
      replyMSG.append("<tr><td width=50>Pvp Flag:</td><td><edit var=\"pvpflag\" width=50></td><td width=50>Karma:</td><td><edit var=\"karma\" width=50></td></tr>");
      replyMSG.append("<tr><td width=50>Class<?> Id:</td><td><edit var=\"classid\" width=50></td><td width=50>Pvp Kills:</td><td><edit var=\"pvpkills\" width=50></td></tr>");
      replyMSG.append("</table><br>");
      replyMSG.append("<center><button value=\"Save Changes\" action=\"bypass -h admin_save_modifications $hp & $mp & $karma & $pvpflag & $pvpkills & $classid &\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center><br>");
      replyMSG.append("</body></html>");
      adminReply.setHtml(replyMSG.toString());
      activeChar.sendPacket(adminReply);
    } else {
      activeChar.sendPacket(Msg.SELECT_TARGET);
    }
  }

  private void showCharacterActions(Player activeChar) {
    GameObject target = activeChar.getTarget();
    if (target != null && target.isPlayer()) {
      Player player = (Player)target;
      NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
      StringBuilder replyMSG = new StringBuilder("<html><body>");
      replyMSG.append("<table width=260><tr>");
      replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
      replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("</tr></table><br><br>");
      replyMSG.append("<center>Admin Actions for: " + player.getName() + "</center><br>");
      replyMSG.append("<center><table width=200><tr>");
      replyMSG.append("<td width=100>Argument(*):</td><td width=100><edit var=\"arg\" width=100></td>");
      replyMSG.append("</tr></table><br></center>");
      replyMSG.append("<table width=270>");
      replyMSG.append("<tr><td width=90><button value=\"Teleport\" action=\"bypass -h admin_teleportto " + player.getName() + "\" width=85 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("<td width=90><button value=\"Recall\" action=\"bypass -h admin_recall " + player.getName() + "\" width=85 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("<td width=90><button value=\"Quests\" action=\"bypass -h admin_quests " + player.getName() + "\" width=85 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
      replyMSG.append("</body></html>");
      adminReply.setHtml(replyMSG.toString());
      activeChar.sendPacket(adminReply);
    }
  }

  private void findCharacter(Player activeChar, String CharacterToFind) {
    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    int CharactersFound = 0;
    StringBuilder replyMSG = new StringBuilder("<html><body>");
    replyMSG.append("<table width=260><tr>");
    replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
    replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr></table>");
    replyMSG.append("<br><br>");
    Iterator var6 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var6.hasNext()) {
      Player element = (Player)var6.next();
      if (element.getName().startsWith(CharacterToFind)) {
        ++CharactersFound;
        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
        replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + element.getName() + "\">" + element.getName() + "</a></td><td width=110>" + element.getTemplate().className + "</td><td width=40>" + element.getLevel() + "</td></tr>");
        replyMSG.append("</table>");
      }
    }

    if (CharactersFound == 0) {
      replyMSG.append("<table width=270>");
      replyMSG.append("<tr><td width=270>Your search did not find any characters.</td></tr>");
      replyMSG.append("<tr><td width=270>Please try again.<br></td></tr>");
      replyMSG.append("</table><br>");
      replyMSG.append("<center><table><tr><td>");
      replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
      replyMSG.append("</td></tr></table></center>");
    } else {
      replyMSG.append("<center><br>Found " + CharactersFound + " character");
      if (CharactersFound == 1) {
        replyMSG.append(".");
      } else if (CharactersFound > 1) {
        replyMSG.append("s.");
      }
    }

    replyMSG.append("</center></body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  private void addExpSp(Player activeChar) {
    GameObject target = activeChar.getTarget();
    if (target == null || !target.isPlayer() || activeChar != target && !activeChar.getPlayerAccess().CanEditCharAll) {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    } else {
      Player player = (Player)target;
      NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
      StringBuilder replyMSG = new StringBuilder("<html><body>");
      replyMSG.append("<table width=260><tr>");
      replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
      replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("</tr></table>");
      replyMSG.append("<br><br>");
      replyMSG.append("<table width=270><tr><td>Name: " + player.getName() + "</td></tr>");
      replyMSG.append("<tr><td>Lv: " + player.getLevel() + " " + player.getTemplate().className + "</td></tr>");
      replyMSG.append("<tr><td>Exp: " + player.getExp() + "</td></tr>");
      replyMSG.append("<tr><td>Sp: " + player.getSp() + "</td></tr></table>");
      replyMSG.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
      replyMSG.append("<tr><td>ruin the game...</td></tr></table><br>");
      replyMSG.append("<table width=270><tr><td>Note: Fill all values before saving the modifications.,</td></tr>");
      replyMSG.append("<tr><td>Note: Use 0 if no changes are needed.</td></tr></table><br>");
      replyMSG.append("<center><table><tr>");
      replyMSG.append("<td>Exp: <edit var=\"exp_to_add\" width=50></td>");
      replyMSG.append("<td>Sp:  <edit var=\"sp_to_add\" width=50></td>");
      replyMSG.append("<td>&nbsp;<button value=\"Save Changes\" action=\"bypass -h admin_add_exp_sp $exp_to_add $sp_to_add\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("</tr></table></center>");
      replyMSG.append("</body></html>");
      adminReply.setHtml(replyMSG.toString());
      activeChar.sendPacket(adminReply);
    }
  }

  private void adminAddExpSp(Player activeChar, long exp, int sp) {
    if (!activeChar.getPlayerAccess().CanEditCharAll) {
      activeChar.sendMessage("You have not enough privileges, for use this function.");
    } else {
      GameObject target = activeChar.getTarget();
      if (target == null) {
        activeChar.sendPacket(Msg.SELECT_TARGET);
      } else if (!target.isPlayable()) {
        activeChar.sendPacket(Msg.INVALID_TARGET);
      } else {
        Playable playable = (Playable)target;
        playable.addExpAndSp(exp, (long)sp);
        activeChar.sendMessage("Added " + exp + " experience and " + sp + " SP to " + playable.getName() + ".");
      }
    }
  }

  private void setSubclass(Player activeChar, Player player) {
    StringBuilder content = new StringBuilder("<html><body>");
    NpcHtmlMessage html = new NpcHtmlMessage(5);
    Set<PlayerClass> subsAvailable = this.getAvailableSubClasses(player);
    if (subsAvailable != null && !subsAvailable.isEmpty()) {
      content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");
      Iterator var6 = subsAvailable.iterator();

      while(var6.hasNext()) {
        PlayerClass subClass = (PlayerClass)var6.next();
        content.append("<a action=\"bypass -h admin_setsubclass " + subClass.ordinal() + "\">" + this.formatClassForDisplay(subClass) + "</a><br>");
      }

      content.append("</body></html>");
      html.setHtml(content.toString());
      activeChar.sendPacket(html);
    } else {
      activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", activeChar, new Object[0]));
    }
  }

  private Set<PlayerClass> getAvailableSubClasses(Player player) {
    int charClassId = player.getBaseClassId();
    PlayerClass currClass = PlayerClass.values()[charClassId];
    Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
    if (availSubs == null) {
      return null;
    } else {
      availSubs.remove(currClass);
      Iterator var5 = availSubs.iterator();

      label40:
      while(var5.hasNext()) {
        PlayerClass availSub = (PlayerClass)var5.next();
        Iterator var7 = player.getSubClasses().values().iterator();

        while(true) {
          while(true) {
            if (!var7.hasNext()) {
              continue label40;
            }

            SubClass subClass = (SubClass)var7.next();
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
      }

      return availSubs;
    }
  }

  private String formatClassForDisplay(PlayerClass className) {
    String classNameStr = className.toString();
    char[] charArray = classNameStr.toCharArray();

    for(int i = 1; i < charArray.length; ++i) {
      if (Character.isUpperCase(charArray[i])) {
        classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);
      }
    }

    return classNameStr;
  }

  private static enum Commands {
    admin_edit_character,
    admin_character_actions,
    admin_current_player,
    admin_nokarma,
    admin_setkarma,
    admin_character_list,
    admin_show_characters,
    admin_show_characters_by_ip,
    admin_find_character,
    admin_save_modifications,
    admin_rec,
    admin_settitle,
    admin_setclass,
    admin_setname,
    admin_setsex,
    admin_setcolor,
    admin_add_exp_sp_to_character,
    admin_add_exp_sp,
    admin_sethero,
    admin_setnoble,
    admin_trans,
    admin_setsubclass,
    admin_setfame,
    admin_setbday,
    admin_give_item,
    admin_remove_item,
    admin_add_bang,
    admin_set_bang,
    admin_set_pa;

    private Commands() {
    }
  }
}
