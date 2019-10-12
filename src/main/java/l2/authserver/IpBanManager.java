package l2.authserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpBanManager {
    private static final Logger _log = LoggerFactory.getLogger(IpBanManager.class);
    private static final IpBanManager _instance = new IpBanManager();
    private final Map<String, IpBanManager.IpSession> ips = new HashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock;
    private final Lock writeLock;

    public static final IpBanManager getInstance() {
        return _instance;
    }

    private IpBanManager() {
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            public void run() {
                long currentMillis = System.currentTimeMillis();
                IpBanManager.this.writeLock.lock();

                try {
                    Iterator itr = IpBanManager.this.ips.values().iterator();

                    while(itr.hasNext()) {
                        IpBanManager.IpSession session = (IpBanManager.IpSession)itr.next();
                        if (session.banExpire < currentMillis && session.lastTry < currentMillis - Config.LOGIN_TRY_TIMEOUT) {
                            itr.remove();
                        }
                    }
                } finally {
                    IpBanManager.this.writeLock.unlock();
                }

            }
        }, 1000L, 1000L);
    }

    public boolean isIpBanned(String ip) {
        if (Config.WHITE_IPS.contains(ip)) {
            return false;
        } else {
            this.readLock.lock();

            boolean var3;
            try {
                IpBanManager.IpSession ipsession;
                if ((ipsession = (IpBanManager.IpSession)this.ips.get(ip)) == null) {
                    var3 = false;
                    return var3;
                }

                var3 = ipsession.banExpire > System.currentTimeMillis();
            } finally {
                this.readLock.unlock();
            }

            return var3;
        }
    }

    public boolean tryLogin(String ip, boolean success) {
        if (Config.WHITE_IPS.contains(ip)) {
            return true;
        } else {
            this.writeLock.lock();

            boolean var6;
            try {
                IpBanManager.IpSession ipsession = (IpBanManager.IpSession)this.ips.get(ip);
                if (ipsession == null) {
                    this.ips.put(ip, ipsession = new IpBanManager.IpSession());
                }

                long currentMillis = System.currentTimeMillis();
                if (currentMillis - ipsession.lastTry < Config.LOGIN_TRY_TIMEOUT) {
                    success = false;
                }

                if (success) {
                    if (ipsession.tryCount > 0) {
                        --ipsession.tryCount;
                    }
                } else if (ipsession.tryCount < Config.LOGIN_TRY_BEFORE_BAN) {
                    ++ipsession.tryCount;
                }

                ipsession.lastTry = currentMillis;
                if (ipsession.tryCount != Config.LOGIN_TRY_BEFORE_BAN) {
                    var6 = true;
                    return var6;
                }

                _log.warn("IpBanManager: " + ip + " banned for " + Config.IP_BAN_TIME / 1000L + " seconds.");
                ipsession.banExpire = currentMillis + Config.IP_BAN_TIME;
                var6 = false;
            } finally {
                this.writeLock.unlock();
            }

            return var6;
        }
    }

    private class IpSession {
        public int tryCount;
        public long lastTry;
        public long banExpire;

        private IpSession() {
        }
    }
}
