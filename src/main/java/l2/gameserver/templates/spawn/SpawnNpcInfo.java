//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.spawn;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class SpawnNpcInfo {
  private final int _npcId;
  private final NpcTemplate _template;
  private Location _spawnLoc;
  private final int _max;
  private final MultiValueSet<String> _parameters;

  public SpawnNpcInfo(int npcId, int max, MultiValueSet<String> set) {
    this._npcId = npcId;
    this._template = NpcHolder.getInstance().getTemplate(npcId);
    this._max = max;
    this._parameters = set;
  }

  public int getNpcId() {
    return this._npcId;
  }

  public Location getSpawnLoc() {
    return this._spawnLoc;
  }

  public NpcTemplate getTemplate() {
    return this._template;
  }

  public int getMax() {
    return this._max;
  }

  public MultiValueSet<String> getParameters() {
    return this._parameters;
  }
}
