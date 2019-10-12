//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

public interface IClientFactory<T extends MMOClient> {
    T create(MMOConnection<T> var1);
}
