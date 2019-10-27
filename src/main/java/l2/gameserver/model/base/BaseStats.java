//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public enum BaseStats {
  STR {
    public final int getStat(Creature actor) {
      return actor == null ? 1 : actor.getSTR();
    }

    public final double calcBonus(Creature actor) {
      return actor == null ? 1.0D : BaseStats.STRbonus[actor.getSTR()];
    }

    public final double calcChanceMod(Creature actor) {
      return Math.min(2.0D - Math.sqrt(this.calcBonus(actor)), 1.0D);
    }
  },
  INT {
    public final int getStat(Creature actor) {
      return actor == null ? 1 : actor.getINT();
    }

    public final double calcBonus(Creature actor) {
      return actor == null ? 1.0D : BaseStats.INTbonus[actor.getINT()];
    }
  },
  DEX {
    public final int getStat(Creature actor) {
      return actor == null ? 1 : actor.getDEX();
    }

    public final double calcBonus(Creature actor) {
      return actor == null ? 1.0D : BaseStats.DEXbonus[actor.getDEX()];
    }
  },
  WIT {
    public final int getStat(Creature actor) {
      return actor == null ? 1 : actor.getWIT();
    }

    public final double calcBonus(Creature actor) {
      return actor == null ? 1.0D : BaseStats.WITbonus[actor.getWIT()];
    }
  },
  CON {
    public final int getStat(Creature actor) {
      return actor == null ? 1 : actor.getCON();
    }

    public final double calcBonus(Creature actor) {
      return actor == null ? 1.0D : BaseStats.CONbonus[actor.getCON()];
    }
  },
  MEN {
    public final int getStat(Creature actor) {
      return actor == null ? 1 : actor.getMEN();
    }

    public final double calcBonus(Creature actor) {
      return actor == null ? 1.0D : BaseStats.MENbonus[actor.getMEN()];
    }
  },
  NONE;

  public static final BaseStats[] VALUES = values();
  protected static final Logger _log = LoggerFactory.getLogger(BaseStats.class);
  private static final int MAX_STAT_VALUE = 100;
  private static final double[] STRbonus = new double[100];
  private static final double[] INTbonus = new double[100];
  private static final double[] DEXbonus = new double[100];
  private static final double[] WITbonus = new double[100];
  private static final double[] CONbonus = new double[100];
  private static final double[] MENbonus = new double[100];

  private BaseStats() {
  }

  public int getStat(Creature actor) {
    return 1;
  }

  public double calcBonus(Creature actor) {
    return 1.0D;
  }

  public double calcChanceMod(Creature actor) {
    return 2.0D - Math.sqrt(this.calcBonus(actor));
  }

  public static final BaseStats valueOfXml(String name) {
    name = name.intern();
    BaseStats[] var1 = VALUES;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      BaseStats s = var1[var3];
      if (s.toString().equalsIgnoreCase(name)) {
        if (s == NONE) {
          return null;
        }

        return s;
      }
    }

    throw new NoSuchElementException("Unknown name '" + name + "' for enum BaseStats");
  }

  static {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setIgnoringComments(true);
    File file = new File(Config.DATAPACK_ROOT, "data/attribute_bonus.xml");
    Document doc = null;

    try {
      doc = factory.newDocumentBuilder().parse(file);
    } catch (SAXException var10) {
      _log.error("", var10);
    } catch (IOException var11) {
      _log.error("", var11);
    } catch (ParserConfigurationException var12) {
      _log.error("", var12);
    }

    if (doc != null) {
      for(Node z = doc.getFirstChild(); z != null; z = z.getNextSibling()) {
        for(Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
          int i;
          double val;
          Node d;
          String node;
          if (n.getNodeName().equalsIgnoreCase("str_bonus")) {
            for(d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              node = d.getNodeName();
              if (node.equalsIgnoreCase("set")) {
                i = Integer.parseInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = (double)Integer.parseInt(d.getAttributes().getNamedItem("val").getNodeValue());
                STRbonus[i] = (100.0D + val) / 100.0D;
              }
            }
          }

          if (n.getNodeName().equalsIgnoreCase("int_bonus")) {
            for(d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              node = d.getNodeName();
              if (node.equalsIgnoreCase("set")) {
                i = Integer.parseInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = (double)Integer.parseInt(d.getAttributes().getNamedItem("val").getNodeValue());
                INTbonus[i] = (100.0D + val) / 100.0D;
              }
            }
          }

          if (n.getNodeName().equalsIgnoreCase("con_bonus")) {
            for(d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              node = d.getNodeName();
              if (node.equalsIgnoreCase("set")) {
                i = Integer.parseInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = (double)Integer.parseInt(d.getAttributes().getNamedItem("val").getNodeValue());
                CONbonus[i] = (100.0D + val) / 100.0D;
              }
            }
          }

          if (n.getNodeName().equalsIgnoreCase("men_bonus")) {
            for(d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              node = d.getNodeName();
              if (node.equalsIgnoreCase("set")) {
                i = Integer.parseInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = (double)Integer.parseInt(d.getAttributes().getNamedItem("val").getNodeValue());
                MENbonus[i] = (100.0D + val) / 100.0D;
              }
            }
          }

          if (n.getNodeName().equalsIgnoreCase("dex_bonus")) {
            for(d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              node = d.getNodeName();
              if (node.equalsIgnoreCase("set")) {
                i = Integer.parseInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = (double)Integer.parseInt(d.getAttributes().getNamedItem("val").getNodeValue());
                DEXbonus[i] = (100.0D + val) / 100.0D;
              }
            }
          }

          if (n.getNodeName().equalsIgnoreCase("wit_bonus")) {
            for(d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              node = d.getNodeName();
              if (node.equalsIgnoreCase("set")) {
                i = Integer.parseInt(d.getAttributes().getNamedItem("attribute").getNodeValue());
                val = (double)Integer.parseInt(d.getAttributes().getNamedItem("val").getNodeValue());
                WITbonus[i] = (100.0D + val) / 100.0D;
              }
            }
          }
        }
      }
    }

  }
}
