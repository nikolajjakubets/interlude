package com.lineage2.interlude.network.l2;

import java.nio.channels.SocketChannel;
import l2.authserver.IpBanManager;
import l2.authserver.ThreadPoolManager;
import l2.authserver.network.l2.s2c.Init;
import l2.commons.net.nio.impl.IAcceptFilter;
import l2.commons.net.nio.impl.IClientFactory;
import l2.commons.net.nio.impl.IMMOExecutor;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.threading.RunnableImpl;

public class SelectorHelper implements IMMOExecutor<L2LoginClient>, IClientFactory<L2LoginClient>, IAcceptFilter {
    public SelectorHelper() {
    }

    public void execute(Runnable r) {
        ThreadPoolManager.getInstance().execute(r);
    }

    public L2LoginClient create(MMOConnection<L2LoginClient> con) {
        final L2LoginClient client = new L2LoginClient(con);
        client.sendPacket(new Init(client));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            public void runImpl() {
                client.closeNow(false);
            }
        }, 60000L);
        return client;
    }

    public boolean accept(SocketChannel sc) {
        return !IpBanManager.getInstance().isIpBanned(sc.socket().getInetAddress().getHostAddress());
    }
}
