//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.taskmanager.SpawnTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;

public class AdminSpawn implements IAdminCommandHandler {
  public AdminSpawn() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminSpawn.Commands command = (AdminSpawn.Commands)comm;
    if (!activeChar.getPlayerAccess().CanEditNPC) {
      return false;
    } else {
      StringTokenizer st;
      NpcInstance target;
      String aiName;
      int respawnTime;
      switch(command) {
        case admin_show_spawns:
          activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/spawns.htm"));
          break;
        case admin_spawn_index:
          try {
            aiName = fullString.substring(18);
            activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/spawns/" + aiName + ".htm"));
          } catch (StringIndexOutOfBoundsException var37) {
          }
          break;
        case admin_spawn1:
          st = new StringTokenizer(fullString, " ");

          try {
            st.nextToken();
            aiName = st.nextToken();
            respawnTime = 1;
            if (st.hasMoreTokens()) {
              respawnTime = Integer.parseInt(st.nextToken());
            }

            this.spawnMonster(activeChar, aiName, 0, respawnTime);
          } catch (Exception var36) {
          }
          break;
        case admin_spawn:
        case admin_spawn_monster:
          st = new StringTokenizer(fullString, " ");

          try {
            st.nextToken();
            aiName = st.nextToken();
            respawnTime = 30;
            int mobCount = 1;
            if (st.hasMoreTokens()) {
              mobCount = Integer.parseInt(st.nextToken());
            }

            if (st.hasMoreTokens()) {
              respawnTime = Integer.parseInt(st.nextToken());
            }

            this.spawnMonster(activeChar, aiName, respawnTime, mobCount);
          } catch (Exception var35) {
          }
          break;
        case admin_setai:
          if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
            activeChar.sendMessage("Please select target NPC or mob.");
            return false;
          }

          st = new StringTokenizer(fullString, " ");
          st.nextToken();
          if (!st.hasMoreTokens()) {
            activeChar.sendMessage("Please specify AI name.");
            return false;
          }

          aiName = st.nextToken();
          target = (NpcInstance)activeChar.getTarget();
          Constructor aiConstructor = null;

          try {
            if (!aiName.equalsIgnoreCase("npc")) {
              aiConstructor = Class.forName("l2.gameserver.ai." + aiName).getConstructors()[0];
            }
          } catch (Exception var34) {
            try {
              aiConstructor = ((Class)Scripts.getInstance().getClasses().get("ai." + aiName)).getConstructors()[0];
            } catch (Exception var33) {
              activeChar.sendMessage("This type AI not found.");
              return false;
            }
          }

          if (aiConstructor != null) {
            try {
              target.setAI((CharacterAI)aiConstructor.newInstance(target));
            } catch (Exception var32) {
              var32.printStackTrace();
            }

            target.getAI().startAITask();
          }
          break;
        case admin_setaiparam:
          if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
            activeChar.sendMessage("Please select target NPC or mob.");
            return false;
          }

          st = new StringTokenizer(fullString, " ");
          st.nextToken();
          if (!st.hasMoreTokens()) {
            activeChar.sendMessage("Please specify AI parameter name.");
            activeChar.sendMessage("USAGE: //setaiparam <param> <value>");
            return false;
          }

          String paramName = st.nextToken();
          if (!st.hasMoreTokens()) {
            activeChar.sendMessage("Please specify AI parameter value.");
            activeChar.sendMessage("USAGE: //setaiparam <param> <value>");
            return false;
          }

          String paramValue = st.nextToken();
          target = (NpcInstance)activeChar.getTarget();
          target.setParameter(paramName, paramValue);
          target.decayMe();
          target.spawnMe();
          activeChar.sendMessage("AI parameter " + paramName + " succesfully setted to " + paramValue);
          break;
        case admin_dumpparams:
          if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
            activeChar.sendMessage("Please select target NPC or mob.");
            return false;
          }

          target = (NpcInstance)activeChar.getTarget();
          MultiValueSet<String> set = target.getParameters();
          if (!set.isEmpty()) {
            System.out.println("Dump of Parameters:\r\n" + set.toString());
          } else {
            System.out.println("Parameters is empty.");
          }
          break;
        case admin_setheading:
          GameObject obj = activeChar.getTarget();
          if (!obj.isNpc()) {
            activeChar.sendMessage("Target is incorrect!");
            return false;
          }

          NpcInstance npc = (NpcInstance)obj;
          npc.setHeading(activeChar.getHeading());
          npc.decayMe();
          npc.spawnMe();
          activeChar.sendMessage("New heading : " + activeChar.getHeading());
          Spawner spawn = npc.getSpawn();
          if (spawn == null) {
            activeChar.sendMessage("Spawn for this npc == null!");
            return false;
          }
          break;
        case admin_generate_loc:
          if (wordList.length < 2) {
            activeChar.sendMessage("Incorrect argument count!");
            return false;
          }

          int id = Integer.parseInt(wordList[1]);
          int id2 = 0;
          if (wordList.length > 2) {
            id2 = Integer.parseInt(wordList[2]);
          }

          int min_x = -2147483648;
          int min_y = -2147483648;
          int min_z = -2147483648;
          int max_x = 2147483647;
          int max_y = 2147483647;
          int max_z = 2147483647;
          String name = "";
          Iterator var40 = World.getAroundNpc(activeChar).iterator();

          while(true) {
            NpcInstance _npc;
            do {
              if (!var40.hasNext()) {
                min_x -= 500;
                min_y -= 500;
                max_x += 500;
                max_y += 500;
                System.out.println("(0,'" + name + "'," + min_x + "," + min_y + "," + min_z + "," + max_z + ",0),");
                System.out.println("(0,'" + name + "'," + min_x + "," + max_y + "," + min_z + "," + max_z + ",0),");
                System.out.println("(0,'" + name + "'," + max_x + "," + max_y + "," + min_z + "," + max_z + ",0),");
                System.out.println("(0,'" + name + "'," + max_x + "," + min_y + "," + min_z + "," + max_z + ",0),");
                System.out.println("delete from spawnlist where npc_templateid in (" + id + ", " + id2 + ") and locx <= " + min_x + " and locy <= " + min_y + " and locz <= " + min_z + " and locx >= " + max_x + " and locy >= " + max_y + " and locz >= " + max_z + ";");
                return true;
              }

              _npc = (NpcInstance)var40.next();
            } while(_npc.getNpcId() != id && _npc.getNpcId() != id2);

            name = _npc.getName();
            min_x = Math.min(min_x, _npc.getX());
            min_y = Math.min(min_y, _npc.getY());
            min_z = Math.min(min_z, _npc.getZ());
            max_x = Math.max(max_x, _npc.getX());
            max_y = Math.max(max_y, _npc.getY());
            max_z = Math.max(max_z, _npc.getZ());
          }
        case admin_dumpspawntasks:
          System.out.println(SpawnTaskManager.getInstance().toString());
          break;
        case admin_dumpspawn:
          st = new StringTokenizer(fullString, " ");

          try {
            st.nextToken();
            String id3 = st.nextToken();
            int respawnTime = 30;
            int mobCount = 1;
            this.spawnMonster(activeChar, id3, respawnTime, mobCount);

            try {
              (new File("dumps")).mkdir();
              File f = new File("dumps/spawndump.txt");
              if (!f.exists()) {
                f.createNewFile();
              }

              FileWriter writer = new FileWriter(f, true);
              writer.write("<spawn count=\"1\" respawn=\"60\" respawn_random=\"0\" period_of_day=\"none\">\n\t<point x=\"" + activeChar.getLoc().x + "\" y=\"" + activeChar.getLoc().y + "\" z=\"" + activeChar.getLoc().z + "\" h=\"" + activeChar.getLoc().h + "\" />\n\t<npc id=\"" + Integer.parseInt(id3) + "\" /><!--" + NpcHolder.getInstance().getTemplate(Integer.parseInt(id3)).getName() + "-->\n</spawn>\n");
              writer.close();
            } catch (Exception var30) {
            }
          } catch (Exception var31) {
          }
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminSpawn.Commands.values();
  }

  private void spawnMonster(Player activeChar, String monsterId, int respawnTime, int mobCount) {
    GameObject target = activeChar.getTarget();
    if (target == null) {
      target = activeChar;
    }

    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher regexp = pattern.matcher(monsterId);
    NpcTemplate template;
    if (regexp.matches()) {
      int monsterTemplate = Integer.parseInt(monsterId);
      template = NpcHolder.getInstance().getTemplate(monsterTemplate);
    } else {
      monsterId = monsterId.replace('_', ' ');
      template = NpcHolder.getInstance().getTemplateByName(monsterId);
    }

    if (template == null) {
      activeChar.sendMessage("Incorrect monster template.");
    } else {
      try {
        SimpleSpawner spawn = new SimpleSpawner(template);
        spawn.setLoc(((GameObject)target).getLoc());
        spawn.setAmount(mobCount);
        spawn.setHeading(activeChar.getHeading());
        spawn.setRespawnDelay(respawnTime);
        spawn.setReflection(activeChar.getReflection());
        if (RaidBossSpawnManager.getInstance().isDefined(template.getNpcId())) {
          activeChar.sendMessage("Raid Boss " + template.name + " already spawned.");
        } else {
          spawn.init();
          if (respawnTime == 0) {
            spawn.stopRespawn();
          }

          activeChar.sendMessage("Created " + template.name + " on " + ((GameObject)target).getObjectId() + ".");
        }
      } catch (Exception var10) {
        var10.printStackTrace();
        activeChar.sendMessage("Target is not ingame.");
      }

    }
  }

  private static enum Commands {
    admin_show_spawns,
    admin_spawn,
    admin_spawn_monster,
    admin_spawn_index,
    admin_spawn1,
    admin_setheading,
    admin_setai,
    admin_setaiparam,
    admin_dumpparams,
    admin_generate_loc,
    admin_dumpspawntasks,
    admin_dumpspawn;

    private Commands() {
    }
  }
}
