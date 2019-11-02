package l2.commons.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class BaseDataConnectionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDataConnectionFactory.class);
    private final ConnectionPoolDataSource _connectionPoolDataSource;
    private final Semaphore _semaphore;
    private final ArrayDeque<PooledConnection> _recycledConnections;
    private final int _maxConnections;
    private final long _timeout;
    private boolean _isDisposed;
    private int _activeConnections;
    private PooledConnection _connectionInTransition;
    private final BaseDataConnectionFactory.PoolConnectionEventListener _poolConnectionEventListener;

    protected BaseDataConnectionFactory(ConnectionPoolDataSource connectionPoolDataSource, int maxConnections, int timeout) {
        this._connectionPoolDataSource = connectionPoolDataSource;
        this._maxConnections = maxConnections;
      this._timeout = timeout;
        if (maxConnections < 1) {
            throw new IllegalArgumentException("Invalid maxConnections value.");
        } else {
            this._semaphore = new Semaphore(maxConnections, true);
            this._recycledConnections = new ArrayDeque(this._maxConnections);
            this._poolConnectionEventListener = new BaseDataConnectionFactory.PoolConnectionEventListener();
            this._isDisposed = false;
            this._activeConnections = 0;

            try {
                this.testDB();
                LOG.info("DatabaseFactory: Database connection tested and working.");
            } catch (SQLException var5) {
                throw new RuntimeException("Can't init database connections pool", var5);
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return this.getConnectionImpl();
    }

    protected abstract void testDB() throws SQLException;

  private Connection getConnectionImpl() throws SQLException {
        synchronized(this) {
            if (this._isDisposed) {
                throw new IllegalStateException("Connection pool has been disposed.");
            }
        }

        Thread.currentThread();
        boolean interrupted = Thread.interrupted();

        Connection var4;
        try {
            try {
                if (!this._semaphore.tryAcquire(this._timeout, TimeUnit.MILLISECONDS)) {
                    throw new BaseDataConnectionFactory.TimeoutException();
                }
            } catch (InterruptedException var16) {
                throw new RuntimeException("Interrupted while waiting for a database connection.", var16);
            }

            boolean ok = false;

            try {
                Connection conn = this.getConnectionImpl0();
                ok = true;
                var4 = conn;
            } finally {
                if (!ok) {
                    this._semaphore.release();
                }

            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }

        }

        return var4;
    }

    private synchronized Connection getConnectionImpl0() throws SQLException {
        if (this._isDisposed) {
            throw new IllegalStateException("Connection pool has been disposed.");
        } else {
            PooledConnection pconn;
            if (!this._recycledConnections.isEmpty()) {
              pconn = this._recycledConnections.remove();
            } else {
                pconn = this._connectionPoolDataSource.getPooledConnection();
                pconn.addConnectionEventListener(this._poolConnectionEventListener);
            }

            Connection conn;
            try {
                this._connectionInTransition = pconn;
                conn = pconn.getConnection();
            } finally {
                this._connectionInTransition = null;
            }

            ++this._activeConnections;
            this.checkInnerState();
            return conn;
        }
    }

    private void checkInnerState() {
        if (this._activeConnections < 0) {
            throw new RuntimeException();
        } else if (this._activeConnections + this._recycledConnections.size() > this._maxConnections) {
            throw new RuntimeException();
        } else if (this._activeConnections + this._semaphore.availablePermits() > this._maxConnections) {
            throw new RuntimeException();
        }
    }

    private synchronized void recycleConnection(PooledConnection pconn) {
        if (this._isDisposed) {
            this.disposeConnection(pconn);
        } else if (this._activeConnections <= 0) {
            throw new AssertionError();
        } else {
            --this._activeConnections;
            this._semaphore.release();
            this._recycledConnections.add(pconn);
            this.checkInnerState();
        }
    }

    private synchronized void disposeConnection(PooledConnection pconn) {
        pconn.removeConnectionEventListener(this._poolConnectionEventListener);
        if (!this._recycledConnections.remove(pconn) && pconn != this._connectionInTransition) {
            if (this._activeConnections <= 0) {
                throw new AssertionError();
            }

            --this._activeConnections;
            this._semaphore.release();
        }

        this.closeConnectionAndIgnoreException(pconn);
        this.checkInnerState();
    }

    private void closeConnectionAndIgnoreException(PooledConnection pconn) {
        try {
            pconn.close();
        } catch (SQLException var3) {
            LOG.error("Error while closing database connection", var3);
        }

    }

    public synchronized void shutdown() throws SQLException {
        if (!this._isDisposed) {
            this._isDisposed = true;
            SQLException e = null;

            while(!this._recycledConnections.isEmpty()) {
              PooledConnection pconn = this._recycledConnections.remove();

                try {
                    pconn.close();
                } catch (SQLException var4) {
                    if (e == null) {
                        e = var4;
                    }
                }
            }

            if (e != null) {
                throw e;
            }
        }
    }

    private class PoolConnectionEventListener implements ConnectionEventListener {
        private PoolConnectionEventListener() {
        }

        public void connectionClosed(ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection)event.getSource();
            BaseDataConnectionFactory.this.recycleConnection(pconn);
        }

        public void connectionErrorOccurred(ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection)event.getSource();
            BaseDataConnectionFactory.this.disposeConnection(pconn);
        }
    }

    public static class TimeoutException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public TimeoutException() {
            super("Timeout while waiting for a free database connection.");
        }

        public TimeoutException(String msg) {
            super(msg);
        }
    }
}
