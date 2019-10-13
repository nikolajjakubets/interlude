//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import gnu.trove.TIntArrayList;
import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.templates.Henna;
import org.dom4j.Element;

public final class HennaParser extends AbstractFileParser<HennaHolder> {
  private static final HennaParser _instance = new HennaParser();

  public static HennaParser getInstance() {
    return _instance;
  }

  protected HennaParser() {
    super(HennaHolder.getInstance());
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/hennas.xml");
  }

  public String getDTDFileName() {
    return "hennas.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator();

    while(iterator.hasNext()) {
      Element hennaElement = (Element)iterator.next();
      int symbolId = Integer.parseInt(hennaElement.attributeValue("symbol_id"));
      int dyeId = Integer.parseInt(hennaElement.attributeValue("dye_id"));
      long price = (long)Integer.parseInt(hennaElement.attributeValue("price"));
      long drawCount = hennaElement.attributeValue("draw_count") == null ? 10L : (long)Integer.parseInt(hennaElement.attributeValue("draw_count"));
      int wit = Integer.parseInt(hennaElement.attributeValue("wit"));
      int str = Integer.parseInt(hennaElement.attributeValue("str"));
      int _int = Integer.parseInt(hennaElement.attributeValue("int"));
      int con = Integer.parseInt(hennaElement.attributeValue("con"));
      int dex = Integer.parseInt(hennaElement.attributeValue("dex"));
      int men = Integer.parseInt(hennaElement.attributeValue("men"));
      TIntArrayList list = new TIntArrayList();
      Iterator classIterator = hennaElement.elementIterator("class");

      while(classIterator.hasNext()) {
        Element classElement = (Element)classIterator.next();
        list.add(Integer.parseInt(classElement.attributeValue("id")));
      }

      Henna henna = new Henna(symbolId, dyeId, price, drawCount, wit, _int, con, str, dex, men, list);
      ((HennaHolder)this.getHolder()).addHenna(henna);
    }

  }
}
