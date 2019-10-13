//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.templates.npc.NpcTemplate;

public class NpcNotSayInstance extends NpcInstance {
  public NpcNotSayInstance(int objectID, NpcTemplate template) {
    super(objectID, template);
    this.setHasChatWindow(false);
  }
}
