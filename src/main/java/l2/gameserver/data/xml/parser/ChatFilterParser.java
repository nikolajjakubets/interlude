//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import gnu.trove.TIntArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.model.chat.chatfilter.matcher.MatchChatChannels;
import l2.gameserver.model.chat.chatfilter.matcher.MatchChatLimit;
import l2.gameserver.model.chat.chatfilter.matcher.MatchFloodLimit;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalAnd;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalNot;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalOr;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalXor;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMaps;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinJobLevel;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinLevel;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinLiveTime;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinOnlineTime;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinPvP;
import l2.gameserver.model.chat.chatfilter.matcher.MatchPremiumState;
import l2.gameserver.model.chat.chatfilter.matcher.MatchRecipientLimit;
import l2.gameserver.model.chat.chatfilter.matcher.MatchWords;
import l2.gameserver.network.l2.components.ChatType;
import org.dom4j.Element;

public class ChatFilterParser extends AbstractFileParser<ChatFilters> {
  private static ChatFilterParser _instance = new ChatFilterParser();

  public static ChatFilterParser getInstance() {
    return _instance;
  }

  protected ChatFilterParser() {
    super(ChatFilters.getinstance());
  }

  protected List<ChatFilterMatcher> parseMatchers(Element n) throws Exception {
    List<ChatFilterMatcher> matchers = new ArrayList();
    Iterator nItr = n.elementIterator();

    while(true) {
      while(nItr.hasNext()) {
        Element e = (Element)nItr.next();
        StringTokenizer st;
        ArrayList words;
        if (e.getName().equals("Channels")) {
          words = new ArrayList();
          st = new StringTokenizer(e.getText(), ",");

          while(st.hasMoreTokens()) {
            words.add(ChatType.valueOf(st.nextToken()));
          }

          matchers.add(new MatchChatChannels((ChatType[])words.toArray(new ChatType[words.size()])));
        } else if (e.getName().equals("Maps")) {
          TIntArrayList maps = new TIntArrayList();
          st = new StringTokenizer(e.getText(), ",");

          while(st.hasMoreTokens()) {
            String[] map = st.nextToken().split("_");
            maps.add(Integer.parseInt(map[0]));
            maps.add(Integer.parseInt(map[1]));
          }

          matchers.add(new MatchMaps(maps.toNativeArray()));
        } else if (e.getName().equals("Words")) {
          words = new ArrayList();
          st = new StringTokenizer(e.getText());

          while(st.hasMoreTokens()) {
            words.add(st.nextToken());
          }

          matchers.add(new MatchWords((String[])words.toArray(new String[words.size()])));
        } else if (e.getName().equals("ExcludePremium")) {
          matchers.add(new MatchPremiumState(Boolean.parseBoolean(e.getText())));
        } else if (e.getName().equals("Level")) {
          matchers.add(new MatchMinLevel(Integer.parseInt(e.getText())));
        } else if (e.getName().equals("PvP_count")) {
          matchers.add(new MatchMinPvP(Integer.parseInt(e.getText())));
        } else if (e.getName().equals("JobLevel")) {
          matchers.add(new MatchMinJobLevel(Integer.parseInt(e.getText())));
        } else if (e.getName().equals("OnlineTime")) {
          matchers.add(new MatchMinOnlineTime(Integer.parseInt(e.getText())));
        } else if (e.getName().equals("LiveTime")) {
          matchers.add(new MatchMinLiveTime(Integer.parseInt(e.getText())));
        } else if (!e.getName().endsWith("Limit")) {
          List matches;
          if (e.getName().equals("Or")) {
            matches = this.parseMatchers(e);
            matchers.add(new MatchLogicalOr((ChatFilterMatcher[])matches.toArray(new ChatFilterMatcher[matches.size()])));
          } else if (e.getName().equals("And")) {
            matches = this.parseMatchers(e);
            matchers.add(new MatchLogicalAnd((ChatFilterMatcher[])matches.toArray(new ChatFilterMatcher[matches.size()])));
          } else if (e.getName().equals("Not")) {
            matches = this.parseMatchers(e);
            if (matches.size() == 1) {
              matchers.add(new MatchLogicalNot((ChatFilterMatcher)matches.get(0)));
            } else {
              matchers.add(new MatchLogicalNot(new MatchLogicalAnd((ChatFilterMatcher[])matches.toArray(new ChatFilterMatcher[matches.size()]))));
            }
          } else if (e.getName().equals("Xor")) {
            matches = this.parseMatchers(e);
            matchers.add(new MatchLogicalXor((ChatFilterMatcher[])matches.toArray(new ChatFilterMatcher[matches.size()])));
          }
        } else {
          int limitCount = 0;
          int limitTime = 0;
          int limitBurst = 0;
          Iterator eItr = e.elementIterator();

          while(eItr.hasNext()) {
            Element d = (Element)eItr.next();
            if (d.getName().equals("Count")) {
              limitCount = Integer.parseInt(d.getText());
            } else if (d.getName().equals("Time")) {
              limitTime = Integer.parseInt(d.getText());
            } else if (d.getName().equals("Burst")) {
              limitBurst = Integer.parseInt(d.getText());
            }
          }

          if (limitCount < 1) {
            throw new IllegalArgumentException("Limit Count < 1!");
          }

          if (limitTime < 1) {
            throw new IllegalArgumentException("Limit Time  < 1!");
          }

          if (limitBurst < 1) {
            throw new IllegalArgumentException("Limit Burst < 1!");
          }

          if (e.getName().equals("Limit")) {
            matchers.add(new MatchChatLimit(limitCount, limitTime, limitBurst));
          } else if (e.getName().equals("FloodLimit")) {
            matchers.add(new MatchFloodLimit(limitCount, limitTime, limitBurst));
          } else if (e.getName().equals("RecipientLimit")) {
            matchers.add(new MatchRecipientLimit(limitCount, limitTime, limitBurst));
          }
        }
      }

      return matchers;
    }
  }

  protected void readData(Element rootElement) throws Exception {
    Object matcher;
    byte action;
    String value;
    for(Iterator iterator = rootElement.elementIterator(); iterator.hasNext(); ((ChatFilters)this.getHolder()).add(new ChatFilter((ChatFilterMatcher)matcher, action, value))) {
      action = 0;
      value = null;
      Element filterElement = (Element)iterator.next();
      Iterator filterItr = filterElement.elementIterator();

      while(filterItr.hasNext()) {
        Element e = (Element)filterItr.next();
        if (e.getName().equals("Action")) {
          String banStr = e.getText();
          if (banStr.equals("BanChat")) {
            action = 1;
          } else if (banStr.equals("WarnMsg")) {
            action = 2;
          } else if (banStr.equals("ReplaceMsg")) {
            action = 3;
          } else if (banStr.equals("RedirectMsg")) {
            action = 4;
          }
        } else if (e.getName().equals("BanTime")) {
          value = String.valueOf(Integer.parseInt(e.getText()));
        } else if (e.getName().equals("RedirectChannel")) {
          value = ChatType.valueOf(e.getText()).toString();
        } else if (e.getName().equals("ReplaceMsg")) {
          value = e.getText();
        } else if (e.getName().equals("WarnMsg")) {
          value = e.getText();
        }
      }

      List<ChatFilterMatcher> matchers = this.parseMatchers(filterElement);
      if (matchers.isEmpty()) {
        throw new IllegalArgumentException("No matchers defined for a filter!");
      }

      if (matchers.size() == 1) {
        matcher = (ChatFilterMatcher)matchers.get(0);
      } else {
        matcher = new MatchLogicalAnd((ChatFilterMatcher[])matchers.toArray(new ChatFilterMatcher[matchers.size()]));
      }
    }

  }

  public File getXMLFile() {
    return new File("config/chatfilters.xml");
  }

  public String getDTDFileName() {
    return "chatfilters.dtd";
  }
}
