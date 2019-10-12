package l2.authserver;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class IpBanManager {
    private static final IpBanManager _instance = new IpBanManager();
    private final Map<String, IpSession> ips = new HashMap<String, IpSession>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock;
    private final Lock writeLock;

    public static IpBanManager getInstance() {
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

                    while (itr.hasNext()) {
                        IpBanManager.IpSession session = (IpBanManager.IpSession) itr.next();
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
                if ((ipsession = this.ips.get(ip)) == null) {
                    return false;
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

            try {
                IpBanManager.IpSession ipsession = this.ips.get(ip);
                if (ipsession == null) {
                    this.ips.put(ip, ipsession = new IpSession());
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
                    return true;
                }

                log.warn("tryLogin: IpBanManager: " + ip + " banned for " + Config.IP_BAN_TIME / 1000L + " seconds.");
                ipsession.banExpire = currentMillis + Config.IP_BAN_TIME;
            } finally {
                this.writeLock.unlock();
            }

            return false;
        }
    }

    private static class IpSession {
        int tryCount;
        long lastTry;
        long banExpire;

        private IpSession() {
        }
    }
}
