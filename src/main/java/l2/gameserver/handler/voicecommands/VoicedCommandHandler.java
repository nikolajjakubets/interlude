//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.impl.Augments;
import l2.gameserver.handler.voicecommands.impl.Banking;
import l2.gameserver.handler.voicecommands.impl.CWHPrivileges;
import l2.gameserver.handler.voicecommands.impl.Cfg;
import l2.gameserver.handler.voicecommands.impl.Help;
import l2.gameserver.handler.voicecommands.impl.InstanceZone;
import l2.gameserver.handler.voicecommands.impl.ItemRemaining;
import l2.gameserver.handler.voicecommands.impl.Offline;
import l2.gameserver.handler.voicecommands.impl.Online;
import l2.gameserver.handler.voicecommands.impl.Relocate;
import l2.gameserver.handler.voicecommands.impl.Relog;
import l2.gameserver.handler.voicecommands.impl.ServerInfo;
import l2.gameserver.handler.voicecommands.impl.Services;
import l2.gameserver.handler.voicecommands.impl.Wedding;
import l2.gameserver.handler.voicecommands.impl.WhoAmI;

public class VoicedCommandHandler extends AbstractHolder {
  private static final VoicedCommandHandler _instance = new VoicedCommandHandler();
  private Map<String, IVoicedCommandHandler> _datatable = new HashMap<>();

  public static VoicedCommandHandler getInstance() {
    return _instance;
  }

  private VoicedCommandHandler() {
    this.registerVoicedCommandHandler(new Offline());
    this.registerVoicedCommandHandler(new Online());
    this.registerVoicedCommandHandler(new ServerInfo());
    this.registerVoicedCommandHandler(new Wedding());
    this.registerVoicedCommandHandler(new Services());
    this.registerVoicedCommandHandler(new WhoAmI());
    this.registerVoicedCommandHandler(new Help());
    this.registerVoicedCommandHandler(new InstanceZone());
    this.registerVoicedCommandHandler(new Relog());
    if (Config.ALT_ALLOW_MENU_COMMAND) {
      this.registerVoicedCommandHandler(new Cfg());
    }

    this.registerVoicedCommandHandler(new CWHPrivileges());
    this.registerVoicedCommandHandler(new Augments());
    this.registerVoicedCommandHandler(new Relocate());
    this.registerVoicedCommandHandler(new ItemRemaining());
    this.registerVoicedCommandHandler(new Banking());
  }

  public void registerVoicedCommandHandler(IVoicedCommandHandler handler) {
    String[] ids = handler.getVoicedCommandList();
    String[] var3 = ids;
    int var4 = ids.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String element = var3[var5];
      this._datatable.put(element, handler);
    }

  }

  public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand) {
    String command = voicedCommand;
    if (voicedCommand.indexOf(" ") != -1) {
      command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
    }

    return (IVoicedCommandHandler)this._datatable.get(command);
  }

  public int size() {
    return this._datatable.size();
  }

  public void clear() {
    this._datatable.clear();
  }
}
