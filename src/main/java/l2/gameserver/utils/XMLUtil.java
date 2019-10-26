//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class XMLUtil {
  private static final Logger _log = LoggerFactory.getLogger(XMLUtil.class);

  public XMLUtil() {
  }

  public static String getAttributeValue(Node n, String item) {
    Node d = n.getAttributes().getNamedItem(item);
    if (d == null) {
      return "";
    } else {
      String val = d.getNodeValue();
      return val == null ? "" : val;
    }
  }

  public static boolean getAttributeBooleanValue(Node n, String item, boolean dflt) {
    Node d = n.getAttributes().getNamedItem(item);
    if (d == null) {
      return dflt;
    } else {
      String val = d.getNodeValue();
      return val == null ? dflt : Boolean.parseBoolean(val);
    }
  }

  public static int getAttributeIntValue(Node n, String item, int dflt) {
    Node d = n.getAttributes().getNamedItem(item);
    if (d == null) {
      return dflt;
    } else {
      String val = d.getNodeValue();
      return val == null ? dflt : Integer.parseInt(val);
    }
  }

  public static long getAttributeLongValue(Node n, String item, long dflt) {
    Node d = n.getAttributes().getNamedItem(item);
    if (d == null) {
      return dflt;
    } else {
      String val = d.getNodeValue();
      return val == null ? dflt : Long.parseLong(val);
    }
  }
}
