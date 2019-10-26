//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.scripts;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Scripts.ScriptClassAndMethod;
import l2.gameserver.utils.Strings;

public final class Events {
  public Events() {
  }

  public static boolean onAction(Player player, GameObject obj, boolean shift) {
    ScriptClassAndMethod handler;
    if (shift) {
      if (player.getVarB("noShift")) {
        return false;
      } else {
        handler = (ScriptClassAndMethod)Scripts.onActionShift.get(obj.getL2ClassShortName());
        if (handler == null && obj.isNpc()) {
          handler = (ScriptClassAndMethod)Scripts.onActionShift.get("NpcInstance");
        }

        if (handler == null && obj.isPet()) {
          handler = (ScriptClassAndMethod)Scripts.onActionShift.get("PetInstance");
        }

        if (handler == null && obj.isSummon()) {
          handler = (ScriptClassAndMethod)Scripts.onActionShift.get("SummonInstance");
        }

        return handler == null ? false : Strings.parseBoolean(Scripts.getInstance().callScripts(player, handler.className, handler.methodName, new Object[]{player, obj}));
      }
    } else {
      handler = (ScriptClassAndMethod)Scripts.onAction.get(obj.getL2ClassShortName());
      if (handler == null && obj.isDoor()) {
        handler = (ScriptClassAndMethod)Scripts.onAction.get("DoorInstance");
      }

      return handler == null ? false : Strings.parseBoolean(Scripts.getInstance().callScripts(player, handler.className, handler.methodName, new Object[]{player, obj}));
    }
  }
}
