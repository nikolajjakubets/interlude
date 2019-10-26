//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.telnet;

import org.apache.commons.lang3.ArrayUtils;

public abstract class TelnetCommand implements Comparable<TelnetCommand> {
  private final String command;
  private final String[] acronyms;

  public TelnetCommand(String command) {
    this(command, ArrayUtils.EMPTY_STRING_ARRAY);
  }

  public TelnetCommand(String command, String... acronyms) {
    this.command = command;
    this.acronyms = acronyms;
  }

  public String getCommand() {
    return this.command;
  }

  public String[] getAcronyms() {
    return this.acronyms;
  }

  public abstract String getUsage();

  public abstract String handle(String[] var1);

  public boolean equals(String command) {
    String[] var2 = this.acronyms;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String acronym = var2[var4];
      if (command.equals(acronym)) {
        return true;
      }
    }

    return this.command.equalsIgnoreCase(command);
  }

  public String toString() {
    return this.command;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return true;
    } else {
      return o instanceof TelnetCommand ? this.command.equals(((TelnetCommand)o).command) : false;
    }
  }

  public int compareTo(TelnetCommand o) {
    return this.command.compareTo(o.command);
  }
}
