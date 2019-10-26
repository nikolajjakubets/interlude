//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.moveroute;

import java.util.ArrayList;
import java.util.List;

public class MoveRoute {
  private final List<MoveNode> _nodes = new ArrayList();
  private final String _name;
  private final MoveRouteType _type;
  private final boolean _isRunning;

  public MoveRoute(String name, MoveRouteType type, boolean isRunning) {
    this._name = name;
    this._type = type;
    this._isRunning = isRunning;
  }

  public List<MoveNode> getNodes() {
    return this._nodes;
  }

  public String getName() {
    return this._name;
  }

  public MoveRouteType getType() {
    return this._type;
  }

  public boolean isRunning() {
    return this._isRunning;
  }
}
