//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.scripts.Events;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class OlympiadBufferInstance extends NpcInstance {
  private TIntHashSet buffs = new TIntHashSet();

  public OlympiadBufferInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onAction(Player player, boolean shift) {
    if (Events.onAction(player, this, shift)) {
      player.sendActionFailed();
    } else {
      MyTargetSelected my;
      if (this != player.getTarget()) {
        player.setTarget(this);
        my = new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel());
        player.sendPacket(my);
        player.sendPacket(new ValidateLocation(this));
      } else {
        my = new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel());
        player.sendPacket(my);
        if (!this.isInActingRange(player)) {
          if (!player.getAI().isIntendingInteract(this)) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
          }
        } else if (this.buffs.size() > 4) {
          this.showChatWindow(player, 1, new Object[0]);
        } else {
          this.showChatWindow(player, 0, new Object[0]);
        }

        player.sendActionFailed();
      }

    }
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (this.buffs.size() > 4) {
        this.showChatWindow(player, 1, new Object[0]);
      }

      if (command.startsWith("Buff")) {
        int id = false;
        int lvl = false;
        StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();
        int id = Integer.parseInt(st.nextToken());
        int lvl = Integer.parseInt(st.nextToken());
        Skill skill = SkillTable.getInstance().getInfo(id, lvl);
        List<Creature> target = new ArrayList();
        target.add(player);
        this.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(this, player, id, lvl, 0, 0L)});
        this.callSkill(skill, target, true);
        this.buffs.add(id);
        if (this.buffs.size() > 4) {
          this.showChatWindow(player, 1, new Object[0]);
        } else {
          this.showChatWindow(player, 0, new Object[0]);
        }
      } else {
        this.showChatWindow(player, 0, new Object[0]);
      }

    }
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "buffer";
    } else {
      pom = "buffer-" + val;
    }

    return "oly/" + pom + ".htm";
  }
}
