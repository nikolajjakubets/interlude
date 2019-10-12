package l2.authserver.accounts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.authserver.ThreadPoolManager;
import l2.authserver.network.l2.SessionKey;
import l2.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger _log = LoggerFactory.getLogger(SessionManager.class);
    private static final SessionManager _instance = new SessionManager();
    private final Map<SessionKey, SessionManager.Session> sessions = new HashMap();
    private final Lock lock = new ReentrantLock();

    public static final SessionManager getInstance() {
        return _instance;
    }

    private SessionManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            public void runImpl() {
                SessionManager.this.lock.lock();

                try {
                    long currentMillis = System.currentTimeMillis();
                    Iterator itr = SessionManager.this.sessions.values().iterator();

                    while(itr.hasNext()) {
                        SessionManager.Session session = (SessionManager.Session)itr.next();
                        if (session.getExpireTime() < currentMillis) {
                            itr.remove();
                        }
                    }
                } finally {
                    SessionManager.this.lock.unlock();
                }

            }
        }, 30000L, 30000L);
    }

    public SessionManager.Session openSession(Account account) {
        this.lock.lock();

        SessionManager.Session var3;
        try {
            SessionManager.Session session = new SessionManager.Session(account);
            this.sessions.put(session.getSessionKey(), session);
            var3 = session;
        } finally {
            this.lock.unlock();
        }

        return var3;
    }

    public SessionManager.Session closeSession(SessionKey skey) {
        this.lock.lock();

        SessionManager.Session var2;
        try {
            var2 = (SessionManager.Session)this.sessions.remove(skey);
        } finally {
            this.lock.unlock();
        }

        return var2;
    }

    public SessionManager.Session getSessionByName(String name) {
        Iterator var2 = this.sessions.values().iterator();

        SessionManager.Session session;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            session = (SessionManager.Session)var2.next();
        } while(!session.account.getLogin().equalsIgnoreCase(name));

        return session;
    }

    public final class Session {
        private final Account account;
        private final SessionKey skey;
        private final long expireTime;

        private Session(Account account) {
            this.account = account;
            this.skey = SessionKey.create();
            this.expireTime = System.currentTimeMillis() + 60000L;
        }

        public SessionKey getSessionKey() {
            return this.skey;
        }

        public Account getAccount() {
            return this.account;
        }

        public long getExpireTime() {
            return this.expireTime;
        }
    }
}
