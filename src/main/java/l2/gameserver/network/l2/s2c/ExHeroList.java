//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.HeroController.HeroRecord;

public class ExHeroList extends L2GameServerPacket {
  private Collection<HeroRecord> heroes = new ArrayList();

  public ExHeroList() {
    Iterator var1 = HeroController.getInstance().getCurrentHeroes().iterator();

    while(var1.hasNext()) {
      HeroRecord hr = (HeroRecord)var1.next();
      if (hr != null && hr.active && hr.played) {
        this.heroes.add(hr);
      }
    }

  }

  protected final void writeImpl() {
    this.writeEx(35);
    this.writeD(this.heroes.size());
    Iterator var1 = this.heroes.iterator();

    while(var1.hasNext()) {
      HeroRecord hero = (HeroRecord)var1.next();
      this.writeS(hero.name);
      this.writeD(hero.class_id);
      this.writeS(hero.clan_name);
      this.writeD(hero.clan_crest);
      this.writeS(hero.ally_name);
      this.writeD(hero.ally_crest);
      this.writeD(hero.count);
    }

  }
}
