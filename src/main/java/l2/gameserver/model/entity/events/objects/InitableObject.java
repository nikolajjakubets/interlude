//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import l2.gameserver.model.entity.events.GlobalEvent;

public interface InitableObject extends Serializable {
  void initObject(GlobalEvent var1);
}
