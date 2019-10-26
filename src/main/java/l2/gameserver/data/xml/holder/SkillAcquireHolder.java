//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.ClassType2;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;

public final class SkillAcquireHolder extends AbstractHolder {
  private static final SkillAcquireHolder _instance = new SkillAcquireHolder();
  private TIntObjectHashMap<List<SkillLearn>> _normalSkillTree = new TIntObjectHashMap();
  private TIntObjectHashMap<List<SkillLearn>> _fishingSkillTree = new TIntObjectHashMap();
  private List<SkillLearn> _pledgeSkillTree = new ArrayList();

  public SkillAcquireHolder() {
  }

  public static SkillAcquireHolder getInstance() {
    return _instance;
  }

  public int getMinLevelForNewSkill(ClassId classId, int currLevel, AcquireType type) {
    switch(type) {
      case NORMAL:
        List<SkillLearn> skills = this._normalSkillTree.get(classId.getId());
        if (skills == null) {
          this.info("skill tree for class " + classId.getId() + " is not defined !");
          return 0;
        } else {
          int minlevel = 0;
          Iterator var6 = skills.iterator();

          while(true) {
            SkillLearn temp;
            do {
              do {
                if (!var6.hasNext()) {
                  return minlevel;
                }

                temp = (SkillLearn)var6.next();
              } while(temp.getMinLevel() <= currLevel);
            } while(minlevel != 0 && temp.getMinLevel() >= minlevel);

            minlevel = temp.getMinLevel();
          }
        }
      default:
        return 0;
    }
  }

  public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type) {
    return this.getAvailableSkills(player, player.getClassId(), type, null);
  }

  public Collection<SkillLearn> getAvailableSkills(Player player, ClassId classId, AcquireType type, SubUnit subUnit) {
    Collection skills;
    switch(type) {
      case NORMAL:
        skills = this._normalSkillTree.get(classId.getId());
        if (skills == null) {
          this.info("skill tree for class " + classId + " is not defined !");
          return Collections.emptyList();
        }

        return this.getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
      case FISHING:
        skills = this._fishingSkillTree.get(player.getRace().ordinal());
        if (skills == null) {
          this.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
          return Collections.emptyList();
        }

        return this.getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
      case CLAN:
        Collection<SkillLearn> pledgeSkillTree = this._pledgeSkillTree;
        Collection<Skill> skls = player.getClan().getSkills();
        return this.getAvaliableList(pledgeSkillTree, skls.toArray(new Skill[skls.size()]), player.getClan().getLevel());
      default:
        return Collections.emptyList();
    }
  }

  private Collection<SkillLearn> getAvaliableList(Collection<SkillLearn> skillLearns, Skill[] skills, int level) {
    return this.getAvaliableList(skillLearns, skills, level, null);
  }

  private Collection<SkillLearn> getAvaliableList(Collection<SkillLearn> skillLearns, Skill[] skills, int level, Player target) {
    Map<Integer, SkillLearn> skillLearnMap = new TreeMap();
    Iterator var6 = skillLearns.iterator();

    while(true) {
      SkillLearn temp;
      boolean knownSkill;
      label67:
      while(true) {
        do {
          if (!var6.hasNext()) {
            return skillLearnMap.values();
          }

          temp = (SkillLearn)var6.next();
        } while(temp.getMinLevel() > level);

        if (target == null || temp.getClassType2() == ClassType2.None) {
          break;
        }

        knownSkill = false;
        Iterator var9 = target.getSubClasses().entrySet().iterator();

        while(true) {
          Entry e;
          do {
            if (!var9.hasNext()) {
              if (!knownSkill) {
                continue label67;
              }
              break label67;
            }

            e = (Entry)var9.next();
          } while(((SubClass)e.getValue()).isBase());

          ClassId[] var11 = ClassId.values();
          int var12 = var11.length;

          for(int var13 = 0; var13 < var12; ++var13) {
            ClassId ci = var11[var13];
            if (ci.getId() == (Integer)e.getKey() && ci.getType2() == temp.getClassType2()) {
              knownSkill = true;
            }
          }
        }
      }

      knownSkill = false;

      for(int j = 0; j < skills.length && !knownSkill; ++j) {
        if (skills[j].getId() == temp.getId()) {
          knownSkill = true;
          if (skills[j].getLevel() == temp.getLevel() - 1) {
            skillLearnMap.put(temp.getId(), temp);
          }
        }
      }

      if (!knownSkill && temp.getLevel() == 1) {
        skillLearnMap.put(temp.getId(), temp);
      }
    }
  }

  public SkillLearn getSkillLearn(Player player, ClassId classId, int id, int level, AcquireType type) {
    List skills;
    switch(type) {
      case NORMAL:
        skills = this._normalSkillTree.get(classId.getId());
        break;
      case FISHING:
        skills = this._fishingSkillTree.get(player.getRace().ordinal());
        break;
      case CLAN:
        skills = this._pledgeSkillTree;
        break;
      default:
        return null;
    }

    if (skills == null) {
      return null;
    } else {
      Iterator var7 = skills.iterator();

      SkillLearn temp;
      do {
        if (!var7.hasNext()) {
          return null;
        }

        temp = (SkillLearn)var7.next();
      } while(temp.getLevel() != level || temp.getId() != id);

      return temp;
    }
  }

  public boolean isSkillPossible(Player player, Skill skill, AcquireType type) {
    Clan clan = null;
    List skills;
    switch(type) {
      case NORMAL:
        skills = this._normalSkillTree.get(player.getActiveClassId());
        break;
      case FISHING:
        skills = this._fishingSkillTree.get(player.getRace().ordinal());
        break;
      case CLAN:
        clan = player.getClan();
        if (clan == null) {
          return false;
        }

        skills = this._pledgeSkillTree;
        break;
      default:
        return false;
    }

    return this.isSkillPossible(skills, skill);
  }

  public boolean isSkillPossible(Player player, ClassId classId, Skill skill, AcquireType type) {
    Clan clan = null;
    List skills;
    switch(type) {
      case NORMAL:
        skills = this._normalSkillTree.get(classId.getId());
        break;
      case FISHING:
        skills = this._fishingSkillTree.get(player.getRace().ordinal());
        break;
      case CLAN:
        clan = player.getClan();
        if (clan == null) {
          return false;
        }

        skills = this._pledgeSkillTree;
        break;
      default:
        return false;
    }

    return this.isSkillPossible(skills, skill);
  }

  private boolean isSkillPossible(Collection<SkillLearn> skills, Skill skill) {
    Iterator var3 = skills.iterator();

    SkillLearn learn;
    do {
      if (!var3.hasNext()) {
        return false;
      }

      learn = (SkillLearn)var3.next();
    } while(learn.getId() != skill.getId() || learn.getLevel() > skill.getLevel());

    return true;
  }

  public boolean isSkillPossible(Player player, Skill skill) {
    AcquireType[] var3 = AcquireType.VALUES;
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      AcquireType aq = var3[var5];
      if (this.isSkillPossible(player, skill, aq)) {
        return true;
      }
    }

    return false;
  }

  public boolean isSkillPossible(Player player, ClassId classId, Skill skill) {
    AcquireType[] var4 = AcquireType.VALUES;
    int var5 = var4.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      AcquireType aq = var4[var6];
      if (this.isSkillPossible(player, classId, skill, aq)) {
        return true;
      }
    }

    return false;
  }

  public List<SkillLearn> getSkillLearnListByItemId(Player player, int itemId) {
    List<SkillLearn> learns = this._normalSkillTree.get(player.getActiveClassId());
    if (learns == null) {
      return Collections.emptyList();
    } else {
      List<SkillLearn> l = new ArrayList(1);
      Iterator var5 = learns.iterator();

      while(var5.hasNext()) {
        SkillLearn $i = (SkillLearn)var5.next();
        if ($i.getItemId() == itemId) {
          l.add($i);
        }
      }

      return l;
    }
  }

  public List<SkillLearn> getAllNormalSkillTreeWithForgottenScrolls() {
    List<SkillLearn> a = new ArrayList();
    TIntObjectIterator i = this._normalSkillTree.iterator();

    while(i.hasNext()) {
      i.advance();
      Iterator var3 = ((List)i.value()).iterator();

      while(var3.hasNext()) {
        SkillLearn learn = (SkillLearn)var3.next();
        if (learn.getItemId() > 0 && learn.isClicked()) {
          a.add(learn);
        }
      }
    }

    return a;
  }

  public void addAllNormalSkillLearns(TIntObjectHashMap<List<SkillLearn>> map) {
    ClassId[] var3 = ClassId.VALUES;
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      ClassId classId = var3[var5];
      if (classId.getRace() != null) {
        int classID = classId.getId();
        List<SkillLearn> temp = map.get(classID);
        if (temp == null) {
          this.info("Not found NORMAL skill learn for class " + classID);
        } else {
          this._normalSkillTree.put(classId.getId(), temp);
          ClassId secondparent = classId.getParent(1);
          if (secondparent == classId.getParent(0)) {
            secondparent = null;
          }

          classId = classId.getParent(0);

          while(classId != null) {
            List<SkillLearn> parentList = this._normalSkillTree.get(classId.getId());
            temp.addAll(parentList);
            classId = classId.getParent(0);
            if (classId == null && secondparent != null) {
              classId = secondparent;
              secondparent = secondparent.getParent(1);
            }
          }
        }
      }
    }

  }

  public void addAllFishingLearns(int race, List<SkillLearn> s) {
    this._fishingSkillTree.put(race, s);
  }

  public void addAllPledgeLearns(List<SkillLearn> s) {
    this._pledgeSkillTree.addAll(s);
  }

  public void log() {
    this.info("load " + this.sizeTroveMap(this._normalSkillTree) + " normal learns for " + this._normalSkillTree.size() + " classes.");
    this.info("load " + this.sizeTroveMap(this._fishingSkillTree) + " fishing learns for " + this._fishingSkillTree.size() + " races.");
    this.info("load " + this._pledgeSkillTree.size() + " pledge learns.");
  }

  /** @deprecated */
  @Deprecated
  public int size() {
    return 0;
  }

  public void clear() {
    this._normalSkillTree.clear();
    this._fishingSkillTree.clear();
    this._pledgeSkillTree.clear();
  }

  private int sizeTroveMap(TIntObjectHashMap<List<SkillLearn>> a) {
    int i = 0;

    for(TIntObjectIterator iterator = a.iterator(); iterator.hasNext(); i += ((List)iterator.value()).size()) {
      iterator.advance();
    }

    return i;
  }
}
