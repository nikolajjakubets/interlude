//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands;

import gnu.trove.TIntObjectHashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.handler.usercommands.impl.ClanPenalty;
import l2.gameserver.handler.usercommands.impl.ClanWarsList;
import l2.gameserver.handler.usercommands.impl.CommandChannel;
import l2.gameserver.handler.usercommands.impl.Escape;
import l2.gameserver.handler.usercommands.impl.InstanceZone;
import l2.gameserver.handler.usercommands.impl.LocCommand;
import l2.gameserver.handler.usercommands.impl.OlympiadStat;
import l2.gameserver.handler.usercommands.impl.PartyInfo;
import l2.gameserver.handler.usercommands.impl.Time;

public class UserCommandHandler extends AbstractHolder {
  private static final UserCommandHandler _instance = new UserCommandHandler();
  private TIntObjectHashMap<IUserCommandHandler> _datatable = new TIntObjectHashMap();

  public static UserCommandHandler getInstance() {
    return _instance;
  }

  private UserCommandHandler() {
    this.registerUserCommandHandler(new ClanWarsList());
    this.registerUserCommandHandler(new ClanPenalty());
    this.registerUserCommandHandler(new CommandChannel());
    this.registerUserCommandHandler(new Escape());
    this.registerUserCommandHandler(new LocCommand());
    this.registerUserCommandHandler(new OlympiadStat());
    this.registerUserCommandHandler(new PartyInfo());
    this.registerUserCommandHandler(new InstanceZone());
    this.registerUserCommandHandler(new Time());
  }

  public void registerUserCommandHandler(IUserCommandHandler handler) {
    int[] ids = handler.getUserCommandList();
    int[] var3 = ids;
    int var4 = ids.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int element = var3[var5];
      this._datatable.put(element, handler);
    }

  }

  public IUserCommandHandler getUserCommandHandler(int userCommand) {
    return (IUserCommandHandler)this._datatable.get(userCommand);
  }

  public int size() {
    return this._datatable.size();
  }

  public void clear() {
    this._datatable.clear();
  }
}
